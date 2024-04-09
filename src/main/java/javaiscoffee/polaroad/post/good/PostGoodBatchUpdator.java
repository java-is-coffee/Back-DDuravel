package javaiscoffee.polaroad.post.good;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.post.PostInfoCachingDto;
import javaiscoffee.polaroad.post.PostRepository;
import javaiscoffee.polaroad.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PostGoodBatchUpdator {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final static String POST_GOOD_BATCH_PREFIX = "pgb:";
    private final static String POST_CACHING_PREFIX = "pc:";
    @Autowired
    public PostGoodBatchUpdator(RedisTemplate<String, String> redisTemplate, PostRepository postRepository, RedisService redisService, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.postRepository = postRepository;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    /**
     * 5분마다 저장되어 있던 추천 변동 사항 db에 적용
     */

    @Scheduled(fixedRate = 300000)
//    @Scheduled(fixedRate = 30000)
    public void updatePostGoodsBatch() {
        String batchPattern = POST_GOOD_BATCH_PREFIX+"*";
        RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(batchPattern).build();

        ConvertingCursor<byte[], String> cursor = redisTemplate.executeWithStickyConnection(redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(scanOptions), keySerializer::deserialize));

        if(cursor==null) return;
        log.info("좋아요 배치 작업 시작");

        while (cursor.hasNext()) {
            String key = Objects.requireNonNull(cursor.next());
            Long postId = extractPostIdFromKey(Objects.requireNonNull(key));
            int goodNumber = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)));

            postRepository.updatePostGoodNumber(goodNumber, postId);
            int updatedGoodNumber = postRepository.getPostGoodNumber(postId);
            if(updatedGoodNumber >= 10) {
                String cachingKey = POST_CACHING_PREFIX + postId;
                log.info("캐싱이 되어 있는지 확인 = {}",redisTemplate.hasKey(cachingKey));
                //레디스에 저장이 안되어 있는 경우 캐싱시간 1시간
                if(Boolean.FALSE.equals(redisTemplate.hasKey(cachingKey))) {
                    PostInfoCachingDto cachingDto = postRepository.getPostCachingDtoById(postId);
                    redisService.saveCachingPostInfo(cachingDto, postId);
                }
                //레디스에 저장되어 있는 경우 캐싱 시간 연장
                else {
                    redisTemplate.expire(cachingKey, 60, TimeUnit.MINUTES);
                }
            }

            redisTemplate.delete(key);
            log.debug("좋아요 변경 postId = {} goodNumber={}",postId,goodNumber);
        }
    }

    public void increasePostGoodCount(Long postId) {
        String postGoodCountKey = POST_GOOD_BATCH_PREFIX + postId;
        redisTemplate.opsForValue().increment(postGoodCountKey);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(postGoodCountKey))) {
            redisTemplate.expire(postGoodCountKey, 15, TimeUnit.MINUTES);
        }
    }

    public void decreasePostGoodCount(Long postId) {
        String postGoodCountKey = POST_GOOD_BATCH_PREFIX + postId;
        redisTemplate.opsForValue().decrement(postGoodCountKey);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(postGoodCountKey))) {
            redisTemplate.expire(postGoodCountKey, 15, TimeUnit.MINUTES);
        }
    }

    private Long extractPostIdFromKey(String key) {
        return Long.parseLong(key.substring(POST_GOOD_BATCH_PREFIX.length()));
    }
}

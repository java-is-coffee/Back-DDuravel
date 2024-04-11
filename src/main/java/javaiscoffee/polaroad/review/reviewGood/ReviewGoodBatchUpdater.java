package javaiscoffee.polaroad.review.reviewGood;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaiscoffee.polaroad.redis.RedisService;
import javaiscoffee.polaroad.review.ReviewInfoCachingDto;
import javaiscoffee.polaroad.review.ReviewRepository;
import javaiscoffee.polaroad.review.ReviewStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ReviewGoodBatchUpdater {
    private final RedisTemplate<String, String> redisTemplate;
    private final ReviewRepository reviewRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final static String REVIEW_GOOD_BATCH_PREFIX = "rgb:";
    private final static String REVIEW_CACHING_PREFIX = "rc:";

    @Autowired
    public ReviewGoodBatchUpdater(RedisTemplate<String, String> redisTemplate, ReviewRepository reviewRepository, RedisService redisService, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.reviewRepository = reviewRepository;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 300000)
    public void updateReviewGoodBatch() {
        String batchPattern = REVIEW_GOOD_BATCH_PREFIX +"*";
        RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(batchPattern).build();

        ConvertingCursor<byte[], String> cursor = redisTemplate.executeWithStickyConnection(redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(scanOptions), keySerializer::deserialize));

        if(cursor==null) return;
        log.info("좋아요 배치 작업 시작");

        while (cursor.hasNext()) {
            String key = Objects.requireNonNull(cursor.next());
            Long reviewId = extractReviewIdFromKey(Objects.requireNonNull(key));
            int goodNumber = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)));

            reviewRepository.updateReviewGoodNumber(goodNumber, reviewId);
            int updateGoodNumber = reviewRepository.getReviewGoodNumber(reviewId);
            if (updateGoodNumber >= 10) {
                String cachingKey = REVIEW_CACHING_PREFIX + reviewId;
                log.info("캐싱 됐는지 확인 = {}", redisTemplate.hasKey(cachingKey));
                // 레디스에 저장 되어 있지 않은 경우 캐싱 시간 : 1시간
                if (Boolean.FALSE.equals(redisTemplate.hasKey(cachingKey))) {
                    ReviewInfoCachingDto cachingDto = reviewRepository.getReviewCachingDto(reviewId, ReviewStatus.ACTIVE);
                    redisService.saveCachingReviewInfo(cachingDto, reviewId);
                }
                // 저장 되어 있는 경우 캐싱 시간 연장
                else {
                    redisTemplate.expire(cachingKey, 60, TimeUnit.MINUTES);
                }
            }

            redisTemplate.delete(key);
            log.debug("좋아요 변경 reviewId = {} goodNumber = {}", reviewId, goodNumber);
        }

    }

    public void increaseReviewGoodCount(Long reviewId) {
        String reviewGoodCountKey = REVIEW_GOOD_BATCH_PREFIX + reviewId;
        redisTemplate.opsForValue().increment(reviewGoodCountKey);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(reviewGoodCountKey))) {
            redisTemplate.expire(reviewGoodCountKey, 15, TimeUnit.MINUTES);
        }
    }
    public void decreaseReviewGoodCount(Long reviewId) {
        String reviewGoodCountKey = REVIEW_GOOD_BATCH_PREFIX + reviewId;
        redisTemplate.opsForValue().decrement(reviewGoodCountKey);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(reviewGoodCountKey))) {
            redisTemplate.expire(reviewGoodCountKey, 15, TimeUnit.MINUTES);
        }
    }


    private Long extractReviewIdFromKey(String key) {
        return Long.parseLong(key.substring(REVIEW_GOOD_BATCH_PREFIX.length()));
    }
}

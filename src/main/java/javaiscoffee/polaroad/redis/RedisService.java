package javaiscoffee.polaroad.redis;

import javaiscoffee.polaroad.post.PostRankingRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String MEMBER_VIEW_PREFIX = "mv:";
    private final static String POST_VIEW_DAILY_PREFIX = "pvd:";
    private final static String POST_VIEW_WEEKLY_PREFIX = "pvw:";
    private final static String POST_VIEW_MONTHLY_PREFIX = "pvm:";
    private final static String POST_GOOD_DAILY_PREFIX = "pgd:";
    private final static String POST_GOOD_WEEKLY_PREFIX = "pgw:";
    private final static String POST_GOOD_MONTHLY_PREFIX = "pgm:";

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, String value) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(key, value, 1, TimeUnit.MINUTES);  // 1분 동안만 데이터 유지
    }

    public String getValue(String key) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(key);
    }

    /**
     * 멤버별 포스트 조회 기록 추가 + 포스트 조회 수 1 증가
     */
    public void addPostView(Long postId, Long memberId) {
        String memberViewKey = MEMBER_VIEW_PREFIX + memberId;
        // 사용자 조회 기록이 있으면 추가 안함
        Double score = redisTemplate.opsForZSet().score(memberViewKey, postId.toString());
        if(score != null) return;
        //개인 조회 기록 생성
        addPersonalPostView(postId, memberId);
        //포스트 조회 기록 생성
        addPostViewCounts(postId);
    }

    //개인별 조회 기록 생성
    private void addPersonalPostView(Long postId, Long memberId) {
        String memberViewKey = MEMBER_VIEW_PREFIX + memberId;
        long timestamp = System.currentTimeMillis() / 1000;  // 초 단위 시간

        // 사용자별 게시글 조회 기록 추가 + 기록은 자정까지 유지
        redisTemplate.opsForZSet().add(memberViewKey, postId.toString(), timestamp);
        redisTemplate.expire(memberViewKey, Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds(), TimeUnit.SECONDS);
    }

    //포스트별 조회 기록 생성
    private void addPostViewCounts(Long postId) {
        // 날짜별 키 설정
        String dailyKey = POST_VIEW_DAILY_PREFIX + LocalDate.now();
        String weeklyKey = POST_VIEW_WEEKLY_PREFIX + LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String monthlyKey = POST_VIEW_MONTHLY_PREFIX + YearMonth.now();

        // 포스트 조회 기록 증가
        Double isNewDaily = redisTemplate.opsForZSet().incrementScore(dailyKey, postId.toString(), 1);
        Double isNewWeekly = redisTemplate.opsForZSet().incrementScore(weeklyKey, postId.toString(), 1);
        Double isNewMonthly = redisTemplate.opsForZSet().incrementScore(monthlyKey, postId.toString(), 1);

        // 각 기간별 조회기록이 새로 생성되었다면 유효 기간 설정
        if (isNewDaily != null && isNewDaily.equals(1d)) {
            redisTemplate.expire(dailyKey, Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds(), TimeUnit.SECONDS);
        }
        if (isNewWeekly != null && isNewWeekly.equals(1d)) {
            redisTemplate.expire(weeklyKey, Duration.between(LocalDateTime.now(), LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atStartOfDay()).getSeconds(), TimeUnit.SECONDS);
        }
        if (isNewMonthly != null && isNewMonthly.equals(1d)) {
            redisTemplate.expire(monthlyKey, Duration.between(LocalDateTime.now(), LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay()).getSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * 일일 조회 수 상위 게시물 조회
     */
    public List<String> getViewRankingList(int page, int pageSize, PostRankingRange range) {
        String key;
        if(range.equals(PostRankingRange.DAILY)) {
            key = POST_VIEW_DAILY_PREFIX + LocalDate.now();
        }
        else if(range.equals(PostRankingRange.WEEKLY)) {
            key = POST_VIEW_WEEKLY_PREFIX + LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        else {
            key = POST_VIEW_MONTHLY_PREFIX + YearMonth.now();
        }
        long start = (long) (page - 1) * pageSize;
        // 조회수 상위 포스트 조회
        Set<String> results = redisTemplate.opsForZSet().reverseRange(key, start, start + pageSize);
        if(results == null) return new ArrayList<>();
        return results.stream().toList();
    }

    public int getViewRankingMaxPageSize(int pageSize, PostRankingRange range) {
        String key;
        if(range.equals(PostRankingRange.DAILY)) {
            key = POST_VIEW_DAILY_PREFIX + LocalDate.now();
        }
        else if(range.equals(PostRankingRange.WEEKLY)) {
            key = POST_VIEW_WEEKLY_PREFIX + LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        else {
            key = POST_VIEW_MONTHLY_PREFIX + YearMonth.now();
        }
        Long total = redisTemplate.opsForZSet().size(key);
        if(total == null) return 0;
        return (int)Math.ceil((double) total / pageSize);
    }

    /**
     * 이메일과 인증번호 저장, 만료시간 매개변수로 설정
     */
    public void saveEmailVerificationCode(String email, String verificationCode, int minutes) {
        redisTemplate.opsForValue().set(email, verificationCode, minutes, TimeUnit.MINUTES);
    }
    /**
     * 인증 번호 기록이 존재하는지 확인
     */
    public boolean checkEmailVerificationExists(String email) {
        Boolean check = redisTemplate.hasKey(email);
        if(check == null) return false;
        return check;
    }
    /**
     * 인증 번호가 맞는지 확인
     * 인증 번호가 존재하지 않아도 false
     */
    public boolean checkEmailVerificationCode(String email, String certificationCode) {
        String savedCode = redisTemplate.opsForValue().get(email);
        return certificationCode.equals(savedCode);
    }

    /**
     * 이메일 인증 번호 요구 시간이 매개변수 시간이하인지 확인
     */
    public boolean checkVerificationTime(String email, int expireMinutes, int seconds) {
        Long expireTime = redisTemplate.getExpire(email, TimeUnit.SECONDS);
        return expireTime != null && (int)(expireMinutes * 60) - expireTime <= seconds;
    }
}


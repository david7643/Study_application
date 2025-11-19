package Android_Project.Study_application.service;
// import ... (기존 임포트문 동일)
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    // --- @BeforeEach setUp() 메서드 삭제 ---
    // 이 메서드의 설정이 deleteData_Success 테스트에서 불필요한 Stubbing을 만들었습니다.

    @Test
    @DisplayName("setData - 분(minutes)을 Duration으로 변환하여 set을 호출한다")
    void setData_Success() {
        // given (준비)
        // ⭐️ 이 테스트에 필요한 Mock 설정을 여기에 직접 추가
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        String key = "email:test@example.com";
        String value = "123456";
        long minutes = 3;
        Duration expectedDuration = Duration.ofMinutes(minutes);

        // when (실행)
        redisService.setData(key, value, minutes);

        // then (검증)
        then(valueOperations).should(times(1)).set(key, value, expectedDuration);
    }

    @Test
    @DisplayName("getData - get을 호출하고 반환된 값을 그대로 반환한다")
    void getData_Success() {
        // given (준비)
        // ⭐️ 이 테스트에 필요한 Mock 설정을 여기에 직접 추가
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        String key = "email:test@example.com";
        String expectedValue = "123456";

        // valueOperations.get(key)가 호출되면 expectedValue를 반환하도록 설정
        given(valueOperations.get(key)).willReturn(expectedValue);

        // when (실행)
        String actualValue = redisService.getData(key);

        // then (검증)
        then(valueOperations).should(times(1)).get(key);
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("deleteData - delete를 호출한다")
    void deleteData_Success() {
        // given (준비)
        String key = "email:test@example.com";
        // (이 테스트는 opsForValue() 설정이 필요 없으므로 추가하지 않음)

        // when (실행)
        redisService.deleteData(key);

        // then (검증)
        then(redisTemplate).should(times(1)).delete(key);
    }
}
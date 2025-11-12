package Android_Project.Study_application.service;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class EmailAuthService {
    // 1. CaffeineConfig에서 등록한 CacheManager를 주입받아요.
    private final CacheManager cacheManager;

    // 2. 사용할 캐시의 이름을 상수로 정의해요. (CaffeineConfig에서 설정한 이름)
    private static final String CACHE_NAME = "emailAuthCodes";

    /**
     * 캐시에서 "emailAuthCodes"라는 이름의 캐시 영역을 가져오는 헬퍼 메소드예요.
     */
    private Cache getCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            // "emailAuthCodes" 캐시가 존재하지 않는 경우 (설정 오류)
            throw new IllegalStateException("'" + CACHE_NAME + "' 캐시가 설정되지 않았습니다.");
        }
        return cache;
    }

    /**
     * [저장] 이메일(Key)과 인증 코드(Value)를 캐시에 저장해요.
     * (CaffeineConfig의 180초 만료 시간이 자동으로 적용돼요.)
     */
    public void saveCode(String email, String code) {
        Cache cache = getCache();
        cache.put(email, code); // key: email, value: code
    }

    /**
     * [조회] 이메일(Key)로 캐시에서 인증 코드(Value)를 불러와요.
     *
     * @return 캐시에 코드가 있으면 코드를 반환하고,
     * 코드가 없거나 만료되었다면 null을 반환해요.
     */
    public String getCode(String email) {
        Cache cache = getCache();
        // .get(key, 반환타입)
        return cache.get(email, String.class);
    }

    public boolean verifyCode(String email, String code) {
        // 1. 캐시에 저장된 원본 코드를 가져와요. (만료 시 null)
        String savedCode = this.getCode(email);

        // 2. 원본 코드가 (만료 등의 이유로) 존재하지 않으면, 즉시 false 반환
        if (savedCode == null) {
            return false;
        }

        // 3. 원본 코드(null이 아님)와 사용자 입력 코드를 비교해요.
        // (userInputCode가 null이라도 NullPointerException이 발생하지 않아요.)
        return savedCode.equals(code);
    }
    /**
     * [삭제] 사용이 끝난 인증 코드를 캐시에서 즉시 삭제해요.
     * (인증 성공 시 호출하면 돼요.)
     */
    public void deleteCode(String email) {
        Cache cache = getCache();
        cache.evict(email);
    }
}

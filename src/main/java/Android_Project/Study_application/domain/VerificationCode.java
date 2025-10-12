package Android_Project.Study_application.domain;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VerificationCode {
    private Long id;
    private Long userId; // email 대신 userId를 사용
    private String code;
    private LocalDateTime expiryTime;

    public VerificationCode(Long userId, String code) {
        this.userId = userId;
        this.code = code;
        this.expiryTime = LocalDateTime.now().plusMinutes(5); // 유효시간 5분
    }
}

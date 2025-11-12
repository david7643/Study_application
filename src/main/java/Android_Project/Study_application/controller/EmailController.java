package Android_Project.Study_application.controller;

import Android_Project.Study_application.service.EmailAuthService;
import Android_Project.Study_application.service.EmailService;
import Android_Project.Study_application.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailAuthService emailAuthService;

    @PostMapping("/api/email/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam("email") String email) {

        try {
            // 1. (EmailService) 인증번호 생성
            String code = emailService.createVerificationCode();

            // 2. (EmailService) 이메일 발송
            emailService.sendEmail(email, code);

            // 3. (RedisService) Redis에 인증번호 저장 (Key: 이메일, Value: 코드, 유효시간: 3분)
            emailAuthService.saveCode(email, code);

            return ResponseEntity.ok("인증번호가 발송되었습니다. 3분 이내에 입력해주세요.");

        } catch (Exception e) {
            // (sendEmail에서 예외가 발생했을 경우)
            log.error("인증 코드 저장 실패. Email: {}", email, e);
            return ResponseEntity.status(500).body("이메일 발송 중 오류가 발생했습니다.");
        }
    }
    @PostMapping("/api/email/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam("code") String code, @RequestParam("email") String email) {
        try {
            if(emailAuthService.verifyCode(email, code)) {
                emailAuthService.deleteCode(email);
                return ResponseEntity.ok("인증완료.");
            }
            else
                return ResponseEntity.badRequest().body("인증코드가 맞지 않습니다.");
        } catch (Exception e) {
            // (sendEmail에서 예외가 발생했을 경우)
            log.error("인증 실패. Email: {}", email, e);
            return ResponseEntity.status(500).body("인증 중 오류가 발생했습니다.");
        }
    }
}

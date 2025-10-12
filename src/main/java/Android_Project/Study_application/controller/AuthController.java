package Android_Project.Study_application.controller;

import Android_Project.Study_application.dto.AuthDtos;
import Android_Project.Study_application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 인증번호 발송 요청 (재발송 포함)
    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody AuthDtos.EmailRequest request) {
        try {
            authService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody AuthDtos.VerificationRequest request) {
        try {
            boolean isVerified = authService.verifyEmail(request.getEmail(), request.getCode());
            if (isVerified) {
                return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("인증번호가 올바르지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

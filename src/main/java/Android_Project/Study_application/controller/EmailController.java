package Android_Project.Study_application.controller;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.dto.FindIdRequestDto;
import Android_Project.Study_application.dto.SendCodeRequest;
import Android_Project.Study_application.dto.VerifyCodeRequest;
import Android_Project.Study_application.repository.MemberRepository;
import Android_Project.Study_application.service.EmailAuthService;
import Android_Project.Study_application.service.EmailService;
import Android_Project.Study_application.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailAuthService emailAuthService;
    private final MemberService memberService;
    @PostMapping("/api/email/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody SendCodeRequest request) {
        String email = request.getEmail();
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
    public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request) {
        String email = request.getEmail(); // DTO에서 이메일 추출
        String code = request.getVerificationCode(); // DTO에서 인증번호 추출 (필드명 주의!)
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

    @PostMapping(value = "/find-id")
    public Optional<Member> findId(@RequestBody FindIdRequestDto request) {
        memberService.findOne(request.getUserid());
        if(memberService.findOne(request.getUserid()).isPresent())
            return Optional.of(memberService.findOne(request.getUserid()).get());
        return Optional.empty();
    }
}

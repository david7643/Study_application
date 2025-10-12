package Android_Project.Study_application.service;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.domain.VerificationCode;
import Android_Project.Study_application.repository.MemberRepository;
import Android_Project.Study_application.repository.VerificationCodeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final VerificationCodeDao verificationCodeDao;
    private final EmailService emailService;

    @Transactional
    public void sendVerificationCode(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 기존에 있던 인증 코드를 삭제 (재요청 시 항상 최신 코드를 유지)
        verificationCodeDao.deleteByUserId(member.getId());

        // 새로운 인증 코드 생성
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationCode verificationCode = new VerificationCode(member.getId(), code);

        verificationCodeDao.save(verificationCode);
        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public boolean verifyEmail(String email, String code) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        VerificationCode verificationCode = verificationCodeDao.findByUserId(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 발급되지 않았습니다."));

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodeDao.deleteByUserId(member.getId());
            throw new IllegalArgumentException("인증 시간이 만료되었습니다.");
        }

        if (!verificationCode.getCode().equals(code)) {
            return false;
        }

        // 인증 성공 시, 회원 정보의 인증 상태 업데이트
        memberRepository.updateEmailVerified(email);
        // 사용된 인증 코드 삭제
        verificationCodeDao.deleteByUserId(member.getId());

        return true;
    }
}

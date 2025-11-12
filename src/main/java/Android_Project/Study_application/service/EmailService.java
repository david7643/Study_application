package Android_Project.Study_application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage; // 텍스트 기반의 간단한 메일 발송 시 사용
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
@Slf4j
@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 생성
public class EmailService {

    // (주의) @Value가 아닌 JavaMailSender 객체를 직접 주입받아야 해요.
    // 이 객체는 'spring-boot-starter-mail'이 application.properties를 읽어 자동으로 생성해 줘요.
    private final JavaMailSender javaMailSender;

    /**
     * 6자리 숫자 인증번호를 생성합니다.
     * @return 6자리 숫자 문자열
     */
    public String createVerificationCode() {
        Random random = new Random();
        // 100000 (6자리 시작) ~ 999999 (6자리 끝)
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 지정된 이메일 주소로 인증번호를 발송합니다.
     * @param toEmail 수신자 이메일
     * @param code    발송할 인증번호
     */
    public void sendEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail); // 수신자
            message.setSubject("퀴잇 회원가입 이메일 인증번호"); // 메일 제목

            // 메일 본문
            String text = "회원가입을 위한 인증번호입니다.\n";
            text += "인증번호: " + code + "\n";
            text += "3분 이내에 입력해주세요.";
            message.setText(text);

            // 메일 발송
            javaMailSender.send(message);

        } catch (Exception e) {
            // 메일 발송 실패 시 예외 처리
            // (실제 운영 시에는 로깅을 하거나, 사용자에게 알림을 주는 등의 처리가 필요)
            log.error("메일 발송 실패. To: {}", toEmail, e);
            // 혹은 커스텀 예외를 던질 수 있습니다.
            // throw new RuntimeException("메일 발송에 실패했습니다.");
        }
    }
}
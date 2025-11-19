package Android_Project.Study_application.controller;

import Android_Project.Study_application.service.EmailService;
import Android_Project.Study_application.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
// Mockito의 given, when, then, any 등을 사용하기 위해 static import
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
@WebMvcTest(EmailController.class) // 테스트할 컨트롤러를 지정
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 시뮬레이션

    @MockBean // 스프링 컨테이너에 실제 EmailService 대신 가짜 객체를 등록
    private EmailService emailService;

    @MockBean // 실제 RedisService 대신 가짜 객체를 등록
    private RedisService redisService;

    @Test
    @DisplayName("이메일 인증 코드 발송 성공 - try 블록 테스트")
    void sendVerificationCode_Success() throws Exception {
        // given (준비)
        String testEmail = "test@example.com";
        String testCode = "123456"; // 가상의 인증 코드

        // 1. EmailService가 createVerificationCode()를 호출하면 "123456"을 반환하도록 설정
        given(emailService.createVerificationCode()).willReturn(testCode);

        // 2. EmailService의 sendEmail()은 어차피 가짜이므로, 아무것도 하지 않도록 설정 (void 메서드)
        willDoNothing().given(emailService).sendEmail(anyString(), anyString());

        // 3. RedisService의 setData()도 아무것도 하지 않도록 설정 (void 메서드)
        willDoNothing().given(redisService).setData(anyString(), anyString(), anyLong());

        // when (실행)
        // MockMvc를 통해 /api/email/send-code 엔드포인트로 POST 요청을 보냄
        // "email" 파라미터로 testEmail 값을 전달
        mockMvc.perform(post("/api/email/send-code")
                        .param("email", testEmail).with(csrf()).with(user("testUser")))
                // then (검증)
                // 1. 응답 상태가 200 (OK)인지 확인
                .andExpect(status().isOk())
                // 2. 응답 본문의 문자열이 예상과 일치하는지 확인
                .andExpect(content().string("인증번호가 발송되었습니다. 3분 이내에 입력해주세요."));

        // 3. (중요) try 블록 안의 서비스 메서드들이 정확히 1번씩 호출되었는지 검증
        then(emailService).should(times(1)).createVerificationCode();
        then(emailService).should(times(1)).sendEmail(testEmail, testCode);
        then(redisService).should(times(1)).setData("email:" + testEmail, testCode, 3L);
    }
}

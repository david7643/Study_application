package Android_Project.Study_application.controller;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.dto.LoginForm;
import Android_Project.Study_application.dto.RegisterForm;
import Android_Project.Study_application.service.EmailService;
import Android_Project.Study_application.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService; // EmailService 주입
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/register")
    public String createForm() {
        return "/members/register";
    }

    @PostMapping(value = "/login-process")
    public String loginProcess(@ModelAttribute LoginForm loginform, HttpServletRequest request, RedirectAttributes rttr) {
        Optional<Member> loginMemberOptional = memberService.Login(loginform.getUserid(), loginform.getPw());

        // 2. 로그인 실패 처리
        if (loginMemberOptional.isEmpty()) {
            rttr.addAttribute("error", true);
            // 4. URL에는 파라미터 부분을 모두 제거
            return "redirect:/login fail";
        }
        Member loginMember = loginMemberOptional.get();
        // 세션을 가져와서 로그인 정보를 저장 (세션이 없으면 새로 생성)
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);
        return "redirect:/";
    }

    @PostMapping(value = "/register-process")
    public String create(@ModelAttribute RegisterForm registerform) {
        Member member = new Member();
        member.setUserid(registerform.getUserid());
        member.setPw(registerform.getPw());
        member.setName(registerform.getName());
        member.setEmail(registerform.getEmail());
        member.setPhone(registerform.getPhone());
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/login form")
    public String loginForm() {
        return "Login";
    }

    @GetMapping("/login fail")
    public String loginFail() {
        return "Login";
    }

    @PostMapping("/register/send-verification")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<String> sendVerificationCode(@RequestBody RegisterForm registerForm, HttpServletRequest request) {
        try {
// 아이디 중복 확인
            if (memberService.isUserIdExists(registerForm.getUserid())) {
                return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
            }
            // Member 객체 생성 (비밀번호는 암호화)
            Member tempMember = new Member();
            tempMember.setUserid(registerForm.getUserid());
            tempMember.setPw(passwordEncoder.encode(registerForm.getPw())); // 중요! 비밀번호 암호화
            tempMember.setName(registerForm.getName());
            tempMember.setEmail(registerForm.getEmail());
            tempMember.setPhone(registerForm.getPhone());
            tempMember.setEmailVerified(false); // 아직 인증 전

            // 인증번호 생성
            String verificationCode = String.format("%06d", new Random().nextInt(999999));

            // HttpSession에 임시 회원 정보와 인증번호 저장
            HttpSession session = request.getSession();
            session.setAttribute("tempMember", tempMember);
            session.setAttribute("verificationCode", verificationCode);
            session.setMaxInactiveInterval(180); // 세션 유효 시간 3분 설정

            // 이메일 발송
            emailService.sendVerificationCode(tempMember.getEmail(), verificationCode);

            return ResponseEntity.ok("인증번호가 발송되었습니다. 3분 안에 입력해주세요.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("오류가 발생했습니다: " + e.getMessage());
        }

        @PostMapping("/register/verify-and-join")
        @ResponseBody
        public ResponseEntity<String> verifyAndJoin(@RequestParam String code, HttpServletRequest request) {
            HttpSession session = request.getSession(false); // 기존 세션 가져오기

            if (session == null || session.getAttribute("tempMember") == null) {
                return ResponseEntity.badRequest().body("인증 시간이 만료되었거나 잘못된 접근입니다. 다시 시도해주세요.");
            }

            String storedCode = (String) session.getAttribute("verificationCode");
            if (!storedCode.equals(code)) {
                return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
            }

            // 인증 성공! 세션에서 임시 회원 정보를 가져와 DB에 최종 저장
            Member memberToRegister = (Member) session.getAttribute("tempMember");
            memberToRegister.setEmailVerified(true); // 이메일 인증 완료 상태로 변경

            memberService.join(memberToRegister); // DB에 저장

            // 사용 완료된 세션 정보 삭제
            session.removeAttribute("tempMember");
            session.removeAttribute("verificationCode");

            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        }

        // 로그인 관련 메소드들은 그대로 유지...
    }
}

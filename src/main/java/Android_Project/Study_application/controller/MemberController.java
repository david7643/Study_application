package Android_Project.Study_application.controller;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.dto.LoginForm;
import Android_Project.Study_application.dto.RegisterForm;
import Android_Project.Study_application.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
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
}

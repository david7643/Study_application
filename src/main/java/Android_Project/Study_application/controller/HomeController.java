package Android_Project.Study_application.controller;

import Android_Project.Study_application.domain.Member;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            return "Login";
        }
        // 세션에서 로그인한 회원 정보를 가져옵니다.
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 모델에 사용자 아이디를 담아서 뷰로 전달합니다.
        model.addAttribute("userid", loginMember.getUserid());
        return "/members/LoginSuccess";
    }
}

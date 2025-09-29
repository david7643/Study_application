package Android_Project.Study_application.controller;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    @GetMapping(value = "/register")
    public String createForm() {
        return "members/register";
    }
    @PostMapping(value = "/register-process")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setPw(form.getPw());
        memberService.join(member);
        return "redirect:/";
    }
}

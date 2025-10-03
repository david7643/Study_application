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
        // MemberForm 객체에서 받은 데이터를 Member 도메인 객체로 변환
        // JdbcTemplateMemberRepository의 Member 객체 필드명과 일치시킴
        member.setUserid(form.getUserid());
        member.setPw(form.getPw());
        member.setName(form.getName());
        member.setEmail(form.getEmail());
        member.setPhone(form.getPhone());
        // 모든 정보가 담긴 member 객체를 서비스 계층으로 전달하여 회원가입 처리
        return "redirect:/";
    }
}

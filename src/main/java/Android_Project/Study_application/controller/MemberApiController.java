package Android_Project.Study_application.controller;

import Android_Project.Study_application.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
public class MemberApiController {
    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/members/check-id")
    public ResponseEntity<Map<String, Boolean>> checkLoginIdDuplicate(@RequestParam("loginId") String loginId) {
        boolean isAvailable = memberService.findOne(loginId).isEmpty();

        Map<String, Boolean> response = Collections.singletonMap("isAvailable", isAvailable);

        return ResponseEntity.ok(response);
    }
}

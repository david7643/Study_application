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

    // 생성자를 통해 MemberService를 주입받습니다.
    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    // HTTP GET 요청을 처리하며, "/api/members/check-id" 경로와 매핑됩니다.
    @GetMapping("/api/members/check-id")
    // @RequestParam("loginId"): URL의 쿼리 파라미터(?loginId=...) 값을 loginId 변수에 담아줍니다.
    public ResponseEntity<Map<String, Boolean>> checkLoginIdDuplicate(@RequestParam("loginId") String loginId) {
        // MemberService를 통해 해당 아이디를 가진 회원이 존재하는지 확인합니다.
        boolean isAvailable = memberService.findOne(loginId).isEmpty();

        // 결과를 담을 Map 객체를 생성합니다.
        // 자바스크립트에서 data.isAvailable로 접근할 수 있도록 key 이름을 "isAvailable"로 지정합니다.
        Map<String, Boolean> response = Collections.singletonMap("isAvailable", isAvailable);

        // ResponseEntity.ok()는 HTTP 상태 코드 200 (성공)과 함께 응답 본문(response Map)을 반환합니다.
        // 이 Map 객체는 Spring에 의해 자동으로 JSON 형식으로 변환되어 클라이언트에게 전달됩니다.
        // 예: {"isAvailable": true}
        return ResponseEntity.ok(response);
    }
}

package Android_Project.Study_application.service;
import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long join(Member member) {
        String encodedPassword = passwordEncoder.encode(member.getPw());
        member.setPw(encodedPassword);
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByUserId(member.getUserid())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(String userId) {
        return memberRepository.findByUserId(userId);

    }

    public Optional<Member> Login(String userId, String password) {
        // 1. 아이디로 회원을 조회합니다.
        Optional<Member> memberOptional = memberRepository.findByUserId(userId);
        // 2. 조회된 회원이 있고, 비밀번호가 일치하는지 확인합니다.
        return memberOptional.filter(member ->
                // passwordEncoder.matches(평문 비밀번호, 암호화된 비밀번호)
                passwordEncoder.matches(password, member.getPw())
        );
    }
}

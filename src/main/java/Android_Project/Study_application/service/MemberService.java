package Android_Project.Study_application.service;

import Android_Project.Study_application.domain.Member;
import Android_Project.Study_application.repository.MemberRepository;
import Android_Project.Study_application.repository.MemmoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }
}

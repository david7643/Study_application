package Android_Project.Study_application.repository;

import Android_Project.Study_application.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByPw(String pw);
    List<Member> findAll();
}

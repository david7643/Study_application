package Android_Project.Study_application.repository;

import Android_Project.Study_application.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findByUserId(String userid);
    Optional<Member> findByUserName(String name);
    List<Member> findAll();
    Optional<Member> findByEmail(String email);
    void updateEmailVerified(String email);
}

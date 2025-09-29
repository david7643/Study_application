package Android_Project.Study_application.repository;

import Android_Project.Study_application.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class MemmoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    private static Long sequence=0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByPw(String pw) {
        return store.values().stream()
                .filter(member -> member.getPw().equals(pw)).findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}

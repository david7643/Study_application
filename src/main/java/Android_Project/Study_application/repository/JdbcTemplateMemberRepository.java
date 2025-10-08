package Android_Project.Study_application.repository;

import Android_Project.Study_application.domain.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public class JdbcTemplateMemberRepository implements MemberRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("users").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        // 모든 필드를 파라미터로 추가
        parameters.put("user_id", member.getUserid());
        parameters.put("password_hash", member.getPw());
        parameters.put("username", member.getName());
        parameters.put("email", member.getEmail());
        parameters.put("phone_number", member.getPhone());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;
    }

    @Override
    public Optional<Member> findByUserId(String user_id) {
        try {
            Member member = jdbcTemplate.queryForObject("select * from users where user_id = ?", memberRowMapper(), user_id);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member>  findByUserName(String username){
        try {
            Member member = jdbcTemplate.queryForObject("select * from users where username = ?", memberRowMapper(), username);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from users", memberRowMapper());
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("id"));
            member.setUserid(rs.getString("user_id"));
            member.setPw(rs.getString("password_hash")); // DB에서 password를 조회
            member.setName(rs.getString("username"));
            member.setEmail(rs.getString("email"));
            member.setPhone(rs.getString("phone_number"));
            return member;
        };
    }
}

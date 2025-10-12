package Android_Project.Study_application.repository;
import Android_Project.Study_application.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationCodeDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<VerificationCode> codeRowMapper = (rs, rowNum) -> new VerificationCode(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("code"),
            rs.getTimestamp("expiry_time").toLocalDateTime()
    );

    public void save(VerificationCode verificationCode) {
        String sql = "INSERT INTO verification_codes (user_id, code, expiry_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, verificationCode.getUserId(), verificationCode.getCode(), verificationCode.getExpiryTime());
    }

    public Optional<VerificationCode> findByUserId(Long userId) {
        String sql = "SELECT * FROM verification_codes WHERE user_id = ?";
        List<VerificationCode> codes = jdbcTemplate.query(sql, codeRowMapper, userId);
        return codes.stream().findFirst();
    }

    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM verification_codes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}

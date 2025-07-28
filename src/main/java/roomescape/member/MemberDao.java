package roomescape.member;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> new Member(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("role")
    );

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member save(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO member(name, email, password, role) VALUES (?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPassword());
            ps.setString(4, member.getRole());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return findById(id)
                .orElseThrow(() -> new IllegalStateException("[ERROR] 저장 후 사용자를 찾을 수 없습니다."));
    }

    public Optional<Member> findByEmailAndPassword(String email, String password) {
        List<Member> result = jdbcTemplate.query(
                "SELECT id, name, email, password, role FROM member WHERE email = ? AND password = ?",
                memberRowMapper,
                email, password
        );
        return result.stream().findAny();
    }

    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query(
                "SELECT id, name, email, password, role FROM member WHERE name = ?",
                memberRowMapper,
                name
        );
        return result.stream().findAny();
    }

    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query(
                "SELECT id, name, email, password, role FROM member WHERE id = ?",
                memberRowMapper,
                id
        );
        return result.stream().findAny();
    }
}

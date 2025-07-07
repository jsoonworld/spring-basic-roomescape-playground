package roomescape.member;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

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
        return findById(id);
    }

    public Member findByEmailAndPassword(String email, String password) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, password, role FROM member WHERE email = ? AND password = ?",
                    memberRowMapper,
                    email, password
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("[ERROR] 이메일 또는 비밀번호가 일치하는 사용자가 없습니다.");
        }
    }

    public Member findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, password, role FROM member WHERE name = ?",
                    memberRowMapper,
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("[ERROR] 해당 이름의 사용자를 찾을 수 없습니다.");
        }
    }

    public Member findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, password, role FROM member WHERE id = ?",
                    memberRowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("[ERROR] 해당 ID의 사용자를 찾을 수 없습니다.");
        }
    }
}

package roomescape.login;

import org.springframework.stereotype.Service;
import roomescape.member.Member;
import roomescape.member.MemberDao;

@Service
public class LoginService {
    private final MemberDao memberDao;
    private final JwtUtil jwtUtil;

    public LoginService(MemberDao memberDao, JwtUtil jwtUtil) {
        this.memberDao = memberDao;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {
        Member member = memberDao.findByEmailAndPassword(email, password);

        return jwtUtil.createToken(member);
    }
}

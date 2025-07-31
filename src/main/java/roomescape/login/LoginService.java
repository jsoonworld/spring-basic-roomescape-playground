package roomescape.login;

import org.springframework.stereotype.Service;
import roomescape.member.Member;
import roomescape.member.MemberRepository;

@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public LoginService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }


    public String login(String email, String password) {
        Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 이메일 또는 비밀번호가 일치하는 사용자가 없습니다."));
        return jwtUtil.createToken(member);
    }

    public LoginMember findLoginMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 해당 ID의 사용자를 찾을 수 없습니다."));
        return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }
}

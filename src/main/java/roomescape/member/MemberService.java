package roomescape.member;

import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member newMember = new Member(
                memberRequest.getName(),
                memberRequest.getEmail(),
                memberRequest.getPassword(),
                "USER"
        );
        Member member = memberRepository.save(newMember);
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }
}

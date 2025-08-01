package roomescape.waiting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.login.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.reservation.ReservationRepository;
import roomescape.reservation.ReservationRequest;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public WaitingService(WaitingRepository waitingRepository, MemberRepository memberRepository, ThemeRepository themeRepository, TimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public WaitingResponse create(ReservationRequest request, LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 사용자입니다."));
        Time time = timeRepository.findById(request.getTime())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 시간입니다."));
        Theme theme = themeRepository.findById(request.getTheme())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 테마입니다."));

        boolean isReserved = reservationRepository.existsByMemberAndDateAndTimeAndTheme(member, request.getDate(), time, theme);
        boolean isWaiting = waitingRepository.existsByMemberAndDateAndTimeAndTheme(member, request.getDate(), time, theme);
        if (isReserved || isWaiting) {
            throw new IllegalArgumentException("[ERROR] 이미 예약 또는 예약 대기 상태입니다.");
        }

        Waiting waiting = new Waiting(member, theme, time, request.getDate());
        Waiting savedWaiting = waitingRepository.save(waiting);
        return new WaitingResponse(savedWaiting.getId());
    }

    @Transactional
    public void delete(Long waitingId, LoginMember loginMember) {
        Waiting waiting = waitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 예약 대기입니다."));

        if (!waiting.getMember().getId().equals(loginMember.id())) {
            throw new SecurityException("[ERROR] 자신의 예약 대기만 취소할 수 있습니다.");
        }
        waitingRepository.delete(waiting);
    }
}

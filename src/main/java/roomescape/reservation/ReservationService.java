package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.login.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TimeRepository timeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public ReservationResponse create(ReservationRequest request, LoginMember loginMember) {
        String reservationName = Optional.ofNullable(request.getName())
                .filter(name -> !name.isBlank())
                .orElse(loginMember.name());

        Time time = timeRepository.findById(request.getTime())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 시간입니다."));
        Theme theme = themeRepository.findById(request.getTheme())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 테마입니다."));

        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 사용자입니다."));

        Reservation reservation = new Reservation(member, reservationName, request.getDate(), time, theme);

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(
                savedReservation.getId(),
                savedReservation.getName(),
                savedReservation.getTheme().getName(),
                savedReservation.getDate(),
                savedReservation.getTime().getValue()
        );
    }

    public List<MyReservationResponse> findReservationsByMemberId(Long memberId) {
        return reservationRepository.findByMemberIdWithDetails(memberId).stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(
                        it.getId(),
                        it.getName(),
                        it.getTheme().getName(),
                        it.getDate(),
                        it.getTime().getValue()
                ))
                .toList();
    }
}

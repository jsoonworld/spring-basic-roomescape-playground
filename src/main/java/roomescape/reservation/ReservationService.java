package roomescape.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.login.LoginMember;
import roomescape.member.Member;
import roomescape.member.MemberRepository;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private static final int MAX_CONFIRMED_RESERVATIONS = 1;

    private final ReservationRepository reservationRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository, TimeRepository timeRepository,
                              ThemeRepository themeRepository, MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ReservationResponse create(ReservationRequest request, LoginMember loginMember) {
        Time time = findTimeById(request.getTime());
        Theme theme = findThemeById(request.getTheme());

        if (reservationRepository.existsByMemberIdAndDateAndTimeAndTheme(loginMember.id(), request.getDate(), time, theme)) {
            throw new IllegalArgumentException("[ERROR] 이미 예약 또는 예약 대기 상태입니다.");
        }

        long confirmedCount = reservationRepository.countByDateAndTimeAndThemeAndStatus(request.getDate(), time, theme, ReservationStatus.CONFIRMED);
        ReservationStatus status = (confirmedCount < MAX_CONFIRMED_RESERVATIONS) ? ReservationStatus.CONFIRMED : ReservationStatus.WAITING;

        Member member = findMemberById(loginMember.id());
        Reservation reservation = new Reservation(member, request.getDate(), time, theme, status);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public void deleteById(Long reservationId, LoginMember loginMember) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 예약입니다."));

        if (!reservation.isOwnedBy(loginMember.id())) {
            throw new SecurityException("[ERROR] 자신의 예약만 취소할 수 있습니다.");
        }

        reservationRepository.delete(reservation);

        if (reservation.isConfirmed()) {
            promoteNextWaitingReservation(reservation);
        }
    }

    public List<MyReservationResponse> findMyReservations(LoginMember loginMember) {
        List<Reservation> myReservations = reservationRepository.findByMemberId(loginMember.id());

        return myReservations.stream()
                .map(reservation -> {
                    Long rank = null;
                    if (reservation.getStatus() == ReservationStatus.WAITING) {
                        rank = calculateWaitingRank(reservation);
                    }
                    return MyReservationResponse.from(reservation, rank);
                })
                .toList();
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private void promoteNextWaitingReservation(Reservation deletedReservation) {
        reservationRepository.findFirstByDateAndTimeAndThemeAndStatusOrderByIdAsc(
                deletedReservation.getDate(),
                deletedReservation.getTime(),
                deletedReservation.getTheme(),
                ReservationStatus.WAITING
        ).ifPresent(Reservation::promoteToConfirmed);
    }

    private Long calculateWaitingRank(Reservation reservation) {
        List<Reservation> waitingsForSlot = reservationRepository.findByDateAndTimeAndThemeAndStatusOrderByIdAsc(
                reservation.getDate(), reservation.getTime(), reservation.getTheme(), ReservationStatus.WAITING);

        return (long) (waitingsForSlot.indexOf(reservation) + 1);
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 사용자입니다."));
    }

    private Time findTimeById(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 시간입니다."));
    }

    private Theme findThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 테마입니다."));
    }
}

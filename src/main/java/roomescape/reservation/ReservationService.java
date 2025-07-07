package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.login.LoginMember;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationDao reservationDao;

    public ReservationService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    public ReservationResponse create(ReservationRequest originalRequest, LoginMember loginMember) {
        String reservationName = Optional.ofNullable(originalRequest.getName())
                .filter(name -> !name.isBlank())
                .orElse(loginMember.name());

        ReservationRequest requestForDao = new ReservationRequest(
                reservationName,
                originalRequest.getDate(),
                originalRequest.getTheme(),
                originalRequest.getTime()
        );

        Reservation savedReservation = reservationDao.save(requestForDao);

        return new ReservationResponse(
                savedReservation.getId(),
                savedReservation.getName(),
                savedReservation.getTheme().getName(),
                savedReservation.getDate(),
                savedReservation.getTime().getValue()
        );
    }

    public void deleteById(Long id) {
        reservationDao.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationDao.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }
}

package roomescape.time;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.Reservation;
import roomescape.reservation.ReservationRepository;
import roomescape.reservation.ReservationStatus;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TimeService {

    private final TimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public TimeService(TimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<AvailableTime> getAvailableTime(String date, Long themeId) {
        List<Reservation> confirmedReservations = reservationRepository.findByDateAndThemeIdAndStatus(
                date,
                themeId,
                ReservationStatus.CONFIRMED
        );
        List<Time> allTimes = timeRepository.findAll();

        return allTimes.stream()
                .map(time -> {

                    boolean isBooked = confirmedReservations.stream()
                            .anyMatch(reservation -> reservation.getTime().getId().equals(time.getId()));
                    return new AvailableTime(time.getId(), time.getValue(), isBooked);
                })
                .toList();
    }

    public List<Time> findAll() {
        return timeRepository.findAll();
    }

    @Transactional
    public Time save(Time time) {
        return timeRepository.save(time);
    }

    @Transactional
    public void deleteById(Long id) {
        timeRepository.deleteById(id);
    }
}

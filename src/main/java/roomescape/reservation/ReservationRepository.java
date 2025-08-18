package roomescape.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.theme.Theme;
import roomescape.time.Time;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findFirstByDateAndTimeAndThemeAndStatusOrderByIdAsc(String date, Time time, Theme theme, ReservationStatus status);

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByDateAndTimeAndThemeAndStatusOrderByIdAsc(String date, Time time, Theme theme, ReservationStatus status);

    List<Reservation> findByDateAndThemeIdAndStatus(String date, Long themeId, ReservationStatus status);

    boolean existsByMemberIdAndDateAndTimeIdAndThemeId(Long memberId, String date, Long timeId, Long themeId);

    long countByDateAndTimeIdAndThemeIdAndStatus(String date, Long timeId, Long themeId, ReservationStatus status);
}

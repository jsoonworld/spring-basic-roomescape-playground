package roomescape.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateAndThemeId(String date, Long themeId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.theme JOIN FETCH r.time WHERE r.member.id = :memberId")
    List<Reservation> findByMemberIdWithDetails(@Param("memberId") Long memberId);
}

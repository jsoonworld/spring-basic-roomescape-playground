package roomescape.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import roomescape.member.Member;
import roomescape.theme.Theme;
import roomescape.time.Time;
import java.util.Objects;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private Time time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(Member member, String date, Time time, Theme theme, ReservationStatus status) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    public void promoteToConfirmed() {
        if (this.status != ReservationStatus.WAITING) {
            throw new IllegalStateException("[ERROR] 대기 상태의 예약만 확정 상태로 변경할 수 있습니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isOwnedBy(Long memberId) {
        return Objects.equals(this.member.getId(), memberId);
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public String getDate() { return date; }
    public Time getTime() { return time; }
    public Theme getTheme() { return theme; }
    public ReservationStatus getStatus() { return status; }
}

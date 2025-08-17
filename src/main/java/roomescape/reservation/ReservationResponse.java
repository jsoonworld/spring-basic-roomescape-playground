package roomescape.reservation;

public class ReservationResponse {
    private Long id;
    private String name;
    private String theme;
    private String date;
    private String time;

    public ReservationResponse(Long id, String name, String theme, String date, String time) {
        this.id = id;
        this.name = name;
        this.theme = theme;
        this.date = date;
        this.time = time;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getValue()
        );
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getTheme() { return theme; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}

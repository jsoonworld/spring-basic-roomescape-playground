package roomescape.reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        String date,
        String time,
        String status
) {
    public static MyReservationResponse from(Reservation reservation, Long rank) {
        String statusText = "예약";
        if (reservation.getStatus() == ReservationStatus.WAITING) {
            statusText = rank + "번째 예약대기";
        }

        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getValue(),
                statusText
        );
    }
}

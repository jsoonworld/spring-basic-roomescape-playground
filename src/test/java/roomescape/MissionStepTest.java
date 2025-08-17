package roomescape;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.reservation.MyReservationResponse;
import roomescape.reservation.ReservationResponse;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

    @Autowired
    private TimeRepository timeRepository;

    @Test
    void 일단계() {
        Map<String, String> params = new HashMap<>();
        params.put("email", "admin@email.com");
        params.put("password", "password");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        String token = response.cookie("token");
        assertThat(token).isNotBlank();

        ExtractableResponse<Response> checkResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract();

        assertThat(checkResponse.body().jsonPath().getString("name")).isEqualTo("어드민");
    }

    @Test
    void 이단계() {
        String token = createToken("admin@email.com", "password");

        Map<String, String> params = new HashMap<>();
        params.put("date", "2024-03-01");
        params.put("time", "1");
        params.put("theme", "1");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.as(ReservationResponse.class).getName()).isEqualTo("어드민");
    }

    @Test
    void 삼단계() {
        String brownToken = createToken("brown@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", brownToken)
                .get("/admin")
                .then().log().all()
                .statusCode(401);

        String adminToken = createToken("admin@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 사단계() {
        Time savedTime = timeRepository.save(new Time("10:00"));

        Time persistTime = timeRepository.findById(savedTime.getId()).orElse(null);

        assertThat(persistTime).isNotNull();
        assertThat(persistTime.getValue()).isEqualTo(savedTime.getValue());
    }

    @Test
    void 오단계() {
        String adminToken = createToken("admin@email.com", "password");

        List<MyReservationResponse> reservations = RestAssured.given().log().all()
                .cookie("token", adminToken)
                .get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", MyReservationResponse.class);

        assertThat(reservations).hasSize(3);
    }

    @Test
    void 예약이_꽉_찼을_때_대기가_생성되는지_검증한다() {
        // given: 먼저 다른 사용자(woni)가 특정 시간(3월 1일 15:40)에 예약을 확정한다.
        String woniToken = createToken("woni@email.com", "password");
        Map<String, String> reservationParams = new HashMap<>();
        reservationParams.put("date", "2024-03-01");
        reservationParams.put("time", "4"); // 15:40 에 해당하는 time id
        reservationParams.put("theme", "1");

        RestAssured.given().log().all()
                .body(reservationParams)
                .cookie("token", woniToken)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .statusCode(201);

        // when: 새로운 사용자(brown)가 동일한 시간에 예약을 시도한다.
        String brownToken = createToken("brown@email.com", "password");
        ReservationResponse reservationResponse = RestAssured.given().log().all()
                .body(reservationParams)
                .cookie("token", brownToken)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationResponse.class);

        // then: brown의 예약 목록을 조회했을 때, 방금 생성된 예약이 '1번째 예약대기' 상태여야 한다.
        List<MyReservationResponse> myReservations = RestAssured.given().log().all()
                .cookie("token", brownToken)
                .contentType(ContentType.JSON)
                .get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", MyReservationResponse.class);

        String status = myReservations.stream()
                .filter(it -> it.reservationId().equals(reservationResponse.getId()))
                .findFirst()
                .map(MyReservationResponse::status)
                .orElseThrow(() -> new AssertionError("[ERROR] 방금 생성한 예약을 찾을 수 없습니다."));

        assertThat(status).isEqualTo("1번째 예약대기");
    }

    private String createToken(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        return response.cookie("token");
    }
}

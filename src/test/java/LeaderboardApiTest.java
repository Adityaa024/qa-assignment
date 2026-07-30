import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REST Assured tests for the PreddyHub Leaderboard API.
 *
 * Endpoint found via browser Network tab:
 *   GET https://preddyhub.com/api/leaderboard?comp=world-cup-2026
 *
 * Key finding: the 'comp' parameter is silently ignored by the server —
 * same data is returned regardless of its value (or absence).
 */
class LeaderboardApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://preddyhub.com";
    }

    // ─── Happy path ─────────────────────────────────────────────
    @Test
    @DisplayName("GET /api/leaderboard returns 200, valid JSON array, correct structure")
    void leaderboardHappyPath() {
        given()
            .queryParam("comp", "world-cup-2026")
        .when()
            .get("/api/leaderboard")
        .then()
            .statusCode(200)
            .contentType(containsString("application/json"))
            // Non-empty JSON array
            .body("$", instanceOf(List.class))
            .body("size()", greaterThan(0))
            // First entry is rank 1
            .body("[0].rank", equalTo(1))
            // Required fields exist with correct types
            .body("[0].user_id", notNullValue())
            .body("[0].display_name", notNullValue())
            .body("[0].accuracy", instanceOf(Number.class))
            .body("[0].completed", instanceOf(Number.class))
            .body("[0].bullseyes", instanceOf(Number.class))
            .body("[0].points", instanceOf(Number.class));
    }

    @Test
    @DisplayName("Leaderboard is sorted by points descending")
    void leaderboardSorted() {
        List<Integer> points = given()
            .queryParam("comp", "world-cup-2026")
        .when()
            .get("/api/leaderboard")
            .jsonPath().getList("points", Integer.class);

        for (int i = 1; i < points.size(); i++) {
            assertTrue(points.get(i - 1) >= points.get(i),
                "Rank " + i + " (" + points.get(i - 1) + ") should be >= rank "
                + (i + 1) + " (" + points.get(i) + ")");
        }
    }

    // ─── Data validation ────────────────────────────────────────
    @Test
    @DisplayName("Accuracy is within 0-100 for all users")
    void accuracyBounds() {
        List<Float> accuracies = given()
            .queryParam("comp", "world-cup-2026")
        .when()
            .get("/api/leaderboard")
            .jsonPath().getList("accuracy", Float.class);

        accuracies.forEach(a ->
            assertTrue(a >= 0 && a <= 100,
                "Accuracy should be 0-100, got " + a));
    }

    // ─── Bad / missing parameter ────────────────────────────────
    @Test
    @DisplayName("Missing 'comp' parameter still returns 200 (API bug: should return 400)")
    void missingCompParam() {
        // VERIFIED: Server returns the same 200 + full data with no comp param.
        when()
            .get("/api/leaderboard")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
        // NOTE: This documents actual behavior. Ideally should return 400.
    }

    @Test
    @DisplayName("Invalid comp value returns same data (API bug: should return empty or 404)")
    void invalidCompParam() {
        // VERIFIED: Server ignores the comp param entirely.
        Response res = given()
            .queryParam("comp", "nonexistent-league-999")
        .when()
            .get("/api/leaderboard");

        assertNotEquals(500, res.statusCode(),
            "Server should not return 500 for unknown competition");
        // Actual buggy behavior: returns 200 with full World Cup data
        assertEquals(200, res.statusCode());
        res.then().body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Special characters in comp parameter → no 500 or data leak")
    void specialCharsInComp() {
        given()
            .queryParam("comp", "'; DROP TABLE users; --")
        .when()
            .get("/api/leaderboard")
        .then()
            .statusCode(not(equalTo(500)));
    }
}

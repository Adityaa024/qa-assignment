import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for PredictionScorer.
 * All expected values verified programmatically.
 */
class PredictionScorerTest {

    // ─── Exact score ────────────────────────────────────────────
    @Test @DisplayName("Exact score → 100")
    void exactScore() {
        assertEquals(100, PredictionScorer.score(2, 1, 2, 1, false));
    }

    @Test @DisplayName("Exact 0-0 draw → 100")
    void exactNilNilDraw() {
        assertEquals(100, PredictionScorer.score(0, 0, 0, 0, false));
    }

    // ─── Correct outcome + correct GD ───────────────────────────
    @Test @DisplayName("Right outcome + right GD, wrong exact → 85")
    void correctOutcomeAndGD() {
        // Pred 3-1, actual 2-0: outcome=H✓(40), GD=+2✓(30), totalΔ=2→10, teamΔ=2→5 = 85
        assertEquals(85, PredictionScorer.score(3, 1, 2, 0, false));
    }

    // ─── Correct outcome only ───────────────────────────────────
    @Test @DisplayName("Right outcome, wrong GD → 55")
    void correctOutcomeWrongGD() {
        // Pred 1-0, actual 3-0: outcome=H✓(40), GD✗(0), totalΔ=2→10, teamΔ=2→5 = 55
        assertEquals(55, PredictionScorer.score(1, 0, 3, 0, false));
    }

    // ─── Completely wrong ───────────────────────────────────────
    @Test @DisplayName("Wrong outcome, wildly wrong score → 0")
    void completelyWrong() {
        // Pred 0-0 (draw), actual 5-1 (home): all components = 0
        assertEquals(0, PredictionScorer.score(0, 0, 5, 1, false));
    }

    // ─── Knockout penalty specials ──────────────────────────────
    @Test @DisplayName("Penalty: predicted exact 90-min draw → 100")
    void penaltyExactDraw() {
        assertEquals(100, PredictionScorer.score(1, 1, 1, 1, true));
    }

    @Test @DisplayName("Penalty: predicted home win → wrong outcome → 23")
    void penaltyHomeWinPredicted() {
        // Pred 3-2, actual 2-2 (pens): outcome=H vs D→✗, GD +1≠0→✗,
        // totalΔ=1→15, teamΔ=1→(int)(2.5)=2→8 = 23
        assertEquals(23, PredictionScorer.score(3, 2, 2, 2, true));
    }

    @Test @DisplayName("Penalty: correct draw, wrong goals → 70")
    void penaltyCorrectDrawWrongGoals() {
        // Pred 2-2, actual 0-0 (pens): outcome=D✓(40), GD=0✓(30),
        // totalΔ=4→0, teamΔ=4→0 = 70
        assertEquals(70, PredictionScorer.score(2, 2, 0, 0, true));
    }

    // ─── Edge cases ─────────────────────────────────────────────
    @Test @DisplayName("Score never exceeds 100")
    void maxCap() {
        assertTrue(PredictionScorer.score(1, 0, 1, 0, false) <= 100);
    }

    @Test @DisplayName("Score is never negative")
    void minFloor() {
        assertTrue(PredictionScorer.score(0, 0, 9, 9, false) >= 0);
    }

    @Test @DisplayName("Reversed score (1-0 vs 0-1) → 25")
    void reversedScore() {
        // outcome✗, GD✗, totalΔ=0→20, teamΔ=2→5 = 25
        assertEquals(25, PredictionScorer.score(1, 0, 0, 1, false));
    }
}

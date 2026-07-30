/**
 * Scores a football prediction 0–100 by how close it is to the real result.
 *
 * Rubric:
 *   100  — exact score match
 *    40  — correct outcome (W/D/L)
 *    30  — correct goal difference
 *  0–20  — close total goals (20 − 5 × |Δtotal|, min 0)
 *  0–10  — close per-team goals (10 − 2.5 × (|ΔH| + |ΔA|), min 0)
 *     0  — completely wrong
 *
 * Knockout penalty rule: 90-min score is a draw → outcome = DRAW for scoring.
 */
public class PredictionScorer {

    /**
     * @param predHome   predicted home goals
     * @param predAway   predicted away goals
     * @param actualHome actual home goals (90-min / full-time)
     * @param actualAway actual away goals (90-min / full-time)
     * @param penalties  true if the match went to a penalty shoot-out (knockout)
     * @return score 0–100
     */
    public static int score(int predHome, int predAway,
                            int actualHome, int actualAway,
                            boolean penalties) {
        // ── Exact score ──
        if (predHome == actualHome && predAway == actualAway) {
            return 100;
        }

        int points = 0;

        // ── Outcome (W / D / L) — 40 pts ──
        // If the match went to penalties, the 90-min score is a draw,
        // so the "correct outcome" is DRAW regardless of who won on pens.
        int predOutcome = Integer.compare(predHome, predAway);   // +1 H, 0 D, -1 A
        int actualOutcome;
        if (penalties) {
            actualOutcome = 0;  // 90-min draw
        } else {
            actualOutcome = Integer.compare(actualHome, actualAway);
        }

        if (predOutcome == actualOutcome) {
            points += 40;
        }

        // ── Goal difference — 30 pts ──
        int predGD   = predHome - predAway;
        int actualGD = actualHome - actualAway;
        if (predGD == actualGD) {
            points += 30;
        }

        // ── Close total goals — max 20 pts ──
        int totalDelta = Math.abs((predHome + predAway) - (actualHome + actualAway));
        points += Math.max(0, 20 - 5 * totalDelta);

        // ── Close per-team goals — max 10 pts ──
        int teamDelta = Math.abs(predHome - actualHome)
                      + Math.abs(predAway - actualAway);
        points += Math.max(0, 10 - (int)(2.5 * teamDelta));

        return Math.min(points, 100);   // safety cap
    }
}

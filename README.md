# 🧪 PreddyHub — QA / SDET Practical Assignment

> A hands-on QA assessment for [PreddyHub](https://preddyhub.com) — a live football score-prediction web app for the FIFA World Cup 2026, Premier League, and La Liga.

---

## 📋 What's Inside

| Task | Description | File |
|------|-------------|------|
| **1. Coding** | Java scoring method (0–100) with JUnit 5 tests | [`PredictionScorer.java`](src/main/java/PredictionScorer.java) / [`PredictionScorerTest.java`](src/test/java/PredictionScorerTest.java) |
| **2. UI Automation** | Selenium E2E test — predict a score and verify persistence | [`PredictScoreE2ETest.java`](src/test/java/PredictScoreE2ETest.java) |
| **3. API Testing** | REST Assured tests for the leaderboard endpoint | [`LeaderboardApiTest.java`](src/test/java/LeaderboardApiTest.java) |
| **4. Bug Report** | 9 real bugs found on the live app | [`bug-report.md`](docs/bug-report.md) / [`BugRegressionTest.java`](src/test/java/BugRegressionTest.java) |

---

## 🎯 Task 1 — Prediction Scoring

A method that scores how close a user's prediction is to the actual match result.

### Scoring Rubric

```
 100 pts  →  Exact score match (e.g., predicted 2-1, result was 2-1)
  40 pts  →  Correct outcome (win / draw / loss)
  30 pts  →  Correct goal difference
0–20 pts  →  Bonus for close total goals
0–10 pts  →  Bonus for close per-team goals
   0 pts  →  Completely wrong prediction
```

**Knockout penalty handling:** When a match goes to penalties, the 90-minute score is a draw — so the "correct outcome" is always DRAW, regardless of who won the shootout.

### Test Results

All expected values verified by tracing through the formula by hand:

```
✅ Exact score (2-1 → 2-1)           = 100
✅ Exact 0-0 draw                     = 100
✅ Right outcome + GD (3-1 → 2-0)    = 85
✅ Right outcome only (1-0 → 3-0)    = 55
✅ Completely wrong (0-0 → 5-1)      = 0
✅ Penalty exact draw (1-1, pens)    = 100
✅ Penalty wrong outcome (3-2 → 2-2) = 23
✅ Penalty correct draw (2-2 → 0-0)  = 70
✅ Reversed score (1-0 → 0-1)        = 25
```

---

## 🖥️ Task 2 — UI Automation

**Approach:** Open the predict-scores page → dismiss onboarding wizard → click `+` to set a prediction → reload page → verify prediction persisted.

- **Waits:** Explicit `WebDriverWait` with `ExpectedConditions` — no `Thread.sleep` for assertions
- **Flaky point:** The onboarding wizard only appears for first-time visitors (gated by `localStorage`), so it may or may not be present

---

## 🔌 Task 3 — API Testing

**Endpoint found via Network tab:**

```
GET /api/leaderboard?comp=world-cup-2026
```

**Key finding:** The `comp` query parameter is silently ignored by the server — the same data is returned whether `comp` is missing, valid, or set to a nonexistent value.

---

## 🐛 Task 4 — Bugs Found (9 Total)

| # | Bug | Severity | Area |
|---|-----|----------|------|
| 1 | FINAL OUTCOME label wrong on knockout matches | P2 | Live Match Centre |
| 2 | My Predictions counter doesn't match list | P3 | My Predictions |
| 3 | EPL My Predictions returns 404 | P2 | Routing |
| 4 | Leaderboard API ignores `comp` param | P3 | API |
| 5 | La Liga My Predictions returns 404 | P2 | Routing |
| 6 | La Liga Leaderboard silently redirects to homepage | P3 | Routing |
| 7 | Minus (−) button doesn't work on prediction cards | P2 | Predict Scores |
| 8 | Nonexistent match ID loads a real match | P3 | Live Match Centre |
| 9 | "Add to Home Screen" banner overlaps content on mobile | P3 | Mobile UX |

👉 Full details with reproduction steps: [`docs/bug-report.md`](docs/bug-report.md)  
👉 Automated regression tests: [`src/test/java/BugRegressionTest.java`](src/test/java/BugRegressionTest.java)

---

## 🛠️ Tech Stack

- Java 17+
- JUnit 5
- Selenium WebDriver 4
- REST Assured 5

---

## 📁 Project Structure

```
qa-assignment/
├── src/
│   ├── main/java/
│   │   └── PredictionScorer.java          # Scoring method
│   └── test/java/
│       ├── PredictionScorerTest.java      # Unit tests
│       ├── PredictScoreE2ETest.java       # Selenium E2E test
│       ├── LeaderboardApiTest.java        # REST Assured tests
│       └── BugRegressionTest.java         # Regression tests for 9 bugs
├── docs/
│   └── bug-report.md                      # Detailed bug report
├── .gitignore
└── README.md
```

# 🐛 Bug Report — PreddyHub

> All bugs tested on **Windows 11, Chrome 137** (desktop: 1280×720, mobile: 375×812).  
> Signed in as "Aditya Raj" via Google. Tested July 30–31, 2026.

---

## Bug 1: "FINAL OUTCOME" Label Wrong on Knockout Matches

**Severity:** P2

**Steps:**
1. Go to `https://preddyhub.com/world-cup-2026/live?match=537390`
2. Score shows **Spain 1 – 0 Argentina** with an **"AET"** badge
3. Scroll to the **"FINAL OUTCOME"** section

**Expected:** Label should say "decided in extra time" — no mention of penalties.

**Actual:** Label says **"draw at 90' → extra time & penalties"** — wrong, because Spain won 1-0 in extra time (no penalties). The bar below shows "100% ESP / 0% draw / 0% ARG" which contradicts the "draw" in the label. Same text appears on all knockout matches.

---

## Bug 2: "My Predictions" Counter Doesn't Match the List

**Severity:** P3

**Steps:**
1. Sign in → make predictions on EPL → lock them in
2. Go to `https://preddyhub.com/world-cup-2026/my-predictions`

**Expected:** Counter and list should be consistent.

**Actual:** Stats card shows **"2 PREDICTED"** but list says **"No predictions yet."** Counter pulls from all competitions, list only shows World Cup.

---

## Bug 3: EPL "My Predictions" Returns 404

**Severity:** P2

**Steps:** Sign in → go to `https://preddyhub.com/premier-league/my-predictions`

**Expected:** Should show EPL predictions page.

**Actual:** Returns **404 — "We couldn't find that page."** Users can lock in EPL predictions but can't review them.

---

## Bug 4: Leaderboard API Ignores the `comp` Parameter

**Severity:** P3

**Steps:** Call `GET /api/leaderboard?comp=nonexistent-league` or remove `comp` entirely.

**Expected:** Should return 400 or empty array for unknown competition.

**Actual:** Returns **200 OK with the full World Cup leaderboard** regardless of the `comp` value. Parameter is silently ignored.

---

## Bug 5: La Liga "My Predictions" Also Returns 404

**Severity:** P2

**Steps:** Sign in → go to `https://preddyhub.com/la-liga/my-predictions`

**Expected:** Should show La Liga predictions.

**Actual:** Same **404** as EPL. "My Predictions" route is missing for all non-World Cup competitions.

---

## Bug 6: La Liga Leaderboard Silently Redirects to Homepage

**Severity:** P3

**Steps:** Sign in → go to `https://preddyhub.com/la-liga/leaderboard`

**Expected:** Should show La Liga leaderboard.

**Actual:** **Silently redirects to homepage** — no error, no message, no feedback.

---

## Bug 7: Minus (−) Button Doesn't Work on Prediction Cards

**Severity:** P2

**Steps:** Go to any predict-scores page → click `+` to set score to 1 → click `−`

**Expected:** Score should decrease back to 0.

**Actual:** Minus button is **visible but not interactive**. Can't undo accidental clicks.

---

## Bug 8: Nonexistent Match ID Loads a Real Match

**Severity:** P3

**Steps:** Go to `https://preddyhub.com/world-cup-2026/live?match=99999999`

**Expected:** Should show 404 or "Match not found."

**Actual:** **Loads the Spain vs Argentina Final** silently. No error whatsoever.

---

## Bug 9: "Add to Home Screen" Banner Overlaps Content on Mobile

**Severity:** P3

**Steps:** Open any page in mobile view (375×812) — e.g. predict-scores or live match.

**Expected:** All content should be visible and scrollable.

**Actual:** Large **"Add PreddyHub to your Home Screen"** banner covers match cards. Combined with bottom nav bar and BETA footer, nearly half the screen is blocked.

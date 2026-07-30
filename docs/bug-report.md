# 🐛 Bug Report: Incorrect "FINAL OUTCOME" Label on Knockout Matches

## Summary

The Live Match Centre displays **"draw at 90' → extra time & penalties"** on all knockout-stage matches — even when the match was decided in extra time without a penalty shootout.

## Severity

**P2 — Medium** · Incorrect data displayed to users; misleads prediction analysis.

## Environment

| Field | Value |
|-------|-------|
| Browser | Chrome 137 |
| OS | Windows 11 |
| Viewport | 1280×720 (desktop), 390×844 (mobile) |
| Account | Signed in as "Aditya Raj" via Google |
| Date | July 30, 2026 |

## Steps to Reproduce

1. Open Chrome on desktop
2. Navigate to `https://preddyhub.com/world-cup-2026/live?match=537390`
3. This loads the **Spain vs Argentina** World Cup Final
4. Observe the score: **Spain 1 – 0 Argentina** with an **"AET"** (After Extra Time) badge
5. Scroll down to the **"FINAL OUTCOME"** section

## Expected Result

Since Spain won **1-0 in extra time** (no penalty shootout occurred), the FINAL OUTCOME label should reflect this — e.g., "decided in extra time" or "Spain win after extra time."

It should **not** mention penalties.

## Actual Result

The label says:

> **"draw at 90' → extra time & penalties"**

This is wrong — the match was decided in extra time, not on penalties. The 1-0 AET scoreline confirms no penalty shootout took place.

Additionally, the prediction probability bar below shows **100% ESP / 0% draw / 0% ARG**, which directly contradicts the "draw" mentioned in the label text.

## Scope

This affects **all knockout matches**, not just the Final:

| Match | Score | FINAL OUTCOME text | Correct? |
|-------|-------|--------------------|----------|
| Spain vs Argentina (Final) | 1 – 0 AET | "draw at 90' → extra time & penalties" | ❌ |
| France vs England (3rd Place) | 4 – 6 | "draw at 90' → extra time & penalties" | ⚠️ partially |
| England vs Argentina (Semi) | 1 – 2 | "draw at 90' → extra time & penalties" | ❌ |

## Root Cause (Hypothesis)

The "FINAL OUTCOME" label appears to be **hardcoded** for all knockout-stage matches rather than being dynamically generated based on how each match was actually decided. The logic likely checks `isKnockout` instead of checking whether penalties actually occurred.

## Regression Test

```java
@Test
void outcomeTextMatchesActualResult() {
    driver.get("https://preddyhub.com/world-cup-2026/live?match=537390");

    String score = wait.until(ExpectedConditions.presenceOfElementLocated(
        By.cssSelector(".match-score"))).getText();
    String[] parts = score.split("\\s*[–-]\\s*");
    int home = Integer.parseInt(parts[0].trim());
    int away = Integer.parseInt(parts[1].trim());

    String outcome = driver.findElement(
        By.xpath("//*[contains(text(),'FINAL OUTCOME')]/following-sibling::*"))
        .getText().toLowerCase();

    // If the score is not a draw, the label shouldn't say "draw"
    if (home != away) {
        assertFalse(outcome.contains("draw"),
            "Score is " + home + "-" + away + " but outcome says: " + outcome);
    }
}
```

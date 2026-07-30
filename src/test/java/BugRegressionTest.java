import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for all 9 bugs found on PreddyHub.
 * Each test catches a specific, reproducible bug.
 */
class BugRegressionTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1280, 720));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ─── Bug 1: FINAL OUTCOME label wrong on knockout matches ───
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

        if (home != away) {
            assertFalse(outcome.contains("draw"),
                "Score is " + home + "-" + away + " but outcome says: " + outcome);
        }
    }

    // ─── Bug 2: My Predictions counter vs list mismatch ─────────
    @Test
    void predictedCountMatchesList() {
        driver.get("https://preddyhub.com/world-cup-2026/my-predictions");

        WebElement counter = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(text(),'PREDICTED')]/preceding-sibling::*")));
        int count = Integer.parseInt(counter.getText().trim());

        boolean emptyState = driver.findElements(
            By.xpath("//*[contains(text(),'No predictions yet')]")).size() > 0;

        if (emptyState) {
            assertEquals(0, count,
                "Counter says " + count + " but list is empty");
        }
    }

    // ─── Bug 3: EPL My Predictions returns 404 ──────────────────
    @Test
    void eplMyPredictionsPageExists() {
        driver.get("https://preddyhub.com/premier-league/my-predictions");

        boolean is404 = driver.findElements(
            By.xpath("//*[contains(text(),'404')]")).size() > 0;

        assertFalse(is404, "My Predictions should exist for Premier League");
    }

    // ─── Bug 4: Leaderboard API ignores comp param ──────────────
    // (See LeaderboardApiTest.java for REST Assured version)

    // ─── Bug 5: La Liga My Predictions returns 404 ──────────────
    @Test
    void laLigaMyPredictionsPageExists() {
        driver.get("https://preddyhub.com/la-liga/my-predictions");

        boolean is404 = driver.findElements(
            By.xpath("//*[contains(text(),'404')]")).size() > 0;

        assertFalse(is404, "My Predictions should exist for La Liga");
    }

    // ─── Bug 6: La Liga Leaderboard silently redirects ──────────
    @Test
    void laLigaLeaderboardDoesNotRedirect() {
        driver.get("https://preddyhub.com/la-liga/leaderboard");

        String currentUrl = driver.getCurrentUrl();

        assertTrue(currentUrl.contains("/la-liga/leaderboard"),
            "Should stay on La Liga leaderboard, but redirected to: " + currentUrl);
    }

    // ─── Bug 7: Minus button doesn't work ───────────────────────
    @Test
    void minusButtonDecrementsScore() {
        driver.get("https://preddyhub.com/la-liga/predict-scores/");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-match-id]")));
        WebElement plus = card.findElement(
            By.cssSelector("button[aria-label*='plus']"));
        plus.click();

        WebElement score = card.findElement(
            By.cssSelector("[data-role='home-score']"));
        assertEquals("1", score.getText());

        WebElement minus = card.findElement(
            By.cssSelector("button[aria-label*='minus']"));
        minus.click();

        assertEquals("0", score.getText(),
            "Minus button should decrease score back to 0");
    }

    // ─── Bug 8: Nonexistent match ID loads wrong match ──────────
    @Test
    void nonexistentMatchShowsError() {
        driver.get("https://preddyhub.com/world-cup-2026/live?match=99999999");

        boolean hasError = driver.findElements(
            By.xpath("//*[contains(text(),'not found') or contains(text(),'404')]"))
            .size() > 0;

        assertTrue(hasError,
            "Nonexistent match ID should show an error, not load a random match");
    }

    // ─── Bug 9: Banner overlaps content on mobile ───────────────
    @Test
    void bannerDoesNotOverlapContent() {
        driver.manage().window().setSize(new Dimension(375, 812));
        driver.get("https://preddyhub.com/world-cup-2026/predict-scores/");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-match-id]")));

        boolean bannerExists = driver.findElements(
            By.xpath("//*[contains(text(),'Add PreddyHub to your Home Screen')]"))
            .size() > 0;

        if (bannerExists) {
            WebElement banner = driver.findElement(
                By.xpath("//*[contains(text(),'Add PreddyHub to your Home Screen')]"));
            int cardBottom = card.getLocation().getY() + card.getSize().getHeight();
            int bannerTop = banner.getLocation().getY();

            assertTrue(bannerTop >= cardBottom,
                "Banner overlaps match cards — banner at " + bannerTop
                + " but card ends at " + cardBottom);
        }
    }
}

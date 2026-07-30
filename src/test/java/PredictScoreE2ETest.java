import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.junit.jupiter.api.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium E2E test: open the app, make a score prediction,
 * verify it persists after a page reload.
 */
class PredictScoreE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        // Use a known, realistic viewport size
        driver.manage().window().setSize(new Dimension(1280, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("Prediction persists after page reload")
    void predictionPersistsAfterReload() {
        // ── 1. Open the predict-scores page ──
        driver.get("https://preddyhub.com/world-cup-2026/predict-scores/");

        // ── 2. Dismiss the onboarding wizard (3-step tour) ──
        dismissOnboarding();

        // ── 3. Wait for match cards to load (async hydration) ──
        WebElement firstCard = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-match-id]")
            )
        );
        // Scroll the card into the viewport centre
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", firstCard
        );

        // ── 4. Click the home team '+' button ──
        WebElement homePlus = wait.until(
            ExpectedConditions.elementToBeClickable(
                firstCard.findElement(
                    By.cssSelector("button[aria-label*='plus'], [data-role='home-plus']")
                )
            )
        );
        homePlus.click();

        // ── 5. Read the predicted score value ──
        WebElement homeScore = firstCard.findElement(
            By.cssSelector("[data-role='home-score']")
        );
        String scoreBefore = homeScore.getText();
        assertEquals("1", scoreBefore, "Home score should be 1 after one click");

        // ── 6. Capture match ID for post-reload lookup ──
        String matchId = firstCard.getAttribute("data-match-id");

        // ── 7. Reload the page ──
        driver.navigate().refresh();

        // ── 8. Wait for the same card to reappear ──
        WebElement reloadedCard = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-match-id='" + matchId + "']")
            )
        );

        // ── 9. Verify the prediction survived the reload ──
        WebElement homeScoreAfter = reloadedCard.findElement(
            By.cssSelector("[data-role='home-score']")
        );
        assertEquals("1", homeScoreAfter.getText(),
            "Draft prediction should persist after page reload (localStorage)");
    }

    /**
     * Clicks through the 3-step onboarding wizard.
     * Uses try-catch so the test doesn't fail if the wizard doesn't appear
     * (e.g. returning user with localStorage flag already set).
     */
    private void dismissOnboarding() {
        for (int step = 0; step < 3; step++) {
            try {
                WebElement nextBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Next') or contains(text(),\"Let's go\")]")
                    )
                );
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", nextBtn
                );
                nextBtn.click();
                Thread.sleep(400); // animation between steps
            } catch (TimeoutException | InterruptedException e) {
                break; // no more steps
            }
        }
    }
}

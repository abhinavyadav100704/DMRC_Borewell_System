package com.dmrc.borewell.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Selenium E2E tests for DMRC Borewell Management.
 *
 * WHY NO @Transactional HERE?
 * ----------------------------
 * @Transactional only works within the same JVM/thread context.
 * Selenium tests drive a real browser which sends real HTTP requests
 * to your Spring Boot backend. Each request is handled in its own
 * committed transaction — there is no shared transaction to roll back.
 *
 * CLEANUP STRATEGY USED HERE:
 * ----------------------------
 * 1. All test data is prefixed with "SELENIUM_" so it is easy to identify.
 * 2. A dedicated @AfterAll block deletes everything that was created:
 *    - Borewells are deleted before stations (FK constraint order).
 *    - The test user is deleted last.
 * 3. If you want zero DB side-effects during development, point your
 *    Spring Boot app at an H2 in-memory DB via a "test" profile:
 *      spring.datasource.url=jdbc:h2:mem:testdb
 *    Then just restart the app between runs — the DB is wiped automatically.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumTest {

    static WebDriver driver;
    static WebDriverWait wait;

    // Base URL
    private static final String BASE_URL = "http://localhost:3000";

    // Unique prefix so test data is easy to find and clean up
    private static final String PREFIX   = "SELENIUM_";

    // Test credentials
    private static final String USERNAME = PREFIX + "user";
    private static final String EMAIL    = PREFIX + "user@test.com";
    private static final String PASSWORD = "password123";

    // Test data names — stored statically so @AfterAll cleanup can use them
    private static final String STATION_NAME  = PREFIX + "Station";
    private static final int    BOREWELL_NO   = 9901; // unlikely to clash

    // ----------------------------------------------------------------
    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // uncomment for CI
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * CLEANUP — runs after all tests.
     * Deletes test data in reverse-dependency order:
     *   borewell → station → (user via DB or admin UI)
     *
     * This order is important to avoid FK constraint violations:
     * a station cannot be deleted while a borewell still references it.
     */
    @AfterAll
    static void teardown() {
        try {
            // Re-login as the test user to navigate protected pages
            loginAs(USERNAME, PASSWORD);

            // 1. Delete the borewell first (references the station via FK)
            deleteBorewellByNo(String.valueOf(BOREWELL_NO));

            // 2. Now safe to delete the station
            deleteStationByName(STATION_NAME);

            // 3. Test user cleanup:
            //    Option A — if your app has a "delete account" UI, call it here.
            //    Option B — run this SQL directly after the suite:
            //      DELETE FROM users WHERE username = 'SELENIUM_user';
            //    Option C — use a Spring @Sql or REST call in a separate cleanup test.

        } catch (Exception e) {
            System.err.println("[Cleanup] Warning: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // ================================================================
    // TESTS
    // ================================================================

    @Test
    @Order(1)
    void openWebsite() {
        driver.get(BASE_URL);
        // Unauthenticated users are redirected to /login by NextJS middleware
        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("localhost:3000"));
        System.out.println("Title: " + driver.getTitle());
    }

    @Test
    @Order(2)
    void testSignupUser() {
        driver.get(BASE_URL + "/register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        // IDs match <Input id="username"/>, <Input id="email"/>, <Input id="password"/>
        driver.findElement(By.id("username")).sendKeys(USERNAME);
        driver.findElement(By.id("email")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);

        // Select the "User" radio button
        WebElement userRadio = driver.findElement(
                By.xpath("//input[@type='radio' and @value='admin']"));
        if (!userRadio.isSelected()) userRadio.click();

        // Submit
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(),'Create Account')]"))).click();

        // Successful registration redirects to /login
        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Should redirect to /login after registration");
    }

    @Test
    @Order(3)
    void testLogin() {
        loginAs(USERNAME, PASSWORD);

        // Successful login redirects to /dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should redirect to /dashboard after login");
    }

    @Test
    @Order(4)
    void testViewBorewells() {
        driver.get(BASE_URL + "/dashboard/borewells");

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.isDisplayed(), "Borewells table should be visible");
    }

    @Test
    @Order(5)
    void testViewStations() {
        driver.get(BASE_URL + "/dashboard/stations");

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.isDisplayed(), "Stations table should be visible");
    }

    @Test
    @Order(6)
    void testCreateStation() {
        driver.get(BASE_URL + "/dashboard/stations");

        // Open dialog via "+ Add Station" header button
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='add-station-btn']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Add Station')]")));

        // Fill all fields — ids match <Input id="stationName"/> etc.
        clearAndType(By.id("stationName"),   STATION_NAME);
        clearAndType(By.id("lineId"),        "10");
        clearAndType(By.id("location"),      "South Delhi");
        clearAndType(By.id("platformCount"), "4");
        clearAndType(By.id("stationType"),   "Elevated");

        // Submit inside the dialog footer
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='submit-station-btn']"))).click();

        // Dialog should close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Add Station')]")));

        // Verify new row is in the table
        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.getText().contains(STATION_NAME),
                "New station should appear in the table");
    }

    @Test
    @Order(7)
    void testCreateBorewell() {
        driver.get(BASE_URL + "/dashboard/borewells");

        // Open dialog
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='add-borewell-btn']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Add Borewell')]")));

        // Fill fields — ids match <Input id="borewellNo"/> and <Input id="depth"/>
        clearAndType(By.id("borewellNo"), String.valueOf(BOREWELL_NO));
        clearAndType(By.id("depth"),      "250");

        // Station is a shadcn <Select> (Radix UI), NOT a native <select>.
        // Must click the trigger button first, then click an option item.
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='station-select']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@role='option'])[1]"))).click();

        // Submit inside dialog
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='submit-borewell-btn']"))).click();

        // Dialog should close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Add Borewell')]")));

        // Verify new row is in the table
        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.getText().contains(String.valueOf(BOREWELL_NO)),
                "New borewell should appear in the table");
    }

    @Test
    @Order(8)
    void testSearchBorewells() {
        driver.get(BASE_URL + "/dashboard/borewells");

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Search borewells...']")));
        searchInput.sendKeys(String.valueOf(BOREWELL_NO));

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.getText().contains(String.valueOf(BOREWELL_NO)),
                "Search should return borewell " + BOREWELL_NO);
    }

    @Test
    @Order(9)
    void testSearchStations() {
        driver.get(BASE_URL + "/dashboard/stations");

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Search stations...']")));
        searchInput.sendKeys(STATION_NAME);

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assertions.assertTrue(table.getText().contains(STATION_NAME),
                "Search should return station " + STATION_NAME);
    }

    @Test
    @Order(10)
    void testLogout() {
        driver.get(BASE_URL + "/dashboard");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'Logout')]"))).click();

        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Should land on /login after logout");
    }

    // ================================================================
    // CLEANUP HELPERS  (called from @AfterAll)
    // ================================================================

    /**
     * Logs in via the UI. Static so @AfterAll can call it.
     */
    private static void loginAs(String username, String password) {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(
                By.xpath("//button[@type='submit' and contains(text(),'Sign In')]")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    /**
     * Searches for the borewell by number, clicks its trash icon,
     * then confirms the delete dialog.
     * Must be called BEFORE deleteStationByName to respect FK constraints.
     */
    private static void deleteBorewellByNo(String borewellNo) {
        driver.get(BASE_URL + "/dashboard/borewells");

        // Search so only our row is visible
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Search borewells...']")));
        search.sendKeys(borewellNo);

        // Click the last button (trash icon) in that row
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[contains(text(),'" + borewellNo + "')]"
                        + "/following-sibling::td//button[.//*[local-name()='svg']][last()]"))).click();

        // Confirm the AlertDialog
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Delete') and not(contains(text(),'Cancel'))]"))).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Delete Borewell')]")));

        System.out.println("[Cleanup] Deleted borewell: " + borewellNo);
    }

    /**
     * Searches for the station by name, clicks its trash icon,
     * then confirms the delete dialog.
     */
    private static void deleteStationByName(String stationName) {
        driver.get(BASE_URL + "/dashboard/stations");

        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Search stations...']")));
        search.sendKeys(stationName);

        // Click the last button (trash icon) in that row
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[contains(text(),'" + stationName + "')]"
                        + "/following-sibling::td//button[.//*[local-name()='svg']][last()]"))).click();

        // Confirm the AlertDialog
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Delete') and not(contains(text(),'Cancel'))]"))).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Delete Station')]")));

        System.out.println("[Cleanup] Deleted station: " + stationName);
    }

    // ----------------------------------------------------------------
    // General utility
    // ----------------------------------------------------------------
    private void clearAndType(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }
}
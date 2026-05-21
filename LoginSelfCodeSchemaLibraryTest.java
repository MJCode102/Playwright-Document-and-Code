package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Selenium + Java version of Playwright test:
 * Login_SelfCode-SchemaLibrary.spec.js
 *
 * This class focuses on the positive flow:
 *  - Login
 *  - Open tenant
 *  - Create schema from URL
 *  - Filter for created schema
 *  - Add description, resource group and labels
 */
public class LoginSelfCodeSchemaLibraryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        // Configure ChromeDriver (path can be set via webdriver.chrome.driver system property)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void loginAndCreateSchemaLibrary() {
        String baseUrl = "https://laxmiw.webconsole.in.scitara.dev/";
        String email = "laxmi.waghmore@scitara.com";
        String password = "Secret1!!";

        String schemaName = "Test_harness_SchemaLibrary_Import_Url_" + System.currentTimeMillis();
        String resourceGroup = "Grp1";

        // === Login ===
        driver.get(baseUrl);

        // Email
        WebElement emailInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("input[placeholder='Email address']")));
        emailInput.sendKeys(email);

        WebElement nextButton = driver.findElement(By.cssSelector("button:has-text('Next')"));
        nextButton.click();

        // Password
        WebElement passwordInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("input[placeholder='Your password']")));
        passwordInput.sendKeys(password);

        WebElement signOnButton = driver.findElement(By.cssSelector("button:has-text('Sign on')"));
        signOnButton.click();

        // === Select tenant Schema Tenant 01 ===
        WebElement tenantCard = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//div[contains(@class,'pointer') and contains(.,'Schema Tenant 01')]")));
        tenantCard.click();

        // === Navigate to Schema Library ===
        WebElement schemaLibraryNav = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_nav_schema_library']")));
        schemaLibraryNav.click();

        // Click + Add Schema
        WebElement addSchemaButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_add_schema']")));
        addSchemaButton.click();

        // Import from URL
        WebElement importFromUrlItem = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_import_from_url_menu_item']")));
        importFromUrlItem.click();

        // Paste URL
        WebElement urlInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='dtid_paste_url_input'] input[placeholder='https://json-schema.org/draft/2020-12/schema']")));
        urlInput.sendKeys("http://purl.allotrope.org/json-schemas/adm/core/REC/2021/09/core.schema");

        // Schema Name
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='dtid_paste_url_name_input'] input[placeholder='Schema Name']")));
        nameInput.sendKeys(schemaName);

        // Import
        WebElement importButton = driver.findElement(By.cssSelector("[data-testid='dtid_paste_url_import_button']"));
        importButton.click();

        // Wait for possible modal and close it if present
        try {
            WebElement cancelBtn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='dtid_schema_cancel_button']")));
            cancelBtn.click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("[data-testid='dtid_nav_schema_library']"))).click();
        } catch (Exception e) {
            // Fallback cancel button
            try {
                WebElement genericCancel = driver.findElement(By.cssSelector("[data-testid='dtid_cancel_button']"));
                genericCancel.click();
            } catch (Exception ignored) {
            }
        }

        // === Filter the created schema and open it ===
        WebElement navSchemaLibrary = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_nav_schema_library']")));
        navSchemaLibrary.click();

        // Filter dropdown (combobox)
        WebElement filterCombobox = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[role='combobox']")));
        filterCombobox.click();

        // Select Schema Name option
        WebElement schemaNameOption = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//li[@role='option' and normalize-space(.)='Schema Name']")));
        schemaNameOption.click();

        // Enter schema name value (assumes last textbox is the value input)
        WebElement valueInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("input[role='textbox'], textarea[role='textbox']")));
        valueInput.sendKeys(schemaName);
        valueInput.sendKeys(Keys.ENTER);

        // Click Apply
        WebElement applyButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button[.='Apply']")));
        applyButton.click();

        // Wait for filtered result then click the schema
        WebElement schemaTile = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[@data-testid='dtid_entity_title' and .='" + schemaName + "']")));
        schemaTile.click();

        // === Add description ===
        WebElement settingsTab = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_schema_navigation_navigation_Settings']")));
        settingsTab.click();

        WebElement descriptionArea = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_schema_description']")));
        descriptionArea.clear();
        descriptionArea.sendKeys("This is test schema description");

        // === Add resource group ===
        WebElement resourceGroupSelect = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("mui-component-select-resourceGroup")));
        resourceGroupSelect.click();

        WebElement resourceOption = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[role='option'][data-value='" + resourceGroup + "']")));
        resourceOption.click();

        // Save
        WebElement saveButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_schema_save_button']")));
        saveButton.click();

        // === Add & delete labels ===
        WebElement addLabelButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button[contains(.,'+ Add Label')]")));
        addLabelButton.click();

        WebElement label0 = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-testid='dtid_label_name_0']")));
        label0.sendKeys("TestLabel1");

        addLabelButton.click();
        WebElement label1 = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-testid='dtid_label_name_1']")));
        label1.sendKeys("TestLabel2");

        saveButton.click();

        // Delete first label
        WebElement deleteLabel0 = wait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("[data-testid='dtid_label_delete_0']")));
        deleteLabel0.click();

        // Add another label
        addLabelButton.click();
        WebElement label1Again = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("[data-testid='dtid_label_name_1']")));
        label1Again.sendKeys("TestLabel3");

        saveButton.click();

        // Navigate back to schema library
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='dtid_nav_schema_library']"))).click();
    }
}


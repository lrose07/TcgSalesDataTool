import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;

public final class SeleniumUtilities {

    public static WebDriver driver;

    private static void establishBrowserInstance() {
        WebDriverManager.getInstance(CHROME).setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
// Currently getting a NoSuchElementException on UserName element when running headless
//        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");

        driver = new ChromeDriver(options);
    }

    public static void pullOrderSlips() {
        establishBrowserInstance();

        driver.get("https://store.tcgplayer.com/admin/Seller/Dashboard/fad82273");

        if (driver.getTitle().equals("Seller Dashboard")) {
            System.out.println("Already logged in");
        } else {
            System.out.println("Logging in...");
            login();
        }

        // get file with order numbers
        // for each order number, go to URL: https://store.tcgplayer.com/admin/orders/manageorder/{order_number}
        downloadOrderSlip();

        // need to figure out the best way to keep track of which order numbers have been processed already
        // option: hold a file in s3 with the timestamp of the last order number pull, only pull orders
        // from that timestamp forward
        // option: daily lambda runs at midnight, pulls orders from previous day
    }

    private static void login() {
        WebElement usernameInput = driver.findElement(By.name("UserName"));
        WebElement passwordInput = driver.findElement(By.name("Password"));
        WebElement logonButton = driver.findElement(By.id("logonButton"));

        usernameInput.sendKeys("laurenarose07@gmail.com");
        String secretNameArn = "arn:aws:secretsmanager:us-east-1:643878261543:secret:tcgPlayerPassword-HQ3PU5";
        String secretName = "tcgPlayerPassword";
        String password = AwsUtilities.getRequestedSecretValue(secretName, secretNameArn);
        System.out.println("Password: " + password);
        passwordInput.sendKeys(password);
        logonButton.click();

        if (driver.getTitle().equals("Seller Dashboard")) {
            System.out.println("Successfully logged in");
        }
    }

    private static void downloadOrderSlip() {
        // find download order slip button element
        // download to local?? -> upload to s3. Is there a way to go direct to s3?
        System.out.println("To be implemented still");
    }
}

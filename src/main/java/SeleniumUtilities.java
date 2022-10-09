import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
//import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;
import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public final class SeleniumUtilities {

    public static WebDriver driver;

    private static void establishBrowserInstance() {
        WebDriverManager.getInstance(CHROME).setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
//        options.addArguments("--proxy-server='direct://'");
//        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--start-maximized");
//        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");

        driver = new ChromeDriver(options);

//        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
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

        downloadOrderSlip();
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

    // URL for individual orders: https://store.tcgplayer.com/admin/orders/manageorder/{order_number}

    private static void downloadOrderSlip() {
        System.out.println("To be implemented still");
    }
}

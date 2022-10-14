import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;

public final class SeleniumUtilities {

    public static WebDriver driver;

    private static void establishBrowserInstance() {
        WebDriverManager.getInstance(CHROME).setup();

        Map<String,Object> preferences= new HashMap<>();
        preferences.put("profile.default_content_settings.popups", 0);
        preferences.put("download.default_directory",
                System.getProperty("user.dir") + File.separator
                + "src" + File.separator
                + "main" + File.separator
                + "resources" + File.separator
                + "orderSlipDownloads");

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

        options.setExperimentalOption("prefs", preferences);

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

        List<String> orderNumbers = getOrderNumbersToProcess();

        for (String s : orderNumbers) {
            downloadOrderSlip(s);
        }

        // need to figure out the best way to keep track of which order numbers have been processed already
        // option: hold a file in s3 with the timestamp of the last order number pull, only pull orders
        // from that timestamp forward
        // option: daily lambda runs at midnight, pulls orders from previous day

        driver.quit();
    }

    private static void login() {
        WebElement usernameInput = driver.findElement(By.name("UserName"));
        WebElement passwordInput = driver.findElement(By.name("Password"));
        WebElement logonButton = driver.findElement(By.id("logonButton"));

        // should prob put these in a prop file maybe?
        usernameInput.sendKeys("laurenarose07@gmail.com");
        String secretNameArn = "arn:aws:secretsmanager:us-east-1:643878261543:secret:tcgPlayerPassword-HQ3PU5";
        String secretName = "tcgPlayerPassword";
        String password = AwsUtilities.getRequestedSecretValue(secretName, secretNameArn);
        passwordInput.sendKeys(password);
        logonButton.click();

        if (driver.getTitle().equals("Seller Dashboard")) {
            System.out.println("Successfully logged in");
        }
    }

    private static List<String> getOrderNumbersToProcess() {
        Path orderNumbersFile = Paths.get("src/main/resources/order_numbers.txt");
        try {
            return Files.readAllLines(orderNumbersFile);
        } catch (IOException e) {
            System.out.println("File issue");
        }
        return new ArrayList<>();
    }

    private static void downloadOrderSlip(String orderNumber) {
        driver.get("https://store.tcgplayer.com/admin/orders/manageorder/" + orderNumber);

        WebElement downloadButton = driver.findElement(By.xpath("//*[@id=\"rightSide\"]/div/div[4]/div[2]/div[1]/div[1]/div[1]/div[2]/div[2]/div[1]/ul/li/a"));
        downloadButton.click();
    }
}

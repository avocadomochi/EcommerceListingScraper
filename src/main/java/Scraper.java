import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Scraper {
    public static void main(String[] args) {

        //mainly used to test isolated return of listingData()
        ArrayList<String[]> data = listingData();
        for(String i[] : data) {
            System.out.println(Arrays.toString(i));
        }

    }

    public static ArrayList<String[]> listingData() {

        ArrayList<String[]> data = new ArrayList<String[]>();

        Scanner preLoad = new Scanner(System.in);
        System.out.println("Have you preloaded links into links.txt? (Y/N)");

        // checks the user input and ensures input is valid
        String boolPreload = preLoad.nextLine();
        while(! boolPreload.equals("Y") && ! boolPreload.equals("N")) {
            System.out.println("Invalid input. Enter (Y/N)");
            boolPreload = preLoad.nextLine();
        }

        String[] linksArr = new String[0];
        String returnValue = "";

        //reads links.txt and converts links into String[]
        if(boolPreload.equals("Y")) {
            try {
                File linkFile = new File("E:\\EcommerceListingScraper\\src\\main\\links\\links.txt");
                Scanner linkReader = new Scanner(linkFile);
                String linkStr = "";
                while(linkReader.hasNextLine()){
                    linkStr += "," + linkReader.nextLine();
                }
                linksArr = linkStr.split(",");
                linkReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("File Not Found");
                e.printStackTrace();
            }
            // asks user for comma seperated list of links and converts them into String[]
        } else {
            Scanner links = new Scanner(System.in);
            System.out.println("Input links seperated by a comma:");
            String linksStr = links.nextLine();
            linksArr = linksStr.split(",");
        }

        System.setProperty("webdriver.chrome.driver","E:\\ChromeDriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        //iterates through Array of links
        for(int i = 1; i < linksArr.length; i++) {
            String tempUrl = linksArr[i];
            driver.get(tempUrl);

            // sleeps to give page time to load (same result can be reached using wait())
            try {
                Thread.sleep(400);
            } catch(InterruptedException e) {
                System.out.println("Interrupted");
            }

            String[] tempArray = new String[4];

            String title = "";
            String price = "";
            String brand = "";
            String url = "";

            // checks whether the link provided is either an amazon or ebay link
            // (almost everything you can buy is on amazon or ebay)

            if(tempUrl.contains("amazon.com")) {
                title = driver.findElement(By.id("productTitle")).getText();
                price = driver.findElement(By.id("corePrice_feature_div")).getText();
                brand = driver.findElement(By.id("bylineInfo")).getText();
                int lastSlash = tempUrl.lastIndexOf("/");
                url = tempUrl.substring(0,lastSlash);

                price = price.replace("$", "").replace("\n", ".");
                brand = brand.replace("Brand: ", "").replace("Visit the ", "").replace("Store", "");

            } else if(tempUrl.contains("ebay.com")) {
                title = driver.findElement(By.className("vi-swc-lsp")).getText();
                price = driver.findElement(By.id("prcIsum")).getText();
                brand = driver.findElement(By.cssSelector("span[itemtype='http://schema.org/Brand']")).getText();
                int firstQM = tempUrl.indexOf("?");
                if(firstQM != -1) url = tempUrl.substring(0, firstQM);
                else url = tempUrl;

                int firstDigit = price.indexOf("$");
                price = price.substring(firstDigit + 1);


            }

            // inserts data into a temporary array
            tempArray[0] = title;
            tempArray[1] = brand.toUpperCase();
            tempArray[2] = price;
            tempArray[3] = url;

            // temporary array is added to our arrayList
            data.add(tempArray);

        }

        driver.close();
        return data;
    }
}


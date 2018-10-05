package com.ashish.bigbasket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BBTestCase {

	WebDriver driver;
	private static int timeout = 10;
	public WebDriverWait wait;
	private final By SearchTextBox = By.id("input");
	private static final String FILE_NAME = "MyFirstExcel.xlsx";
	
	// Apache POI
	@Test
	public static void test4() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
		Object[][] datatypes = { { "Datatype", "Type", "Size(in bytes)" }, { "int", "Primitive", 2 },
				{ "float", "Primitive", 4 }, { "double", "Primitive", 8 }, { "char", "Primitive", 1 },
				{ "String", "Non-Primitive", "No fixed size" } };
		int rowNum = 0;
		System.out.println("Creating excel");

		for (Object[] datatype : datatypes) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : datatype) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}
		
		try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
        
        try {

            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellType() == CellType.STRING) {
                        System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        

	}

	// REST POST call
	public static void test3() {
	//To start this service run below command from c:\sandbox\jsonserver json-server employees.js  Or json-server --watch db.json (Change this db.json as required) 
		//https://medium.com/codingthesmartway-com-blog/create-a-rest-api-with-json-server-36da8680136d 
		RestAssured.baseURI = "http://localhost:3000";
		RequestSpecification request = RestAssured.given();

		// JSONObject is a class that represents a Simple JSON.
		// We can add Key - Value pairs using the put method
		JSONObject requestParams = new JSONObject();
		requestParams.put("first_name", "Virender");
		requestParams.put("last_name", "Singh");
		requestParams.put("email", "someuser@gmail.com");

		// Add a header stating the Request body is a JSON
		request.header("Content-Type", "application/json");
		// Add the Json to the body of the request
		request.body(requestParams.toJSONString());
		// Post the request and check the response
		Response response = request.post("/employees");

		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, "201");
		String successCode = response.jsonPath().get("SuccessCode");
		Assert.assertEquals("Correct Success code was returned", successCode, "OPERATION_SUCCESS");

	}

	public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {

		// Convert web driver object to TakeScreenshot

		TakesScreenshot scrShot = ((TakesScreenshot) webdriver);

		// Call getScreenshotAs method to create image file

		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

		// Move image file to new destination

		File DestFile = new File(fileWithPath);

		// Copy file at destination

		FileUtils.copyFile(SrcFile, DestFile);

	}

//Take snapshot read table to an object 
	public void test2() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.get(
				"C:\\Users\\askumar\\workspace1\\BigBasketOrder\\src\\test\\java\\com\\ashish\\bigbasket\\test.html");
		driver.manage().window().maximize();
		WebElement h1tag = driver.findElement(By.tagName("h1"));
		try {
			takeSnapShot(driver, "C:\\Users\\askumar\\Desktop\\bigbasket\\test.png");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		System.out.println(h1tag.getText());

		List<WebElement> listRows;
		listRows = driver.findElements(By.tagName("tr"));
		List<Employee> empList = new ArrayList<Employee>();
		// Create Employee Array.
		int count = 0;
		Employee e = null;
		for (WebElement row : listRows) {
			if (count == 0) {
				count++;
				continue;
			}
			List<WebElement> listColumns = row.findElements(By.tagName("td"));
			e = new Employee();
			int c = 0;
			for (WebElement rowdata : listColumns) {
				switch (c) {
				case 0:
					e.id = Integer.parseInt(rowdata.getText());
					c++;
					break;
				case 1:
					e.name = rowdata.getText();
					c++;
					break;
				case 2:
					e.salary = Long.parseLong(rowdata.getText());
					c++;
					break;

				}
				// System.out.print(rowdata.getText());
			}
			empList.add(e);
			// System.out.println();
		}

		for (Employee e1 : empList) {
			System.out.println("Employee : " + e1.id);
		}
	}

	@AfterTest
	public void cleanup() {
		driver.quit();
	}

	public void test1() throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");
		driver = new ChromeDriver();

		driver.get("http://bigbasket.com");
		driver.manage().window().maximize();
		WebElement loginButton = driver.findElement(By.linkText("Login"));
		loginButton.click();
		WebElement email = driver.findElement(By.id("loginemail"));
		email.sendKeys("ashish5social@gmail.com");
		WebElement pwd = driver.findElement(By.id("password"));
		pwd.sendKeys("password");
		// WebElement loginButton1 =
		// driver.findElement(By.xpath("//*[@id=\"login\"]/login/div/form/button"));
		WebElement loginButton1 = driver.findElement(By.xpath("//button[@type='submit']"));
		loginButton1.click();
		//// *[@id="login"]/login/div/form/button "//span[@id='red']/span"

		waitUntilElementIsDisplayedOnScreen(SearchTextBox);
		WebElement searchBox = driver.findElement(SearchTextBox);
		searchBox.sendKeys("Potato");
		WebElement searchButton = driver.findElement(By.cssSelector(".btn.btn-default.bb-search"));
		searchButton.click();

//		//*[@id="dynamicDirective"]/product-deck/section/div[2]/div[4]/div[1]/div/div[1]/div[2]/div/div[1]/product-template/div/div[3]/a
//		var elm=document.getElementsByClassName('items')[0]
//				
//				var elm1= elm.getElementsByClassName('item prod-deck row ng-scope')[0]

		WebElement potato = driver.findElement(By.xpath(
				"//*[@id=\"dynamicDirective\"]/product-deck/section/div[2]/div[4]/div[1]/div/div[1]/div[2]/div/div[1]/product-template/div/div[3]/a/img"));
		potato.click();
		WebElement addButton = driver.findElement(By.cssSelector(".fade.sc-bbmXgH.cEBnvi"));
		addButton.click();

//		Actions tooltip = new Actions(driver);
//		Actions a1 = tooltip.moveToElement(driver.findElement(By.className("my-basket-btn")));  
//		a1.build().perform();
		// Use jqueryui.com for testing Actions class

		WebElement btn1 = driver.findElement(By.className("my-basket-btn"));
		btn1.click();

		Thread.sleep(2000);
		WebElement btn2 = driver.findElement(By.className("uiv2-checkout-button"));
		btn2.click();
		Thread.sleep(2000);

		// .build.perform();
		// After this, U can then try to get the element in that tooltip

		System.out.println("Here after login button");

	}

	public void waitUntilElementIsDisplayedOnScreen(By selector) {
		try {
			wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		} catch (Exception e) {
			throw new NoSuchElementException(String.format("The following element was not visible: %s ", selector));
		}
	}

}

class Employee {
	public int id;
	public String name;
	public long salary;
}

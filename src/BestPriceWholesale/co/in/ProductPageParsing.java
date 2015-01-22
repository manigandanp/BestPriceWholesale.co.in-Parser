package BestPriceWholesale.co.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ProductPageParsing {
	private String homeDomain = "http://www.bestpricewholesale.co.in/";

	public ProductPageParsing() {
		String default_userName = "11734100008800369";
		String default_passWord = "Krishna9";
		new ProductPageParsing(default_userName, default_passWord);
	}

	String user = null;
	String pass = null;

	public ProductPageParsing(String user, String pass) {
		this.user = user;
		this.pass = pass;
		WebDriver htmDriver = new HtmlUnitDriver();
		htmDriver.get(homeDomain);

		htmDriver.findElement(
				By.id("ctl00_ContentPlaceHolder1_ctl00_ctl01_Login1_UserName"))
				.sendKeys(user); // Type username in user name field
		htmDriver.findElement(
				By.id("ctl00_ContentPlaceHolder1_ctl00_ctl01_Login1_Password"))
				.sendKeys(pass); // Type password in password field
		htmDriver
				.findElement(
						By.id("ctl00_ContentPlaceHolder1_ctl00_ctl01_Login1_LoginImageButton"))
				.click(); // Click submit button

		// Cookies part
		Set<Cookie> cookie = htmDriver.manage().getCookies();
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(
				"Site_Config",
				"{\"LocationCodes\":\"4734\",\"LocationCodes_PinCode\":\"Hyderabad\",\"LocationId\":\"108\"}");
		for (Cookie s : cookie) {
			cookies.put(s.getName(), s.getValue());
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String temppath = "";
		temppath = System.getProperty("user.home")
				+ "\\Desktop\\BestPriceWholeSale.co.in\\"
				+ dateFormat.format(date);
		File dirCreate = new File(temppath);
		dirCreate.mkdirs();
		File fl = new File(temppath + "\\ProductUrls.csv");
		File writeFile = new File(temppath + "\\ProuctDetails.csv");
		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(writeFile);
			String header = "Product Url" + ", " + "Product Title" + ", "
					+ "Our Price" + ", " + "MRP" + ", " + "Brand" + ","
					+ "Item No:" + "," + "Description" + ","
					+ "Additional Information" + "," + "In Stock" + ","
					+ "Traveling Path" + "\n";
			fop.write(header.getBytes());
			// }catch(IOException e){}
			String proTitle, proOurPrice, proMRP, proBrand, proDesc, proTravelPath, proUrl, itemNo, addInfo, inStock;

			try {
				FileInputStream productUrlFile = new FileInputStream(fl);
				BufferedReader brProUrls = new BufferedReader(
						new InputStreamReader(productUrlFile));

				String productUrl;
				while ((productUrl = brProUrls.readLine()) != null) {
					// System.out.println(i + " : " +line);
					productUrl = productUrl.replaceAll("\"http", "http")
							.replaceAll(".aspx\"", ".aspx");
					System.out.println(productUrl);
					try {
						Document productPage = Jsoup
								.connect(productUrl)
								.cookies(cookies)
								.timeout(60000)
								.userAgent(
										"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36")
								.get();
						proTitle = productPage
								.select("div[itemprop=\"name\"] > h1").text()
								.trim();
						proOurPrice = productPage
								.select("#ctl00_ContentPlaceHolder1_Price_ctl00_lblOfferPrice")
								.text().replaceAll("Rs.", "").trim();
						proMRP = productPage
								.select("#ctl00_ContentPlaceHolder1_Price_ctl00_lblMrp")
								.text().replaceAll("Rs.", "").trim();
						proBrand = productPage
								.select(".productbrand .brandlname").text()
								.trim();
						addInfo = productPage.select(".ctl_aboutproduct")
								.text().trim().replaceAll("\\n", " ")
								.replaceAll("\"", "'").replaceAll("  ", "");
						proDesc = productPage.select("#Description").text()
								.trim().replaceAll("\\n", " ")
								.replaceAll("\"", "'").replaceAll("  ", "");
						proTravelPath = "";// productPage.select("#ctl00_ContentPlaceHolder1_Breadcrum_ctl00_brdCrumbNormal").text().trim();
						for (Element path : productPage
								.select("#ctl00_ContentPlaceHolder1_Breadcrum_ctl00_brdCrumbNormal a")) {
							proTravelPath = proTravelPath + " > " + path.text();
						}
						itemNo = productPage.select("div.sku").text()
								.replaceAll("Item No :", "").trim();
						proUrl = productPage.baseUri();
						inStock = productPage
								.select("#instock > .instock")
								.text()
								.replaceFirst("Available at Select Locations.",
										"Stock Available");

						String output = "\"" + proUrl + "\",\"" + proTitle
								+ "\",\"" + proOurPrice + "\",\"" + proMRP
								+ "\",\"" + proBrand + "\",\"" + itemNo
								+ "\",\"" + proDesc + "\",\"" + addInfo
								+ "\",\"" + inStock + "\",\"" + proTravelPath
								+ "\"\n";
						System.out.println(output);
						byte[] opInBytes = output.getBytes();
						fop.write(opInBytes);

					} catch (Exception e) {
						System.out.println("Product Page loading error" + e);
					}
				}
				brProUrls.close();
				fop.close();
			} catch (IOException e) {
				System.out.println("File Input stream read error");
			}
			fop.close();
		} catch (IOException e) {
			System.out
					.println("Output Stream file writing error - Details part");
		}
	}
}

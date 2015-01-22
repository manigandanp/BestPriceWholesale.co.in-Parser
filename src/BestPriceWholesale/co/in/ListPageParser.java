package BestPriceWholesale.co.in;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Cookie;

public class ListPageParser {

	private String homeDomain = "http://www.bestpricewholesale.co.in/";

	public ListPageParser() {
		String default_userName = "11734100008800369";
		String default_passWord = "Krishna9";
		new ListPageParser(default_userName, default_passWord);
	}

	String user = null;
	String pass = null;

	public ListPageParser(String user, String pass) {
		this.user = user;
		this.pass = pass;

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities
				.setBrowserName("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36");
		capabilities.setVersion("Firefox/24.0");

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
				.click();
		String indexPageSource = htmDriver.getPageSource();
		// Cookies part
		Set<Cookie> cookie = htmDriver.manage().getCookies();
		System.out.println(cookie.toString());
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(
				"Site_Config",
				"{\"LocationCodes\":\"4734\",\"LocationCodes_PinCode\":\"Hyderabad\",\"LocationId\":\"108\"}");
		for (Cookie s : cookie) {
			cookies.put(s.getName(), s.getValue());
		}
		Document indexPage = Jsoup.parse(indexPageSource);
		Elements categoryPart = indexPage.select("ul.l2_inner_ul li.l2 > a");
		String[] categoryUrls = new String[categoryPart.size()];
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String temppath = "";
		temppath = System.getProperty("user.home")
				+ "\\Desktop\\BestPriceWholeSale.co.in\\"
				+ dateFormat.format(date);
		File dirCreate = new File(temppath);
		dirCreate.mkdirs();
		File fl = new File(temppath + "\\ProductUrls.csv");
		FileWriter productUrls = null;
		try {
			productUrls = new FileWriter(fl);
			// productUrls.write("ListPage Url" + "," + "Product Urls" + "\n");

			int i = 0;
			for (Element hrf : categoryPart) {
				String tempHref = hrf.attr("href");
				if (tempHref.indexOf("/categories/") != -1) {
					categoryUrls[i] = tempHref;
					i++;
				}
			}
			for (int j = 0; j < categoryUrls.length; j++) {
				if (categoryUrls[j] != null) {
					System.out.println(categoryUrls[j]);
					try {
						Document categoryPageSource = Jsoup
								.connect(categoryUrls[j])
								.timeout(60000)
								.cookies(cookies)
								.userAgent(
										"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36")
								.get();
						System.out.println(categoryPageSource.select(
								"#ctl00_Searchbox_PinCodeDisplay").text());
						Pattern ptrn = Pattern
								.compile(".PgControlId.*[\\W\\w]*?}");
						Matcher match = ptrn.matcher(categoryPageSource.html());
						String urlPattern = "";
						if (match.find()) {
							urlPattern = "http://www.bestpricewholesale.co.in/Handler/ProductShowcaseHandler.ashx?ProductShowcaseInput={"
									+ match.group();
							urlPattern = URLEncoder.encode(urlPattern, "UTF-8")
									.replaceAll("%0D", "")
									.replaceAll("%0A", "")
									.replaceAll("\\+\\+", "");
							urlPattern = URLDecoder
									.decode(urlPattern, "UTF-8")
									.replaceFirst("\"LocationId.*?\",",
											"\"LocationIds\":\"4734\",")
									.replaceAll(": ", ":")
									+ "&_=" + new Date().getTime();

							int pageNo = 1;
							String productCount = "";
							do {
								urlPattern = urlPattern.replaceFirst(
										"\"PageNo\":(\\d+)", "\"PageNo\":"
												+ pageNo);
								try {
									// Document listPages =
									// Jsoup.connect(urlPattern).timeout(50000).cookies(cookies).userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36").get();
									htmDriver.get(urlPattern);
									String listPageSource = htmDriver
											.getPageSource();
									Document listPages = Jsoup
											.parse(listPageSource);
									productCount = listPages
											.select("span.p_count").text()
											.trim();
									System.out.println(urlPattern);
									System.out.println(pageNo);
									if (listPages.select(
											"div.bucket_left>a>img").size() == 0) {
										System.out.println("Break form if");
										break;
									} else {
										Elements listUrls = listPages
												.select("div.bucket_left>a>img");
										for (Element listUrl : listUrls) {
											String listUrlHref = listUrl
													.parent().attr("href");
											// System.out.println(listUrlHref);
											String input = "\"" + listUrlHref
													+ "\"\n";
											productUrls.write(input);
										}
									}
								} catch (NullPointerException e) {
									break;
								} catch (Exception e) {
									System.out
											.println("List Page Loading Error."
													+ e);
								}
								pageNo++;
							} while (productCount != "0");

						}

					} catch (Exception e) {
						System.out
								.println("While Loading category page some unexpected error occured. "
										+ e);
					}

				}

			}
			productUrls.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}

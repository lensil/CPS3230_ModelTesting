package abc.pageobjects;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomepagePageObject {

    By liveLink = By.cssSelector(".AnchorLink.navMenu__link.live");
    By articles = By.cssSelector(".zZygg.UbGlr.iFzkS.qdXbA.WCDhQ.DbOXS.tqUtK.GpWVU.iJYzE");
    By articleHeading = By.cssSelector(".ZfQkn.GdxUi.PFoxV.eBpQD.rcQBv.bQtjQ.lQUdN.GpQCA.mAkiF.FvMyr.WvoqU.nPLLM.tuAKv");
    By searchLens = By.cssSelector(".search__trigger.search__trigger--default");
    By searchInput = By.cssSelector(".search__input");
    By searchSubmit = By.cssSelector(".search__input__submit");
    By businessLink = By.linkText("Business");
    By mainArticle = By.cssSelector("div.liAe.uMOq.zYIfP");
    By homePageLink = By.cssSelector(".AnchorLink.navLogo__link");    
    WebDriver driver;

    public HomepagePageObject(WebDriver driver) {
        this.driver = driver;
    }

    public void clickLiveLink() {
        WebElement live = driver.findElement(liveLink);
        live.click();
    }

    public void clickBusinessLink() {
        WebElement business = driver.findElement(businessLink);
        business.click();
    }

    public void searchFor(String query) {
        WebElement search = driver.findElement(searchLens);
        search.click();
        WebElement searchInputField = driver.findElement(searchInput);
        searchInputField.sendKeys(query);
        WebElement searchSubmitButton = driver.findElement(searchSubmit);
        searchSubmitButton.click();
    }

    public void clickOnArticle(int index) {
        List<WebElement> articleList = driver.findElements(articles);
        WebElement article = articleList.get(index);
        article.click();
    }

    public String getArticleHeading() {
        WebElement heading = driver.findElement(articleHeading);
        String headingText = heading.getText();
        return headingText;
    }

    public String getArticleHeadingIndex(int index) {
        List<WebElement> articleHeadingList = driver.findElements(articleHeading);
        WebElement heading = articleHeadingList.get(index);
        String headingText = heading.getText();
        return headingText;
    }

    public String getMainArticleTitle() {
        WebElement mainArticleElement = driver.findElement(mainArticle);
        WebElement titleElement = mainArticleElement.findElement(By.cssSelector("h2"));
        String title = titleElement.getText();
        return title;
    }

    public void clickOnMainArticle() {
        WebElement mainArticleElement = driver.findElement(mainArticle);
        mainArticleElement.click();
    }

    public void clickOnHomePageLink() {
        WebElement homePageLinkElement = driver.findElement(homePageLink);
        homePageLinkElement.click();
    }

    public boolean isHomePageLoaded() {
        try {
            return driver.getCurrentUrl().equals("https://abcnews.go.com/") &&
                   driver.findElement(mainArticle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Track click results for model verification
    public String clickOnArticleAndGetTitle(int index) {
        String expectedTitle = getArticleHeadingIndex(index);
        clickOnArticle(index);
        return expectedTitle;
    }
}

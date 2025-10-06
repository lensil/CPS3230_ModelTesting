package abc.pageobjects;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPageObject {

    By searchResultsTotal = By.cssSelector(".Search__results");
    By dateFilter = By.name("after");
    By sortByFilter = By.name("sort");
    By searchedArticles = By.cssSelector(".ContentRoll__Item");
    By searchArticleTimeStamp = By.cssSelector(".TimeStamp__Date");
    By noResults = By.cssSelector(".Search__No__Results");
    By articleHeadlines = By.cssSelector(".ContentRoll__Headline h2 a");
    By articleContainers = By.cssSelector(".ContentRoll__Item");
    private String currentFilter;

    private final WebDriverWait wait;
    WebDriver driver;

    public SearchPageObject(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
    
    public String getSearchResultsTotal() {
        WebElement searchResults = driver.findElement(searchResultsTotal);
        String searchResultsText = searchResults.getText();
        return searchResultsText;
    }

    public void clickOnArticle(int index) {
        List<WebElement> articleList = driver.findElements(searchedArticles);
        WebElement article = articleList.get(index);
        article.click();
    }

    public void sortBy(String option) {
        WebElement sortByFilterElement = driver.findElement(sortByFilter);
        sortByFilterElement.click();
        Select selectSortByFilter = new Select(sortByFilterElement);
        selectSortByFilter.selectByVisibleText(option);
    }

    public String getArticleTimeStamp() {
        WebElement articleTimeStamp = driver.findElement(searchArticleTimeStamp);
        String articleTimeStampText = articleTimeStamp.getText();
        return articleTimeStampText;
    }

    public boolean noResultsFound() {
        List<WebElement> noResultsElements = driver.findElements(searchedArticles);
        return noResultsElements.size() == 0;
    }

    public String getNoResultsText() {
        WebElement noResultsElement = driver.findElement(noResults);
        return noResultsElement.getText();
    }

    public int getSearchResultsCount() {
        List<WebElement> searchResults = driver.findElements(searchedArticles);
        return searchResults.size();
    }

    public boolean isSearchResultVisible() {
        try {
            return getSearchResultsCount() > 0 || noResultsFound();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasActiveFilter() {
        return currentFilter != null;
    }

    public void filterByDate(String option) {
        WebElement dateFilterElement = driver.findElement(dateFilter);
        Select selectDateFilter = new Select(dateFilterElement);
        selectDateFilter.selectByVisibleText(option);
        currentFilter = option;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public String getSearchResultsHeadline(int index) {
        // Wait for headlines to be present and get all headlines
        List<WebElement> headlines = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(articleHeadlines)
        );
        
        if (index >= 0 && index < headlines.size()) {
            return headlines.get(index).getText();
        } else {
            throw new IndexOutOfBoundsException("Article index " + index + " is out of bounds. Total articles: " + headlines.size());
        }
    }

    public String getArticleUrl(int index) {
        List<WebElement> articles = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(articleHeadlines)
        );
        
        if (index >= 0 && index < articles.size()) {
            return articles.get(index).getAttribute("href");
        } else {
            throw new IndexOutOfBoundsException("Article index " + index + " is out of bounds. Total articles: " + articles.size());
        }
    }
}

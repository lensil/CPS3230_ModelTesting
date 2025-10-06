package abc.tests;

import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import java.time.Duration;

import static org.junit.Assert.*;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import abc.pageobjects.ArticlePageObject;
import abc.pageobjects.HomepagePageObject;
import abc.pageobjects.SearchPageObject;



public class AbcNewsModelTest implements FsmModel {
    // Update states to match the diagram exactly
    public enum State {
        HOMEPAGE,           // Starting state
        ARTICLE_PAGE,       // After clicking article
        SEARCH_PAGE,        // After performing search
        FILTERED_SEARCH_PAGE // After filtering results
    }
    
    private State currentState; // Track current state of the FSM
    private WebDriver driver;  // WebDriver instance for browser automation
    private WebDriverWait wait; // Wait instance for WebDriver

    // Page objects for the ABC News website to interact with the UI
    private HomepagePageObject homePage; // Page object for the homepage
    private ArticlePageObject articlePage; // Page object for the article page
    private SearchPageObject searchPage; // Page object for the search results page

    @Override
    // Return the current state of the finite state machine
    public State getState() {
        if (currentState == null) {
            // If somehow our state tracking got lost, we should assume we're on the homepage
            // since that's our starting state
            currentState = State.HOMEPAGE;
        }
        
        // Return the current state of our finite state machine
        return currentState;
    }

    @Override
    // Reset the finite state machine to its initial state
    public void reset(boolean testing) {
        currentState = State.HOMEPAGE; // Reset to starting state to the homepage since it is our starting state
        
        if (testing) {
            try {
                // Clean up existing WebDriver instance if it exists
                if (driver != null) {
                    driver.quit();
                }
                
                // Create new WebDriver instance
                driver = new ChromeDriver();
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                driver.manage().window().maximize();
                
                // Navigate to starting page
                driver.get("https://abcnews.go.com");
                
                // Initialize page objects
                homePage = new HomepagePageObject(driver);
                articlePage = new ArticlePageObject(driver);
                searchPage = new SearchPageObject(driver, wait);
                
                // Verify initial state
                assertTrue("Should start on homepage", 
                        driver.getCurrentUrl().equals("https://abcnews.go.com/"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize test environment", e);
            }
        }
    }

    // Article guard
    // Article can only be clicked from the homepage
    public boolean clickArticleGuard() {
        return currentState == State.HOMEPAGE;
    }
    
    @Action
    // Click on an article from the homepage
    public void clickArticle() {

        // Check if we can click an article
        if (!clickArticleGuard()) {
            throw new IllegalStateException(
                "Cannot click article from state " + currentState
            );
        }

        // Get the title of the article that is displayed on the homepage
        String articleTitle = homePage.getArticleHeadingIndex(7);
        homePage.clickOnArticle(7);

        // Transition to ArticlePage state
        currentState = State.ARTICLE_PAGE;

        // Wait for the article title element to appear on the Article Page
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".vMjAx.gjbzK.tntuS.eHrJ.mTgUP")
        ));

        // Verify we reached the correct article and perform tests
        assertTrue("Article page should be fully loaded", 
              articlePage.isArticleContentVisible());
        assertEquals("Article title should match",
                    articleTitle,
                    articlePage.getArticleTitle());
    }

    // Return from article to home guard
    // Can only return from article to home from the article page
    public boolean returnFromArticleToHomeGuard() {
        return currentState == State.ARTICLE_PAGE;
    }
    
    @Action
    // Return from article to home
    public void returnFromArticleToHome() {
        
        // Check if we can return from article to home
        if (!returnFromArticleToHomeGuard()) {
            throw new IllegalStateException(
                "Cannot return from article to home from state " + currentState
            );
        }

        // Click on the homepage link to return to the homepage
        homePage.clickOnHomePageLink();
        
        
        // Wait for the homepage to load 
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div.liAe.uMOq.zYIfP")  
        ));
        wait.until(ExpectedConditions.urlToBe("https://abcnews.go.com/"));
        
        currentState = State.HOMEPAGE; // Transition back to homepage state

        // Verify we're back on the homepage and perform tests
        assertTrue("Should be on homepage", 
                driver.getCurrentUrl().equals("https://abcnews.go.com/"));
    }

    // Search from article guard
    // Can only search from the article page
    public boolean searchFromArticleGuard() {
        return currentState == State.ARTICLE_PAGE;
    }

    @Action
    // Search from article  
    public void searchFromArticle() {
        // Check if we can search from article
        if (!searchFromArticleGuard()) {
            throw new IllegalStateException(
                "Cannot search from article from state " + currentState
            );
        }

        String searchTerm = "manslaughter"; // Search term to use
        
        // Perform the search
        homePage.searchFor(searchTerm);
        
        // Wait for search results to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".ContentRoll__Item")
        ));
        
        currentState = State.SEARCH_PAGE; // Transition to SearchPage state
        
        // Verify search results and perform tests
        assertTrue("Search term should be in URL",
                  driver.getCurrentUrl().contains(searchTerm));
        assertTrue("Search should return at least one result",
                  searchPage.getSearchResultsCount() > 0);    
    }


    // Search guard
    // Can only search from the homepage    
    public boolean searchGuard() {
        return currentState == State.HOMEPAGE;
    }
    
    @Action
    public void search() {

        // Check if we can search
        if (!searchGuard()) {
            throw new IllegalStateException(
                "Cannot search from state " + currentState
            );
        }

        String searchTerm = "manslaughter"; // Search term to use
        
        // Perform the search
        homePage.searchFor(searchTerm);
        
        // Wait for search results to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".ContentRoll__Item")
        ));
        
        currentState = State.SEARCH_PAGE; // Transition to SearchPage state
        
        // Verify search results and perform tests
        assertTrue("Search term should be in URL",
                  driver.getCurrentUrl().contains(searchTerm));
        assertTrue("Search should return at least one result",
                  searchPage.getSearchResultsCount() > 0);    
    }

    // Return from search to home guard
    // Can only return from search to home from the search page
    public boolean returnFromSearchToHomeGuard() {
        return currentState == State.SEARCH_PAGE;
    }

    @Action
    // Return from search to home
    public void returnFromSearchToHome() {
        // Check if we can return from search to home
        if (!returnFromSearchToHomeGuard()) {
            throw new IllegalStateException(
                "Cannot return from search to home from state " + currentState
            );
        }

        // Click on the homepage link to return to the homepage
        homePage.clickOnHomePageLink();
        
        // Wait for the homepage to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div.liAe.uMOq.zYIfP")  
        ));
        wait.until(ExpectedConditions.urlToBe("https://abcnews.go.com/"));
        
        currentState = State.HOMEPAGE; // Transition back to homepage state

        // Verify we're back on the homepage and perform tests
        assertTrue("Should be on homepage", 
                driver.getCurrentUrl().equals("https://abcnews.go.com/"));
    }

    // Click article from search guard
    // Can only click article from search from the search page
    public boolean clickArticleFromSearchGuard() {
        return currentState == State.SEARCH_PAGE;
    }

    @Action
    // Click article from search
    public void clickArticleFromSearch() {

        // Check if we can click article from search
        if (!clickArticleFromSearchGuard()) {
            throw new IllegalStateException(
                "Cannot click article from search from state " + currentState
            );
        }

        // Get the title of the article that is displayed on the search page
        String articleTitle = searchPage.getSearchResultsHeadline(1);

        // Using the new clickOnArticle method which now properly targets the headline link
        String articeURl = searchPage.getArticleUrl(1);
        driver.get(articeURl);

        // Transition to ArticlePage state
        currentState = State.ARTICLE_PAGE;

        // Wait for the article title element to appear on the Article Page
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".vMjAx.gjbzK.tntuS.eHrJ.mTgUP")
        ));

        // Verify we reached the correct article and perform tests
        assertTrue("Article page should be fully loaded",
            articlePage.isArticleContentVisible());
        assertEquals("Article title should match",
            articleTitle,
            articlePage.getArticleTitle());
    }

    // Filter results guard
    // Can only filter results from the search page
    public boolean filterResultsGuard() {
        return currentState == State.SEARCH_PAGE;
    }
    
    @Action
    // Filter search results
    public void filterResults() {
        // Store initial count for comparison
        int initialCount = searchPage.getSearchResultsCount();
            
        // Perform the filtering
        searchPage.filterByDate("Last Week");
            
        currentState = State.FILTERED_SEARCH_PAGE; // Transition to FilteredSearchPage state
            
        // Verify filtering changed the results and perform tests
        int finalCount = searchPage.getSearchResultsCount();
        assertTrue("Filter should change number of results",
                      finalCount <= initialCount);
    }


    // Return from filtered search to home guard
    // Can only return from filtered search to home from the filtered search page
    public boolean returnFromFilteredSearchToHomeGuard() {
        return currentState == State.FILTERED_SEARCH_PAGE;
    }
    
    @Action
    // Return from filtered search to home
    public void returnFromFilteredSearchToHome() {
        // Use homepage link to return to homepage
        homePage.clickOnHomePageLink();

        // Transition back to HomePage state
        currentState = State.HOMEPAGE;

        // Verify we're back on homepage and perform tests
        assertTrue("Should be on homepage", 
                   driver.getCurrentUrl().equals("https://abcnews.go.com/"));
    }


    // Click article from filtered search guard
    // Can only click article from filtered search from the filtered search page
    public boolean clickArticleFromFilteredSearchGuard() {
        return currentState == State.FILTERED_SEARCH_PAGE;
    }

    @Action
    // Click article from filtered search
    public void clickArticleFromFilteredSearch() {
        // Get the title of the article that is displayed on the search page
        String articleTitle = searchPage.getSearchResultsHeadline(1);

        // Using the new clickOnArticle method which now properly targets the headline link
        String articeURl = searchPage.getArticleUrl(1);
        driver.get(articeURl);

        // Transition to ArticlePage state
        currentState = State.ARTICLE_PAGE;

        // Wait for the article title element to appear on the Article Page
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".vMjAx.gjbzK.tntuS.eHrJ.mTgUP")
        ));

        // Verify we reached the correct article and perform tests
        assertTrue("Article page should be fully loaded",
            articlePage.isArticleContentVisible());
        assertEquals("Article title should match",
            articleTitle,
            articlePage.getArticleTitle());
    }

    @Test
    // Test the model using the GreedyTester
    public void testModel() {
        Tester tester = new GreedyTester(new AbcNewsModelTest()); // Create a new GreedyTester
        tester.setRandom(new Random()); // Use random seed for random testing
        tester.buildGraph(); // Build the graph
        
        // Add listeners to the tester so that it can report on the testing process
        tester.addListener(new VerboseListener());
        tester.addListener(new StopOnFailureListener()); 
        
        // Add detailed coverage metrics
        tester.addCoverageMetric(new TransitionCoverage());
        tester.addCoverageMetric(new StateCoverage());
        tester.addCoverageMetric(new ActionCoverage());
        
        // Generate test cases
        tester.generate(30);
        
        // Print detailed coverage metrics
        tester.printCoverage();
    }

}


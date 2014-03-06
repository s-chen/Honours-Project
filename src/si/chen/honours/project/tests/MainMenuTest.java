package si.chen.honours.project.tests;

import com.robotium.solo.Solo;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.MainMenu;
import android.test.ActivityInstrumentationTestCase2;

/**
 * JUnit test case using Robotium framework - MainMenuTest
 * Clicks on each item from Main Menu and check that the correct activity is opened
 *
 */
public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenu> {

	private Solo solo;


	public MainMenuTest() {
		super(MainMenu.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		//setUp() is run before a test case is started. 
		//This is where the solo object is created.
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
		//tearDown() is run after a test case has finished. 
		//finishOpenedActivities() will finish all the activities that have been opened during the test execution.
		solo.finishOpenedActivities();
	}
	
	public void testClickTouristAttractions() {
		solo.clickOnMenuItem("Tourist Attractions");
		//Assert that Attractions activity is opened
		solo.assertCurrentActivity("Expected Attractions activity", "Attractions");
		solo.goBack();
	}
	
	public void testClickFood() {
		solo.clickOnMenuItem("Food");
		//Assert that Restaurants activity is opened
		solo.assertCurrentActivity("Expected Restaurants activity", "Restaurants"); 
		solo.goBack();
	}
	
	public void testClickDrinks() {
		solo.clickOnMenuItem("Drinks");
		//Assert that Drinks activity is opened
		solo.assertCurrentActivity("Expected Drinks activity", "Drinks");
		solo.goBack();
	}
	
	public void testClickAccommodation() {
		solo.clickOnMenuItem("Accommodation");
		//Assert that Drinks activity is opened
		solo.assertCurrentActivity("Expected Accommodation activity", "Accommodation"); 
		solo.goBack();
	}
	
	public void testClickShopping() {
		solo.clickOnMenuItem("Shopping");
		//Assert that Shops activity is opened
		solo.assertCurrentActivity("Expected Shops activity", "Shops"); 
		solo.goBack();
	}
	
	public void testClickNearbyPlaces() {
		solo.clickOnMenuItem("Nearby Places");
		//Assert that DisplayNearbyPlaces activity is opened
		solo.assertCurrentActivity("Expected DisplayNearbyPlaces activity", "DisplayNearbyPlaces"); 
		solo.goBack();
	}
	
	public void testClickItineraryPlanner() {
		solo.clickOnMenuItem("Itinerary Planner");
		//Assert that ItineraryPlanner activity is opened
		solo.assertCurrentActivity("Expected ItineraryPlanner activity", "ItineraryPlanner"); 
		solo.goBack();
	}
	
	public void testClickMap() {
		solo.clickOnActionBarItem(R.id.action_maps);
		//Assert that Map activity is opened
		solo.assertCurrentActivity("Expected Map activity", "MapActivity"); 
		solo.goBack();
	}
	
	public void testClickLogin() {
		solo.clickOnActionBarItem(R.id.action_log_in);
		//Assert that FacebookLogin activity is opened
		solo.assertCurrentActivity("Expected Facebook Login activity", "FacebookLogin"); 
		solo.goBack();
	}
	

}

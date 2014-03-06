package si.chen.honours.project.tests;

import java.util.Random;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.Restaurants;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - RestaurantsTest
 * Clicks on items from ListView under 'Food' section
 */
public class RestaurantsTest extends ActivityInstrumentationTestCase2<Restaurants> {

	private Solo solo;


	public RestaurantsTest() {
		super(Restaurants.class);
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
	
	public void testClickRestaurantListItemTop() {
		
		// Click on top item in ListView
		solo.clickInList(1);
		
		// Assert correct list item selected
		assertTrue(solo.searchText("102"));
		solo.goBack();
	}
	
	// Search for place items, click on item and verify the correct item is selected
	public void testClickRestaurantListItemAfterSearch() {
		
		// Enter the place id for "A Room at the West End" in EditText
		solo.enterText(0, "1016.");
		// Select item
		solo.clickInList(1);
		// Assert correct item selected
		assertTrue(solo.searchText("A Room at the West End"));
		solo.goBack();
		
		// For "Beirut" list item
		solo.enterText(0, "676.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Beirut"));
		solo.goBack();
		
		// For "Chop Chop" list item
		solo.enterText(0, "418.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Chop Chop"));
	}
	
	public void testRandomClickRestaurantListItem() {
		
		// Generate random number from range 1-100
		Random rand = new Random();
		int random_item_position = rand.nextInt(2) + 99;
		
		// Scroll to random list item position
		solo.scrollListToLine(0, random_item_position);
		// Click on item
		solo.clickInList(1);
		// Assert the resulting view for when map is displayed
		View restaurant_info = solo.getView(R.id.restaurant_map);
		assertTrue(restaurant_info.isShown());
	}

	

}

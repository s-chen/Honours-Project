package si.chen.honours.project.tests;

import java.util.Random;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.Shops;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - ShopsTest
 * Clicks on items from ListView under 'Shopping' section
 */
public class ShopsTest extends ActivityInstrumentationTestCase2<Shops> {

	private Solo solo;


	public ShopsTest() {
		super(Shops.class);
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
	
	public void testClickShopsListItemTop() {
		
		// Click on top item in ListView
		solo.clickInList(1);
		
		// Assert correct list item selected
		assertTrue(solo.searchText("106"));
		solo.goBack();
	}
	
	// Search for place items, click on item and verify the correct item is selected
	public void testClickShopsListItemAfterSearch() {
		
		// Enter the place id for "BHS" in EditText
		solo.enterText(0, "1048.");
		// Select item
		solo.clickInList(1);
		// Assert correct item selected
		assertTrue(solo.searchText("BHS"));
		solo.goBack();
		
		// For "Debenhams" list item
		solo.enterText(0, "931.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Debenhams"));
		solo.goBack();
		
		// For "John Lewis" list item
		solo.enterText(0, "1182.");
		solo.clickInList(1);
		assertTrue(solo.searchText("John Lewis"));
	}
	
	public void testRandomClickShopsListItem() {
		
		// Generate random number from range 1-100
		Random rand = new Random();
		int random_item_position = rand.nextInt(2) + 99;
		
		// Scroll to random list item position
		solo.scrollListToLine(0, random_item_position);
		// Click on item
		solo.clickInList(1);
		// Assert the resulting view for when map is displayed
		View shops_info = solo.getView(R.id.shop_map);
		assertTrue(shops_info.isShown());
		
	}


}

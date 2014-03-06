package si.chen.honours.project.tests;

import java.util.Random;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.Drinks;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - DrinksTest
 * Clicks on items from ListView under 'Drinks' section
 */
public class DrinksTest extends ActivityInstrumentationTestCase2<Drinks> {

	private Solo solo;


	public DrinksTest() {
		super(Drinks.class);
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
	
	public void testClickDrinksListItemTop() {
		
		// Click on top item in ListView
		solo.clickInList(1);
		
		// Assert correct list item selected
		assertTrue(solo.searchText("1780"));
		solo.goBack();
	}
	
	// Search for place items, click on item and verify the correct item is selected
	public void testClickDrinksListItemAfterSearch() {
		
		// Enter the place id for "The Auld Hoose" in EditText
		solo.enterText(0, "80.");
		// Select item
		solo.clickInList(1);
		// Assert correct item selected
		assertTrue(solo.searchText("The Auld Hoose"));
		solo.goBack();
		
		// For "Pear Tree" list item
		solo.enterText(0, "200.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Pear Tree"));
		solo.goBack();
		
		// For "Drouthy Neebors" list item
		solo.enterText(0, "332.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Drouthy Neebors"));
	}
	
	public void testRandomClickDrinksListItem() {
		
		// Generate random number from range 1-100
		Random rand = new Random();
		int random_item_position = rand.nextInt(2) + 99;
		
		// Scroll to random list item position
		solo.scrollListToLine(0, random_item_position);
		// Click on item
		solo.clickInList(1);
		// Assert the resulting view for when map is displayed
		View drinks_info = solo.getView(R.id.drinks_map);
		assertTrue(drinks_info.isShown());
	}


}

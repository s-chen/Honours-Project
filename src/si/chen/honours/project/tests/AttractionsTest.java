package si.chen.honours.project.tests;

import java.util.Random;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.Attractions;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - AttractionsTest
 * Clicks on items from ListView under 'Tourist Attractions' section
 */
public class AttractionsTest extends ActivityInstrumentationTestCase2<Attractions> {

	private Solo solo;


	public AttractionsTest() {
		super(Attractions.class);
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
	
	public void testClickAttractionListItemTop() {
		
		// Click on top item in ListView
		solo.clickInList(1);
		
		// Assert correct list item selected
		assertTrue(solo.searchText("6 Times"));
		solo.goBack();
	}
	
	// Search for place items, click on item and verify the correct item is selected
	public void testClickAttractionListItemAfterSearch() {
		
		// Enter the place id for "Our Dynamic Earth" in EditText
		solo.enterText(0, "2058.");
		// Select item
		solo.clickInList(1);
		// Assert correct item selected
		assertTrue(solo.searchText("Our Dynamic Earth"));
		solo.goBack();
		
		// For "Edinburgh Castle" list item
		solo.enterText(0, "341.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Edinburgh Castle"));
		solo.goBack();
		
		// For "Edinburgh Zoo" list item
		solo.enterText(0, "2057.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Edinburgh Zoo"));
	}
	
	public void testRandomClickAttractionListItem() {
		
		// Generate random number from range 1-100
		Random rand = new Random();
		int random_item_position = rand.nextInt(2) + 99;
		
		// Scroll to random list item position
		solo.scrollListToLine(0, random_item_position);
		// Click on item
		solo.clickInList(1);
		// Assert the resulting view for when map is displayed
		View attraction_info = solo.getView(R.id.attractions_map);
		assertTrue(attraction_info.isShown());
	}

	

}

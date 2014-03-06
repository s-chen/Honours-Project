package si.chen.honours.project.tests;

import java.util.Random;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.Accommodation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - AccommodationTest
 * Clicks on items from ListView under 'Accommodation' section
 */
public class AccommodationTest extends ActivityInstrumentationTestCase2<Accommodation> {

	private Solo solo;


	public AccommodationTest() {
		super(Accommodation.class);
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
	
	public void testClickAccommodationListItemTop() {
		
		// Click on top item in ListView
		solo.clickInList(1);
		
		// Assert correct list item selected
		assertTrue(solo.searchText("Hotel Ceilidh-Donia"));
		solo.goBack();
	}
	
	// Search for place items, click on item and verify the correct item is selected
	public void testClickAccommodationListItemAfterSearch() {
		
		// Enter the place id for "Albert Hotel" in EditText
		solo.enterText(0, "113.");
		// Select item
		solo.clickInList(1);
		// Assert correct item selected
		assertTrue(solo.searchText("Albert Hotel"));
		solo.goBack();
		
		// For "Apex Edinburgh City" list item
		solo.enterText(0, "349.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Apex Edinburgh City"));
		solo.goBack();
		
		// For "Travelodge Princes Street" list item
		solo.enterText(0, "1970.");
		solo.clickInList(1);
		assertTrue(solo.searchText("Travelodge Princes Street"));
	}
	
	public void testRandomClickAccommodationListItem() {
		
		// Generate random number from range 1-100
		Random rand = new Random();
		int random_item_position = rand.nextInt(2) + 99;
		
		// Scroll to random list item position
		solo.scrollListToLine(0, random_item_position);
		// Click on item
		solo.clickInList(1);
		// Assert the resulting view for when map is displayed
		View accommodation_info = solo.getView(R.id.accommodation_map);
		assertTrue(accommodation_info.isShown());
	}


}

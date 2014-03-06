package si.chen.honours.project.tests;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.ItineraryPlanner;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - ItineraryPlannerTest
 * Ensure "Delete all itinerary items" button work as intended
 * (deletes all SharedPref data)
 */
public class ItineraryPlannerTest extends ActivityInstrumentationTestCase2<ItineraryPlanner> {
	
	private Solo solo;

	
	public ItineraryPlannerTest() {
		super(ItineraryPlanner.class);
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
	
	// Test for successful deletion of all itinerary items (when button is enabled)
	public void testConfirmDeleteAllItineraryItems() {
		
		ListView lv_itinerary_items = (ListView) solo.getView(R.id.listView_itinerary);
		
		// Click delete button
		solo.clickOnButton("Delete All Itinerary Items");
		// Confirm deletion
		solo.clickOnButton("Delete");
		// Go back to refresh activity
		solo.goBack();
		
		// Assert 0 list items in ListView (all itinerary items deleted)
		assertTrue(lv_itinerary_items.getCount() == 0);
	
	}
	
	// Test for cancel deletion of itinerary items (when button is enabled)
	public void testCancelDeleteAllItineraryItems() {
		
		ListView lv_itinerary_items = (ListView) solo.getView(R.id.listView_itinerary);
		
		// Click delete button
		solo.clickOnButton("Delete All Itinerary Items");
		// Cancel deletion
		solo.clickOnButton("Cancel");
		
		assertTrue(lv_itinerary_items.getCount() > 0);
		solo.goBack();
		
	}

}

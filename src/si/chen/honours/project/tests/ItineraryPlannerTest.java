package si.chen.honours.project.tests;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.ItineraryPlanner;
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
		// Initially cancel deletion
		solo.clickOnButton("Delete");
		// Check adapter used for ListView is not null
		//assertTrue(lv_itinerary_items.getAdapter() != null || !lv_itinerary_items.getAdapter().isEmpty());
		assertNull(lv_itinerary_items.getAdapter());
		
		solo.goBack();
		// Now confirm deletion of itinerary items
		//solo.clickOnButton("Delete All Itinerary Items");
		//solo.clickOnButton("Delete");
		// Check adapter used for ListView is now set to Null (cleared)
		//assertTrue(lv_itinerary_items.getAdapter() == null || lv_itinerary_items.getAdapter().isEmpty());
		//assertNull(lv_itinerary_items.getAdapter());
	}
	
	// Test for cancellation of deletion (when button is enabled)
	public void testCancelDeleteAllItineraryItems() {
		
		ListView lv_itinerary_items = (ListView) solo.getView(R.id.listView_itinerary);
		// Now confirm deletion of itinerary items
		solo.clickOnButton("Delete All Itinerary Items");
		solo.clickOnButton("Cancel");
		// Check adapter used for ListView is now set to Null (cleared)
				//assertTrue(lv_itinerary_items.getAdapter() == null || lv_itinerary_items.getAdapter().isEmpty());
		assertNotNull(lv_itinerary_items.getAdapter());
		solo.goBack();
		
	}
	

}

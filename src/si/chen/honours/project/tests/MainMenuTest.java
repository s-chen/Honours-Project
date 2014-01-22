package si.chen.honours.project.tests;

import si.chen.honours.project.ui.MainMenu;
import android.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenu> {
	
	private MainMenu mMainMenu;
	private Button mButtonFood;

	public MainMenuTest() {
		super(MainMenu.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mMainMenu = getActivity();
		//mButtonFood = (Button) mMainMenu.findViewById(R.string)
	}
	
	public void testPreconditions() {
	    assertNotNull("mMainMenu is null", mMainMenu);
	}
	
	public void testButton_labelText() {
		
	}

}

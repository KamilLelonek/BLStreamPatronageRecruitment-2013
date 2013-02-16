package fourth.task.android;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class FourthTaskAndroid extends Activity implements ActionBar.TabListener {
	private ViewPager mViewPager;
	private Fragment currentFragment;
	
	//private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_fourth_task_android);
		
		// Adapter which returns appropriate fragment when tabs are being switched
		//mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
		
		// Specify that we will be displaying tabs in the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Layout manager that allows the user to flip left and right through pages of data
		//		mViewPager = (ViewPager) findViewById(R.id.pager);
		//		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		//		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
		//			@Override public void onPageSelected(int position) {
		//				actionBar.setSelectedNavigationItem(position);
		//			}
		//		});
		
		// Adding tabs to action bar for each page from adapter
		//		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
		// Create a tab with text corresponding to the page title defined by the adapter.
		// Also specify TabListener for tab when it is selected.
		actionBar.addTab(actionBar.newTab().setText("1").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("2").setTabListener(this));
		//		}
	}
	
	@Override public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When tab is selected, switch to the corresponding page in the ViewPager.
		//		mViewPager.setCurrentItem(tab.getPosition());
		switch (tab.getPosition()) {
			case 0:
				currentFragment = new ListViewFragment();
				ft.replace(android.R.id.content, currentFragment);
				break;
			case 1:
				currentFragment = new PreferencesFragment();
				ft.replace(android.R.id.content, currentFragment);
				break;
		}
	}
	
	@Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	@Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.detach(currentFragment);
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_fourth_task_android_activity, menu);
		return true;
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				return true;
			case R.id.menu_preferences:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/* FragmentPagerAdapter to facilitate different fragments, and tabs' titles. */
	//	private class AppSectionsPagerAdapter extends FragmentPagerAdapter {
	//		public AppSectionsPagerAdapter(FragmentManager fm) {
	//			super(fm);
	//		}
	//		
	//		/* Depends on tabs position ListViewFragment and MapViewFragment will be
	//		 * held in first and second respectively */
	//		@Override public Fragment getItem(int i) {
	//			switch (i) {
	//				case 0:
	//					return new ListViewFragment();
	//				case 1:
	//					return new MapViewFragment();
	//				default:
	//					return null;
	//			}
	//		}
	//		
	//		/* Identifying tabs by specific title for map and list */
	//		@Override public CharSequence getPageTitle(int position) {
	//			switch (position) {
	//				case 0:
	//					return getString(R.string.label_list);
	//				case 1:
	//					return getString(R.string.label_map);
	//				default:
	//					return "";
	//			}
	//		}
	//		
	//		@Override public int getCount() {
	//			return 2;
	//		}
	//	}
}
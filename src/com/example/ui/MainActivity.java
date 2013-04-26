package com.example.ui;
import com.example.test_app.*;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import com.actionbarsherlock.app.SherlockFragmentActivity;
public class MainActivity extends SherlockFragmentActivity {
	private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private boolean mDualPane;
    
    //private final static int TAB_ID_DIALER = 0;
    private final static int TAB_ID_CALL = 1;
    private final static int TAB_ID_CONTACTS = 2;
    private final static int TAB_ID_LOG = 3;
    private final static int TAB_ID_SETTINGS = 4;
    
    // This will save persistent data
    // private PreferencesProviderWrapper prefProviderWrapper;
    /**
     * Listener interface for Fragments accommodated in {@link ViewPager}
     * enabling them to know when it becomes visible or invisible inside the
     * ViewPager.
     */
    public interface ViewPagerVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		final ActionBar actionbar = getSupportActionBar();
		
		
		
		// removing the title bar that shows icon and app. name from actionBar
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		//
		
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		Tab callTab = actionbar.newTab()
                .setText("Call");
       Tab contactsTab = actionbar.newTab()
                .setText("Contacts");
       Tab logTab = actionbar.newTab()
               .setText("Log");
       Tab settingsTab = actionbar.newTab()
               .setText("Settings");
       mViewPager = (ViewPager) findViewById(R.id.pager);
       mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);
       mTabsAdapter.addTab(callTab, CallFrag.class, 1);
       mTabsAdapter.addTab(contactsTab, ContactsFrag.class, 2);
       mTabsAdapter.addTab(logTab, LogFrag.class, 3);
       mTabsAdapter.addTab(settingsTab, SettingsFrag.class, 4);
       
	}
	
	   private class TabsAdapter extends FragmentPagerAdapter implements
       ViewPager.OnPageChangeListener, ActionBar.TabListener {
   private final Context mContext;
   private final ActionBar mActionBar;
   private final ViewPager mViewPager;
   private final List<String> mTabs = new ArrayList<String>();
   private final List<Integer> mTabsId = new ArrayList<Integer>();
   private boolean hasClearedDetails = false;
   

   private int mCurrentPosition = -1;
   /**
    * Used during page migration, to remember the next position
    * {@link #onPageSelected(int)} specified.
    */
   private int mNextPosition = -1;

   public TabsAdapter(FragmentActivity activity, ActionBar actionBar, ViewPager pager) {
       super(activity.getSupportFragmentManager());
       mContext = activity;
       mActionBar = actionBar;
       mViewPager = pager;
       mViewPager.setAdapter(this);
       mViewPager.setOnPageChangeListener(this);
   }

   public void addTab(ActionBar.Tab tab, Class<?> clss, int tabId) {
       mTabs.add(clss.getName());
       mTabsId.add(tabId);
       mActionBar.addTab(tab.setTabListener(this));
       notifyDataSetChanged();
   }
   
   public void removeTabAt(int location) {
       mTabs.remove(location);
       mTabsId.remove(location);
       mActionBar.removeTabAt(location);
       notifyDataSetChanged();
   }
   
   public Integer getIdForPosition(int position) {
       if(position >= 0 && position < mTabsId.size()) {
           return mTabsId.get(position);
       }
       return null;
   }
   
   public Integer getPositionForId(int id) {
       int fPos = mTabsId.indexOf(id);
       if(fPos >= 0) {
           return fPos;
       }
       return null;
   }

   @Override
   public int getCount() {
       return mTabs.size();
   }

   @Override
   public Fragment getItem(int position) {
       return Fragment.instantiate(mContext, mTabs.get(position), new Bundle());
   }

   @Override
   public void onTabSelected(Tab tab, FragmentTransaction ft) {
       clearDetails();
       if (mViewPager.getCurrentItem() != tab.getPosition()) {
           mViewPager.setCurrentItem(tab.getPosition(), true);
       }
   }

   @Override
   public void onPageSelected(int position) {
       mActionBar.setSelectedNavigationItem(position);

       if (mCurrentPosition == position) {
           Log.d("ch3ar", "Previous position and next position became same (" + position
                   + ")");
       }

       mNextPosition = position;
   }

   @Override
   public void onTabReselected(Tab tab, FragmentTransaction ft) {
       // Nothing to do
   }

   @Override
   public void onTabUnselected(Tab tab, FragmentTransaction ft) {
       // Nothing to do
   }

   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       // Nothing to do
   }

   /*
    * public void setCurrentPosition(int position) { mCurrentPosition =
    * position; }
    */

   @Override
   public void onPageScrollStateChanged(int state) {
       switch (state) {
           case ViewPager.SCROLL_STATE_IDLE: {
               if (mCurrentPosition >= 0) {
                   sendFragmentVisibilityChange(mCurrentPosition, false);
               }
               if (mNextPosition >= 0) {
                   sendFragmentVisibilityChange(mNextPosition, true);
               }
               supportInvalidateOptionsMenu();

               mCurrentPosition = mNextPosition;
               break;
           }
           case ViewPager.SCROLL_STATE_DRAGGING:
               clearDetails();
               hasClearedDetails = true;
               break;
           case ViewPager.SCROLL_STATE_SETTLING:
               hasClearedDetails = false;
               break;
           default:
               break;
       }
   }

   private void clearDetails() {
       if (mDualPane && !hasClearedDetails) {
           FragmentTransaction ft = MainActivity.this.getSupportFragmentManager()
                   .beginTransaction();
          // ft.replace(R.id.details, new Fragment(), null);
           ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
           ft.commit();
       }
   }
}
	   private CallFrag callFrag;
	    private ContactsFrag contactsFrag;
	    private LogFrag logFrag;
	    private SettingsFrag settingsFrag;
	   private Fragment getFragmentAt(int position) {
	        Integer id = mTabsAdapter.getIdForPosition(position);
	        if(id != null) {
	            if (id == 1) {
	                return callFrag;
	            } else if (id == 2) {
	                return contactsFrag;
	            } else if (id == 3) {
	                return logFrag;
	            } else //if (id == 4) {
	                return settingsFrag;
	            /*} /*else if (id == TAB_ID_WARNING) {
	                return mWarningFragment;
	            }*/
	        }
	        throw new IllegalStateException("Unknown fragment index: " + position);
	    }
	   private void sendFragmentVisibilityChange(int position, boolean visibility) {
	        try {
	            final Fragment fragment = getFragmentAt(position);
	            if (fragment instanceof ViewPagerVisibilityListener) {
	                ((ViewPagerVisibilityListener) fragment).onVisibilityChanged(visibility);
	            }
	        }catch(IllegalStateException e) {
	            Log.d("ch3ar", "Fragment not anymore managed");
	        }
	    }
}

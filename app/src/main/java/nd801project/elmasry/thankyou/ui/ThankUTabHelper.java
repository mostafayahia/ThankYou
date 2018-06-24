package nd801project.elmasry.thankyou.ui;

import android.app.Activity;
import android.support.design.widget.TabLayout;

import nd801project.elmasry.thankyou.R;

public class ThankUTabHelper implements TabLayout.OnTabSelectedListener {

    private final TabSelectedCallback mTabSelectedCallback;

    private TabLayout mTabLayout;

    static final int POSITION_TAB_ALL = 0;
    static final int POSITION_TAB_FAV = 1;
    static final int TABS_NUM = 2;

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mTabSelectedCallback.tabSelectedHandler(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    interface TabSelectedCallback {
        void tabSelectedHandler(TabLayout.Tab tab);
    }

    public ThankUTabHelper(Activity activity, TabSelectedCallback tabSelectedCallback, int selectedTabPosition) {
        mTabSelectedCallback = tabSelectedCallback;

        mTabLayout = activity.findViewById(R.id.tab_layout);

        // you **MUST** add tabs before adding the listener
        createAndaddTabs(selectedTabPosition);

        mTabLayout.addOnTabSelectedListener(this);
    }

    private void createAndaddTabs(int selectedTabPosition) {
        TabLayout.Tab[] tabs = new TabLayout.Tab[TABS_NUM];
        TabLayout.Tab allTab = mTabLayout.newTab().setText(R.string.label_all);
        TabLayout.Tab favTab = mTabLayout.newTab().setText(R.string.label_fav);
        tabs[POSITION_TAB_ALL] = allTab;
        tabs[POSITION_TAB_FAV] = favTab;

        for (int i = 0; i < TABS_NUM; i++) {
            TabLayout.Tab tab = tabs[i];
            mTabLayout.addTab(tab, i == selectedTabPosition);
        }
    }

    public int getSelectedTabPosition() {
        return mTabLayout.getSelectedTabPosition();
    }



}

package alberapps.android.tiempobus.infolineas.sliding;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 */
public class InfoLineaFragmentPagerAdapter extends FragmentPagerAdapter {

    Context context;

    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        //private final Bundle args;

        TabInfo(String _tag, Class<?> _class) {
            tag = _tag;
            clss = _class;
            //args = _args;
        }
    }

    public void addTab(String _tag, Class<?> _class) {
        TabInfo info = new TabInfo(_tag, _class);
        mTabs.add(info);
        notifyDataSetChanged();
    }


    public InfoLineaFragmentPagerAdapter(FragmentManager fm, Context context1) {
        super(fm);
        context = context1;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);

        return mTabs.get(position).tag;

    }

    @Override
    public Fragment getItem(int position) {
        //return ArrayListFragment.newInstance(position);

        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(context, info.clss.getName());



    }
}
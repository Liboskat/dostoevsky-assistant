package rizvanov.dostoevskyassistant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import rizvanov.dostoevskyassistant.fragment_epilepsy.EpilepsyFragment;
import rizvanov.dostoevskyassistant.fragment_notes.NoteList;

/**
 * Created by Ильшат on 11.07.2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    private int tabCount;
    //Titles for pager's tabs
    private String[] tabsTitles = new String[] {"Tab 1", "Tab 2", "Tab 3"};

    //Constructor to the class
    public PagerAdapter(FragmentManager fm) {
        super(fm);
        //Initializing tab count
        this.tabCount= 3;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                return new NoteList();
            case 1:
                return new NoteList();
            case 2:
                return new EpilepsyFragment();
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

    //Set tabs' titles
    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }
}

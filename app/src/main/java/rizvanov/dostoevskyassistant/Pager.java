package rizvanov.dostoevskyassistant;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import rizvanov.dostoevskyassistant.CharacterFragment.PoemsFragment;
import rizvanov.dostoevskyassistant.fragment_notes.NotesList;

/**
 * Created by Ильшат on 11.07.2017.
 */

public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    private Activity activity;

    //Constructor to the class
    public Pager(FragmentManager fm, Activity activity) {
        super(fm);
        //Initializing tab count
        this.tabCount= 3;
        this.activity = activity;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                PoemsFragment tab1 = new PoemsFragment(); //образы
                return tab1;
            case 1:
                NotesList tab2 = new NotesList(); //
                return tab2;
            case 2:
                NotesList tab3 = new NotesList(); //эпилепсия
                return tab3;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}

package rizvanov.dostoevskyassistant.fragment_notes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by Ильшат on 14.07.2017.
 */

public class NotesList extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.diary_page_layout, container, false);
    }
}

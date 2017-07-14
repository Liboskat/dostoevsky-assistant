package rizvanov.dostoevskyassistant.CharacterFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by user on 14.07.2017.
 */

public class PoemsFragment  extends Fragment implements PoemsAdapter.PoemsPageOnClickListener {

    private RecyclerView recyclerView;

    private List<Poem> quests;

    private PoemsAdapter adapter;

    private LinearLayoutManager layoutManager;

    public static final String TAG = "TESTFRAGMENT";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reflections_page_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initQuests();

        adapter = new PoemsAdapter(quests,this);

        layoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.reflections_page_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);



    }

    private void initQuests(){
        quests = new ArrayList<>();
        quests.add(new Poem("Poem1"));
        quests.add(new Poem("Poem2"));
        quests.add(new Poem("Poem3"));
        quests.add(new Poem("Poem4"));
        quests.add(new Poem("Poem5"));
        quests.add(new Poem("Poem6"));
        quests.add(new Poem("Poem7"));
        quests.add(new Poem("Poem8"));
        quests.add(new Poem("Poem9"));
        quests.add(new Poem("Poem10"));
    }

    public void addPoem(View view) {

    }

    @Override
    public void openPageCharacter() {
        Intent intent = new Intent(PoemsFragment.this.getActivity(),CommonCharacterActivity.class);
        startActivity(intent);
    }
}

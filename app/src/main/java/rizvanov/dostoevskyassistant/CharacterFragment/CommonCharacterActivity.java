package rizvanov.dostoevskyassistant.CharacterFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by user on 14.07.2017.
 */

public class CommonCharacterActivity extends AppCompatActivity implements CharacterAdapter.CharacterOnClickListener{

    private RecyclerView recyclerView;

    private List<Character> characters;

    private CharacterAdapter adapter;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reflections_characterspage_layout);

        initQuests();

        adapter = new CharacterAdapter(characters,this);

        layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.reflections_characterpage_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initQuests(){
        characters = new ArrayList<>();
        characters.add(new Character("Character1","12.00","editText"));
        characters.add(new Character("Character2","14.00","editText"));
        characters.add(new Character("Character3","14.00","editText"));
        characters.add(new Character("Character4","14.00","editText"));
        characters.add(new Character("Character5","13.00","editText"));
        characters.add(new Character("Character6","14.00","editText"));
        characters.add(new Character("Character7","16.00","editText"));
        characters.add(new Character("Character8","11.00","editText"));
        characters.add(new Character("Character9","10.00","editText"));
        characters.add(new Character("Character10","18.00","editText"));
        characters.add(new Character("Character8","11.00","editText"));
        characters.add(new Character("Character9","10.00","editText"));
        characters.add(new Character("Character10","18.00","editText"));
    }

  //public void AddCharacter(View view) {
  //    characters.add(new Character())
  //}

    @Override
    public void completeCharacter(Character character) {

        Intent intent = new Intent(this, CharacterFull.class);
        String photo = character.getPhoto();
        String name = character.getName();
        String editText = character.getEditText();
        intent.putExtra("photo", photo);
        intent.putExtra("name", name);
        intent.putExtra("editText", editText);
        startActivity(intent);
    }
}

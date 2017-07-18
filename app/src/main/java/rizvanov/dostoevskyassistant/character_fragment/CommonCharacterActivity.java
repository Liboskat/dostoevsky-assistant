package rizvanov.dostoevskyassistant.character_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import rizvanov.dostoevskyassistant.R;

import static rizvanov.dostoevskyassistant.R.id.btnOK;

/**
 * Created by user on 14.07.2017.
 */

public class CommonCharacterActivity extends AppCompatActivity implements CharacterAdapter.CharacterOnClickListener {

    private RecyclerView recyclerView;

    private List<Character> characters;

    private CharacterAdapter adapter;

    private LinearLayoutManager layoutManager;

    private static long count;

    private String namePoem;

    private String FILE_CHARACTERS = "Characters";

    public static String id = "";

    int position;
    private boolean flagDeleted;
    private Type itemsListType;
    private LinkedList<String> idCharsNames;

    private static final String TAG = "myLogs";

    private EditText editTextSearch;
    private boolean flagSearh;

    SharedPreferences sharedPreferences;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reflections_characterspage_layout);

        sharedPreferences = getSharedPreferences(FILE_CHARACTERS, MODE_PRIVATE);
        itemsListType = new TypeToken<List<String>>() {}.getType();

        gson = new Gson();
        this.namePoem = getIntent().getStringExtra("namePoem");
        Log.d(TAG, "namePoem = " + namePoem);
        this.setTitle(namePoem);
        List<String> list = gson.fromJson(sharedPreferences.getString(namePoem + "ListNames",""),itemsListType);

        if(list != null){
            idCharsNames = new LinkedList<>(list);
        }else{
            idCharsNames = new LinkedList<>();
        }
        Log.d(TAG, "idPoemNames.size = " + idCharsNames.size());

        count = Integer.parseInt(sharedPreferences.getString(namePoem + "countChar", "0"));
        Log.d(TAG, "countCharacterActiv = " + count + "");
        Log.d(TAG, "initCharacters");
        if (idCharsNames.size() != 0) {
            initQuests();
        } else {
            characters = new LinkedList<>();
        }

        adapter = new CharacterAdapter(characters, this);
        layoutManager = new LinearLayoutManager(this);

        ImageButton searchBtn = (ImageButton) findViewById(R.id.reflections_page_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPoem();
            }
        });

        editTextSearch = (EditText) findViewById(R.id.reflections_page_search_edit);
        editTextSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editTextSearch.setText("");
                flagSearh = false;
                if(CommonCharacterActivity.this.getCurrentFocus() != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            CommonCharacterActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
             //   InputMethodManager imm = (InputMethodManager)
             //          getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
             //   if(imm != null){
             //       imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
             //   }
                editTextSearch.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.reflections_characterpage_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void searchPoem() {
        editTextSearch.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
        String nameCharacter =  String.valueOf(editTextSearch.getText());
        if(!nameCharacter.equals("")) {
            if (!flagSearh) {
                editTextSearch.setSelection(nameCharacter.length());
                flagSearh = true;
            } else {
                boolean flagFind = false;
                Character character = null;
                int position = 0;
                for (int i = 0; !flagFind && i < characters.size(); i++) {
                    if (nameCharacter.equals(characters.get(i).getName())) {
                        flagFind = true;
                        editTextSearch.setVisibility(View.INVISIBLE);
                        editTextSearch.setText("");
                        character = characters.get(i);
                        position = i;
                    }
                }
                if(flagFind){
                    completeCharacter(character,position);
                }
                flagSearh = true;
                if (!flagFind) {
                    editTextSearch.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "Произведение не найдено", Toast.LENGTH_SHORT).show();
                    editTextSearch.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void initQuests() {
        characters = new LinkedList<>();
        String name = idCharsNames.get(0);
        Log.d(TAG, name);
        Character character = gson.fromJson(sharedPreferences.getString(name, ""), Character.class);
        if (character != null) {
            String characterName = character.getName();
            Log.d(TAG, characterName);
            character.setId(name);
            characters.add(0, character);
            for (int i = 1; i < idCharsNames.size(); i++) {
                name = idCharsNames.get(i);
                Log.d(TAG, "nameInit = " + name);
                character = gson.fromJson(sharedPreferences.getString(name, ""), Character.class);
                if (character != null) {
                    characterName = character.getName();
                    Log.d(TAG, characterName);
                    character.setId(name);
                    characters.add(0, character);
                }
            }
        }
    }

    public void addCharacter(View view) {
        InputMethodManager imm = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            editTextSearch.setText("");
        }
        editTextSearch.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View content = layoutInflater.inflate(R.layout.reflections_material_dialog, null);
        builder.setView(content);
        Button btnOk = (Button) content.findViewById(btnOK);
        Button btnCancel = (Button) content.findViewById(R.id.btnCancel);

        final AlertDialog alertDialog = builder.create();
        builder = null;
        TextView tv_alert = (TextView) content.findViewById(R.id.tv_alert_dialog);
        tv_alert.setText("Название персонажа");
        alertDialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case btnOK:
                        EditText name = (EditText) content.findViewById(R.id.editName);
                        String nameCharacter = String.valueOf(name.getText());
                        Log.d(TAG, nameCharacter);
                        boolean flagHasCharacter = false;
                       for(Character character : characters){
                           if(nameCharacter.equals(character.getName())){
                               flagHasCharacter = true;
                               break;
                           }
                       }
                       if (flagHasCharacter) {
                           Toast.makeText(getApplicationContext(), "Такое название уже есть,введите другое или удалите существующее", Toast.LENGTH_LONG).show();
                           name.setText(nameCharacter);
                           alertDialog.dismiss();
                           alertDialog.show();
                       }else {
                            Character character = new Character("", nameCharacter, "");
                            String gsonCharacter = gson.toJson(character);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            count++;
                            Log.d(TAG, count + "");
                            character.setId(namePoem + "Character" + count);
                            idCharsNames.add(character.getId());
                            editor.putString(character.getId(), gsonCharacter);
                            editor.putString(namePoem + "countChar", String.valueOf(count));
                            editor.apply();
                            characters.add(0, character);
                            completeCharacter(character, 0);
                            alertDialog.dismiss();
                        }
                        break;
                    case R.id.btnCancel:
                        alertDialog.dismiss();
                }
            }
        };
        btnOk.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume" + " = id  = " + id);
        Log.d(TAG, "position = " + position);
        if (!id.equals("")) {
            characters.set(position, gson.fromJson(sharedPreferences.getString(id, ""), Character.class));
            Log.d(TAG,"OnResume_characterName = " + characters.get(position).getName());
            Log.d(TAG,"onResume_idNames.size = " + idCharsNames.size());
            String listNames = gson.toJson(idCharsNames);
            sharedPreferences.edit().putString(namePoem + "ListNames",listNames).apply();

            id = "";
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }
        if (flagDeleted) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            flagDeleted = false;
        }
        super.onResume();
    }



    @Override
    public void completeCharacter(Character character, int position) {
        this.position = position;
        Log.d(TAG, "Complete_position = " + position + "^" + character.getName());
        Intent intent = new Intent(this, CharacterFull.class);
        editTextSearch.setText("");
        editTextSearch.setVisibility(View.GONE);
        String gsonCharacter = gson.toJson(character);
        Log.d(TAG, "CompleteChar = " + character.getId());
        intent.putExtra("character", gsonCharacter);
        String listNames = gson.toJson(idCharsNames);
        intent.putExtra("listNames",listNames);
        intent.putExtra("namePoem",namePoem);
        this.position = position;

       //InputMethodManager imm = (InputMethodManager)
       //        getSystemService(Context.INPUT_METHOD_SERVICE);
       //if(imm != null){
       //    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
       //}

        startActivity(intent);

    }

    @Override
    public boolean changeCharacter(final int adapterPosition) {
      // InputMethodManager imm = (InputMethodManager)
      //         this.getSystemService(Context.INPUT_METHOD_SERVICE);
      // if(imm != null){
      //     imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
      //     editTextSearch.setText("");
      // }
      // editTextSearch.setVisibility(View.INVISIBLE);
        editTextSearch.setText("");
        editTextSearch.setVisibility(View.GONE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View content = layoutInflater.inflate(R.layout.reflections_dialog_change, null);
        builder.setView(content);
        Log.d(TAG,"ChangeAdPos = " + adapterPosition);
        Button btnChange = (Button) content.findViewById(R.id.reflection_change_btn);
        Button btnDelete = (Button) content.findViewById(R.id.reflection_delete_btn);
        Log.d(TAG,"ChangePoem");
        final AlertDialog alertDialog = builder.create();
        final View contentForEditName = layoutInflater.inflate(R.layout.reflections_material_dialog, null);
        final String nameId = characters.get(adapterPosition).getId();
        String name = characters.get(adapterPosition).getName();
        Log.d(TAG,"currentNamePoem = " + nameId);
        TextView tv_change_alert = (TextView) content.findViewById(R.id.reflection_change_dialog);
        tv_change_alert.setText(tv_change_alert.getText() + name + " ?");
        alertDialog.show();
         View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.reflection_change_btn:
                        changeNameCharacter(builder,contentForEditName,adapterPosition, nameId);
                        alertDialog.dismiss();
                        break;
                    case R.id.reflection_delete_btn:
                        removeCharacter(adapterPosition);
                        alertDialog.dismiss();
                }
            }
        };
        btnChange.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        return true;
    }

    private void changeNameCharacter(AlertDialog.Builder builder, final View contentForEditName, final int adapterPosition, final String nameId) {
        final Button btnOK = (Button) contentForEditName.findViewById(R.id.btnOK);
        Button btnCancel = (Button) contentForEditName.findViewById(R.id.btnCancel);
        Log.d(TAG,"changeNameCharacter");
        builder.setView(contentForEditName);
        final AlertDialog alertDialog = builder.create();
        TextView tv_alert = (TextView)contentForEditName.findViewById(R.id.tv_alert_dialog);
        tv_alert.setText("Название персонажа");
        final EditText[] name = {(EditText) contentForEditName.findViewById(R.id.editName)};
        String title = characters.get(adapterPosition).getName();
        name[0].setText(title);
        name[0].setSelection(title.length());
        alertDialog.show();
         View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnOK:
                        name[0] = (EditText) contentForEditName.findViewById(R.id.editName);
                        String characterName = String.valueOf(name[0].getText());
                        boolean flagHasCharacter = false;
                        for(Character character : characters){
                            if(characterName.equals(character.getName())){
                                flagHasCharacter = true;
                                break;
                            }
                        }
                        if (flagHasCharacter) {
                            Toast.makeText(getApplicationContext(), "Такое название уже есть,введите другое или удалите существующее", Toast.LENGTH_LONG).show();
                            name[0].setText(characterName);
                            name[0].setSelection(characterName.length());
                            alertDialog.dismiss();
                            alertDialog.show();
                        }else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Log.d(TAG, "ChangeCharacterAdPos = " + adapterPosition);
                            characters.get(adapterPosition).setName(characterName);
                            String jsonNewName = gson.toJson(characters.get(adapterPosition));
                            editor.putString(nameId, jsonNewName);
                            editor.apply();
                            Log.d(TAG, "changed quests.size = " + characters.size());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(layoutManager);
                            alertDialog.dismiss();
                        }
                            break;

                    case R.id.btnCancel:
                        alertDialog.dismiss();
                }
            }
        };
        btnOK.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    public boolean removeCharacter(int charPosition) {
        if (characters.size() > 0) {
            Log.d(TAG, "REMOVE_character" + characters.size());
            String name = characters.get(charPosition).getId();
            if (characters.remove(charPosition) != null) {
                Log.d(TAG, "really deleted = " + name);
                flagDeleted = true;
                idCharsNames.remove(name);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(name);
                String listNames = gson.toJson(idCharsNames);
                Log.d(TAG,"REMOVE idPoemNames.size = " + idCharsNames.size());
                editor.putString(namePoem + "ListNames",listNames).apply();
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
                return true;
            }
        }
        return false;
    }
}

package rizvanov.dostoevskyassistant.character_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 14.07.2017.
 */

public class PoemsFragment  extends Fragment implements PoemsAdapter.PoemsPageOnClickListener {

    private RecyclerView recyclerView;
    private List<Poem> quests;
    private PoemsAdapter adapter;
    private LinearLayoutManager layoutManager;

    private static int count;
    private View.OnClickListener onClickListener;
    private final String FILE_POEMS = "Poems";

    public static final String TAG = "PoemTAG";

    private Button btnOK;

    Gson gson;
    private Type itemsListType; // для Gson()
    private LinkedList<String> idPoemNames; //id poems

    private boolean flagAdded;
    private boolean flagDeleted;
    private EditText editTextSearch;
    private boolean flagSearh;

    private final String FILE_LIST_NAMES = "poemNames";
    private final String FILE_COUNT_POEMS = "count";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reflections_page_layout,container,false);
        SharedPreferences sPref = this.getActivity().getSharedPreferences(FILE_POEMS,MODE_PRIVATE);
        itemsListType = new TypeToken<List<String>>() {}.getType();
        gson = new Gson();

        List<String> list = gson.fromJson(sPref.getString(FILE_LIST_NAMES,""),itemsListType);
        if(list != null){
            idPoemNames = new LinkedList<>(list);
        }else{
            idPoemNames = new LinkedList<>();
        }
        Log.d(TAG, "idPoemNames.size = " + idPoemNames.size());
        sharedPreferences = this.getActivity().getSharedPreferences(FILE_POEMS,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        count = Integer.parseInt(sPref.getString(FILE_COUNT_POEMS,"0"));
        Log.d(TAG,"poemCount = " + count + "");
        ImageButton addBtn = (ImageButton) view.findViewById(R.id.reflections_page_add_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPoem();
            }
        });
        ImageButton searchBtn = (ImageButton) view.findViewById(R.id.reflections_page_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPoem();
            }
        });


        editTextSearch = (EditText) view.findViewById(R.id.reflections_page_search_edit);
     //  editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
     //      @Override
     //      public void onFocusChange(View view, boolean hasFocused) {
     //         // if(!hasFocused && editTextSearch.getVisibility() == View.VISIBLE && !flagSearh ){
     //          if(!hasFocused ){
     //             // lastSearch = String.valueOf(editTextSearch.getText());
     //              editTextSearch.setVisibility(View.INVISIBLE);
     //          }
     //      }
     //  });

        editTextSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editTextSearch.setVisibility(View.INVISIBLE);
                editTextSearch.setText("");
                if(PoemsFragment.this.getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getContext().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            PoemsFragment.this.getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                flagSearh = false;
                return true;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "initPoems");
        if (idPoemNames.size() != 0) {
            initQuests();
        } else {
            quests = new LinkedList<>();
        }

        adapter = new PoemsAdapter(quests,this);
        layoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.reflections_page_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initQuests(){
        quests = new LinkedList<>();
        String namePoem = idPoemNames.get(0);
        Log.d(TAG,"namePoem = " + namePoem);
        quests.add(0,new Poem(namePoem));
        for (int i = 1; i < idPoemNames.size(); i++) {
            namePoem = idPoemNames.get(i);
            quests.add(0, new Poem(namePoem));
        }

}
    public void searchPoem() {
        editTextSearch.setVisibility(View.VISIBLE);
        String namePoem = String.valueOf(editTextSearch.getText());
        if(!namePoem.equals("")) {
            if (!flagSearh) {
                editTextSearch.setSelection(namePoem.length());
                flagSearh = true;
            } else {
                boolean flagFind = false;
                for (int i = 0; !flagFind && i < quests.size(); i++) {
                    if (namePoem.equals(quests.get(i).getTitle())) {
                        flagFind = true;
                        editTextSearch.setVisibility(View.INVISIBLE);
                        editTextSearch.setText("");
                    }
                }
                if(flagFind){
                    openPageCharacter(namePoem);
                }
                flagSearh = true;
                if (!flagFind) {
                    editTextSearch.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Произведение не найдено", Toast.LENGTH_SHORT).show();
                    editTextSearch.setVisibility(View.VISIBLE);
                }
            }
        }

    }



    public void addPoem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View content = layoutInflater.inflate(R.layout.reflections_material_dialog, null);
        builder.setView(content);
        btnOK = (Button) content.findViewById(R.id.btnOK);
        Button btnCancel = (Button) content.findViewById(R.id.btnCancel);

        final AlertDialog alertDialog = builder.create();
        TextView tv_alert = (TextView)content.findViewById(R.id.tv_alert_dialog);
        tv_alert.setText("Название произведения");
        alertDialog.show();
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnOK:
                        String namePoem = "";
                        EditText name = (EditText) content.findViewById(R.id.editName);
                        namePoem = String.valueOf(name.getText());
                        if (idPoemNames.contains(namePoem)) {
                                Toast.makeText(getContext(), "Такое название уже есть,введите другое или удалите существующее", Toast.LENGTH_LONG).show();
                            name.setText(namePoem);
                            alertDialog.dismiss();
                            alertDialog.show();
                        }else {
                            count++;
                            idPoemNames.add(namePoem);
                            flagAdded = true;
                            Log.d(TAG, "added count = " + count);
                            Log.d(TAG, "added namePoem = " + namePoem);
                            String listNames = new Gson().toJson(idPoemNames);
                            Log.d(TAG, "ADD idPoemNames.size = " + idPoemNames.size());
                            editor.putString(FILE_LIST_NAMES, listNames).apply();
                            editor.putString(FILE_COUNT_POEMS, String.valueOf(count));
                            editor.apply();
                            quests.add(0, new Poem(namePoem));
                            Log.d(TAG, "quests.size = " + quests.size());
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

    @Override
    public void onResume() {
        if(flagDeleted || flagAdded){
            Log.d(TAG,"onResume_idNames.size = " + idPoemNames.size());
            for(String poemNames : idPoemNames){
                Log.d(TAG,"OnResume_poemNmaes = " + poemNames);
            }
            String listNames = gson.toJson(idPoemNames);
            editor.putString(FILE_LIST_NAMES,listNames).apply();

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }
        super.onResume();
    }

    @Override
    public void openPageCharacter(String title) {
        Intent intent = new Intent(PoemsFragment.this.getActivity(),CommonCharacterActivity.class);
        intent.putExtra("namePoem",title);
        startActivity(intent);
    }

    @Override
    public boolean changePoem(final int adapterPosition) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View content = layoutInflater.inflate(R.layout.reflections_dialog_change, null);
        builder.setView(content);
        Log.d(TAG,"ChangeAdPos = " + adapterPosition);
        Button btnChange = (Button) content.findViewById(R.id.reflection_change_btn);
        Button btnDelete = (Button) content.findViewById(R.id.reflection_delete_btn);
        Log.d(TAG,"ChangePoem");
        final AlertDialog alertDialog = builder.create();
        final View contentForEditName = layoutInflater.inflate(R.layout.reflections_material_dialog, null);
        final String namePoem = quests.get(adapterPosition).getTitle();
        Log.d(TAG,"currentNamePoem = " + namePoem);
        TextView tv_change_alert = (TextView) content.findViewById(R.id.reflection_change_dialog);
        tv_change_alert.setText(tv_change_alert.getText() + namePoem + " ?");
        alertDialog.show();
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.reflection_change_btn:
                        changeNamePoem(builder,contentForEditName,adapterPosition,namePoem);
                        alertDialog.dismiss();
                        break;
                    case R.id.reflection_delete_btn:
                        deletePoem(adapterPosition,namePoem);
                        alertDialog.dismiss();
                }
            }
        };
        btnChange.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        return true;
    }

    public void changeNamePoem(AlertDialog.Builder builder, final View contentForEditName, final int adapterPosition, String namePoem){
        btnOK = (Button) contentForEditName.findViewById(R.id.btnOK);
        Button btnCancel = (Button) contentForEditName.findViewById(R.id.btnCancel);
        Log.d(TAG,"changeNamePoem");
        builder.setView(contentForEditName);
        final AlertDialog alertDialog = builder.create();
        final String lastName = namePoem;
        TextView tv_alert = (TextView)contentForEditName.findViewById(R.id.tv_alert_dialog);
        tv_alert.setText("Название произведения");
        final EditText[] name = {(EditText) contentForEditName.findViewById(R.id.editName)};
        String title = quests.get(adapterPosition).getTitle();
        name[0].setText(title);
        name[0].setSelection(title.length());
        alertDialog.show();
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnOK:
                        name[0] = (EditText) contentForEditName.findViewById(R.id.editName);
                        String namePoem = String.valueOf(name[0].getText());
                        if (idPoemNames.contains(namePoem)) {
                            Toast.makeText(getContext(), "Такое название уже есть,введите другое или удалите существующее", Toast.LENGTH_LONG).show();
                            name[0].setText(namePoem);
                            alertDialog.dismiss();
                            alertDialog.show();
                        }else {
                            Log.d(TAG, "ChangePoemAdPos = " + adapterPosition);
                            int pos = idPoemNames.indexOf(lastName);
                            idPoemNames.set(pos, namePoem);
                            String listNames = gson.toJson(idPoemNames);
                            for (String poemNames : idPoemNames) {
                                Log.d(TAG, "justChanged_poemNames = " + poemNames);
                            }
                            editor.putString("poemNames", listNames);
                            editor.apply();
                            changeCharacters(namePoem, lastName);
                            flagAdded = true;
                            Log.d(TAG, "changed namePoem = " + namePoem);
                            quests.get(adapterPosition).setTitle(namePoem);
                            Log.d(TAG, "changed quests.size = " + quests.size());
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


    public void deletePoem(int adapterPosition,String namePoem){

        if (quests.size() > 0) {
            Log.d(TAG, "REMOVE_character" + quests.size());
            String name = quests.get(adapterPosition).getTitle();
            if (quests.remove(adapterPosition) != null) {
                Log.d(TAG, "really deleted = " + name);
                flagDeleted = true;
                idPoemNames.remove(name);
                removeCharacters(name);
                SharedPreferences sPref = this.getActivity().getSharedPreferences("Characters",MODE_PRIVATE);
                sPref.edit().remove(namePoem + "countChar").apply();
                String listNames = new Gson().toJson(idPoemNames);
                Log.d(TAG,"REMOVE idPoemNames.size = " + idPoemNames.size());
                editor.putString(FILE_LIST_NAMES,listNames).apply();
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }

        }
    }

    private void removeCharacters(String name) {
        String fileCharacters = "Characters";
        String fileCharList = name + "ListNames";
        SharedPreferences sPref = this.getActivity().getSharedPreferences(fileCharacters, MODE_PRIVATE);
        itemsListType = new TypeToken<List<String>>() {}.getType();

        Log.d(TAG, "namePoem = " + name);
        List<String> list = new Gson().fromJson(sPref.getString(fileCharList,""),itemsListType);

        if(list != null) {
            List<String> listChars = new LinkedList<>(list);
            Log.d(TAG, "idPoemNames.size = " + listChars.size());
            for(String nameCharacter : listChars){
                sPref.edit().remove(nameCharacter).apply();
            }
            sPref.edit().remove(fileCharList).apply();

        }


    }

    public void changeCharacters(String newName, String lastName){
        String fileCharacters = "Characters";
        SharedPreferences sPref = this.getActivity().getSharedPreferences(fileCharacters, MODE_PRIVATE);
        itemsListType = new TypeToken<List<String>>() {}.getType();

        Log.d(TAG, "namePoem = " + newName);
        List<String> list = gson.fromJson(sPref.getString(lastName + "ListNames",""),itemsListType);
        Log.d(TAG,"changeCharacters");
        if(list != null) {
            List<String> listCharacters = new LinkedList<>(list);
            Log.d(TAG, "idCharactersNames.size = " + listCharacters.size());
            List<String> newCharacters = new LinkedList<>();
            Character character;
            for(String nameCharacter : listCharacters){
                Log.d(TAG,"currentNameCharacter = " + nameCharacter);
                character = gson.fromJson(sPref.getString(nameCharacter,""),Character.class);
                String suffix = nameCharacter.substring(lastName.length(),nameCharacter.length());
                Log.d(TAG, nameCharacter + "_suffix = " + suffix);
                character.setId(newName + suffix);
                newCharacters.add(newName + suffix);
                String jsonChar = gson.toJson(character);
                sPref.edit().remove(nameCharacter).apply();
                sPref.edit().putString(character.getId(),jsonChar).apply();
            }
            sPref.edit().remove(lastName + "ListNames").apply();
            String count = sPref.getString(lastName + "countChar","0");
            sPref.edit().remove(lastName + "countChar").apply();
            sPref.edit().putString(newName + count,count).apply();
            String listChar = gson.toJson(newCharacters);
            sPref.edit().putString(newName + "ListNames",listChar).apply();
        }
    }
}

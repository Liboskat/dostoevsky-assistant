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

    private RecyclerView recyclerView; // для списка
    private List<Poem> poems;
    private PoemsAdapter adapter;
    private LinearLayoutManager layoutManager;

    private View.OnClickListener onClickListener; // слушатель
    private final String FILE_POEMS = "Poems"; // место сохранения произведений

    public static final String TAG = "PoemTAG";

    private Button btnOK; // кнопка согласия добавления,изменения

    Gson gson;
    private Type itemsListType; // для Gson()
    private LinkedList<String> idPoemNames; //id poems

    private boolean flagAdded;
    private boolean flagDeleted;
    private EditText editTextSearch; // editText для поиска
    private boolean flagSearh;

    private final String FILE_LIST_NAMES = "poemNames";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reflections_page_layout,container,false);

        sharedPreferences = this.getActivity().getSharedPreferences(FILE_POEMS,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        gson = new Gson(); // загрузка списка с именами произведений
        itemsListType = new TypeToken<List<String>>() {}.getType();
        List<String> list = gson.fromJson(sharedPreferences.getString(FILE_LIST_NAMES,""),itemsListType);
        if(list != null){
            idPoemNames = new LinkedList<>(list);
        }else{
            idPoemNames = new LinkedList<>();
        }

        // добавление слушателя на кнопка add
        ImageButton addBtn = (ImageButton) view.findViewById(R.id.reflections_page_add_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPoem();
            }
        });

        // добавление слушателя на кнопка search
        ImageButton searchBtn = (ImageButton) view.findViewById(R.id.reflections_page_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPoem();
            }
        });

        //добавления слущателя на editTextSearch , для закрытия keyboard
        editTextSearch = (EditText) view.findViewById(R.id.reflections_page_search_edit);
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
        if (idPoemNames.size() != 0) {
            initPoems();
        } else {
            poems = new LinkedList<>();
        }

        adapter = new PoemsAdapter(poems,this);
        layoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.reflections_page_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initPoems() { // инициализация произведений
        poems = new LinkedList<>();
        String namePoem;
        for (int i = 0; i < idPoemNames.size(); i++) {
            namePoem = idPoemNames.get(i);
            poems.add(0, new Poem(namePoem));
        }

    }


    public void searchPoem() { // поиск произведения
        editTextSearch.setVisibility(View.VISIBLE);
        Helper.showKeyboard(this.getActivity()); //показать клавиатуру

        String namePoem = String.valueOf(editTextSearch.getText());
        String nameForSearch = namePoem.toLowerCase().trim();

        if(!namePoem.equals("")) { // осущесвлять ли поиск,или отобразить ввод
            if (!flagSearh) { // если не найдено,то сохраняем позицию
                editTextSearch.setSelection(namePoem.length());
                flagSearh = true;
            } else {            // если уже было нажато,то проверим
                boolean flagFind = false;
                for (int i = 0; !flagFind && i < poems.size(); i++) {
                    if (nameForSearch.equals(poems.get(i).getTitle().toLowerCase().trim())) { // осуществляем поиск
                        flagFind = true;

                        editTextSearch.setText("");
                    }
                }
                editTextSearch.setVisibility(View.INVISIBLE);
                if(flagFind){
                    openPageCharacter(namePoem); // если поиск успешен,открываем страницу
                }
                if (!flagFind) { // иначе
                    Toast.makeText(getContext(), "Произведение не найдено", Toast.LENGTH_SHORT).show();
                    editTextSearch.setVisibility(View.VISIBLE);
                }
                flagSearh = true;
            }
        }

    }



    public void addPoem() {
        Helper.setVisibilityGone(editTextSearch);

        final View content = getActivity().getLayoutInflater().inflate(R.layout.reflections_material_dialog, null);

        btnOK = (Button) content.findViewById(R.id.btnOK);
        Button btnCancel = (Button) content.findViewById(R.id.btnCancel);

        final AlertDialog alertDialog = Helper.createDialog(getActivity(),content);

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
                            idPoemNames.add(namePoem);
                            flagAdded = true;
                            String listNames = new Gson().toJson(idPoemNames);

                            editor.putString(FILE_LIST_NAMES, listNames).apply();
                            editor.apply();
                            poems.add(0, new Poem(namePoem));

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
            String listNames = gson.toJson(idPoemNames);
            editor.putString(FILE_LIST_NAMES,listNames).apply();

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }
        super.onResume();
    }

    @Override
    public void openPageCharacter(String title) {
        Helper.setVisibilityGone(editTextSearch);

        Intent intent = new Intent(PoemsFragment.this.getActivity(),CommonCharacterActivity.class);
        intent.putExtra("namePoem",title);
        startActivity(intent);
    }

    @Override
    public boolean changePoem(final int adapterPosition) {
        Helper.setVisibilityGone(editTextSearch);

        final View content = getActivity().getLayoutInflater().inflate(R.layout.reflections_dialog_change, null);

        Button btnChange = (Button) content.findViewById(R.id.reflection_change_btn);
        Button btnDelete = (Button) content.findViewById(R.id.reflection_delete_btn);

        final AlertDialog alertDialog = Helper.createDialog(getActivity(),content);;
        final View contentForEditName = getActivity().getLayoutInflater().inflate(R.layout.reflections_material_dialog, null);

        final String namePoem = poems.get(adapterPosition).getTitle();

        alertDialog.show();
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.reflection_change_btn:
                        changeNamePoem(contentForEditName,adapterPosition,namePoem);
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

    public void changeNamePoem(final View contentForEditName, final int adapterPosition, String namePoem){
        btnOK = (Button) contentForEditName.findViewById(R.id.btnOK);
        Button btnCancel = (Button) contentForEditName.findViewById(R.id.btnCancel);

        final AlertDialog alertDialog = Helper.createDialog(getActivity(),contentForEditName);

        final String lastName = namePoem;

        TextView tv_alert = (TextView)contentForEditName.findViewById(R.id.tv_alert_dialog);
        tv_alert.setText("Название произведения");

        final EditText[] name = {(EditText) contentForEditName.findViewById(R.id.editName)};
        String title = poems.get(adapterPosition).getTitle();
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
                            name[0].setSelection(namePoem.length());
                            alertDialog.dismiss();
                            alertDialog.show();
                        }else {
                            int pos = idPoemNames.indexOf(lastName);
                            idPoemNames.set(pos, namePoem);
                            String listNames = gson.toJson(idPoemNames);

                            editor.putString("poemNames", listNames);
                            editor.apply();

                            changeCharacters(namePoem, lastName);
                            flagAdded = true;

                            poems.get(adapterPosition).setTitle(namePoem);

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


    public void deletePoem(int adapterPosition,String namePoem){ // удаление произведения

        if (poems.size() > 0) {
            String name = poems.get(adapterPosition).getTitle();
            if (poems.remove(adapterPosition) != null) {
                flagDeleted = true;
                idPoemNames.remove(name);
                removeCharacters(name);
                SharedPreferences sPref = this.getActivity().getSharedPreferences("Characters",MODE_PRIVATE);
                sPref.edit().remove(namePoem + "countChar").apply();
                String listNames = new Gson().toJson(idPoemNames);
                editor.putString(FILE_LIST_NAMES,listNames).apply();

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }

        }
    }

    private void removeCharacters(String name) { // удаления названий персонажей произведения
        String fileCharacters = "Characters";
        String fileCharList = name + "ListNames";
        SharedPreferences sPref = this.getActivity().getSharedPreferences(fileCharacters, MODE_PRIVATE);
        List<String> list = new Gson().fromJson(sPref.getString(fileCharList,""),itemsListType);

        if(list != null) {
            List<String> listChars = new LinkedList<>(list);
            for(String nameCharacter : listChars){
                sPref.edit().remove(nameCharacter).apply();
            }
            sPref.edit().remove(fileCharList).apply();

        }

    }

    public void changeCharacters(String newName, String lastName){ // изменение id персонажей
        String fileCharacters = "Characters";
        SharedPreferences sPref = this.getActivity().getSharedPreferences(fileCharacters, MODE_PRIVATE);

        List<String> list = gson.fromJson(sPref.getString(lastName + "ListNames",""),itemsListType);
        if(list != null) {
            List<String> listCharacters = new LinkedList<>(list);
            List<String> newCharacters = new LinkedList<>();
            Character character;
            for(String nameCharacter : listCharacters){
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
            sPref.edit().remove(lastName + "countChar").apply();
            String listChar = gson.toJson(newCharacters);
            sPref.edit().putString(newName + "ListNames",listChar).apply();
        }
    }
}

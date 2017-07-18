package rizvanov.dostoevskyassistant.fragment_notes;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rizvanov.dostoevskyassistant.R;
import rizvanov.dostoevskyassistant.db.DBHelper;
import rizvanov.dostoevskyassistant.db.DiaryTable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ильшат on 14.07.2017.
 */

public class NoteList extends Fragment implements NoteListAdapter.OnEventClickListener{
    private RecyclerView recyclerView;
    private List<Note> notes;
    private NoteListAdapter adapter;
    private DBHelper helper;
    private DiaryTable diaryTable;
    private SQLiteDatabase db;
    private ImageButton recordingButton;
    protected static final int RESULT_SPEECH = 1;
    protected static final int RESULT_TEXT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diary_page_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notes = new ArrayList<>();
        helper = new DBHelper(this.getContext());
        db = helper.getWritableDatabase();

        diaryTable = new DiaryTable();
        notes = diaryTable.getAllNotesFromDb(db);
        //initNotes();

        adapter = new NoteListAdapter(notes, this);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.diary_page_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);

        recordingButton = (ImageButton) getActivity().findViewById(R.id.diary_page_addbutton);
        recordingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ru-RU");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getContext(),
                            "Великий русский писатель, ваше устройство не поддерживает эту функцию",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                    String strDate = sdf.format(c.getTime());

                    Note note = new Note(strDate, text.get(0), new Date().getTime());

                    notes.add(note);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    diaryTable.insert(db, note);
                }
                break;
            }
            case RESULT_TEXT: {
                if (resultCode == RESULT_OK && null != data) {
                    Note editedNote = new Note("", "", 0);
                    String editedText = "";
                    if(data.getExtras() != null) {
                        editedNote = (Note) data.getSerializableExtra(FullNoteActivity.KEY_NOTE);
                        editedText = data.getStringExtra(FullNoteActivity.KEY_EDITED_TEXT);
                    }
                    editedNote.setText(editedText);
                    diaryTable.update(db, editedNote);

                    notes = diaryTable.getAllNotesFromDb(db);
                    adapter = new NoteListAdapter(notes, this);
                    recyclerView.setAdapter(adapter);
                }
                break;
            }
        }
    }


    @Override
    public void OnEventClick(Note note) {
        Intent intent = new Intent(getActivity(), FullNoteActivity.class);
        intent.putExtra(FullNoteActivity.KEY_NOTE, note);

        startActivityForResult(intent, RESULT_TEXT);
    }

    @Override
    public void OnLongEventClick(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удаление заметки");
        builder.setMessage("Вы точно хотите удалить заметку?");

        String positiveText = "Да";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(note);
                        diaryTable.remove(db, note);
                        adapter.notifyDataSetChanged();
                    }
                });

        String negativeText = "Нет";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}

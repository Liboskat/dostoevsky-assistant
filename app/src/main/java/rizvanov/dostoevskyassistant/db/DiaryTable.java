package rizvanov.dostoevskyassistant.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rizvanov.dostoevskyassistant.fragment_notes.Note;

/**
 * Created by Ильшат on 12.07.2017.
 */

public class DiaryTable {
    public static final String DIARY_TABLE_NAME = "noteTable";
    public static final String COLUMN_NOTE_TEXT = "note_text";
    public static final String COLUMN_NOTE_DATE = "note_date";
    public static final String COLUMN_NOTE_ID = "note_id";

    public static String createTable() {
        return "CREATE TABLE " + DIARY_TABLE_NAME + " ("
                + COLUMN_NOTE_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TEXT + " TEXT, " +
                COLUMN_NOTE_DATE + " TEXT)";
    }

    public void insert(SQLiteDatabase db, Note note) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTE_TEXT, note.getText());
        cv.put(COLUMN_NOTE_DATE, note.getDate());
        db.insert(DIARY_TABLE_NAME, null, cv);
    }

    public List<Note> getAllNotesFromDb(SQLiteDatabase db) {
        String selectQuery = " SELECT " +
                COLUMN_NOTE_ID + ", " +
                COLUMN_NOTE_TEXT + ", "
                + COLUMN_NOTE_DATE
                + " FROM " + DIARY_TABLE_NAME;
        Log.d("DBTag", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Note> notes = new ArrayList<>();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cursor.getColumnNames()) {
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("DBTag", str);
                    notes.add(new Note(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TEXT)), cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DATE))));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return notes;
    }
}

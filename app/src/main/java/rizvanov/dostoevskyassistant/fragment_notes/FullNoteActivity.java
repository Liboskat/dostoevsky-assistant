package rizvanov.dostoevskyassistant.fragment_notes;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import rizvanov.dostoevskyassistant.R;

public class FullNoteActivity extends AppCompatActivity{
    public static final String KEY_NOTE = "note";
    private EditText text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        text = (EditText) findViewById(R.id.diary_element_full_text);

        Note note = new Note("", "", 0);
        if(getIntent().getExtras() != null) {
            note = (Note) getIntent().getExtras().getSerializable(KEY_NOTE);
        }

        text.setText(note.getText());
    }
}

package rizvanov.dostoevskyassistant.fragment_notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import rizvanov.dostoevskyassistant.R;

public class FullNoteActivity extends AppCompatActivity{
    public static final String KEY_NOTE = "note";
    public static final String KEY_EDITED_TEXT = "edited_text";
    private EditText text;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_element_full_layout);

        text = (EditText) findViewById(R.id.diary_element_full_text);

        note = new Note("", "", 0);
        if(getIntent().getExtras() != null) {
            note = (Note) getIntent().getExtras().getSerializable(KEY_NOTE);
        }

        text.setText(note.getText());
    }

   public void finish() {
       String newText = text.getText().toString();
       Intent intent = new Intent();
       intent.putExtra(KEY_NOTE, note);
       intent.putExtra(KEY_EDITED_TEXT, newText);
       setResult(RESULT_OK, intent);
       super.finish();
   }
}

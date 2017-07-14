package rizvanov.dostoevskyassistant.CharacterFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by user on 14.07.2017.
 */

public class CharacterFull extends AppCompatActivity {

    private ImageView photo;
    private TextView name;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reflection_full_layout);

        photo = (ImageView) findViewById(R.id.character_photo);
        name = (TextView) findViewById(R.id.character_name);
        editText = (EditText) findViewById(R.id.character_edit);

        Intent intent = getIntent();
        String photoStr = intent.getStringExtra("photo");
        String nameStr = intent.getStringExtra("name");
        String editStr = intent.getStringExtra("edit");

        if (!photoStr.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(photoStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            photo.setImageBitmap(bitmap);
        }
        name.setText(nameStr);
        editText.setText(editStr);
    }
}

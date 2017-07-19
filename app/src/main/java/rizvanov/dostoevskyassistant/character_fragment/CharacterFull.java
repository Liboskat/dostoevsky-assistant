package rizvanov.dostoevskyassistant.character_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by user on 14.07.2017.
 */

public class CharacterFull extends AppCompatActivity {

    private static final String TAG = "TAG";
    private ImageView photo;
    private EditText name;
    private EditText description;

    private Character character;
    private String lastName;
    private String lastEdit;
    private String FILE_CHARACTERS = "Characters";

    final int REQUEST_CODE_PHOTO = 1;
    File directory;
    final int TYPE_PHOTO = 1;

    SharedPreferences sharedPreferences;

    Gson gson;
    Uri uri;

    boolean flagImage = false;//сделано ли фото
    boolean nameClicked = true;
    boolean descriptionClicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reflection_full_layout);

        photo = (ImageView) findViewById(R.id.character_photo);
        name = (EditText) findViewById(R.id.character_name);
        disabledEditText(name);

        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) { // ставим по долгому нажатию блокировку/разблокировку сообщения
                if(!nameClicked) {
                    enabledEditText(name);
                }else {
                    disabledEditText(name);
                }
                return true;
            }
        });



        description = (EditText) findViewById(R.id.character_edit);
        disabledEditText(description);

        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!descriptionClicked) {
                    enabledEditText(description);
                }else {
                    disabledEditText(description);
                }
                return true;
            }
        });

        lastName = String.valueOf(name.getText());
        lastEdit = String.valueOf(description.getText());

        createDirectory();
        Intent intent = getIntent();
        gson = new Gson();
        sharedPreferences = getSharedPreferences(FILE_CHARACTERS,MODE_PRIVATE);

        loadText(intent); // загрузить данные
        this.setTitle(name.getText()); // установить значени имени активити


    }

    private void enabledEditText(EditText editText) { //разблокировка для редактирования
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        if(editText.equals(name)) {
            nameClicked = true;
        } else {
            descriptionClicked = true;
        }
        Helper.showKeyboard(this);

    }

    private void disabledEditText(EditText editText){ // блокировка для редактирования
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        if(editText.equals(name)) {
            nameClicked = false;
        } else {
            descriptionClicked = false;
        }
        Helper.hideKeyboard(this);

    }

    public void takePhotoOrGallery(View view){ // сделать фотографию
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = generateFileUri(TYPE_PHOTO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        flagImage = true;
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                setPic(true); // уменьшаем вес изображения
                String uriStr = uri.getPath();
                character.setPhoto(uriStr); // устанавливаем путь к нашему изображению
            }

        }
    }

    private void setPic(boolean flagTarget) {
        int targetW;
        int targetH;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(flagTarget) {
            targetW = photo.getWidth();
            targetH = photo.getHeight();
            editor.putInt("targetW",targetW);
            editor.putInt("targetH",targetH);
            editor.apply();

        }else {

            targetW = sharedPreferences.getInt("targetW",480);
            targetH = sharedPreferences.getInt("targetH",480);
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), bmOptions);
        bitmap = rotateBitmap(bitmap,uri.getPath());
        photo.setImageBitmap(bitmap); // устанавливаем изображения в наше ImageView
    }

    public static Bitmap rotateBitmap(Bitmap srcBitmap, String path) { // изображения будет расположено в портретном режиме(была неисправность)
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(0));
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                srcBitmap.getHeight(), matrix, true);

    }


   @Override
   protected void onRestart() { // если было сделано фото ,то сохраняем

       if(flagImage) {
           saveText();
       }
       flagImage = false;
       super.onRestart();

   }

    @Override
    protected void onPause() { // сохранение,если было изменено одно из editText полей
        if (!lastName.equals(String.valueOf(name.getText()))
                || !lastEdit.equals(String.valueOf(description.getText()))) {
            saveText();
        }

        super.onPause();
    }

    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                break;
        }
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }


    void saveText() { // сохранения обьекта ,сохранения изменений в списке персонажей для произведения
        SharedPreferences.Editor ed = sharedPreferences.edit();
        character.setName(String.valueOf(name.getText()));
        character.setEditText(String.valueOf(description.getText()));
        String gsonCharacter = gson.toJson(character);
        CommonCharacterActivity.id = character.getId();
        ed.putString(character.getId(), gsonCharacter);
        String listNames = getIntent().getStringExtra("listNames");
        String namePoem = getIntent().getStringExtra("namePoem");
        sharedPreferences.edit().putString(namePoem + "ListNames",listNames).apply();
        ed.apply();

    }



    void loadText(Intent intent) { // загрузка данных

        this.character = gson.fromJson(intent.getStringExtra("character"),Character.class);
        name.setText(character.getName());
        name.setSelection(character.getName().length());
        description.setText(character.getEditText());
        description.setSelection(character.getEditText().length());
        String photoStr = character.getPhoto();

        if (!photoStr.equalsIgnoreCase("")) { // если сделано фото,то загрузить фотографию
            uri = Uri.parse(photoStr);
            setPic(false);
        }
        else{
            photo.setImageResource(R.mipmap.ic_addphoto);
        }

    }

}

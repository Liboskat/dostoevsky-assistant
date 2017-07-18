package rizvanov.dostoevskyassistant.character_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //для портретного режима
        setContentView(R.layout.reflection_full_layout);

        photo = (ImageView) findViewById(R.id.character_photo);
        name = (EditText) findViewById(R.id.character_name);
        disabledEditText(name);

        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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

        loadText(intent);
        this.setTitle(name.getText());


    }

    private void enabledEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        if(editText.equals(name)) {
            nameClicked = true;
        } else {
            descriptionClicked = true;
        }
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }

    }

    private void disabledEditText(EditText editText){
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        if(editText.equals(name)) {
            nameClicked = false;
        } else {
            descriptionClicked = false;
        }
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public void takePhotoOrGallery(View view){
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
                setPic(true);
                String uriStr = uri.getPath();
                character.setPhoto(uriStr);
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
        photo.setImageBitmap(bitmap);
    }

    public static Bitmap rotateBitmap(Bitmap srcBitmap, String path) {
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
   protected void onRestart() {

       Log.d(TAG,"OnRestart save = " + flagImage);
       if(flagImage) {
           saveText();
       }
       flagImage = false;
       super.onRestart();

   }

    @Override
    protected void onPause() {
        Log.d(TAG, "OnPause save");
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
        Log.d(TAG, "FILE_CHARACTERS = " + file);
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



    void saveText() {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        Log.d(TAG,"CharacterSaveName = " + character.getName());
        character.setName(String.valueOf(name.getText()));
        Log.d(TAG,"CharacterSaveNameNEW = " + character.getName());
        character.setEditText(String.valueOf(description.getText()));
        String gsonCharacter = gson.toJson(character);
        Log.d(TAG,"CharacterFull _ id = " + character.getId());
        CommonCharacterActivity.id = character.getId();
        ed.putString(character.getId(), gsonCharacter);
        String listNames = getIntent().getStringExtra("listNames");
        Log.d(TAG,"listNames = " + listNames);
        String namePoem = getIntent().getStringExtra("namePoem");
        Log.d(TAG,namePoem);
        sharedPreferences.edit().putString(namePoem + "ListNames",listNames).apply();
        Log.d(TAG,"All is good");
        ed.apply();

    }



    void loadText(Intent intent) {

        this.character = gson.fromJson(intent.getStringExtra("character"),Character.class);
        Log.d(TAG,character.getId());
        name.setText(character.getName());
        name.setSelection(character.getName().length());
        description.setText(character.getEditText());
        description.setSelection(character.getEditText().length());
        String photoStr = character.getPhoto();

        if (!photoStr.equalsIgnoreCase("")) {
            uri = Uri.parse(photoStr);
            setPic(false);
        }
        else{
            photo.setImageResource(R.mipmap.ic_addphoto);
        }

    }

}

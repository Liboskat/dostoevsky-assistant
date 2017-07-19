package rizvanov.dostoevskyassistant.character_fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by user on 19.07.2017.
 */

public class Helper {

    public static void hideKeyboard(Activity activity){

    }

    public static void showKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static AlertDialog createDialog(Activity activity,View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    public static void setVisibilityGone(EditText editText){
        editText.setText("");
        editText.setVisibility(View.GONE);
    }
}

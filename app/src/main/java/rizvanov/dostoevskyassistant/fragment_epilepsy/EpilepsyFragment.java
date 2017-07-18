package rizvanov.dostoevskyassistant.fragment_epilepsy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rizvanov.dostoevskyassistant.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EpilepsyFragment extends Fragment {

    public static final String PREF_TAG = "help_pref_data";

    public static final String HELP_NUMBER_KEY = "help_number";
    public static final String HELP_MESSAGE_KEY = "help_message";
    public static final String HELP_NAME_KEY = "help_name";
    public static final String HELP_POWER_KEY = "help_power";

    private static final int NUMBER_PICK_REQUEST_CODE = 1;
    private static final String LOG_TAG = "sam_tag";

    private TextView textViewHelperName;
    private EditText editTextHelperNumber;
    private EditText editTextHelpMessage;
    private CheckBox checkboxHelpMessagePower;
    private ImageButton imageButtonPickNumberFromContacts;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        sharedPreferences = getActivity().getSharedPreferences(PREF_TAG, MODE_PRIVATE);
        EventBus.getDefault().register(this);
        saveBooleanDataByKey(SensorListener.IS_APP_RUN, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHelpFieldsData();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveHelpFieldsData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        saveBooleanDataByKey(SensorListener.IS_APP_RUN, false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.epilepsy_page_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewHelperName = (TextView) view.findViewById(R.id.tv_epilepsy_helper_name);
        editTextHelperNumber = (EditText) view.findViewById(R.id.et_epilepsy_helper_number);
        editTextHelperNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                textViewHelperName.setText("");
                saveStringDataByKey(HELP_NUMBER_KEY, editable.toString());
            }
        });
        editTextHelpMessage = (EditText) view.findViewById(R.id.et_epilepsy_help_message);
        editTextHelpMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                saveStringDataByKey(HELP_MESSAGE_KEY, editable.toString());
            }
        });
        imageButtonPickNumberFromContacts = (ImageButton) view.findViewById(R.id.button_epilepsy_search_in_contacts);
        imageButtonPickNumberFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, NUMBER_PICK_REQUEST_CODE);
            }
        });
        checkboxHelpMessagePower = (CheckBox) view.findViewById(R.id.checkbox_epilepsy_help_message_confirm);
        checkboxHelpMessagePower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean newCheckedState) {
                if (newCheckedState) {
                    startShakeListener(compoundButton);
                } else {
                    stopShakeListener();
                }
                saveBooleanDataByKey(HELP_POWER_KEY, newCheckedState);
            }
        });
    }

    private void startShakeListener(CompoundButton button) {
        String phone = editTextHelperNumber.getText().toString();
        String message = editTextHelpMessage.getText().toString();
        if (isHelpFieldsEmpty(phone, message)) {
            Toast.makeText(getContext(), "Заполните поля номера телефона и сообщения", Toast.LENGTH_SHORT).show();
            button.setChecked(false);
        } else {
            Toast.makeText(getContext(), "Оповещения включены", Toast.LENGTH_SHORT).show();
            Intent sensorListenerIntent = new Intent(getContext(), SensorListener.class);
            getActivity().startService(sensorListenerIntent);
        }
    }

    private void stopShakeListener() {
        Intent sensorListenerIntent = new Intent(getContext(), SensorListener.class);
        Toast.makeText(getContext(), "Оповещения выключены", Toast.LENGTH_LONG).show();
        getActivity().stopService(sensorListenerIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NUMBER_PICK_REQUEST_CODE:
                putNameAndPhoneInPrefByKey(resultCode, data, HELP_NAME_KEY, HELP_NUMBER_KEY);
                break;
        }
    }

    private void putNameAndPhoneInPrefByKey(int resultCode, Intent data, String helpNameKey, String helpNumberKey) {
        String contactName = "";
        String contactPhoneNumber = "";
        if (resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor contactCursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if (contactCursor != null & contactCursor.moveToNext()) {
                String contactId = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.Contacts._ID)
                );
                contactName = contactCursor.getString(
                        contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                );
                String hasPhone = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                );
                if (hasPhone.equals("1")) {
                    Cursor phonesCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null

                    );
                    while (phonesCursor.moveToNext()) {
                        contactPhoneNumber = phonesCursor.getString(
                                phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        );
                    }
                    phonesCursor.close();
                }
            }
            contactCursor.close();
        } else {
            Log.d(LOG_TAG, "ERROR");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(helpNameKey, contactName);
        editor.putString(helpNumberKey, contactPhoneNumber);
        editor.apply();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean check) {
        checkboxHelpMessagePower.setChecked(check);
    }

    private boolean isHelpFieldsEmpty(String phone, String message) {
        return phone.equals("") || message.equals("");
    }

    private void saveHelpFieldsData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = textViewHelperName.getText().toString();
        String phone = editTextHelperNumber.getText().toString();
        String message = editTextHelpMessage.getText().toString();
        boolean power = checkboxHelpMessagePower.isChecked();
        editor.putString(HELP_MESSAGE_KEY, message);
        editor.putString(HELP_NAME_KEY, name);
        editor.putString(HELP_NUMBER_KEY, phone);
        editor.putBoolean(HELP_POWER_KEY, power);
        editor.apply();
    }

    private void loadHelpFieldsData() {
        String name = sharedPreferences.getString(HELP_NAME_KEY, "");
        String message = sharedPreferences.getString(HELP_MESSAGE_KEY, "");
        String phone = sharedPreferences.getString(HELP_NUMBER_KEY, "");
        boolean power = sharedPreferences.getBoolean(HELP_POWER_KEY, false);
        editTextHelperNumber.setText(phone);
        textViewHelperName.setText(name);
        editTextHelpMessage.setText(message);
        checkboxHelpMessagePower.setChecked(power);
    }

    private void saveStringDataByKey(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void saveBooleanDataByKey(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}

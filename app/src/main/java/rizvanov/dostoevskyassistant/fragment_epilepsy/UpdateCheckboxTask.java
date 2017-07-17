package rizvanov.dostoevskyassistant.fragment_epilepsy;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

public class UpdateCheckboxTask extends AsyncTask<Void, Void, Boolean>{

    private Boolean checkboxValue;

    public UpdateCheckboxTask(Boolean checkboxValue) {
        this.checkboxValue = checkboxValue;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return checkboxValue;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        EventBus.getDefault().post(aBoolean);
    }
}

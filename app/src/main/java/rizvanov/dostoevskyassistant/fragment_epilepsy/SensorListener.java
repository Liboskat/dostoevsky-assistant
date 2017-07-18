package rizvanov.dostoevskyassistant.fragment_epilepsy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import rizvanov.dostoevskyassistant.MainActivity;
import rizvanov.dostoevskyassistant.R;

public class SensorListener extends Service implements SensorEventListener {


    public static final String HELP_NUMBER_KEY = "help_number";
    public static final String HELP_MESSAGE_KEY = "help_message";
    public static final String IS_APP_RUN = "app_destroy_check";

    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    public static final String TAG = SensorListener.class.getName();

    private final int EPILEPSY_TIME_REGISTER_MILLIS = 3000;
    private final int FAKE_SHAKE_TIME_WINDOW_MILLIS = 500;

    private SensorManager sensorManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;

    private UpdateCheckboxTask updateCheckboxTask;

    private long lastUpdate;
    private int timeSum;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        updateCheckboxTask = new UpdateCheckboxTask(false);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        sharedPreferences = getSharedPreferences(EpilepsyFragment.PREF_TAG, MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        unregisterListener();
        wakeLock.release();
        stopForeground(true);
    }

    private void registerListener() {
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            long actualTime = System.currentTimeMillis();
            float accelerationSquareRoot = countAcceleration(sensorEvent);

            if (accelerationSquareRoot >= 2) {
                if (lastUpdate == 0) {
                    lastUpdate = System.currentTimeMillis();
                } else {
                    long timeDif = actualTime - lastUpdate;
                    if (timeDif > FAKE_SHAKE_TIME_WINDOW_MILLIS) {
                        clearSumAndMark();
                    } else {
                        timeSum += timeDif;
                        if (timeSum >= EPILEPSY_TIME_REGISTER_MILLIS) {
                            clearSumAndMark();
                            sendSMS();
                            setupBuildSmsNotification();
                            stopSensorListener();
                        }
                        lastUpdate = actualTime;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(Process.myPid(), buildForegroundNotification());
        registerListener();
        wakeLock.acquire();
        return START_STICKY;
    }

    private void stopSensorListener() {
        saveBooleanDataByKey(EpilepsyFragment.HELP_POWER_KEY, false);
        if (getBooleanByKey(IS_APP_RUN)) {
            updateCheckboxTask.execute();
        } else {
            stopSelf();
        }

    }

    private void sendSMS() {
        String message = sharedPreferences.getString(HELP_MESSAGE_KEY, "");
        String phoneNumber = sharedPreferences.getString(HELP_NUMBER_KEY, "");
        /*SmsManager.getDefault().sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
        );*/
    }

    private void setupBuildSmsNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_off)
                .setContentTitle("Сообщение было отправлено.")
                .setContentText("Служба оповещений отключена! ")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(1, 800, 300)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(0, notification);
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_service_on);
        notificationBuilder.setContentTitle("Экстраоповещения активны!");
        notificationBuilder.setContentText("");
        return notificationBuilder.build();
    }

    private void clearSumAndMark() {
        timeSum = 0;
        lastUpdate = 0;
    }

    private float countAcceleration(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        return (x * x + y * y + z * z)
                /(SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    private boolean getBooleanByKey(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    private void saveBooleanDataByKey(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}

package android.example.sd_lab4;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    DatePicker datePicker;
    Button save;

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;

    long differenceInDays = 0;
    Calendar dateForNotification;

    public final static String ID_FOR_NOTIFICATION = "idForNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getIntExtra(ID_FOR_NOTIFICATION,0) == 415) {
            Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "notification_id")
                    .setAutoCancel(true)
                    .setContentTitle("Today is selected day")
                    .setContentText("Good morning!")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);


            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }


        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);


        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.activity_main);
        save = findViewById(R.id.set_button);
        datePicker = findViewById(R.id.date_picker);
        datePicker.setMinDate(System.currentTimeMillis());
        final Calendar today = Calendar.getInstance();
        final Date dateToday = today.getTime();
        onClickListenerExitAndSave();

        dateForNotification = Calendar.getInstance();
        dateForNotification.set(Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH,Calendar.HOUR_OF_DAY,Calendar.MINUTE);

        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        dateForNotification.set(year,monthOfYear,dayOfMonth,9,0);
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year,monthOfYear,dayOfMonth);
                        Date newDateTime = newDate.getTime();
                        long differenceInTime = (newDateTime.getTime() - dateToday.getTime());
                        int millsecInDay = 86400000;
                        differenceInDays = (differenceInTime/millsecInDay);
                    }
                });


    }

    void onClickListenerExitAndSave()
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mSettings = getSharedPreferences("daysBetween", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("days" + widgetID,"" + differenceInDays);
                editor.apply();
                setResult(RESULT_OK, resultValue);

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.putExtra(ID_FOR_NOTIFICATION,415);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 2,notificationIntent, 0);

                AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                am.setExact(AlarmManager.RTC_WAKEUP, dateForNotification.getTime().getTime(), pendingIntent);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                NewAppWidget.updateAppWidget(MainActivity.this, appWidgetManager, widgetID);
                finish();
            }
        });
    }
}



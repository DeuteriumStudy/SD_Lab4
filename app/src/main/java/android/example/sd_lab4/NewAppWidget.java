package android.example.sd_lab4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    final static String NAME_OF_SHARED = "daysBetween";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        String daysBetween = context.getSharedPreferences(NAME_OF_SHARED, Context.MODE_PRIVATE).getString("days" + appWidgetId, "");
        if (widgetText == null) return;

        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intent,0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        views.setTextViewText(R.id.appwidget_text, daysBetween);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences mSettings = context.getSharedPreferences(
                NAME_OF_SHARED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        if (strDate.substring(0,2).equals("00")) {
            int timeNeeded = Integer.parseInt(strDate.substring(3,5));
            if ((timeNeeded <=30) && (timeNeeded >=0)) {
                for (int appWidgetId : appWidgetIds) {
                if ((!mSettings.getString("days" + appWidgetId, "").equals(""))) {
                    int dayBetween = Integer.parseInt(mSettings.getString("days" + appWidgetId, ""));
                    if (dayBetween > 0) {
                        dayBetween--;
                        editor.putString("days" + appWidgetId,"" + dayBetween);
                        editor.apply();
                    }
                    else{
                        editor.putString("days" + appWidgetId,"");
                        editor.apply();
                    }
                }
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }
            else {
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }
        }
        else {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences.Editor editor = context.getSharedPreferences(
                NAME_OF_SHARED, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove("days" + widgetID);
        }
        editor.apply();
    }

}


package cn.jsyjst.dictionary.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.ui.TranslateActivity;


/**
 * Implementation of App Widget functionality.
 */
public class DictionaryWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.seek_widget);

        /**
         * 点击事件
         */
        Intent intent=new Intent(context, TranslateActivity.class);
        PendingIntent i = PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.rv_widget_dictionary,i);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}


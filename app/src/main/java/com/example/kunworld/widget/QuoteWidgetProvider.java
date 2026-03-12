package com.example.kunworld.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.kunworld.MainActivity;
import com.example.kunworld.R;
import com.example.kunworld.notifications.QuoteRepository;

/**
 * Widget provider for Quote of the Day widget.
 */
public class QuoteWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_REFRESH = "com.example.kunworld.REFRESH_QUOTE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_REFRESH.equals(intent.getAction())) {
            // Refresh all widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, QuoteWidgetProvider.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        try {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quote);

            // Get daily quote with fallback
            String quote;
            try {
                QuoteRepository quoteRepository = new QuoteRepository(context);
                quote = quoteRepository.getDailyQuote();
            } catch (Exception e) {
                quote = "Every day is a new beginning. Take a deep breath and start again.";
            }
            views.setTextViewText(R.id.tvQuote, quote);

            // Open app when widget is clicked
            Intent openAppIntent = new Intent(context, MainActivity.class);
            openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                    context, 0, openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widgetContainer, openAppPendingIntent);

            // Refresh button
            Intent refreshIntent = new Intent(context, QuoteWidgetProvider.class);
            refreshIntent.setAction(ACTION_REFRESH);
            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                    context, 0, refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btnRefresh, refreshPendingIntent);

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (Exception e) {
            // Log error but don't crash
            e.printStackTrace();
        }
    }

    /**
     * Static method to update all widgets from anywhere in the app.
     */
    public static void updateAllWidgets(Context context) {
        Intent intent = new Intent(context, QuoteWidgetProvider.class);
        intent.setAction(ACTION_REFRESH);
        context.sendBroadcast(intent);
    }
}

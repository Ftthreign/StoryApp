package com.ftthreign.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.ftthreign.storyapp.R

class StoryImageBannerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    companion object {
        const val EXTRA_ITEM = "image_banner"
        const val ACTION = "action"
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val intent = Intent(context, StackWidgetService::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

    val views = RemoteViews(context.packageName, R.layout.story_image_banner_widget)
    views.setRemoteAdapter(R.id.stack_view, intent)

    val toastIntent = Intent(context, StoryImageBannerWidget::class.java)
    toastIntent.action = StoryImageBannerWidget.ACTION
    toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

    val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        else 0
    )
    views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
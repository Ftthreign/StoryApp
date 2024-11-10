package com.ftthreign.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.ftthreign.storyapp.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val storyRepository = Injection.provideStoryRepository(this.applicationContext)
        return StackRemoteViewsFactory(this.applicationContext, storyRepository)
    }
}
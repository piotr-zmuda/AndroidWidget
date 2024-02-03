package com.example.smb_poprawione

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import java.util.Random

/**
 * Implementation of App Widget functionality.
 */
class Widget : AppWidgetProvider() {
    private var currentImageResource = R.drawable.third
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioIndex = 1
    private var isPlaying = false
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, currentImageResource)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        println("SOMETHIIING")
        if (intent?.action == "IMAGE_CLICK") {
            // Handle image click action, change the image resource here
            // For simplicity, let's just change to the next image in sequence
            currentImageResource = getNextImageResource(currentImageResource)

            println("UPDATEE")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context!!, Widget::class.java)
            )
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, currentImageResource)
            }
        }else if(intent?.action == "PLAY_PAUSE"){
            if (context != null) {
                togglePlayPause(context)
            }
        }else if(intent?.action == "BACK"){
                playNextAudio()
        }else if(intent?.action == "NEXT"){
                playNextAudio()
        }
    }
    private fun togglePlayPause(context: Context) {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            startAudio(context)
            isPlaying = true
        }
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    private fun playNextAudio() {
        stopAudio()
        currentAudioIndex = Random().nextInt(audioResources.size)
        println(currentAudioIndex)
    }

    private fun startAudio(context: Context) {
        stopAudio()
        println(currentAudioIndex)
        mediaPlayer = MediaPlayer.create(
            context,
            audioResources[currentAudioIndex]
        )
        mediaPlayer?.start()
    }
    companion object {
        // Define audio resources (R.raw.audio1, R.raw.audio2, etc.)
        private val audioResources = intArrayOf(
            R.raw.first,
            R.raw.second
        )
    }
    private fun getNextImageResource(currentResource: Int): Int {
        // Define an array of image resources in the desired order
        println("CHANGE WIDGET ")
        val imageResources = intArrayOf(
            R.drawable.first,
            R.drawable.second,
            R.drawable.third,
        )

        // Find the index of the current resource in the array
        val currentIndex = imageResources.indexOf(currentResource)

        // Calculate the index of the next resource, considering looping back to the start
        // Generate a random index
        val randomIndex = Random().nextInt(imageResources.size)

        // Return the randomly chosen image resource
        return imageResources[randomIndex]
    }
}
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    imageResource: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget)
    views.setTextViewText(R.id.btnWeb1, "Blog");
    views.setTextViewText(R.id.btnWeb2, "Widget");
    val int = Intent(Intent.ACTION_VIEW)
    int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    int.data = Uri.parse("https://www.etuo.pl/blog/gdzie-sa-widgety-w-androidzie/2934")
    val pendingIntent = PendingIntent.getActivity(context,0,int, PendingIntent.FLAG_IMMUTABLE)

    val int2 = Intent(Intent.ACTION_VIEW)
    int2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    int2.data = Uri.parse("https://www.download.net.pl/jak-stworzyc-wlasny-widget-na-androidzie/n/4177/")
    val pendingIntent2 = PendingIntent.getActivity(context,0,int2, PendingIntent.FLAG_IMMUTABLE)

    val int3 = Intent(context, Widget::class.java)
    int3.action = "IMAGE_CLICK"
    val pendingIntent3 = PendingIntent.getBroadcast(context, 2, int3, PendingIntent.FLAG_IMMUTABLE)
    views.setImageViewResource(R.id.imageVieww, imageResource)


    // Audio control intents
    val playPauseIntent = Intent(context, Widget::class.java)
    playPauseIntent.action = "PLAY_PAUSE"
    val playPausePendingIntent = PendingIntent.getBroadcast(
        context, 3, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val stopIntent = Intent(context, Widget::class.java)
    stopIntent.action = "BACK"
    val stopPendingIntent = PendingIntent.getBroadcast(
        context, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val nextIntent = Intent(context, Widget::class.java)
    nextIntent.action = "NEXT"
    val nextPendingIntent = PendingIntent.getBroadcast(
        context, 5, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    views.setOnClickPendingIntent(
        R.id.btnWeb1,pendingIntent
    )
    views.setOnClickPendingIntent(
        R.id.btnWeb2,pendingIntent2
    )
    views.setOnClickPendingIntent(
        R.id.imageVieww,pendingIntent3
    )
    views.setOnClickPendingIntent(
        R.id.back,stopPendingIntent
    )
    views.setOnClickPendingIntent(
        R.id.start,playPausePendingIntent
    )
    views.setOnClickPendingIntent(
        R.id.next,nextPendingIntent
    )
    println("WidgetUpdate")

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

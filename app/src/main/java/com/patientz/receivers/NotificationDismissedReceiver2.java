package com.patientz.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.patientz.utils.Log;

public class NotificationDismissedReceiver2 extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      Log.d("NotificationDismissedReceiver2","NotificationDismissedReceiver2");
      AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      final int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
  }
}
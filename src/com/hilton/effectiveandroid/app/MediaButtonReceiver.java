package com.hilton.effectiveandroid.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "intent " + intent);
        String intentAction = intent.getAction();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = (KeyEvent)
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume. 
            // double press: next track
            // long press: start auto-shuffle mode.
            
            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = "command stop";
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = "command toggle pause play";
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = "command next";
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = "command previous";
                    break;
            }
        }
    }
}
package com.augmentum.minote.ui;

import com.augmentum.minote.R;
import com.augmentum.minote.constant.BtnTitle;
import com.augmentum.minote.constant.DialogTitle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

public class AlarmActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        String content = getIntent().getStringExtra("content");
        AlertDialog.Builder ab = new Builder(this);
        ab.setTitle(DialogTitle.ALARM);
        ab.setMessage(content);
        ab.setCancelable(false);
        ab.setPositiveButton(BtnTitle.KNOWN, new Onclick());
        ab.setNegativeButton(BtnTitle.TO_SEE, new Onclick());
        ab.create().show();
    }

    class Onclick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            mediaPlayer.stop();
            finish();

        }

    }
}

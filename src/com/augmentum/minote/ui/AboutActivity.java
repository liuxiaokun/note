package com.augmentum.minote.ui;

import com.augmentum.minote.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity implements OnClickListener {

    private Button commend;
    private static final String smsBody = "HI, 最近我用小米便签感觉很好用，推荐你试试！http://sj.xiaomi.com/notes/xmnotes.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        commend = (Button) findViewById(R.id.btn_commend);
        commend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.btn_commend:

            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.putExtra("sms_body", smsBody);
            sendIntent.setData(Uri.parse("smsto:" + ""));
            startActivity(sendIntent);
            break;

        default:
            break;
        }

    }

}

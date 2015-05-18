package com.augmentum.minote.ui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.augmentum.minote.MainActivity;
import com.augmentum.minote.R;
import com.augmentum.minote.constant.BtnTitle;
import com.augmentum.minote.constant.DialogTitle;
import com.augmentum.minote.constant.MyColor;
import com.augmentum.minote.constant.ToastConst;
import com.augmentum.minote.constant.WeiboConsts;
import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;
import com.augmentum.minote.util.DateUtil;
import com.augmentum.minote.util.ToWeekDay;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.AccessTokenKeeper;

public class AddNoteActivity extends Activity implements OnClickListener {

    public static final String TITLE = "设定时间提醒我";
    public static final String SETTING = "设定";
    public static final String CLEAR = "清除";
    public static final String ACTION = "com.augmentum.minote.widget";

    private InputMethodManager inputMethodManager;
    private int mAppWidgetId;
    private ImageButton changecolor;
    private LinearLayout colorBoard;
    private LinearLayout adjustFont;
    private LinearLayout addNoteHead;
    private SeekBar seekbarFont;
    private EditText noteBg;
    private ImageView yellow;
    private ImageView red;
    private ImageView blue;
    private ImageView green;
    private ImageView white;
    private TextView addNoteDatetime;
    private ImageButton remindIcon;
    private TextView remindTips;

    private TimePicker timePicker;
    private Button setDate;
    private boolean isNewNote;
    private boolean flag;
    private Note exitedNote;

    private NoteDao noteDao;
    private int color = MyColor.YELLOW;
    private int id;
    private long rowId;
    private String fontSize = "20";
    private String remaining;
    private AlarmManager mAlarmManager;

    private Note saveNote;
    private Intent intent;
    private List<Integer> dateTime = new ArrayList<Integer>(5);

    // weibo
    private static Handler handler;
    private Oauth2AccessToken accessToken;
    private Weibo weibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        initView();
        initData();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /*
     * private void ininWeiBoSDK() {
     * 
     * weiboAPI = WeiboSDK.createWeiboAPI(this, WeiboConsts.APP_KEY); }
     * 
     * private void regWeibo() {
     * 
     * weiboAPI.registerApp(); }
     * 
     * private void reqTextMsg() {
     * 
     * TextObject textObject = new TextObject(); textObject.text =
     * noteBg.getText().toString();
     * 
     * WeiboMessage weiboMessage = new WeiboMessage(); weiboMessage.mediaObject
     * = textObject;
     * 
     * SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
     * req.transaction = String.valueOf(System.currentTimeMillis()); req.message
     * = weiboMessage;
     * 
     * weiboAPI.sendRequest(this, req); }
     */

    public void initData() {

        accessToken = AccessTokenKeeper.readAccessToken(AddNoteActivity.this);
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                accessToken = (Oauth2AccessToken) msg.obj;

                if (accessToken.isSessionValid()) {
                    AccessTokenKeeper.keepAccessToken(AddNoteActivity.this, accessToken);
                    Toast.makeText(AddNoteActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    reqMsg();
                }
            }
        };
    }

    private void initView() {

        noteDao = new NoteDaoImpl(AddNoteActivity.this);

        changecolor = (ImageButton) findViewById(R.id.btn_changecolor);
        remindIcon = (ImageButton) findViewById(R.id.remind_icon);
        remindTips = (TextView) findViewById(R.id.remind_tips);
        colorBoard = (LinearLayout) findViewById(R.id.color_board);
        adjustFont = (LinearLayout) findViewById(R.id.adjust_font);
        addNoteHead = (LinearLayout) findViewById(R.id.addnote_head);
        noteBg = (EditText) findViewById(R.id.notes_bg);
        addNoteDatetime = (TextView) findViewById(R.id.add_note_datetime);
        seekbarFont = (SeekBar) findViewById(R.id.seekbar_font);
        seekbarFont.setMax(50);
        yellow = (ImageView) findViewById(R.id.current_yellow);
        red = (ImageView) findViewById(R.id.current_red);
        blue = (ImageView) findViewById(R.id.current_blue);
        green = (ImageView) findViewById(R.id.current_green);
        white = (ImageView) findViewById(R.id.current_white);

        saveNote = new Note();

        changecolor.setOnClickListener(this);
        yellow.setOnClickListener(this);
        red.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);
        white.setOnClickListener(this);

        noteBg.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (v.getId() == R.id.notes_bg) {
                    adjustFont.setVisibility(View.GONE);
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {

        intent = getIntent();
        exitedNote = (Note) intent.getSerializableExtra("item");
        id = intent.getIntExtra("item_id", 0);

        if (null != exitedNote) {
            // set the background color according to the data.
            color = exitedNote.getColor();

            if (null != exitedNote.getFont()) {
                fontSize = exitedNote.getFont();
                noteBg.setTextSize(Integer.parseInt(fontSize));
            } else {
                noteBg.setTextSize(Integer.parseInt(fontSize));
            }
            // current note is not new.
            isNewNote = false;
            // set the data which the note lasted updated.
            String datetime = DateUtil.getMonthAndTime(Long.parseLong(exitedNote.getAddTime()));
            addNoteDatetime.setText(datetime);
            // set the note's content.
            String content = exitedNote.getContent();
            noteBg.setText(content);

            if (null != exitedNote.getRemindTime()) {
                remindIcon.setVisibility(View.VISIBLE);
                long current = System.currentTimeMillis();
                long setMillis = Long.parseLong(exitedNote.getRemindTime());

                if ((setMillis - current) > 0) {
                    remaining = DateUtil.whenRemind(setMillis - current) + "后提醒";
                } else {
                    remaining = "提醒时间已过";
                }
                remindTips.setText(remaining);
            }
        } else if (id != 0) {
            exitedNote = noteDao.findById(id);
            color = exitedNote.getColor();

            if (null != exitedNote.getFont()) {
                fontSize = exitedNote.getFont();
                noteBg.setTextSize(Integer.parseInt(fontSize));
            } else {
                noteBg.setTextSize(Integer.parseInt(fontSize));
            }
            // current note is not new.
            isNewNote = false;
            // set the data which the note lasted updated.
            String datetime = DateUtil.getMonthAndTime(Long.parseLong(exitedNote.getAddTime()));
            addNoteDatetime.setText(datetime);
            // set the note's content.
            String content = exitedNote.getContent();
            noteBg.setText(content);

            if (null != exitedNote.getRemindTime()) {
                remindIcon.setVisibility(View.VISIBLE);
                long current = System.currentTimeMillis();
                long setMillis = Long.parseLong(exitedNote.getRemindTime());

                if ((setMillis - current) > 0) {
                    remaining = DateUtil.whenRemind(setMillis - current) + "后提醒";
                } else {
                    remaining = "提醒时间已过";
                }
                remindTips.setText(remaining);
            }
        } else {
            // the current note is the new.
            SharedPreferences sp = this.getSharedPreferences("SP", MODE_PRIVATE);
            color = sp.getInt("color", MyColor.YELLOW);
            isNewNote = true;
            String datetime = DateUtil.getMonthAndTime(System.currentTimeMillis());
            addNoteDatetime.setText(datetime);
        }
        setBgColor(color);

        noteBg.requestFocus();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (20 == resultCode) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {

        Intent data = new Intent();
        setResult(20, data);
        finish();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.note_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        inputMethodManager.hideSoftInputFromWindow(noteBg.getWindowToken(), 0);
        switch (item.getItemId()) {
        // the menu of the remind me.
        case R.id.remind_me:
            View view = getLayoutInflater().inflate(R.layout.timepicker, null);
            timePicker = (TimePicker) view.findViewById(R.id.time_picker);
            setDate = (Button) view.findViewById(R.id.btn_set_date);

            if (null != exitedNote && null != exitedNote.getRemindTime()) {
                long remindTime = Long.parseLong(exitedNote.getRemindTime());
                setDate.setText(DateUtil.getDateAndWeek(remindTime));
            } else {
                setDate.setText(DateUtil.getDateAndWeek(System.currentTimeMillis()));
            }

            Calendar c = Calendar.getInstance();
            dateTime.add(0, c.get(Calendar.YEAR));
            dateTime.add(1, c.get(Calendar.MONTH));
            dateTime.add(2, c.get(Calendar.DAY_OF_MONTH));
            dateTime.add(3, c.get(Calendar.HOUR_OF_DAY));
            dateTime.add(4, c.get(Calendar.MINUTE));

            timePicker.setIs24HourView(true);
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                    dateTime.set(3, hourOfDay);
                    dateTime.set(4, minute);
                }
            });

            setDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Calendar c = Calendar.getInstance();
                    if (v.getId() == R.id.btn_set_date) {

                        new DatePickerDialog(AddNoteActivity.this, new OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                setDate.setText(ToWeekDay.toWeekDay(year, monthOfYear, dayOfMonth));
                                dateTime.set(0, year);
                                dateTime.set(1, monthOfYear);
                                dateTime.set(2, dayOfMonth);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
            });

            AlertDialog.Builder ad = new Builder(AddNoteActivity.this);
            ad.setTitle(TITLE);
            ad.setView(view);
            ad.setPositiveButton(SETTING, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    long remindTime = DateUtil.getMillisecond(dateTime.get(0) - 1900, dateTime.get(1), dateTime.get(2),
                            dateTime.get(3), dateTime.get(4));
                    if (isNewNote) {
                        saveNote.setRemindTime(String.valueOf(remindTime));
                    } else {
                        exitedNote.setRemindTime(String.valueOf(remindTime));
                    }
                    mAlarmManager = (AlarmManager) AddNoteActivity.this.getSystemService(Service.ALARM_SERVICE);
                    Intent alertIntent = new Intent(AddNoteActivity.this, AlarmReceiver.class);
                    alertIntent.putExtra("content", noteBg.getText().toString());
                    PendingIntent pi = PendingIntent.getBroadcast(AddNoteActivity.this, 0, alertIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, remindTime, pi);
                    long current = System.currentTimeMillis();
                    if ((remindTime - current) > 0) {
                        remaining = DateUtil.whenRemind(remindTime - current) + "后提醒";
                    } else {
                        remaining = "提醒时间已过";
                    }
                    remindIcon.setVisibility(View.VISIBLE);
                    remindTips.setText(remaining);
                }
            });
            ad.setNegativeButton(CLEAR, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    remindIcon.setVisibility(View.GONE);
                    remindTips.setText("");

                }
            }).create();
            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            ad.show();
            break;

        case R.id.add_note:
            Intent intent = new Intent(AddNoteActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, 1);
            break;

        case R.id.delete_note:
            new AlertDialog.Builder(AddNoteActivity.this).setTitle(DialogTitle.CONFIRM_DELETE)
                    .setPositiveButton(BtnTitle.CONFIRM, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (isNewNote) {
                                noteBg.setText("");
                                finish();
                            } else {
                                finish();
                                noteDao.delete(exitedNote.getId());
                            }
                        }
                    }).setNegativeButton(BtnTitle.CANCEL, null).create().show();
            break;

        case R.id.font_size:
            adjustFont.setVisibility(View.VISIBLE);
            seekbarFont.setProgress(Integer.parseInt(fontSize));
            seekbarFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    // do nothing.
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                    // do nothing.
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    noteBg.setTextSize(progress);
                    fontSize = String.valueOf(progress);
                }
            });
            break;

        case R.id.add_desktop:
            Intent addDesktop = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addDesktop.putExtra("duplicate", false);
            String content = noteBg.getText().toString();
            Parcelable icon = Intent.ShortcutIconResource.fromContext(AddNoteActivity.this, R.drawable.icon_one);
            addDesktop.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

            if (!TextUtils.isEmpty(content)) {
                String title = content.substring(0, content.length());
                addDesktop.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
                Intent in = new Intent(AddNoteActivity.this, AddNoteActivity.class);
                in.putExtra("item_id", exitedNote.getId());
                // set the shortcut title and icon.
                addDesktop.putExtra(Intent.EXTRA_SHORTCUT_INTENT, in);
                sendBroadcast(addDesktop);
                Toast.makeText(AddNoteActivity.this, ToastConst.HAD_ADDED, Toast.LENGTH_SHORT).show();
            }
            break;

        case R.id.share:
            /*
             * AlertDialog.Builder ab = new Builder(AddNoteActivity.this);
             * String s[] = { DialogTitle.SMS, DialogTitle.EMAIL };
             * ab.setItems(s, new DialogInterface.OnClickListener() {
             * 
             * @Override public void onClick(DialogInterface dialog, int which)
             * {
             * 
             * if (0 == which) { Intent sms = new Intent(Intent.ACTION_SENDTO);
             * String smsBody = noteBg.getText().toString();
             * sms.putExtra("sms_body", smsBody); sms.setData(Uri.parse("smsto:"
             * + "")); startActivity(sms); } else if (1 == which) { Intent mail
             * = new Intent(Intent.ACTION_SEND); String[] tos = {
             * "ataaw.com@gmail.com" }; mail.putExtra(Intent.EXTRA_EMAIL, tos);
             * mail.putExtra(Intent.EXTRA_TEXT, noteBg.getText());
             * mail.putExtra(Intent.EXTRA_SUBJECT, "share");
             * mail.setType("message/rfc822");
             * AddNoteActivity.this.startActivity(Intent.createChooser(mail,
             * "选择要使用的应用")); }
             * 
             * } }); ab.create().show();break;
             */

            if (accessToken.isSessionValid()) {
                reqMsg();

            } else {
                weibo = Weibo.getInstance(WeiboConsts.APP_KEY, WeiboConsts.REDIRECT_URL, WeiboConsts.SCOPE);
                weibo.anthorize(this, new AuthDialogListener());
            }
            break;
        default:
            break;
        }
        return true;
    }

    @Override
    protected void onPause() {

        String content = noteBg.getText().toString();

        if (!TextUtils.isEmpty(content)) {

            if (isNewNote) {
                saveNote.setColor(color);
                saveNote.setContent(content);
                saveNote.setIsFolder(0);
                saveNote.setFont(String.valueOf(fontSize));
                if (null != intent.getStringExtra("parent_folder")) {
                    saveNote.setParentFolder(intent.getStringExtra("parent_folder"));
                }
                rowId = noteDao.add(saveNote);
                // widget related.
                // if (rowId > 0) {

                // int appWidgetId =
                // getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                // AppWidgetManager.INVALID_APPWIDGET_ID);
                // Intent createIntent = new Intent();
                // createIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                // appWidgetId);
                // setResult(RESULT_OK, createIntent);

                // Intent inent = new Intent("com.augmentum.minote.widget");
                // Bundle b = new Bundle();
                // b.putString("content", content);
                // intent.putExtra("content", content);
                // intent.putExtra("id", rowId);
                // sendBroadcast(inent);
                // }
                SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
                Editor editor = sp.edit();
                editor.putInt("color", color);
                editor.commit();
            } else if (id != 0) {
                //
                exitedNote.setColor(color);
                exitedNote.setContent(content);
                exitedNote.setFont(String.valueOf(fontSize));
                noteDao.update(exitedNote);
                Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                exitedNote.setColor(color);
                exitedNote.setContent(content);
                exitedNote.setFont(String.valueOf(fontSize));
                noteDao.update(exitedNote);
            }
        } else {

            if (isNewNote) {
                // do nothing.
            } else {
                noteDao.delete(exitedNote.getId());
            }
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.btn_changecolor:
            if (flag == false) {
                switch (color) {

                case Color.RED:
                    red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
                    break;

                case Color.YELLOW:
                    yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
                    break;

                case Color.BLUE:
                    blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
                    break;

                case Color.GREEN:
                    green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
                    break;

                case Color.WHITE:
                    white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
                    break;

                default:
                    break;
                }
                colorBoard.setVisibility(View.VISIBLE);
                flag = true;
            } else {
                colorBoard.setVisibility(View.GONE);
                flag = false;
            }
            break;

        case R.id.current_yellow:
            yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
            red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);

            // update the background image.
            addNoteHead.setBackgroundResource(R.drawable.notes_header_yellow);
            noteBg.setBackgroundResource(R.drawable.notes_bg_yellow);

            // log the current color
            color = MyColor.YELLOW;
            colorBoard.setVisibility(View.GONE);
            break;

        case R.id.current_red:
            yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
            blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_pink);
            noteBg.setBackgroundResource(R.drawable.notes_bg_pink);
            // log the current color
            color = MyColor.RED;
            colorBoard.setVisibility(View.GONE);
            break;

        case R.id.current_blue:
            yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
            green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_blue);
            noteBg.setBackgroundResource(R.drawable.notes_bg_blue);
            // log the current color
            color = MyColor.BLUE;
            colorBoard.setVisibility(View.GONE);
            break;

        case R.id.current_green:
            yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
            white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_green);
            noteBg.setBackgroundResource(R.drawable.notes_bg_green);
            // log the current color
            color = MyColor.GREEN;
            colorBoard.setVisibility(View.GONE);
            break;

        case R.id.current_white:
            yellow.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            red.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            blue.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            green.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian_bai);
            white.setBackgroundResource(R.drawable.zhengwen_xuanyanse_dangqian);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_gray);
            noteBg.setBackgroundResource(R.drawable.notes_bg_gray);
            // log the current color
            color = MyColor.WHITE;
            colorBoard.setVisibility(View.GONE);
            break;

        default:
            break;
        }
    }

    private void setBgColor(int color) {

        // setting the edit background color.
        switch (color) {
        case MyColor.RED:
            noteBg.setBackgroundResource(R.drawable.notes_bg_pink);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_pink);
            break;
        case MyColor.GREEN:
            noteBg.setBackgroundResource(R.drawable.notes_bg_green);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_green);
            break;
        case MyColor.YELLOW:
            noteBg.setBackgroundResource(R.drawable.notes_bg_yellow);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_yellow);
            break;
        case MyColor.WHITE:
            noteBg.setBackgroundResource(R.drawable.notes_bg_gray);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_gray);
            break;
        case MyColor.BLUE:
            noteBg.setBackgroundResource(R.drawable.notes_bg_blue);
            addNoteHead.setBackgroundResource(R.drawable.notes_header_blue);
            break;

        default:
            break;
        }
    }

    @Override
    public void onBackPressed() {

        // first step
        Intent in = getIntent();
        Bundle extras = in.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        // Editor editor = sp.edit();
        // editor.putLong(mAppWidgetId+"", mAppWidgetId);
        // editor.commit();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AddNoteActivity.this);
        Intent resp = new Intent(this, AddNoteActivity.class);
        resp.putExtra("" + id, mAppWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resp, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widget_tv, pendingIntent);
        views.setTextViewText(R.id.widget_tv, noteBg.getText());

        switch (color) {
        case MyColor.RED:
            views.setInt(R.id.widget_tv, "setBackgroundResource", R.drawable.widget_small_red);
            break;
        case MyColor.GREEN:
            views.setInt(R.id.widget_tv, "setBackgroundResource", R.drawable.widget_small_green);
            break;
        case MyColor.YELLOW:
            views.setInt(R.id.widget_tv, "setBackgroundResource", R.drawable.widget_small_yellow);
            break;
        case MyColor.WHITE:
            views.setInt(R.id.widget_tv, "setBackgroundResource", R.drawable.widget_small_gray);
            break;
        case MyColor.BLUE:
            views.setInt(R.id.widget_tv, "setBackgroundResource", R.drawable.widget_small_blue);
            break;

        default:
            break;
        }

        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
        super.onBackPressed();
    }

    // weibo
    private void reqMsg() {

        final String data = noteBg.getText().toString();

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {

                    HttpClient httpClient = new DefaultHttpClient();

                    List<BasicNameValuePair> nameValuePairList = new ArrayList<BasicNameValuePair>();
                    nameValuePairList.add(new BasicNameValuePair("status", data));
                    nameValuePairList.add(new BasicNameValuePair("access_token", accessToken.getToken()));

                    HttpPost httpPost = new HttpPost("https://api.weibo.com/2/statuses/update.json");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, "utf-8"));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    String temp = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = new JSONObject(temp);

                    System.out.println(accessToken.getToken());
                    System.out.println(jsonObject);

                    Toast.makeText(AddNoteActivity.this, "发送成功", Toast.LENGTH_SHORT).show();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
 
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onCancel() {
            Toast.makeText(AddNoteActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Bundle values) {

            final String code = values.getString("code");

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        HttpPost post = new HttpPost(
                                URI.create("https://api.weibo.com/oauth2/access_token?client_id="
                                        + WeiboConsts.APP_KEY + "&client_secret=" + WeiboConsts.APP_SECERT
                                        + "&grant_type=authorization_code&redirect_uri="
                                        + WeiboConsts.REDIRECT_URL + "&code=" + code));
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpResponse response = httpClient.execute(post);

                        if (response.getStatusLine().getStatusCode() == 200) {

                            String temp = EntityUtils.toString(response.getEntity());
                            JSONObject o = new JSONObject(temp);
                            String access_token = o.getString("access_token");
                            String express_in = o.getString("expires_in");

                            Oauth2AccessToken accessToken = new Oauth2AccessToken();

                            accessToken.setExpiresIn(express_in);
                            accessToken.setToken(access_token);

                            AccessTokenKeeper.keepAccessToken(AddNoteActivity.this, accessToken);

                            Message msg = new Message();
                            msg.obj = accessToken;
                            handler.sendMessage(msg);

                            // AccessTokenKeeper.keepAccessToken(NoteActivity.this,
                            // accessToken);
                            // Toast.makeText(NoteActivity.this,
                            // R.string.toast_weibo_auth_success,
                            // Toast.LENGTH_SHORT).show();
                            // reqMsg();
                        }
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(AddNoteActivity.this, "授权错误" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }
}

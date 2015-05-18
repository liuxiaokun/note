package com.augmentum.minote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.augmentum.minote.adapter.NotesAdapter;
import com.augmentum.minote.constant.BtnTitle;
import com.augmentum.minote.constant.DialogTitle;
import com.augmentum.minote.constant.ItemTitle;
import com.augmentum.minote.constant.ToastConst;
import com.augmentum.minote.constant.WeiboConsts;
import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;
import com.augmentum.minote.ui.AboutActivity;
import com.augmentum.minote.ui.AddNoteActivity;
import com.augmentum.minote.ui.FolderActivity;
import com.augmentum.minote.util.BackUpUtil;
import com.augmentum.minote.util.DateUtil;
import com.augmentum.minote.util.OutputTxtFile;
import com.augmentum.minote.util.ParseXmlUtil;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.AccessTokenKeeper;

public class MainActivity extends Activity implements OnClickListener {

    private static final String MAIL_SUBJECT = "备份小米便签";
    private static final String CHOOSE_ACTIVITY = "选择要使用的应用";
    private static final String SHARE_URL = "http://www.xiaomi.com";
    private static final String OUTPUT_XML_TO_SD = "已将备份文件\"notes.xml导出至SD卡\"xm_notes\"目录";
    private static final String OUTPUT_TXT_TO_SD = "已将TXT文件\"notes_" + "notes_"
            + DateUtil.getYearAndDate(System.currentTimeMillis()) + ".txt" + "\"输出至SD卡\"xm_notes\"目录";

    private InputMethodManager inputMethodManager;
    private LinearLayout mAddFolderGroup;
    private LinearLayout mDelNoteUi;
    private LinearLayout mMoveToFolder;

    private ListView mListItems;
    private ImageButton mEnterAddNoteUi;
    private ImageView mNoteShadow;

    private NotesAdapter mNotesAdapter;

    private Button mDelNote;
    private Button mDelNoteCancel;
    private Button mMoveFolderConfirm;
    private Button mMoveFolderCancel;
    private Button mCompleteFolder;
    private Button mCancelFolder;

    private EditText mAddFolderName;

    private List<Note> mNoteList;
    private NoteDao noteDao = new NoteDaoImpl(MainActivity.this);

    // sina weibo
    private Weibo mWeibo;
    
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mWeibo = Weibo.getInstance(WeiboConsts.APP_KEY, WeiboConsts.REDIRECT_URL, WeiboConsts.SCOPE);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        validatePassword();
    }

    @Override
    protected void onResume() {

        int count = noteDao.getNoteCount();

        if (count == 0) {
            mNoteShadow.setVisibility(View.GONE);
        } else {
            mNoteShadow.setVisibility(View.VISIBLE);
        }
        mNoteList = noteDao.findAll();
        mNotesAdapter = new NotesAdapter(MainActivity.this, mNoteList);
        mListItems.setAdapter(mNotesAdapter);
        super.onResume();
    }

    private void initViews() {

        mAddFolderGroup = (LinearLayout) findViewById(R.id.add_folder_group);
        mDelNoteUi = (LinearLayout) findViewById(R.id.del_note_ui);
        mMoveToFolder = (LinearLayout) findViewById(R.id.move_to_folder);

        mListItems = (ListView) findViewById(R.id.list_items);
        mEnterAddNoteUi = (ImageButton) findViewById(R.id.enter_addnote_ui);
        mNoteShadow = (ImageView) findViewById(R.id.note_shadow);

        mDelNote = (Button) findViewById(R.id.btn_del_note);
        mDelNoteCancel = (Button) findViewById(R.id.del_note_cancel);

        mMoveFolderConfirm = (Button) findViewById(R.id.move_folder_confirm);
        mMoveFolderCancel = (Button) findViewById(R.id.move_folder_cancel);
        mCompleteFolder = (Button) findViewById(R.id.complete_folder);
        mCancelFolder = (Button) findViewById(R.id.cancel_folder);

        mAddFolderName = (EditText) findViewById(R.id.add_folder_name);

        mDelNoteCancel.setOnClickListener(this);
        mMoveFolderCancel.setOnClickListener(this);
        mEnterAddNoteUi.setOnClickListener(this);
        mCompleteFolder.setOnClickListener(this);
        mCancelFolder.setOnClickListener(this);
        mDelNote.setOnClickListener(this);
        mMoveFolderConfirm.setOnClickListener(this);
        mListItems.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (null == mNoteList.get(position).getFolderName()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, AddNoteActivity.class);
                    intent.putExtra("item", mNoteList.get(position));
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent();

                    intent.setClass(MainActivity.this, FolderActivity.class);
                    intent.putExtra("item", mNoteList.get(position));
                    intent.putExtra("folderName", mNoteList.get(position).getFolderName());
                    MainActivity.this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * the listener of the menu.
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {

        case R.id.menu_add_note:
            Intent addNote = new Intent();
            addNote.setClass(MainActivity.this, AddNoteActivity.class);
            startActivity(addNote);
            break;

        case R.id.menu_new_folder:
            mAddFolderGroup.setVisibility(View.VISIBLE);
            mAddFolderName.requestFocus();
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            break;

        case R.id.menu_move_folder:

            List<Note> folders = noteDao.findAllFolder();

            if (0 == folders.size() || noteDao.getNoteCount() == 0) {
                Toast.makeText(MainActivity.this, ToastConst.HAS_NO_NOTE, Toast.LENGTH_SHORT).show();
                break;
            }
            mMoveToFolder.setVisibility(View.VISIBLE);
            mNotesAdapter.checkItem(false, "MOVE");
            break;

        case R.id.menu_del_note:
            mDelNoteUi.setVisibility(View.VISIBLE);
            mNotesAdapter.checkItem(false, "DELETE");
            break;

        case R.id.menu_export:
            new AlertDialog.Builder(MainActivity.this).setItems(
                    new String[] { ItemTitle.TOSDCARD, ItemTitle.TOMAILBOX }, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {
                                boolean flag = OutputTxtFile.toSdCard(noteDao.findAllNoteAndFolder());

                                if (flag) {
                                    AlertDialog.Builder ab = new Builder(MainActivity.this);
                                    ab.setTitle(DialogTitle.OUTPUT_SUCCESS);
                                    ab.setMessage(OUTPUT_TXT_TO_SD);
                                    ab.setPositiveButton(DialogTitle.CONFIRM, null);
                                    ab.create().show();
                                }
                            }
                        }
                    }).show();
            break;

        case R.id.menu_backup:
            new AlertDialog.Builder(MainActivity.this).setItems(
                    new String[] { ItemTitle.TOSDCARD, ItemTitle.TOMAILBOX }, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (0 == which) {
                                boolean isSuccess = BackUpUtil.backup(noteDao.findAllNoteAndFolder());

                                if (isSuccess) {
                                    AlertDialog.Builder ab = new Builder(MainActivity.this);
                                    ab.setTitle(DialogTitle.BACKUP_SUCCESS);
                                    ab.setMessage(OUTPUT_XML_TO_SD);
                                    ab.setPositiveButton(DialogTitle.CONFIRM, null);
                                    ab.create().show();
                                }
                            } else if (1 == which) {
                                /*
                                 * Intent intent = new
                                 * Intent(Intent.ACTION_SEND); String[] tos = {
                                 * "ataaw.com@gmail.com" };
                                 * intent.putExtra(Intent.EXTRA_EMAIL, tos);
                                 * intent.putExtra(Intent.EXTRA_TEXT, "...");
                                 * intent.putExtra(Intent.EXTRA_SUBJECT,
                                 * MAIL_SUBJECT);
                                 * intent.setType("message/rfc822");
                                 * MainActivity
                                 * .this.startActivity(Intent.createChooser
                                 * (intent, CHOOSE_ACTIVITY));
                                 */
                                BackUpUtil.backup(noteDao.findAllNoteAndFolder());
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_SUBJECT, MAIL_SUBJECT);
                                intent.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/xm_notes/ntoes.xml");
                                MainActivity.this.startActivity(Intent.createChooser(intent, CHOOSE_ACTIVITY));
                            }
                        }
                    }).show();
            break;

        case R.id.menu_store:
            new AlertDialog.Builder(MainActivity.this).setTitle(DialogTitle.RESTORE)
                    .setPositiveButton(BtnTitle.CONFIRM, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator
                                    + "xm_notes" + File.separator + "notes.xml");
                            InputStream in;
                            try {
                                in = new FileInputStream(file);
                                List<Note> list = ParseXmlUtil.parse(in);
                                noteDao.deleteAll();
                                for (Note tem : list) {
                                    noteDao.add(tem);
                                }
                                mNoteList.clear();
                                mNoteList.addAll(noteDao.findAll());
                                mNotesAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, ToastConst.RESTORE_SUCCESS, Toast.LENGTH_LONG).show();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton(BtnTitle.CANCEL, null).show();
            break;

        case R.id.set_pwd_dialog:

            SharedPreferences sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE);
            String pwd = sharedPreferences.getString("password", null);

            if (null == pwd) {
                View layout = getLayoutInflater().inflate(R.layout.set_password_dialog, null);
                final EditText mSetPassword = (EditText) layout.findViewById(R.id.set_password);
                final EditText mConfirmPassword = (EditText) layout.findViewById(R.id.confirm_password);
                new AlertDialog.Builder(this).setTitle(DialogTitle.SETPWD).setView(layout)
                        .setPositiveButton(BtnTitle.CONFIRM, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String pwd = mSetPassword.getText().toString();
                                String rePwd = mConfirmPassword.getText().toString();

                                if (pwd.equals(rePwd)) {
                                    SharedPreferences sp = MainActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
                                    Editor editor = sp.edit();
                                    editor.putString("password", pwd);
                                    editor.commit();
                                    Toast.makeText(MainActivity.this, ToastConst.SET_PWD_SUCCESS, Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(MainActivity.this, ToastConst.SET_PWD_FAILUE, Toast.LENGTH_SHORT)
                                            .show();
                                }

                            }
                        }).setNegativeButton(BtnTitle.RETURN, null).show();
            } else {
                AlertDialog.Builder ab = new Builder(MainActivity.this);
                ab.setItems(new String[] { DialogTitle.MODIFY_PWD, DialogTitle.CLEAR_PWD },
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                case 0:
                                    View layout = getLayoutInflater().inflate(R.layout.set_password_dialog, null);
                                    final EditText mSetPassword = (EditText) layout.findViewById(R.id.set_password);
                                    final EditText mConfirmPassword = (EditText) layout
                                            .findViewById(R.id.confirm_password);
                                    new AlertDialog.Builder(MainActivity.this).setTitle(DialogTitle.CHANGE_PWD)
                                            .setView(layout)
                                            .setPositiveButton(BtnTitle.CONFIRM, new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String pwd = mSetPassword.getText().toString();
                                                    String rePwd = mConfirmPassword.getText().toString();

                                                    if (pwd.equals(rePwd)) {
                                                        SharedPreferences sp = MainActivity.this.getSharedPreferences(
                                                                "SP", MODE_PRIVATE);
                                                        Editor editor = sp.edit();
                                                        editor.putString("password", pwd);
                                                        editor.commit();
                                                        Toast.makeText(MainActivity.this, ToastConst.SET_PWD_SUCCESS,
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, ToastConst.SET_PWD_FAILUE,
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).setNegativeButton(BtnTitle.RETURN, null).show();
                                    break;
                                case 1:
                                    SharedPreferences sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE);
                                    sharedPreferences.edit().remove("password").commit();
                                    Toast.makeText(MainActivity.this, ToastConst.PWD_CLEARED, Toast.LENGTH_SHORT)
                                            .show();
                                    break;

                                default:
                                    break;
                                }
                            }
                        });
                ab.create().show();
            }
            break;

        case R.id.shared_to_friends:
            Intent intent = new Intent();
            intent.setData(Uri.parse(SHARE_URL));
            intent.setAction(Intent.ACTION_VIEW);
            this.startActivity(intent);
            break;

        case R.id.about:
            Intent about = new Intent();
            about.setClass(MainActivity.this, AboutActivity.class);
            startActivity(about);
            break;

        default:
            break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.enter_addnote_ui:

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
            break;

        case R.id.cancel_folder:
            mAddFolderGroup.setVisibility(View.GONE);
            inputMethodManager.hideSoftInputFromWindow(mAddFolderName.getWindowToken(), 0);
            break;

        case R.id.complete_folder:
            String folderName = mAddFolderName.getText().toString();

            if (!TextUtils.isEmpty(folderName)) {
                Note note = new Note(null, 1, folderName);
                noteDao.add(note);
                mNoteList.clear();
                mNoteList.addAll(noteDao.findAll());
                mNotesAdapter.notifyDataSetChanged();
                mAddFolderGroup.setVisibility(View.GONE);
            }
            break;

        case R.id.btn_del_note:

            List<Integer> ids = mNotesAdapter.getDeleteIdList();
            for (Integer tem : ids) {
                noteDao.delete(tem);
            }
            mNoteList.clear();
            mNoteList.addAll(noteDao.findAll());
            mNotesAdapter.notifyDataSetChanged();
            mDelNoteUi.setVisibility(View.GONE);
            mNotesAdapter.checkItem(true, "DELETE");
            int count = noteDao.getNoteCount();

            if (count == 0) {
                mNoteShadow.setVisibility(View.GONE);
            } else {
                mNoteShadow.setVisibility(View.VISIBLE);
            }
            break;

        case R.id.del_note_cancel:
            mDelNoteUi.setVisibility(View.GONE);
            mNotesAdapter.checkItem(true, "DELETE");
            break;

        case R.id.move_folder_confirm:

            AlertDialog.Builder ab = new Builder(MainActivity.this);
            final List<Note> folders = noteDao.findAllFolder();
            final String nameArray[] = new String[folders.size()];
            for (int i = 0; i < folders.size(); i++) {
                nameArray[i] = folders.get(i).getFolderName();
            }
            ab.setItems(nameArray, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String clickName = nameArray[which];
                    List<Note> notes = mNotesAdapter.getMovedNoteList();

                    for (Note tem : notes) {
                        tem.setParentFolder(clickName);
                        noteDao.update(tem);
                    }
                    mNoteList.clear();
                    mNoteList.addAll(noteDao.findAll());
                    mNotesAdapter.notifyDataSetChanged();
                    mMoveToFolder.setVisibility(View.GONE);
                    mNotesAdapter.checkItem(true, "MOVE");
                    int count = noteDao.getNoteCount();

                    if (count == 0) {
                        mNoteShadow.setVisibility(View.GONE);
                    } else {
                        mNoteShadow.setVisibility(View.VISIBLE);
                    }
                }
            });
            ab.create().show();

            break;
        case R.id.move_folder_cancel:
            mMoveToFolder.setVisibility(View.GONE);
            mNotesAdapter.checkItem(true, "MOVE");
            break;

        default:
            break;
        }
    }

    // listen the button which named by back.

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (mMoveToFolder.getVisibility() == View.VISIBLE) {
                mMoveToFolder.setVisibility(View.GONE);
                mNotesAdapter.checkItem(true, "MOVE");
                return true;
            }
            if (mDelNoteUi.getVisibility() == View.VISIBLE) {
                mDelNoteUi.setVisibility(View.GONE);
                mNotesAdapter.checkItem(true, "DELETE");
                return true;
            }
            if (mAddFolderGroup.getVisibility() == View.VISIBLE) {
                mAddFolderGroup.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void validatePassword() {

        SharedPreferences sp = MainActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
        final String truePassword = sp.getString("password", null);

        if (null != truePassword) {
            AlertDialog.Builder dialog = new Builder(MainActivity.this);
            dialog.setTitle(DialogTitle.INPUT_PWD);
            dialog.setCancelable(false);
            final View view = (View) getLayoutInflater().inflate(R.layout.validate_pwd, null);
            dialog.setView(view);
            dialog.setPositiveButton(BtnTitle.CONFIRM, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    EditText password = (EditText) view.findViewById(R.id.input_pwd);
                    String input = password.getText().toString();
                    if (truePassword.equals(input)) {
                        dialog.dismiss();
                    } else {
                        validatePassword();
                        Toast.makeText(MainActivity.this, DialogTitle.WRONG_PWD, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.create().show();
        }
    }

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {

            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(mAccessToken
                        .getExpiresTime()));

                AccessTokenKeeper.keepAccessToken(MainActivity.this, mAccessToken);
                Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {

            Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {

            Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {

            Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

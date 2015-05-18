package com.augmentum.minote.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.augmentum.minote.R;
import com.augmentum.minote.adapter.NotesInFolderAdapter;
import com.augmentum.minote.constant.ToastConst;
import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;

public class FolderActivity extends Activity implements OnClickListener {

    private String currentFolderName;
    private TextView folderName;
    private ListView mListItems;
    private ImageButton folderAddNote;
    private NotesInFolderAdapter adapter;
    private List<Note> listFolderNote;

    private LinearLayout mDelFolderNote;
    private Button mDelFolderNoteConfirm;
    private Button mDelFolderNoteCancel;

    private LinearLayout mModifyFolder;
    private EditText modifyFolderName;
    private Button modifyFolderNameComplete;
    private Button modifyFolderNameCancel;
    private ImageView mNoteShadow;

    private LinearLayout moveOutFolder;
    private Button moveOutFolderConfirm;
    private Button moveOutFolderCancel;

    private Note currentFolder;

    private NoteDao noteDao = new NoteDaoImpl(FolderActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        initView();
    }

    @Override
    protected void onStart() {

        int count = noteDao.getCountInFolder(currentFolderName);
        if (count == 0) {
            mNoteShadow.setVisibility(View.GONE);
        } else {
            mNoteShadow.setVisibility(View.VISIBLE);
        }
        listFolderNote = noteDao.findAllByFolder(currentFolderName);
        adapter = new NotesInFolderAdapter(this, listFolderNote);
        mListItems.setAdapter(adapter);
        super.onStart();
    }

    private void initView() {

        folderName = (TextView) findViewById(R.id.folder_name);
        folderAddNote = (ImageButton) findViewById(R.id.folder_add_note);
        mListItems = (ListView) findViewById(R.id.list_folder_items);

        mDelFolderNote = (LinearLayout) findViewById(R.id.del_folder_note);
        mDelFolderNoteConfirm = (Button) findViewById(R.id.del_folder_note_confirm);
        mDelFolderNoteCancel = (Button) findViewById(R.id.del_folder_note_cancel);

        mModifyFolder = (LinearLayout) findViewById(R.id.modify_folder);
        modifyFolderName = (EditText) findViewById(R.id.et_folder_name);
        modifyFolderNameComplete = (Button) findViewById(R.id.modify_folder_name_complete);
        modifyFolderNameCancel = (Button) findViewById(R.id.modify_folder_name_cancel);

        moveOutFolder = (LinearLayout) findViewById(R.id.move_out_of_folder);
        moveOutFolderConfirm = (Button) findViewById(R.id.move_out_folder_confirm);
        moveOutFolderCancel = (Button) findViewById(R.id.move_out_folder_cancel);

        mNoteShadow = (ImageView) findViewById(R.id.note_shadow);
        Intent intent = getIntent();
        currentFolderName = intent.getStringExtra("folderName");
        currentFolder = (Note) intent.getSerializableExtra("item");
        folderName.setText(currentFolderName);

        folderAddNote.setOnClickListener(this);
        modifyFolderNameComplete.setOnClickListener(this);
        modifyFolderNameCancel.setOnClickListener(this);
        moveOutFolderConfirm.setOnClickListener(this);
        moveOutFolderCancel.setOnClickListener(this);
        mDelFolderNoteConfirm.setOnClickListener(this);
        mDelFolderNoteCancel.setOnClickListener(this);

        mListItems.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (null == listFolderNote.get(position).getFolderName()) {
                    Intent intent = new Intent();
                    intent.setClass(FolderActivity.this, AddNoteActivity.class);
                    intent.putExtra("item", listFolderNote.get(position));
                    FolderActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(FolderActivity.this, FolderActivity.class);
                    intent.putExtra("folderName", listFolderNote.get(position).getFolderName());
                    FolderActivity.this.startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.folder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.folder_add_note:
            Intent intent = new Intent();
            intent.putExtra("parent_folder", currentFolderName);
            intent.setClass(this, AddNoteActivity.class);
            startActivity(intent);
            break;

        case R.id.modify_folder:
            mModifyFolder.setVisibility(View.VISIBLE);
            modifyFolderName.setText(currentFolderName);
            break;

        case R.id.move_out_folder:
            moveOutFolder.setVisibility(View.VISIBLE);
            adapter.checkItem(false, "MOVE");
            break;
        case R.id.folder_delete_note:
            mDelFolderNote.setVisibility(View.VISIBLE);
            adapter.checkItem(false, "DELETE");
            break;
        case R.id.add_desktop:

            Intent addDesktop = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            Parcelable icon = Intent.ShortcutIconResource.fromContext(FolderActivity.this, R.drawable.icon_group);
            Intent in = new Intent(FolderActivity.this, FolderActivity.class);
            in.putExtra("folderName", currentFolderName);
            // set the shortcut title and icon.
            addDesktop.putExtra(Intent.EXTRA_SHORTCUT_NAME, currentFolderName);
            addDesktop.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            addDesktop.putExtra(Intent.EXTRA_SHORTCUT_INTENT, in);
            addDesktop.putExtra("duplicate", false);
            sendBroadcast(addDesktop);
            Toast.makeText(FolderActivity.this, ToastConst.HAD_ADDED, Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.folder_add_note:
            Intent intent = new Intent();
            intent.putExtra("parent_folder", currentFolderName);
            intent.setClass(this, AddNoteActivity.class);
            startActivity(intent);
            break;

        case R.id.modify_folder_name_complete:
            String folderName = modifyFolderName.getText().toString();

            if (!TextUtils.isEmpty(folderName)) {
                currentFolder.setFolderName(folderName);
                noteDao.update(currentFolder);
                currentFolderName = folderName;

                for (Note tem : listFolderNote) {
                    tem.setParentFolder(folderName);
                    noteDao.update(tem);
                }
                listFolderNote.clear();
                listFolderNote.addAll(noteDao.findAllByFolder(folderName));
                adapter.notifyDataSetChanged();
                this.folderName.setText(folderName);
                mModifyFolder.setVisibility(View.GONE);
            }
            break;
        case R.id.modify_folder_name_cancel:
            mModifyFolder.setVisibility(View.GONE);
            break;

        case R.id.move_out_folder_confirm:
            List<Note> notes = adapter.getMovedNoteList();
            for (Note tem : notes) {
                tem.setParentFolder(null);
                noteDao.update(tem);
            }
            listFolderNote.clear();
            listFolderNote.addAll(noteDao.findAllByFolder(currentFolderName));
            adapter.notifyDataSetChanged();
            moveOutFolder.setVisibility(View.GONE);
            adapter.checkItem(true, "MOVE");
            int count = noteDao.getCountInFolder(currentFolderName);
            if (count == 0) {
                mNoteShadow.setVisibility(View.GONE);
            } else {
                mNoteShadow.setVisibility(View.VISIBLE);
            }
            break;

        case R.id.move_out_folder_cancel:
            moveOutFolder.setVisibility(View.GONE);
            adapter.checkItem(true, "MOVE");
            break;

        case R.id.del_folder_note_confirm:
            List<Integer> ids = adapter.getDeleteIdList();
            for (Integer tem : ids) {
                noteDao.delete(tem);
            }
            listFolderNote.clear();
            listFolderNote.addAll(noteDao.findAllByFolder(currentFolderName));
            adapter.notifyDataSetChanged();
            mDelFolderNote.setVisibility(View.GONE);
            adapter.checkItem(true, "DELETE");
            int countDel = noteDao.getCountInFolder(currentFolderName);
            if (countDel == 0) {
                mNoteShadow.setVisibility(View.GONE);
            } else {
                mNoteShadow.setVisibility(View.VISIBLE);
            }
            break;

        case R.id.del_folder_note_cancel:
            mDelFolderNote.setVisibility(View.GONE);
            adapter.checkItem(true, "DELETE");
            break;

        default:
            break;
        }

    }

}

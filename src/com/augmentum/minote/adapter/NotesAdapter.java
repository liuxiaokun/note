package com.augmentum.minote.adapter;

import java.util.ArrayList;
import java.util.List;

import com.augmentum.minote.R;
import com.augmentum.minote.constant.MyColor;
import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;
import com.augmentum.minote.util.DateUtil;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Note> mNotes;
    private boolean flag = true;
    private LinearLayout mItem;

    // note item layout.
    private TextView briefContent;
    private TextView addNoteDate;
    private CheckBox checkedNote;

    // item folder layout.
    private TextView mItemFolderName;
    private TextView mAddFolderDate;
    private CheckBox checkedFolder;
    private ImageView pngTip;

    private String operation;

    private List<Integer> deleteIdList = new ArrayList<Integer>();
    private List<Note> movedNoteList = new ArrayList<Note>();

    public NotesAdapter(Context context, List<Note> notes) {

        this.mContext = context;
        this.mNotes = notes;
    }

    @Override
    public int getCount() {

        return mNotes.size();
    }

    @Override
    public Object getItem(int position) {

        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Integer myPosition = position;
        View item = null;

        if (mNotes.get(position).getIsFolder() == 0) {
            item = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            briefContent = (TextView) item.findViewById(R.id.brief_content);
            addNoteDate = (TextView) item.findViewById(R.id.add_note_date);
            checkedNote = (CheckBox) item.findViewById(R.id.checked_note);
            pngTip = (ImageView) item.findViewById(R.id.png_tip);
            // set the brief content.
            String brief = mNotes.get(position).getContent();

            String remindTime = mNotes.get(position).getRemindTime();
            if (remindTime != null && Long.parseLong(remindTime) > System.currentTimeMillis()) {
                pngTip.setVisibility(View.VISIBLE);
            }
            briefContent.setText(brief);
            // set the add time.
            long date = Long.parseLong(mNotes.get(position).getAddTime());
            String time;
            if (DateUtils.isToday(date)) {
                time = DateUtil.getTime(date);
            } else {
                time = DateUtil.getWeek(date);
            }            
            addNoteDate.setText(time);
            
            mItem = (LinearLayout) item.findViewById(R.id.ll_item);

            switch (mNotes.get(position).getColor()) {
            case MyColor.BLUE:
                mItem.setBackgroundResource(R.drawable.biaoqian_lan);
                break;

            case MyColor.RED:
                mItem.setBackgroundResource(R.drawable.biaoqian_fen);
                break;

            case MyColor.YELLOW:
                mItem.setBackgroundResource(R.drawable.biaoqian_huang);
                break;

            case MyColor.WHITE:
                mItem.setBackgroundResource(R.drawable.biaoqian_hui);
                break;

            case MyColor.GREEN:
                mItem.setBackgroundResource(R.drawable.biaoqian_lu);
                break;

            default:
                mItem.setBackgroundResource(R.drawable.biaoqian_huang);
                break;
            }

            if (flag) {
                addNoteDate.setVisibility(View.VISIBLE);
                checkedNote.setVisibility(View.GONE);
            } else {
                addNoteDate.setVisibility(View.GONE);
                checkedNote.setVisibility(View.VISIBLE);
            }
            checkedNote.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if ("MOVE".equals(operation)) {
                        if (isChecked) {
                            movedNoteList.add(mNotes.get(myPosition));
                        } else {
                            movedNoteList.remove(mNotes.get(myPosition));
                        }
                    }

                    if ("DELETE".equals(operation)) {
                        if (isChecked) {
                            deleteIdList.add(mNotes.get(myPosition).getId());
                        } else {
                            deleteIdList.remove(mNotes.get(myPosition).getId());
                        }
                    }
                }
            });
        } else {
            item = LayoutInflater.from(mContext).inflate(R.layout.listview_item_folder, null);
            mItemFolderName = (TextView) item.findViewById(R.id.item_folder_name);
            checkedFolder = (CheckBox) item.findViewById(R.id.checked_folder);
            mAddFolderDate = (TextView) item.findViewById(R.id.add_folder_date);
            String folderName = mNotes.get(position).getFolderName();
            // TODO
            NoteDao noteDao = new NoteDaoImpl(mContext);
            mItemFolderName.setText(folderName + "(" + noteDao.getCountInFolder(folderName) + ")");
            // set the add time.
            mAddFolderDate = (TextView) item.findViewById(R.id.add_folder_date);
            Note note = noteDao.findFolderLastAdded(folderName);

            if (null != note) {
                long date = Long.parseLong(note.getAddTime());
                String time;
                if (DateUtils.isToday(date)) {
                    time = DateUtil.getTime(date);
                } else {
                    time = DateUtil.getWeek(date);
                }
                mAddFolderDate.setText(time);
            }

            if (flag) {
                mAddFolderDate.setVisibility(View.VISIBLE);
                checkedFolder.setVisibility(View.GONE);
            } else if (!false && "MOVE".equals(operation)) {
                mAddFolderDate.setVisibility(View.VISIBLE);
                checkedFolder.setVisibility(View.GONE);
            } else {
                mAddFolderDate.setVisibility(View.GONE);
                checkedFolder.setVisibility(View.VISIBLE);
            }
            checkedFolder.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if ("MOVE".equals(operation)) {
                        if (isChecked) {
                            movedNoteList.add(mNotes.get(myPosition));
                        } else {
                            movedNoteList.remove(mNotes.get(myPosition));
                        }
                    }

                    if ("DELETE".equals(operation)) {
                        if (isChecked) {
                            deleteIdList.add(mNotes.get(myPosition).getId());
                        } else {
                            deleteIdList.remove(mNotes.get(myPosition).getId());
                        }
                    }

                }
            });
        }

        return item;
    }

    // true £ºdatetime. false£ºcheckbox.
    public void checkItem(boolean isVisible, String operation) {

        flag = isVisible;
        this.operation = operation;
        notifyDataSetChanged();
    }

    public List<Integer> getDeleteIdList() {

        return deleteIdList;
    }

    public void setDeleteIdList(List<Integer> deleteIdList) {

        this.deleteIdList = deleteIdList;
    }

    public List<Note> getMovedNoteList() {

        return movedNoteList;
    }

    public void setMovedNoteList(List<Note> movedNoteList) {

        this.movedNoteList = movedNoteList;
    }
}

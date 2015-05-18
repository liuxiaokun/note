package com.augmentum.minote.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augmentum.minote.R;
import com.augmentum.minote.constant.MyColor;
import com.augmentum.minote.model.Note;
import com.augmentum.minote.util.DateUtil;

public class NotesInFolderAdapter extends BaseAdapter {

    private Context mContext;
    private List<Note> mNotes;
    private boolean flag = true;
    private LayoutInflater mInflater;
    private String operation;
    private List<Integer> deleteIdList = new ArrayList<Integer>();
    private List<Note> movedNoteList = new ArrayList<Note>();

    public NotesInFolderAdapter(Context context, List<Note> notes) {

        this.mContext = context;
        this.mNotes = notes;
        mInflater = LayoutInflater.from(mContext);
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

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_item, null);
            holder.briefContent = (TextView) convertView.findViewById(R.id.brief_content);
            holder.addNoteDate = (TextView) convertView.findViewById(R.id.add_note_date);
            holder.checkedNote = (CheckBox) convertView.findViewById(R.id.checked_note);
            holder.pngTip = (ImageView) convertView.findViewById(R.id.png_tip);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.ll_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Integer myPosition = position;
        // set the brief content.
        String remindTime = mNotes.get(position).getRemindTime();

        if (remindTime != null && Long.parseLong(remindTime) > System.currentTimeMillis()) {
            holder.pngTip.setVisibility(View.VISIBLE);
        }
        String brief = mNotes.get(position).getContent();
        holder.briefContent.setText(brief);

        // set the add time.
        String date = mNotes.get(position).getAddTime();
        String time = DateUtil.getTime(Long.parseLong(date));
        holder.addNoteDate.setText(time);

        switch (mNotes.get(position).getColor()) {
        case MyColor.BLUE:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_lan);
            break;

        case MyColor.RED:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_fen);
            break;

        case MyColor.YELLOW:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_huang);
            break;

        case MyColor.WHITE:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_hui);
            break;

        case MyColor.GREEN:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_lu);
            break;

        default:
            holder.linearLayout.setBackgroundResource(R.drawable.biaoqian_huang);
            break;
        }

        if (flag) {
            holder.addNoteDate.setVisibility(View.VISIBLE);
            holder.checkedNote.setVisibility(View.GONE);
        } else {
            holder.addNoteDate.setVisibility(View.GONE);
            holder.checkedNote.setVisibility(View.VISIBLE);
        }
        holder.checkedNote.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && "MOVE".equals(operation)) {
                    movedNoteList.add(mNotes.get(myPosition));
                } else {
                    movedNoteList.remove(mNotes.get(myPosition));
                }

                if (isChecked && "DELETE".equals(operation)) {
                    deleteIdList.add(mNotes.get(myPosition).getId());
                } else {
                    deleteIdList.remove(mNotes.get(myPosition).getId());
                }

            }
        });

        return convertView;
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

    public final class ViewHolder {

        public TextView briefContent;
        public TextView addNoteDate;
        public CheckBox checkedNote;
        public ImageView pngTip;
        public LinearLayout linearLayout;
    }
}

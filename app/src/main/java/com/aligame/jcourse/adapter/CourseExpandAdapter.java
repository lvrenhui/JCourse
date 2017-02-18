package com.aligame.jcourse.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.library.realm.RealmHelper;
import com.aligame.jcourse.library.toast.ToastUtil;
import com.aligame.jcourse.model.CourseRm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lvrh on 17/2/14.
 */

public class CourseExpandAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<CourseRm> mData;
    private MediaPlayer mediaPlayer;
    private RealmHelper mRealmHleper;
    private int lastPos = -1;

    private static final String BASE_PATH = "baidu/jap/audio/";

    Handler monitorHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mediaPlayerMonitor();
        }

    };

    public CourseExpandAdapter(Context context, List<CourseRm> mData) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;

        mRealmHleper = new RealmHelper(context);
        mediaPlayer = new MediaPlayer();

        initSchedule();//播放时长显示

    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    private String getParentTitle(int groupPosition) {
        return mData.get(groupPosition).title;
    }

    private List<String> getChildren(int groupPosition) {
        List<String> parts = new ArrayList<>();
        parts.add("1.刨根问底");
        parts.add("2.顺藤摸瓜");
        parts.add("3.移花接木");
        parts.add("4.枝繁叶茂");
        return parts;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getChildren(groupPosition).size();
    }

    @Override
    public CourseRm getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Integer getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getPart(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.course_parent, null);
        }
        view.setTag(R.layout.course_parent, groupPosition);
        view.setTag(R.layout.course_item, -1);
        TextView text = (TextView) view.findViewById(R.id.expand_title);
        text.setText(getParentTitle(groupPosition));
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.course_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.viewBtn = (Button) convertView.findViewById(R.id.btn_play);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(getChildren(groupPosition).get(childPosition));
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = getPlayer(groupPosition, childPosition);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updateItemText(groupPosition, childPosition, 1);
                } else {
                    int seek = getChild(groupPosition, childPosition);
                    ToastUtil.showToast(context, seek + "");
                    mediaPlayer.seekTo(seek * 1000);
                    mediaPlayer.start();
                    updateItemText(groupPosition, childPosition, 0);
                }
            }
        });
        holder.time.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    private void updateItemText(int groupPosition, int childPosition, int state) {
        String play_label = "NULL";
        if (state == 0) {
            play_label = "PAUSE";
        } else {
            play_label = "PLAY";
        }



    }

    public final class ViewHolder {
        public TextView title;
        public Button viewBtn;
        public TextView time;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private MediaPlayer getPlayer(int groupPosition, int childPosition) {
        String course_file = BASE_PATH + String.format("第%d课.mp3", groupPosition + 1);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + course_file;
        if (lastPos >= 0 && lastPos == groupPosition) {
            return mediaPlayer;
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, Uri.parse(path));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lastPos = groupPosition;
        return mediaPlayer;
    }

    private void mediaPlayerMonitor() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                String time = getTimeString(mediaPlayer.getCurrentPosition()) + "/" + getTimeString(mediaPlayer.getDuration());
//                ToastUtil.showToast(context, time);
//                notifyDataSetChanged();
            }
        }
    }

    private void initSchedule() {
        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);
        myScheduledExecutorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
                    }
                },
                200, //initialDelay
                200, //delay
                TimeUnit.MILLISECONDS);
    }

    public void realseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();
//        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        buf
//                .append(String.format("%02d", hours))
//                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }
}

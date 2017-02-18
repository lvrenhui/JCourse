package com.aligame.jcourse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.library.realm.RealmHelper;
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
    private List<Integer[]> playStatus = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private RealmHelper mRealmHleper;
    private int lastGroupPos = -1;
    private int lastChildPos = -1;

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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });

        initPlayStatus(mData);

        initSchedule();//播放时长显示

    }

    private void initPlayStatus(List<CourseRm> mData) {
        for (int i = 0; i < mData.size(); i++) {
            playStatus.add(new Integer[]{0, 0, 0, 0, 0});
        }
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
        parts.add("1.身临其境");
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
            holder.playBtn = (Button) convertView.findViewById(R.id.btn_play);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(getChildren(groupPosition).get(childPosition));
        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = getPlayer(groupPosition, childPosition);
                int seek = getChild(groupPosition, childPosition) * 1000;
                Log.d("####player", groupPosition + "_" + childPosition);
                //同一课程内点击
                if (mediaPlayer.isPlaying()) {
                    //点击的是当前正在播放的part,暂停
                    if (lastChildPos == childPosition) {
                        mediaPlayer.pause();
                        updatePlayStatus(groupPosition, childPosition, 0);
                    } else {
                        //不用停止，直接跳到指定part
                        mediaPlayer.seekTo(seek);
                        updatePlayStatus(groupPosition, childPosition, 1);
                    }
                } else if (lastGroupPos == groupPosition && lastChildPos == childPosition) {
                    //恢复上次暂停的播放
                    mediaPlayer.start();
                    updatePlayStatus(groupPosition, childPosition, 1);
                } else {
                    //开始新的播放
                    mediaPlayer.seekTo(seek);
                    mediaPlayer.start();
                    updatePlayStatus(groupPosition, childPosition, 1);
                }
                //记住当前播放的目标
                lastGroupPos = groupPosition;
                lastChildPos = childPosition;
            }

        });

//        holder.playBtn.setText(getPlayLabel(groupPosition, childPosition));
        holder.playBtn.setBackgroundResource(getBtnImg(groupPosition, childPosition));
        holder.time.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    private int getBtnImg(int groupPosition, int childPosition) {
        if (playStatus.get(groupPosition)[childPosition] == 0) {
            return android.R.drawable.ic_media_play;
        } else {
            return android.R.drawable.ic_media_pause;
        }
    }

    private String getPlayLabel(int groupPosition, int childPosition) {

        if (playStatus.get(groupPosition)[childPosition] == 0) {
            return "PLAY";
        } else {
            return "PAUSE";
        }
    }

    private void updatePlayStatus(int groupPosition, int childPosition, int state) {
        //先将所有状态还原
        for (int i = 0; i < playStatus.size(); i++) {
            for (int j = 0; j < 4; j++) {
                playStatus.get(i)[j] = 0;
            }
        }
        playStatus.get(groupPosition)[childPosition] = state;
        notifyDataSetInvalidated();
    }

    public void finish() {
        this.realseMedia();
    }

    public final class ViewHolder {
        public TextView title;
        public Button playBtn;
        public TextView time;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private MediaPlayer getPlayer(int groupPosition, int childPosition) {
        String course_file = BASE_PATH + String.format("第%d课.mp3", groupPosition + 1);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + course_file;
        if (lastGroupPos >= 0 && lastGroupPos == groupPosition) {
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
        mediaPlayer = null;
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

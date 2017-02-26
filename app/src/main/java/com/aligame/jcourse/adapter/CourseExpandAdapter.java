package com.aligame.jcourse.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.aligame.jcourse.activity.MainActivity;
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
    //    private List<Integer[]> playStatus = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private RealmHelper mRealmHleper;
    private int lastGroupPos = -1;
    private int lastChildPos = -1;
    private String play_time = "";
    private ISeek seek_control;

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
        this.seek_control = (MainActivity) context;

        mRealmHleper = new RealmHelper(context);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });

//        initPlayStatus(mData);

        initSchedule();//播放时长显示

    }

   /* private void initPlayStatus(List<CourseRm> mData) {
        for (int i = 0; i < mData.size(); i++) {
            playStatus.add(new Integer[]{0, 0, 0, 0, 0});
        }
    }*/

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    private String getParentTitle(int groupPosition) {
        return mData.get(groupPosition).title;
    }

    private List<String> getChildren(int groupPosition) {
        List<String> parts = new ArrayList<>();
        parts.add("♧身临其境");
        parts.add("♧顺藤摸瓜");
        parts.add("♧移花接木");
        parts.add("♧枝繁叶茂");
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
        TextView title = (TextView) view.findViewById(R.id.expand_title);
        TextView time = (TextView) view.findViewById(R.id.seek_time);
        title.setText(getParentTitle(groupPosition));
        time.setText(getSeekTime(groupPosition));
        return view;
    }

    private String getSeekTime(int groupPosition) {
        if (lastGroupPos == groupPosition) {
            return play_time;
        }
        return "";
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
        //btn的事件也得写一遍，不然点不了
        holder.playBtn.setOnClickListener(getOnClickListener(groupPosition, childPosition));
        convertView.setOnClickListener(getOnClickListener(groupPosition, childPosition));
        holder.playBtn.setOnLongClickListener(getOnLongClickListener(groupPosition, childPosition));
        convertView.setOnLongClickListener(getOnLongClickListener(groupPosition, childPosition));

//        holder.playBtn.setBackgroundResource(getBtnImg(groupPosition, childPosition));
        holder.time.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    private void savePartTime(final int groupPosition, final int childPosition, final int currentPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认调整" + getChildren(groupPosition).get(childPosition) + "开始时间？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int seek = currentPosition / 1000 - 2;//留点缓冲时间
//                        mData.get(groupPosition).setPart(childPosition, seek);
                        mRealmHleper.updateCourse(groupPosition + 1, getParentTitle(groupPosition), childPosition, seek);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /*private void updatePlayStatus(int groupPosition, int childPosition, int state) {
        //先将所有状态还原
        for (int i = 0; i < playStatus.size(); i++) {
            for (int j = 0; j < 4; j++) {
                playStatus.get(i)[j] = 0;
            }
        }
        playStatus.get(groupPosition)[childPosition] = state;
        notifyDataSetInvalidated();

    }*/

    public void finish() {
        this.realseMedia();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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

    private MediaPlayer getPlayer(final int groupPosition) {
        //显示进度条
        seek_control.show(1);
        if (lastGroupPos >= 0 && lastGroupPos == groupPosition) {
            return mediaPlayer;
        } else {
            initPlayer(groupPosition);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initPlayer(groupPosition);
                seek_control.reset();
            }
        });
        return mediaPlayer;
    }

    private void initPlayer(int groupPosition) {
        try {
            mediaPlayer.reset();
            String course_file = BASE_PATH + String.format("第%d课.mp3", groupPosition + 1);
            String path = Environment.getExternalStorageDirectory().getPath() + "/" + course_file;
            mediaPlayer.setDataSource(context, Uri.parse(path));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e) {
            ToastUtil.showToast(context, "播放失败，找不到对应文件！");
            e.printStackTrace();
        }

    }

    private void mediaPlayerMonitor() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                long cur = mediaPlayer.getCurrentPosition();
                long dur = mediaPlayer.getDuration();
                int seek = (int) (((double) cur / dur) * 100);
                Log.d("####", seek + "");
                seek_control.seekTo(seek);
                play_time = getTimeString(cur) + "/" + getTimeString(dur);
                notifyDataSetChanged();
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
                1000, //delay
                TimeUnit.MILLISECONDS);
    }

    public void realseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

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

    private View.OnClickListener getOnClickListener(final int groupPosition, final int childPosition)

    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = getPlayer(groupPosition);
                int seek = getChild(groupPosition, childPosition) * 1000;
                //同一课程内点击
                if (mediaPlayer.isPlaying()) {
                    //不用停止，直接跳到指定part
                    mediaPlayer.seekTo(seek);
                } else {
                    mediaPlayer.seekTo(seek);
                    mediaPlayer.start();
                }
                seek_control.btn_to_start();
                //记住当前播放的目标
                lastGroupPos = groupPosition;
                lastChildPos = childPosition;
            }

        };
    }

    private View.OnLongClickListener getOnLongClickListener(final int groupPosition, final int childPosition) {
        return new View.OnLongClickListener()

        {
            @Override
            public boolean onLongClick(View v) {
                if (!play_time.equals("") && lastGroupPos == groupPosition) {
                    savePartTime(groupPosition, childPosition, mediaPlayer.getCurrentPosition());
                    return true;
                } else {
                    return false;
                }

            }

        };
    }
}

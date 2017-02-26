//package com.aligame.jcourse.adapter;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.aligame.jcourse.R;
//import com.aligame.jcourse.model.AudioFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by lvrh on 17/2/14.
// */
//
//public class CourseListAdapter extends BaseAdapter {
//    private Context context;
//    private LayoutInflater mInflater;
//    private List<AudioFile> mData = new ArrayList<>();
//    private MediaPlayer mediaPlayer;
//    private int currentPos = -1;
//    Handler monitorHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            mediaPlayerMonitor();
//        }
//
//    };
//
//    public CourseListAdapter(Context context) {
//        this.context = context;
//        this.mInflater = LayoutInflater.from(context);
//
//        initSchedule();//播放时长显示
//    }
//
//    @Override
//    public int getCount() {
//        return mData.size();
//    }
//
//    @Override
//    public AudioFile getItem(int position) {
//        return mData.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = mInflater.inflate(R.layout.course_item, null);
//            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
//            holder.viewBtn = (Button) convertView.findViewById(R.id.btn_play);
//            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
//            convertView.setTag(holder);
//
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.title.setText(mData.get(position).getTitle());
//        final ViewHolder finalHolder = holder;
//        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initPlayer();
//                currentPos = position;
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.btn_to_pause();
//                    finalHolder.viewBtn.setText("Play");
//                } else {
//                    mediaPlayer.btn_to_start();
//                    finalHolder.viewBtn.setText("Pause");
//                }
//
//            }
//        });
//        holder.time.setText(mData.get(position).getTime());
//
//        return convertView;
//    }
//
//    public final class ViewHolder {
//        public TextView title;
//        public Button viewBtn;
//        public TextView time;
//    }
//
//    public void initData() {
//        for (int i = 0; i < 10; i++) {
//            mData.add(new AudioFile("title" + i, "url" + i, ""));
//        }
//
//    }
//
//    private void initPlayer() {
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(context, Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/baidu/1006-1.mp3"));
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void mediaPlayerMonitor() {
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                if (currentPos >= 0) {
//                    String time = getTimeString(mediaPlayer.getCurrentPosition()) + "/" + getTimeString(mediaPlayer.getDuration());
//                    getItem(currentPos).setTime(time);
//                    notifyDataSetChanged();
//                }
//            }
//        }
//    }
//
//    private String getTimeString(long millis) {
//        StringBuffer buf = new StringBuffer();
////        int hours = (int) (millis / (1000 * 60 * 60));
//        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
//        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
//        buf
////                .append(String.format("%02d", hours))
////                .append(":")
//                .append(String.format("%02d", minutes))
//                .append(":")
//                .append(String.format("%02d", seconds));
//
//        return buf.toString();
//    }
//
//    private void initSchedule() {
//        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);
//        myScheduledExecutorService.scheduleWithFixedDelay(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
//                    }
//                },
//                200, //initialDelay
//                200, //delay
//                TimeUnit.MILLISECONDS);
//    }
//}

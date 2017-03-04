package com.aligame.jcourse.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.constant.VideoType;
import com.aligame.jcourse.library.toast.ToastUtil;
import com.aligame.jcourse.model.VideoInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvrh on 17/3/4.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<VideoInfo> mData = new ArrayList<>();
    private MyItemClickListener myItemClickListener;

    public VideoListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void initData(final int videoType) {
        Gson gson = new GsonBuilder().create();
        InputStream stream = null;
        String jsonFile = null;
        if (videoType == VideoType.COURSE_LIST.getValue()) {
            jsonFile = "course.json";
            try {
                stream = context.getAssets().open(jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
            List<String> tmpData = gson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
            for (String s : tmpData) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setTitle(s);
                videoInfo.setPath(s + ".flv");
                mData.add(videoInfo);
            }

        } else if (videoType == VideoType.DAY_PRACTICE.getValue()) {
            jsonFile = "day_practice.json";
            try {
                stream = context.getAssets().open(jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
            mData = gson.fromJson(json, new TypeToken<List<VideoInfo>>() {
            }.getType());
        }

        setMyItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String video_file;
                if (videoType == VideoType.COURSE_LIST.getValue()) {
//                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/course/" + mData.get(postion).getPath();
                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/course/" + postion+".flv";
                } else if (videoType == VideoType.DAY_PRACTICE.getValue()) {
                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/day/" + mData.get(postion).getPath();

                } else {
                    ToastUtil.showToast(context, "暂不支持该类型视频播放!");
                    return;
                }
                intent.setDataAndType(Uri.parse(video_file), "video/*");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.course_video_item, parent, false);
        return new MyViewHolder(view, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv;
        MyItemClickListener listener;

        public MyViewHolder(View view, MyItemClickListener listener) {
            super(view);
            this.listener = listener;
            view.setOnClickListener(this);
            tv = (TextView) view.findViewById(R.id.video_title);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getLayoutPosition());
            }

        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }
}

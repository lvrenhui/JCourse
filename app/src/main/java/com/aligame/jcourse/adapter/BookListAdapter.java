package com.aligame.jcourse.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.library.toast.ToastUtil;
import com.aligame.jcourse.model.BookInfo;
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
 * Created by lvrh on 17/3/19.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<BookInfo> mData = new ArrayList<>();
    private MyItemClickListener myItemClickListener;

    public BookListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void initData() {

        InputStream stream = null;
        try {
            stream = context.getAssets().open("course.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));

        List<String> titles = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

        for (int i = 0; i < titles.size(); i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setTitle(titles.get(i));
            mData.add(bookInfo);
        }

        setMyItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                ToastUtil.showToast(context, "" + postion);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String video_file;
//                if (videoType == VideoType.COURSE_LIST.getValue()) {
////                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/course/" + mData.get(postion).getPath();
//                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/course/" + postion + ".flv";
//                } else if (videoType == VideoType.DAY_PRACTICE.getValue()) {
//                    video_file = "file://" + Environment.getExternalStorageDirectory() + "/baidu/jap/video/day/" + mData.get(postion).getPath();
//
//                } else {
//                    ToastUtil.showToast(context, "暂不支持该类型视频播放!");
//                    return;
//                }
//                intent.setDataAndType(Uri.parse(video_file), "video/*");
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_item, parent, false);
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
        BookListAdapter.MyItemClickListener listener;

        public MyViewHolder(View view, BookListAdapter.MyItemClickListener listener) {
            super(view);
            this.listener = listener;
            view.setOnClickListener(this);
            tv = (TextView) view.findViewById(R.id.book_title);
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
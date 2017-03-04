package com.aligame.jcourse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.VideoListAdapter;
import com.aligame.jcourse.constant.VideoType;

public class VideoListFragment extends Fragment {

    private static final String ARG_PARAM_TYPE = "VIDEO_TYPE";
    private int videoType;
    private RecyclerView recyclerView;

    public static VideoListFragment newInstance(Integer param) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_TYPE, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoType = getArguments().getInt(ARG_PARAM_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.video_list);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);//设置为一个3列的纵向网格布局
        recyclerView.setLayoutManager(mLayoutManager);
        //添加分隔线
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        VideoListAdapter adapter = new VideoListAdapter(getContext());
        adapter.initData(videoType);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

}

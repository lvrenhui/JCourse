package com.aligame.jcourse.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.AudioExpandlistAdapter;
import com.aligame.jcourse.adapter.ISeek;
import com.aligame.jcourse.library.realm.RealmHelper;
import com.aligame.jcourse.model.CourseRm;
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

public class AudioListFragment extends Fragment implements View.OnClickListener, ISeek {

    AudioExpandlistAdapter audioExpandlistAdapter;
    RealmHelper realmHelper;
    SeekBar seekBar;
    MediaPlayer mediaPlayer = null;
    View seekLayout;
    Button playBtn;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio_list, container, false);

        initListview(rootView);
        initSeekbar(rootView);
        mediaPlayer = audioExpandlistAdapter.getMediaPlayer();

        seekLayout = rootView.findViewById(R.id.layout_seekbar);
        playBtn = (Button) rootView.findViewById(R.id.btn_play);
        playBtn.setOnClickListener(this);


        // Inflate the layout for this fragment
        return rootView;
    }


    private void initListview(View rootView) {
        final ExpandableListView courseListView = (ExpandableListView) rootView.findViewById(R.id.courseList);
        courseListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < audioExpandlistAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        courseListView.collapseGroup(i);
                    }
                }
            }
        });
        realmHelper = new RealmHelper(getContext());
        List<CourseRm> courseRmList = initData();
        audioExpandlistAdapter = new AudioExpandlistAdapter(this, courseRmList);
        courseListView.setAdapter(audioExpandlistAdapter);
    }

    private void initSeekbar(View rootView) {
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true && mediaPlayer != null) {
                    int dur = mediaPlayer.getDuration();
                    int seek = (int) (((double) progress / 100) * dur);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(seek);
                    } else if (mediaPlayer.getCurrentPosition() > 1) {
                        mediaPlayer.seekTo(seek);
                        mediaPlayer.start();
                    }
                    btn_to_start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private List<CourseRm> initData() {
        InputStream stream;
        List<CourseRm> courseRmList = new ArrayList<>();
        try {
            stream = getContext().getAssets().open("course.json");
        } catch (IOException e) {
            return null;
        }

        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));

        List<String> titles = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

        for (int i = 0; i < titles.size(); i++) {
            CourseRm newCourse = new CourseRm();
            newCourse.id = i + 1;
            newCourse.title = titles.get(i);
            newCourse.audio_file = "";
            newCourse.part1_time = 10;
            newCourse.part2_time = 20;
            newCourse.part3_time = 30;
            newCourse.part4_time = 40;

            CourseRm dbCourse = realmHelper.queryById(i + 1);
            if (dbCourse != null) {
                courseRmList.add(dbCourse);
            } else {
                courseRmList.add(newCourse);
            }
        }
        return courseRmList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioExpandlistAdapter.finish();
    }

    @Override
    public void seekTo(int seek) {
        seekBar.setProgress(seek);

    }

    @Override
    public void show(int show) {
        if (show == 1) {
            seekLayout.setVisibility(View.VISIBLE);
        } else {
            seekLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void btn_to_pause() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void btn_to_start() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void reset() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        seekBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_to_pause();
                } else {
                    mediaPlayer.start();
                    btn_to_start();
                }

            }
        }
    }
}

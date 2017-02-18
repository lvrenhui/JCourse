package com.aligame.jcourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.CourseExpandAdapter;
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

public class MainActivity extends AppCompatActivity {
    CourseExpandAdapter courseExpandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //在OnCreate方法中调用下面方法，然后再使用线程，就能在uncaughtException方法中捕获到异常
//        Thread.setDefaultUncaughtExceptionHandler(this);

        final ExpandableListView courseListView = (ExpandableListView) findViewById(R.id.courseList);
        courseListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < courseExpandAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        courseListView.collapseGroup(i);
                    }
                }
            }
        });
        List<CourseRm> courseRmList = initData();
        courseExpandAdapter = new CourseExpandAdapter(this, courseRmList);
        courseListView.setAdapter(courseExpandAdapter);

    }

    private List<CourseRm> initData() {
        InputStream stream;
        List<CourseRm> courseRmList = new ArrayList<>();
        try {
            stream = getAssets().open("course.json");
        } catch (IOException e) {
            return null;
        }

        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));

        List<String> titles = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

        for (String title : titles) {
            CourseRm course = new CourseRm();
            course.title = title;
            course.audio_file = "";
            course.part1_time = 10;
            course.part2_time = 20;
            course.part3_time = 30;
            course.part4_time = 40;
            courseRmList.add(course);
        }
        return courseRmList;
    }

    @Override
    protected void onStop() {
        super.onStop();

        courseExpandAdapter.finish();
    }

    //    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        //在此处理异常， arg1即为捕获到的异常
//        ex.printStackTrace();
//    }
}

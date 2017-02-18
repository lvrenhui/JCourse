package com.aligame.jcourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.CourseExpandAdapter;
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

public class MainActivity extends AppCompatActivity {
    CourseExpandAdapter courseExpandAdapter;
    RealmHelper realmHelper;

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
        realmHelper = new RealmHelper(this);
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
    protected void onDestroy() {
        super.onDestroy();
        courseExpandAdapter.finish();
    }

    //    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        //在此处理异常， arg1即为捕获到的异常
//        ex.printStackTrace();
//    }
}

package com.aligame.jcourse.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CourseRm extends RealmObject {
    @PrimaryKey
    public int id;
    /**
     * 课文标题
     */
    public String title;
    /**
     * 录音文件
     */
    public String audio_file;
    /**
     * 刨根问底
     */
    public Integer part1_time;
    /**
     * 顺藤摸瓜
     */
    public Integer part2_time;
    /**
     * 移花接木
     */
    public Integer part3_time;
    /**
     * 枝繁叶茂
     */
    public Integer part4_time;

    public int getPart(int childPosition) {
        switch (childPosition) {
            case 0:
                return part1_time;
            case 1:
                return part2_time;
            case 2:
                return part3_time;
            case 3:
                return part4_time;
            default:
                return part1_time;
        }
    }
}

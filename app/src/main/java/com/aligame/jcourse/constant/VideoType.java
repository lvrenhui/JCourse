package com.aligame.jcourse.constant;

/**
 * Created by lvrh on 17/3/4.
 */

public enum VideoType {
    COURSE_LIST(1), DAY_PRACTICE(2);

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

    VideoType(int i) {
        value = i;
    }
}

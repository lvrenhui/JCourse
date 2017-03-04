package com.aligame.jcourse;

import com.aligame.jcourse.constant.VideoType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        long dur = 148373;
        int progress = 6;
        int seek = (int) (((double) progress / 100) * dur);
        System.out.println(seek);
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEnum() {
        System.out.println(VideoType.DAY_PRACTICE.getValue());
    }

}
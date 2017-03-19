package com.aligame.jcourse.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.TabFragmentPagerAdapter;
import com.aligame.jcourse.library.view.SyncHorizontalScrollView;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    private RelativeLayout rl_nav;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup rg_nav_content;
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private int indicatorWidth;
    public static String[] tabTitle = {"课文录音", "配套视频", "每日一句", "配套教材"};    //标题
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //在OnCreate方法中调用下面方法，然后再使用线程，就能在uncaughtException方法中捕获到异常
//        Thread.setDefaultUncaughtExceptionHandler(this);

        rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
        iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
        iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);

        initHeaderTab();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabFragmentPagerAdapter mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        //设置缓存数量，避免切换页面时数据丢失
        viewPager.setOffscreenPageLimit(3);

        setListener();

    }

    private void setListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (rg_nav_content != null && rg_nav_content.getChildCount() > position) {
                    rg_nav_content.getChildAt(position).performClick();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        rg_nav_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rg_nav_content.getChildAt(checkedId) != null) {

                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            rg_nav_content.getChildAt(checkedId).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    //执行位移动画
                    iv_nav_indicator.startAnimation(animation);

                    viewPager.setCurrentItem(checkedId);   //ViewPager 跟随一起 切换

                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = rg_nav_content.getChildAt(checkedId).getLeft();

                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? rg_nav_content.getChildAt(checkedId).getLeft() : 0) - rg_nav_content.getChildAt(2).getLeft(), 0);
                }
            }
        });
    }

    private void initHeaderTab() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / tabTitle.length;

        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);

        //获取布局填充器
        mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        //另一种方式获取
//      LayoutInflater mInflater = LayoutInflater.from(this);

        initNavigationHSV();
    }

    private void initNavigationHSV() {

        rg_nav_content.removeAllViews();

        for (int i = 0; i < tabTitle.length; i++) {

            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            rb.setText(tabTitle[i]);
            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
        }

    }

    //    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        //在此处理异常， arg1即为捕获到的异常
//        ex.printStackTrace();
//    }
}

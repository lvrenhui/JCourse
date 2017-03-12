package com.aligame.jcourse.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.aligame.jcourse.R;
import com.aligame.jcourse.library.view.PhotoViewPager;
import com.aligame.jcourse.library.view.PinchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.LinkedList;

public class GalleryActivity extends AppCompatActivity {
    private String[] mImages = new String[]{
            "http://ww2.sinaimg.cn/mw1024/6df127bfjw1esojfinxmxj20xc18gqfm.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1esiveg31hwj20u00gvn18.jpg"
            , "http://ww1.sinaimg.cn/mw1024/6df127bfjw1esivelw317j20u00gvq77.jpg"
            , "http://g.hiphotos.baidu.com/zhidao/pic/item/09fa513d269759eee314015bb3fb43166c22dfde.jpg"
            , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1esbuy81ovzj20ku04taah.jpg"
            , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1esaen9u5k8j20hs0nq75k.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1es1ixs8uctj20hs0vkgpc.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erveujphhxj20xc18gwsy.jpg"
            , "http://ww1.sinaimg.cn/mw1024/6df127bfjw1eroxgfbkopj216o0m543e.jpg"
            , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1erox2ywpn6j218g0xcwjp.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erovfvilebj20hs0vkacd.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1erj3jcayb1j20qo0zkgrv.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfgw1erc5yiqciaj20ke0b0abg.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erbuxu8qa7j20ds0ct3zy.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0erdh1jaj20qo0zkq9j.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0enq1o3lj218g18gnbx.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0gnhidtdj20hs0nogoi.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0gqq4ff9j20hs0non07.jpg"
            , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1eqmfsasl7fj218g0p07a2.jpg"
            , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1eqmfuizagpj218g0r9dms.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        final LinkedList<PinchImageView> viewCache = new LinkedList<>();

        final PhotoViewPager pager = (PhotoViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImages.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(GalleryActivity.this);
                }
                DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).build();
                imageLoader.displayImage(mImages[position], piv, options, getImageLoadingListener());
                container.addView(piv);
                return piv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                PinchImageView piv = (PinchImageView) object;
                container.removeView(piv);
                viewCache.add(piv);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                pager.setMainPinchImageView((PinchImageView) object);
            }
        });
    }

    public ImageLoadingListener getImageLoadingListener() {
        return new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                findViewById(R.id.loading_tv).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                hiddeTv();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        };
    }

    public void hiddeTv() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loading_tv).setVisibility(View.GONE);
            }
        }, 500);
    }
}

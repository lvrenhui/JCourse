package com.aligame.jcourse.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aligame.jcourse.R;
import com.aligame.jcourse.library.toast.ToastUtil;
import com.aligame.jcourse.library.view.PhotoViewPager;
import com.aligame.jcourse.library.view.PinchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.LinkedList;

public class GalleryActivity extends Activity {
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

    PhotoViewPager pager;
    private PopupWindow mPopupWindow;
    RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

//        getActionBar().hide();

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        final LinkedList<View> viewCache = new LinkedList<>();

        pager = (PhotoViewPager) findViewById(R.id.pager);
        //设置图片缓存
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
                View layoutView;
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    layoutView = viewCache.remove();
                    //从缓存拿出来要恢复状态
                    piv = (PinchImageView) layoutView.findViewById(R.id.gallery_img);
                    piv.reset();
                } else {
                    layoutView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.img_gallery, null);
                    piv = (PinchImageView) layoutView.findViewById(R.id.gallery_img);
                }
                DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).build();
                imageLoader.displayImage(mImages[position], piv, options, getImageLoadingListener(layoutView));

                piv.setOnLongClickListener(getLongClick());
                container.addView(layoutView);
                return layoutView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View layout = (View) object;
                container.removeView(layout);
                viewCache.add(layout);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                pager.setMainPinchImageView((PinchImageView) ((View) object).findViewById(R.id.gallery_img));
            }
        });

        parentLayout = (RelativeLayout) findViewById(R.id.rl_parent);

        initPopwindow();
    }

    private void initPopwindow() {
        View popupView = getLayoutInflater().inflate(R.layout.layout_popup, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        popupView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getApplicationContext(), pager.getCurrentItem() + "");
            }
        });
    }

    public ImageLoadingListener getImageLoadingListener(final View layout) {
        return new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                layout.findViewById(R.id.loading_avi).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                layout.findViewById(R.id.loading_avi).setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        };
    }

    public View.OnLongClickListener getLongClick() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPopupWindow.showAtLocation(parentLayout, Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
                return true;
            }
        };
    }

}

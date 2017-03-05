package com.aligame.jcourse.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aligame.jcourse.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by lvrh on 17/3/5.
 */

public class SwipeFragment extends Fragment {
    public static final String[] IMAGE_NAME = {"eagle", "horse", "bonobo", "wolf", "owl", "bear",};
    TextView positionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
        ImageViewTouch imageView = (ImageViewTouch) swipeView.findViewById(R.id.imageView);
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        int total = bundle.getInt("total");
        String imgPath = Environment.getExternalStorageDirectory().getPath() + "/baidu/a.jpg";
        imageView.setImageBitmap(decodeSampledBitmapFromResource(imgPath, 1080, 1920));
        imageView.setVisibility(View.VISIBLE);
        positionText = (TextView) swipeView.findViewById(R.id.position_info);
        positionText.setText(position + 1 + "/" + total);

        return swipeView;
    }

    public static SwipeFragment newInstance(int position, int total) {
        SwipeFragment swipeFragment = new SwipeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("total", total);
        swipeFragment.setArguments(bundle);
        return swipeFragment;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
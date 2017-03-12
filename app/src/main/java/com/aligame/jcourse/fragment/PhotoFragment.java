package com.aligame.jcourse.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aligame.jcourse.R;
import com.aligame.jcourse.activity.GalleryActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    ImageButton cameraBtn;
    ImageView imgPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        cameraBtn = (ImageButton) rootView.findViewById(R.id.btn_camera);
        cameraBtn.setOnClickListener(this);

//        imgPreview = (ImageView) rootView.findViewById(R.id.img_preview);
//
//        ControllableViewPager viewPager = (ControllableViewPager) rootView.findViewById(R.id.pager);
//        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(getContext(), viewPager);
//        pagerAdapter.setmData();
//        viewPager.setAdapter(pagerAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_camera) {

            Intent intent = new Intent(getContext(), GalleryActivity.class);
            startActivity(intent);

            /*
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
            */
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPreview.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/baidu");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}

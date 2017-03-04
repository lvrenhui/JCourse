package com.aligame.jcourse.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.CourseExpandAdapter;
import com.aligame.jcourse.adapter.ISeek;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AudioListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioListFragment extends Fragment implements View.OnClickListener, ISeek {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    CourseExpandAdapter courseExpandAdapter;
    RealmHelper realmHelper;
    SeekBar seekBar;
    MediaPlayer mediaPlayer = null;
    View seekLayout;
    Button playBtn;

    private OnFragmentInteractionListener mListener;

    public AudioListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioListFragment newInstance(String param1, String param2) {
        AudioListFragment fragment = new AudioListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio_list, container, false);

        initListview(rootView);
        initSeekbar(rootView);
        mediaPlayer = courseExpandAdapter.getMediaPlayer();

        seekLayout = rootView.findViewById(R.id.layout_seekbar);
        playBtn = (Button) rootView.findViewById(R.id.btn_play);
        playBtn.setOnClickListener(this);

        rootView.findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/baidu/112.wmv"), "video/*");
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initListview(View rootView) {
        final ExpandableListView courseListView = (ExpandableListView) rootView.findViewById(R.id.courseList);
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
        realmHelper = new RealmHelper(getContext());
        List<CourseRm> courseRmList = initData();
        courseExpandAdapter = new CourseExpandAdapter(this,courseRmList);
        courseListView.setAdapter(courseExpandAdapter);
    }

    private void initSeekbar(View rootView) {
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true && mediaPlayer != null) {
                    int dur = mediaPlayer.getDuration();
                    int seek = (int) (((double) progress / 100) * dur);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(seek);
                    } else if (mediaPlayer.getCurrentPosition() > 1) {
                        mediaPlayer.seekTo(seek);
                        mediaPlayer.start();
                    }
                    btn_to_start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private List<CourseRm> initData() {
        InputStream stream;
        List<CourseRm> courseRmList = new ArrayList<>();
        try {
            stream = getContext().getAssets().open("course.json");
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
    public void onDestroy() {
        super.onDestroy();
        courseExpandAdapter.finish();
    }

    @Override
    public void seekTo(int seek) {
        seekBar.setProgress(seek);

    }

    @Override
    public void show(int show) {
        if (show == 1) {
            seekLayout.setVisibility(View.VISIBLE);
        } else {
            seekLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void btn_to_pause() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void btn_to_start() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void reset() {
        playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        seekBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_to_pause();
                } else {
                    mediaPlayer.start();
                    btn_to_start();
                }

            }
        }
    }
}

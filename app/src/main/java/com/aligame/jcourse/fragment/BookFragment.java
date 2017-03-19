package com.aligame.jcourse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aligame.jcourse.R;
import com.aligame.jcourse.adapter.BookListAdapter;

/**
 * Created by lvrh on 17/3/19.
 */

public class BookFragment extends Fragment {
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.book_list);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);//设置为一个3列的纵向网格布局
        recyclerView.setLayoutManager(mLayoutManager);
        //添加分隔线
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        BookListAdapter adapter = new BookListAdapter(getContext());
        adapter.initData();
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}

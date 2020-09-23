package com.xuqiqiang.camera2.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuqiqiang.camera2.app.ui.filter.MoreFilterAdapter;

import org.wysaid.filter.base.FiltersConstant;
import org.wysaid.filter.origin.SimpleFilter;

public class FiltersActivity extends BaseActivity {

    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecycler = findViewById(R.id.recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(mLayoutManager);
        MoreFilterAdapter mAdapter = new MoreFilterAdapter();
        mAdapter.setOnItemClickListener(new MoreFilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Intent intent = new Intent();
                intent.putExtra("filter", new SimpleFilter(
                        position < FiltersConstant.NAME_FILTERS.length ?
                                FiltersConstant.NAME_FILTERS[position] : ("filter" + position),
                        FiltersConstant.FILTERS[position + 1]));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mRecycler.setAdapter(mAdapter);
    }
}

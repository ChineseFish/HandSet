package com.handheld.IDCardDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.hdhe.idcarddemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridActivity extends AppCompatActivity {

    private GridView grid_select;
    private GridViewAdapter mAdapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    private String[] iconName={"��ͤ","��ɽ","��Ϳ","��ɽ","С��ɽ","����"};
    private int[] icon = {R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        initView();
        //��ʼ������
        initData();

    }

    private void initView() {
        grid_select = (GridView) findViewById(R.id.grid_select);
    }

    void initData() {
        dataList.clear();

        Map<String, Object> map = null;
        for(int i=0;i<icon.length;i++){
            map = new HashMap<String, Object>();
            map.put("text", iconName[i]);
            map.put("image", icon[i]);
            dataList.add(map);
        }

        mAdapter = new GridViewAdapter(GridActivity.this,dataList);
        grid_select.setAdapter(mAdapter);

    }

}

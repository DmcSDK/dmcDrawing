package net.pobbay.mms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import net.pobbay.adapter.GifgridAdapter;
import net.pobbay.entity.GifDetail;
import net.pobbay.service.GifGridList;
import net.pobbay.service.Item;

import java.util.ArrayList;

/**
 * 宫格图片预览
 *
 * @author Yichou
 *
 */
public  class GridFmg extends Fragment implements AdapterView.OnItemClickListener {
    private Item item;
    private GifgridAdapter adapter;
    private ArrayList<GifDetail> details;
    private GifGridList g;
    private int count;
    private String path;
    private String classtiy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new GifGridList();
        int i = getArguments() != null ? getArguments().getInt("index") : 0;
        path = ((ImagePicker) getActivity()).giflistAll.get(i).getPath();


        count = ((ImagePicker) getActivity()).giflistAll.get(i).getCount();
        classtiy = ((ImagePicker) getActivity()).giflistAll.get(i).getClassify();
        type = ((ImagePicker) getActivity()).giflistAll.get(i).getGif();
        ((ImagePicker) getActivity()).textView_loading.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                details = g.getlist(count, path, classtiy, type);
                Message message = new Message();
                if (details.size() > 0) {
                    message.what = 1;
                } else {
                    message.what = 0;
                }
                handler.sendMessage(message);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }



    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ((ImagePicker) getActivity()).textView_loading.setVisibility(View.INVISIBLE);
            switch (msg.what) {
                case 1:
                    if(((ImagePicker) getActivity())!=null){
                        adapter = new GifgridAdapter(((ImagePicker) getActivity()),details);
                        gridView.setAdapter(adapter);
                    }

                    break;
                case 0:
                    Toast.makeText(((ImagePicker) getActivity()),
                            "无法获取数据请检查网络", 5000).show();
                    break;
            }
        };
    };
    private GridView gridView;
    private int type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gridView = (GridView) inflater.inflate(R.layout.grid, container,
                false);
        gridView.setOnItemClickListener(this);
        return gridView;
    }

    // TODO 九宫格点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String path = details.get(position).getBimg();
        Intent intent = new Intent();
        intent.putExtra("path", path);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}

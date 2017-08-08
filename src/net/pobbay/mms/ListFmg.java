package net.pobbay.mms;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.pobbay.adapter.GifListAdapter;
import net.pobbay.service.GifGridList;

/**
 * 列表
 *
 * @author Yichou
 *
 */
public  class ListFmg extends Fragment implements AdapterView.OnItemClickListener {
    public GifListAdapter adapter;
    public Context context;
    public GifGridList g;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = new GifGridList();
        Log.i("----", "ListFmg onCreate");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(R.layout.list, container, false);
        GifListAdapter adapter = new GifListAdapter(getActivity(), ((ImagePicker) getActivity()).giflistAll);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        Log.i("----", "ListFmg onCreateView");
        return listView;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        GridFmg f = new GridFmg();
        Bundle args = new Bundle();
        args.putInt("index", position);
        args.putString("path", ((ImagePicker) getActivity()).giflistAll.get(position).getClassify());
        f.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.simple_fragment, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}

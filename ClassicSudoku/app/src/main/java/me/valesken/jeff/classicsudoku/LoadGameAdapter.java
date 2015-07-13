package me.valesken.jeff.classicsudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jeff on 6/25/2015.
 * Last updated on 7/12/2015
 */
public class LoadGameAdapter extends BaseAdapter implements ListAdapter {
    Context context;
    ArrayList<String> filenames;
    View[] views;

    public LoadGameAdapter(String[] _filenames, Context _context) {
        filenames = new ArrayList<String>();
        filenames.addAll(Arrays.asList(_filenames));
        context = _context;
        views = new View[filenames.size()];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.load_list_item_layout, null);
            ((TextView)listItemView.findViewById(R.id.load_list_item_text)).setText(filenames.get(position));
            views[position] = listItemView;
        }
        else
            listItemView = convertView;

        return listItemView;
    }

    @Override
    public int getCount() { return filenames.size(); }

    @Override
    public Object getItem(int position) { return views[position]; }

    @Override
    public long getItemId(int position) { return 0; }

    public void renewAdapter(String[] _filenames) {
        filenames.clear();
        for (int i = 0; i < _filenames.length; ++i) {
            filenames.add(_filenames[i]);
            ((TextView)views[i].findViewById(R.id.load_list_item_text)).setText(filenames.get(i));
        }
        this.notifyDataSetChanged();
    }
}

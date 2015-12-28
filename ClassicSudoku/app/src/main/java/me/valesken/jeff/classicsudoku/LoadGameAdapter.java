package me.valesken.jeff.classicsudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jeff on 6/25/2015.
 * Last updated on 7/12/2015
 */
public class LoadGameAdapter extends BaseAdapter implements ListAdapter {
    Context context;
    int length;
    JSONObject loadGamesJSON;
    View[] views;

    public LoadGameAdapter(JSONObject _loadGamesJSON, Context _context) {
        length = 0;
        loadGamesJSON = _loadGamesJSON;
        try {
            length = loadGamesJSON.getInt(context.getResources().getString(R.string.json_length_id));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        context = _context;
        views = new View[length];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.load_list_item_layout, null);
            try {
                JSONObject thisLoadGameJSON = loadGamesJSON.getJSONObject(Integer.toString(position));
                String filenameString = thisLoadGameJSON.getString(context.getResources().getString(R.string.json_filename_id));
                String timeString = thisLoadGameJSON.getString(context.getResources().getString(R.string.json_time_id));
                ((TextView) listItemView.findViewById(R.id.load_list_item_text)).setText(filenameString);
                ((TextView) listItemView.findViewById(R.id.load_list_item_time)).setText(timeString);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            views[position] = listItemView;
        }
        else
            listItemView = convertView;

        return listItemView;
    }

    @Override
    public int getCount() { return length; }

    @Override
    public Object getItem(int position) { return views[position]; }

    @Override
    public long getItemId(int position) { return 0; }

    public void renewAdapter(JSONObject _loadGamesJSON) {
        length = 0;
        loadGamesJSON = _loadGamesJSON;
        try {
            length = loadGamesJSON.getInt(context.getResources().getString(R.string.json_length_id));
            for (int i = 0; i < length; ++i) {
                JSONObject thisLoadGameJSON = loadGamesJSON.getJSONObject(Integer.toString(i));
                String filenameString = thisLoadGameJSON.getString(context.getResources().getString(R.string.json_filename_id));
                String timeString = thisLoadGameJSON.getString(context.getResources().getString(R.string.json_time_id));
                ((TextView)views[i].findViewById(R.id.load_list_item_text)).setText(filenameString);
                ((TextView)views[i].findViewById(R.id.load_list_item_time)).setText(timeString);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }
}

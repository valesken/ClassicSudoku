package me.valesken.jeff.classicsudoku;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jeff on 7/12/2015.
 *
 * This fragment contains the load game screen, which allows you to select which saved game to load.
 * It also allows you to delete the saved games listed there.
 */
public class LoadFragment extends Fragment {
    private View rootView;
    private ListView loadList;
    private View mostRecentView;
    private int mostRecentPosition;
    private String filename;
    private MainActivity activity;
    private File loadGamesFile;
    private JSONObject loadGamesJSON;
    private File[] files;
    private FragmentManager fm;
    private LayoutInflater inflater;

    public LoadFragment() {
        mostRecentView = null;
        mostRecentPosition = -1;
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = _inflater;
        rootView = inflater.inflate(R.layout.fragment_load, container, false);
        activity = (MainActivity)getActivity();
        fm = activity.getFragmentManager();
        activity.setTitle(getResources().getString(R.string.app_name).concat(" - Load"));
        loadGamesFile = activity.getLoadGamesFile();

        /* This OnTouchListener ensures the user cannot accidentally touch Views from the previous fragment. */
        rootView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) { return true; }
        });
        loadList = (ListView)rootView.findViewById(R.id.load_list);
        TextView loadEmptyMessage = (TextView)rootView.findViewById(R.id.load_empty_message);

        activity.loadFiles();
        files = activity.getFiles();
        loadGamesJSON = activity.getLoadGamesJSON();

        if(files == null || files.length == 0) {
            loadList.setVisibility(View.INVISIBLE);
            loadEmptyMessage.setVisibility(View.VISIBLE);
        }
        else {
            loadEmptyMessage.setVisibility(View.INVISIBLE);
            loadList.setVisibility(View.VISIBLE);
            final LoadGameAdapter loadGameAdapter = new LoadGameAdapter(loadGamesJSON, rootView.getContext());
            loadList.setAdapter(loadGameAdapter);
            loadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mostRecentView != null) {
                        mostRecentView.setBackground(getResources().getDrawable(android.R.color.transparent));
                        mostRecentView.findViewById(R.id.load_list_item_delete).setVisibility(View.INVISIBLE);
                    }

                    mostRecentView = view;
                    mostRecentPosition = position;
                    mostRecentView.setBackground(getResources().getDrawable(android.R.color.holo_blue_light));
                    filename = ((TextView)(mostRecentView.findViewById(R.id.load_list_item_text))).getText().toString();

                    Button deleteButton = (Button)mostRecentView.findViewById(R.id.load_list_item_delete);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteGame(loadGameAdapter, loadList);
                        }
                    });
                }
            });
        }

        //region Ok-Load Button
        Button ok = (Button)rootView.findViewById(R.id.load_load_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mostRecentView != null) {
                    new LoadGameTask().execute(getResources().getInteger(R.integer.board_size), mostRecentPosition);
                    Toast.makeText(rootView.getContext(), "Loading...", Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion

        //region Cancel Button
        Button cancel = (Button)rootView.findViewById(R.id.load_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        //endregion

        return rootView;
    }

    //region Game deletion logic
    public void deleteGame(final LoadGameAdapter loadGameAdapter, final View loadList) {
        final AlertDialog deleteAlert = new AlertDialog.Builder(rootView.getContext()).create();
        View deleteAlertView = inflater.inflate(R.layout.delete_game_dialog_layout, null);
        deleteAlertView.findViewById(R.id.delete_game_no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert.cancel();
            }
        });
        deleteAlertView.findViewById(R.id.delete_game_yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete file on disk
                String _filename = ((TextView)mostRecentView.findViewById(R.id.load_list_item_text)).getText().toString();
                File deleteFile = new File(activity.getSaveDir(), _filename.concat(".txt"));
                if (deleteFile.delete()) {
                    // Reset graphical state
                    if (mostRecentPosition < (files.length - 1)) {
                        mostRecentView.setBackground(getResources().getDrawable(android.R.color.transparent));
                        mostRecentView.findViewById(R.id.load_list_item_delete).setVisibility(View.INVISIBLE);
                    }
                    mostRecentView = null;
                    mostRecentPosition = -1;
                    // Update activity copies
                    activity.loadFiles(); // Updates files
                    updateLoadGamesFile();
                    files = activity.getFiles();
                    loadGameAdapter.renewAdapter(activity.getLoadGamesJSON());
                    if (loadGameAdapter.getCount() == 0) {
                        loadList.setVisibility(View.INVISIBLE);
                        rootView.findViewById(R.id.load_empty_message).setVisibility(View.VISIBLE);
                    }
                }
                deleteAlert.cancel();
            }
        });
        deleteAlert.setView(deleteAlertView);
        deleteAlert.show();
    }

    private void updateLoadGamesFile() {
        try {
            int length = loadGamesJSON.getInt("length");
            for(int i = length-1; i > mostRecentPosition; --i)
                loadGamesJSON.put(Integer.toString(i-1), loadGamesJSON.get(Integer.toString(i)));
            loadGamesJSON.remove(Integer.toString(length-1));
            loadGamesJSON.put("length", --length);

            BufferedWriter buff = new BufferedWriter(new FileWriter(loadGamesFile, false));
            buff.write(loadGamesJSON.toString());
            buff.flush();
            buff.close();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    private class LoadGameTask extends AsyncTask<Integer, Void, GameFragment> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected GameFragment doInBackground(Integer... ints) {
            int boardSize = ints[0];
            int position = ints[1];
            File saveDir = activity.getSaveDir();
            File saveFile = new File(saveDir.getAbsolutePath().concat("/".concat(filename.concat(".txt"))));
            GameFragment game = new GameFragment();
            game.loadGame(boardSize, saveFile, position);
            return game;
        }

        @Override
        protected void onProgressUpdate(Void... voids) { }

        @Override
        protected void onPostExecute(GameFragment game) {

            activity.setGameFragment(game);
            fm.popBackStack();
            fm.beginTransaction()
                    .add(R.id.container, game)
                    .addToBackStack("Game")
                    .commit();
            Toast.makeText(rootView.getContext(), "Loaded!", Toast.LENGTH_SHORT).show();
        }
    }
}
package me.valesken.jeff.classicsudoku;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

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
    private MainActivity activity;
    private File[] files;
    private String[] filenames;
    private FragmentManager fm;

    public LoadFragment() {
        mostRecentView = null;
        mostRecentPosition = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_load, container, false);
        activity = (MainActivity)getActivity();
        fm = activity.getFragmentManager();
        activity.setTitle("Simply Sudoku - Load");

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
        filenames = activity.getFilenames();

        if(files == null || files.length == 0) {
            loadList.setVisibility(View.INVISIBLE);
            loadEmptyMessage.setVisibility(View.VISIBLE);
        }
        else {
            loadEmptyMessage.setVisibility(View.INVISIBLE);
            loadList.setVisibility(View.VISIBLE);
            final LoadGameAdapter loadGameAdapter = new LoadGameAdapter(filenames, rootView.getContext());
            loadList.setAdapter(loadGameAdapter);
            loadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mostRecentView != null) {
                        mostRecentView.setBackground(getResources().getDrawable(android.R.color.white));
                        mostRecentView.findViewById(R.id.load_list_item_delete).setVisibility(View.INVISIBLE);
                    }

                    mostRecentView = view;
                    mostRecentPosition = position;
                    mostRecentView.setBackground(getResources().getDrawable(android.R.color.holo_blue_light));

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
                if(mostRecentPosition != -1) {
                    GameFragment game = new GameFragment();
                    game.loadGame(getResources().getInteger(R.integer.board_size), files[mostRecentPosition]);
                    activity.setGame(game);
                    fm.popBackStack();
                    fm.beginTransaction()
                        .add(R.id.container, game)
                        .addToBackStack("Game")
                        .commit();
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

    /* Game deletion logic */
    public void deleteGame(final LoadGameAdapter loadGameAdapter, final View loadList) {
        new AlertDialog.Builder(rootView.getContext())
            .setTitle("Confirm Delete")
            .setMessage(String.format("Are you sure you want to delete %s?", filenames[mostRecentPosition]))
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (files[mostRecentPosition].delete()) {
                        if (filenames[mostRecentPosition].equals("AutoSave"))
                            activity.enableResumeGameButton(false);
                        if (mostRecentPosition < (filenames.length - 1)) {
                            mostRecentView.setBackground(getResources().getDrawable(android.R.color.white));
                            mostRecentView.findViewById(R.id.load_list_item_delete).setVisibility(View.INVISIBLE);
                        }
                        mostRecentView = null;
                        mostRecentPosition = -1;
                        activity.loadFiles();
                        files = activity.getFiles();
                        filenames = activity.getFilenames();
                        loadGameAdapter.renewAdapter(filenames);
                        if (loadGameAdapter.getCount() == 0) {
                            loadList.setVisibility(View.INVISIBLE);
                            rootView.findViewById(R.id.load_empty_message).setVisibility(View.VISIBLE);
                        }
                    }
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }
}
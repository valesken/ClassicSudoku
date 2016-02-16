package me.valesken.jeff.classicsudoku;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jeff on 7/12/2015.
 *
 * This fragment contains the current high scores for each difficulty level
 */
public class HighScoresFragment extends Fragment {
    private static int currentPosition;
    private View rootView;
    private MainActivity activity;
    private static JSONObject highScoresJSON;
    private String easyString, mediumString, hardString;

    public HighScoresFragment() {
        currentPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_high_score, container, false);
        activity = (MainActivity)getActivity();

        easyString = getResources().getString(R.string.easy);
        mediumString = getResources().getString(R.string.medium);
        hardString = getResources().getString(R.string.hard);

        activity.setTitle(getResources().getString(R.string.app_name).concat(" - High Scores"));
        final File highScores = new File(activity.getFilesDir(), "HighScores.txt");
        highScoresJSON = activity.getHighScoresJSON();

        /* This OnTouchListener ensures the user cannot accidentally touch Views from the previous fragment. */
        rootView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) { return true; }
        });

        setupSpinner();

        //region Reset Button
        Button resetButton = (Button) rootView.findViewById(R.id.high_scores_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < 10; ++i)
                        jsonArray.put("");
                    highScoresJSON.put(easyString, new JSONArray(jsonArray.toString()));
                    highScoresJSON.put(mediumString, new JSONArray(jsonArray.toString()));
                    highScoresJSON.put(hardString, new JSONArray(jsonArray.toString()));
                    BufferedWriter buff = new BufferedWriter(new FileWriter(highScores, false));
                    buff.write(highScoresJSON.toString());
                    buff.flush();
                    buff.close();
                    setupSpinner();
                }
                catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //endregion

        Button backButton = (Button) rootView.findViewById(R.id.high_scores_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { activity.onBackPressed(); }
        });

        return rootView;
    }

    private void setupSpinner() {
        final ListView[] lists = {
            (ListView)rootView.findViewById(R.id.easy_high_scores),
            (ListView)rootView.findViewById(R.id.medium_high_scores),
            (ListView)rootView.findViewById(R.id.hard_high_scores)
        };
        try {
            //region easy scores
            JSONArray easy = highScoresJSON.getJSONArray(easyString);
            String[] easyScores = new String[10];
            for(int i = 0; i < 10; ++i)
                easyScores[i] = easy.getString(i);
            lists[0].setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, easyScores));
            //endregion

            //region medium scores
            JSONArray medium = highScoresJSON.getJSONArray(mediumString);
            String[] mediumScores = new String[10];
            for(int i = 0; i < 10; ++i)
                mediumScores[i] = medium.getString(i);
            lists[1].setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, mediumScores));
            //endregion

            //region hard scores
            JSONArray hard = highScoresJSON.getJSONArray(hardString);
            String[] hardScores = new String[10];
            for(int i = 0; i < 10; ++i)
                hardScores[i] = hard.getString(i);
            lists[2].setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, hardScores));
            //endregion

            //region Spinner
            final String[] difficulties = {easyString, mediumString, hardString};
            final Spinner highScoresSpinner = (Spinner) rootView.findViewById(R.id.high_scores_spinner);
            highScoresSpinner.setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_spinner_dropdown_item, difficulties));
            highScoresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    lists[currentPosition].setVisibility(View.INVISIBLE);
                    currentPosition = position;
                    lists[currentPosition].setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    for(ListView l : lists)
                        l.setVisibility(View.INVISIBLE);
                    lists[0].setVisibility(View.VISIBLE);
                }
            });
            //endregion
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
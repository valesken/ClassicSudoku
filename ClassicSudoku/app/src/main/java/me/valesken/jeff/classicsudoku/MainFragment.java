package me.valesken.jeff.classicsudoku;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jeff on 7/12/2015.
 *
 * This fragment is basically the landing page for the app.
 */
public class MainFragment extends Fragment {

    private View rootView;
    private Button resumeGameButton;
    private MainActivity activity;
    private FragmentManager fm;
    private boolean loadingGame;

    public MainFragment() {
        loadingGame = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LayoutInflater f_inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        activity = (MainActivity)getActivity();

        activity.setTitle(getResources().getString(R.string.app_name));
        fm = activity.getFragmentManager();
        File autoSaveFile = activity.getAutoSaveFile();

        resumeGameButton = (Button) rootView.findViewById(R.id.resume_game_button);
        Button newGameButton = (Button) rootView.findViewById(R.id.new_game_button);
        Button loadGameButton = (Button) rootView.findViewById(R.id.load_game_button);
        Button aboutButton = (Button) rootView.findViewById(R.id.about_button);
        Button highScoresButton = (Button) rootView.findViewById(R.id.high_scores_button);

        //region OnClickListeners
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadGameTask().execute(getResources().getInteger(R.integer.board_size));
                Toast.makeText(rootView.getContext(), "Loading...", Toast.LENGTH_LONG).show();
            }
        });
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!loadingGame)
                    toNew(f_inflater, v);
            }
        });
        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFragment lf = new LoadFragment();
                fm.beginTransaction()
                    .add(R.id.container, lf)
                    .addToBackStack("Load")
                    .commit();
            }
        });
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                    .add(R.id.container, new AboutFragment())
                    .addToBackStack("About")
                    .commit();
            }
        });
        highScoresButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                    .add(R.id.container, new HighScoresFragment())
                    .addToBackStack("High Scores")
                    .commit();
            }
        });
        //endregion

        enableResumeGameButton(autoSaveFile != null);

        return rootView;
    }

    private void toNew(LayoutInflater inflater, final View button)
    {
        loadingGame = true;

        final AlertDialog ad = new AlertDialog.Builder(rootView.getContext()).create();

        final View newGameDialogView = inflater.inflate(R.layout.new_game_dialog_layout, null);
        newGameDialogView.findViewById(R.id.new_game_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
        newGameDialogView.findViewById(R.id.new_game_play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) newGameDialogView.findViewById(R.id.difficultyRadGroup);
                int id = radioGroup.getCheckedRadioButtonId();
                int difficulty = 1; /* Defaults to Easy */
                switch (id) {
                    case R.id.mediumRadButton:
                        difficulty = 2;
                        break;
                    case R.id.hardRadButton:
                        difficulty = 3;
                        break;
                    case R.id.randomRadButton:
                        difficulty = 4;
                        break;
                    default:
                        break;
                }
                new NewGameTask().execute(getResources().getInteger(R.integer.board_size), difficulty);
                Toast.makeText(rootView.getContext(), "Loading...", Toast.LENGTH_LONG).show();
                ad.cancel();
            }
        });

        ad.setView(newGameDialogView);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                button.setEnabled(true);
            }
        });
        ad.show();
    }

    public void enableResumeGameButton(boolean enabled) { resumeGameButton.setEnabled(enabled); }

    private class NewGameTask extends AsyncTask<Integer, Void, GameFragment> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected GameFragment doInBackground(Integer... ints) {
            int boardSize = ints[0];
            int difficulty = ints[1];
            GameFragment game = new GameFragment();
            game.newGame(boardSize, difficulty);
            activity.setGameFragment(game);
            return game;
        }

        @Override
        protected void onProgressUpdate(Void... voids) { }

        @Override
        protected void onPostExecute(GameFragment game) {
            fm.beginTransaction()
                    .add(R.id.container, game)
                    .addToBackStack("Game")
                    .commit();
            loadingGame = false;
            Toast.makeText(rootView.getContext(), "Loaded!", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadGameTask extends AsyncTask<Integer, Void, GameFragment> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected GameFragment doInBackground(Integer... ints) {
            GameFragment game = new GameFragment();
            game.loadGame(getResources().getInteger(R.integer.board_size), activity.getAutoSaveFile(), -1);
            return game;
        }

        @Override
        protected void onProgressUpdate(Void... voids) { }

        @Override
        protected void onPostExecute(GameFragment game) {

            activity.setGameFragment(game);
            fm.beginTransaction()
                    .add(R.id.container, game)
                    .addToBackStack("Game")
                    .commit();
            Toast.makeText(rootView.getContext(), "Loaded!", Toast.LENGTH_SHORT).show();
        }
    }
}
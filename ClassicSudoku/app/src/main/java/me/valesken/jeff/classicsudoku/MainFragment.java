package me.valesken.jeff.classicsudoku;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

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
    private GameFragment game;
    private FragmentManager fm;
    private File autoSaveFile;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LayoutInflater f_inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        activity = (MainActivity)getActivity();

        activity.setTitle(getResources().getString(R.string.app_name));
        game = activity.getGame();
        fm = activity.getFragmentManager();
        autoSaveFile = activity.getAutoSaveFile();

        resumeGameButton = (Button) rootView.findViewById(R.id.resume_game_button);
        Button newGameButton = (Button) rootView.findViewById(R.id.new_game_button);
        Button loadGameButton = (Button) rootView.findViewById(R.id.load_game_button);
        Button aboutButton = (Button) rootView.findViewById(R.id.about_button);
        Button highScoresButton = (Button) rootView.findViewById(R.id.high_scores_button);

        //region OnClickListeners
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSaveFile = activity.getAutoSaveFile();
                game = new GameFragment();
                game.loadGame(getResources().getInteger(R.integer.board_size), autoSaveFile, -1);
                activity.setGame(game);
                fm.beginTransaction()
                    .add(R.id.container, game)
                    .addToBackStack("Game")
                    .commit();
            }
        });
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        button.setEnabled(false);

        final AlertDialog ad = new AlertDialog.Builder(rootView.getContext()).create();

        final View newGameDialogView = inflater.inflate(R.layout.new_game_dialog_layout, null);
        newGameDialogView.findViewById(R.id.new_game_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(true);
                ad.cancel();
            }
        });
        newGameDialogView.findViewById(R.id.new_game_play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new GameFragment();
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
                game.newGame(getResources().getInteger(R.integer.board_size), difficulty);
                activity.setGame(game);

                fm.beginTransaction()
                        .add(R.id.container, game)
                        .addToBackStack("Game")
                        .commit();
                button.setEnabled(true);
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
}
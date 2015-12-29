package me.valesken.jeff.classicsudoku;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.valesken.jeff.sudoku_structure.Board;

/**
 * Created by Jeff on 7/12/2015.
 *
 * This fragment contains the actual board and UI that comprise the main game.
 */
public class GameFragment extends Fragment {
    //region Member Variables
    private LayoutInflater inflater;
    private View rootView;
    private ViewGroup container;
    private MainActivity activity;
    private int difficulty;
    private int boardSize;
    private Board board;
    private GridManager gridManager;
    private View currentTile;
    private int currentPosition;
    private View save_dialog_view;
    private View overwrite_dialog_view;
    private AlertDialog save_alert;
    private AlertDialog overwrite_alert;
    private AlertDialog paused_alert;
    private String filename;
    private File saveFile;
    private File[] files;
    private JSONObject loadJSON;
    private int loadJSONPosition;
    private TextView clock_tv;
    private volatile boolean paused = false;
    private volatile boolean gameOver = false;
    //endregionNewGameTask

    //region Clock Thread
    private Thread clockThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String clock = (String)clock_tv.getText();
            int seconds = Integer.parseInt(clock.substring(3, 5));
            int minutes = Integer.parseInt(clock.substring(0, 2));
            while(!gameOver) {
                try {
                    do {
                        TimeUnit.SECONDS.sleep(1);
                    }while(paused);
                    if(gameOver)
                        break;
                    char[] temp = clock.toCharArray();
                    seconds += 1;
                    if (seconds > 59) {
                        seconds = 0;
                        minutes += 1;
                    }

                    if (minutes < 10)
                        temp[0] = '0';
                    else
                        temp[0] = Character.forDigit(minutes / 10, 10);
                    temp[1] = Character.forDigit(minutes % 10, 10);
                    if (seconds < 10)
                        temp[3] = '0';
                    else
                        temp[3] = Character.forDigit(seconds / 10, 10);
                    temp[4] = Character.forDigit(seconds % 10, 10);

                    final String clock_string = String.valueOf(temp);
                    clock_tv.post(new Runnable() {
                        @Override
                        public void run() {
                            clock_tv.setText(clock_string);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    });
    //endregion

    public GameFragment() {
        difficulty = 0;
    }

    public void newGame(int _boardSize, int _difficulty)
    {
        boardSize = _boardSize;
        board = new Board(boardSize);
        difficulty = board.NewGame(_difficulty);
        loadJSON = null;
        saveFile = null;
        loadJSONPosition = -1;
    }

    public void loadGame(int _boardSize, File _saveFile, int _loadJSONPosition)
    {
        try {
            loadJSONPosition = _loadJSONPosition;
            saveFile = _saveFile;
            boardSize = _boardSize;
            board = new Board(boardSize);
            BufferedReader buff = new BufferedReader(new FileReader(saveFile));
            JSONObject jsonObject = new JSONObject(buff.readLine());
            buff.close();
            difficulty = board.LoadGame(jsonObject);
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle savedInstanceState) {
        container = _container;
        inflater = _inflater;
        rootView = inflater.inflate(R.layout.fragment_game, container, false);
        activity = (MainActivity)getActivity();
        final String youWinMsg = getResources().getString(R.string.you_win_msg);
        activity.setTitle(getResources().getString(R.string.app_name).concat(" - Game"));
        try {
            JSONObject loadGamesJSON = activity.getLoadGamesJSON();
            if (loadJSONPosition > -1) {
                loadJSON = loadGamesJSON.getJSONObject(Integer.toString(loadJSONPosition));
                filename = loadJSON.getString(getResources().getString(R.string.json_filename_id));
            }
            else {
                loadJSON = null;
                filename = "";
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        /* This OnTouchListener ensures the user cannot accidentally touch Views from the previous fragment. */
        rootView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) { return true; }
        });

        files = activity.getFiles();

        //region Difficulty Level
        TextView difficulty_tv = (TextView)rootView.findViewById(R.id.difficulty_text);
        switch (difficulty)
        {
            case 1:
                difficulty_tv.setText(rootView.getContext().getResources().getString(R.string.easy));
                break;
            case 2:
                difficulty_tv.setText(rootView.getContext().getResources().getString(R.string.medium));
                break;
            default:
                difficulty_tv.setText(rootView.getContext().getResources().getString(R.string.hard));
                break;
        }
        //endregion

        //region Playing Grid Setup
        TableLayout grid = (TableLayout) rootView.findViewById(R.id.grid);
        gridManager = new GridManager(rootView.getContext(), board, boardSize, grid, this);
        gridManager.initializeGrid();
        //endregion

        //region Control Grid OnClickListeners
        Button one = (Button) rootView.findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 1);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button two = (Button) rootView.findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 2);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button three = (Button) rootView.findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 3);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button four = (Button) rootView.findViewById(R.id.four);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 4);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button five = (Button) rootView.findViewById(R.id.five);
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 5);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button six = (Button) rootView.findViewById(R.id.six);
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 6);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button seven = (Button) rootView.findViewById(R.id.seven);
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 7);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button eight = (Button) rootView.findViewById(R.id.eight);
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 8);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button nine = (Button) rootView.findViewById(R.id.nine);
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver) {
                    gameOver = gridManager.updateTile(currentPosition, 9);
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button clear = (Button) rootView.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTile != null && !gameOver)
                    gridManager.clearTile(currentPosition);
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button noteMode_button = (Button) rootView.findViewById(R.id.mode_switch);
        noteMode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
                else if(currentTile != null)
                    gridManager.toggleMode(currentPosition);
            }
        });
        Button hint_button = (Button) rootView.findViewById(R.id.hint);
        hint_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameOver) {
                    gameOver = gridManager.getHint();
                    if(gameOver)
                        updateHighScore();
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button solve_button = (Button) rootView.findViewById(R.id.solve);
        solve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameOver) {
                    gridManager.solve();
                    gameOver = true;
                }
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
            }
        });
        Button pause_button = (Button) rootView.findViewById(R.id.pause);
        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
                else {
                    pause();
                }
            }
        });

        //endregion

        //region Save Logic & Button
        overwrite_alert = new AlertDialog.Builder(rootView.getContext()).create();
        overwrite_alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overwrite_alert.cancel();
            }
        });
        overwrite_dialog_view = inflater.inflate(R.layout.confirm_dialog_layout, container, false);
        overwrite_dialog_view.findViewById(R.id.confirm_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
                overwrite_alert.cancel();
            }
        });
        overwrite_dialog_view.findViewById(R.id.confirm_continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGame(activity.getSaveDir(), filename);
                paused = false;
                overwrite_alert.cancel();
            }
        });
        overwrite_alert.setView(overwrite_dialog_view);

        save_alert = new AlertDialog.Builder(rootView.getContext()).create();
        save_alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused = false;
            }
        });

        Button save_button = (Button) rootView.findViewById(R.id.save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameOver)
                    Toast.makeText(rootView.getContext(), youWinMsg, Toast.LENGTH_LONG).show();
                else {
                    paused = true;

                    // Get filename to display
                    if(filename.equals("")) {
                        int num, maxNum = 0;
                        String name;
                        switch(difficulty) {
                            case 1:
                                filename = rootView.getContext().getResources().getString(R.string.easy);
                                break;
                            case 2:
                                filename = rootView.getContext().getResources().getString(R.string.medium);
                                break;
                            default:
                                filename = rootView.getContext().getResources().getString(R.string.hard);
                                break;
                        }
                        for (File f : files) {
                            name = f.getName().replace(".txt", "");
                            if (name.startsWith(filename) && name.length() > filename.length()) {
                                try {
                                    num = Integer.parseInt(name.replace(filename, ""));
                                    if (num > maxNum)
                                        maxNum = num;
                                }
                                catch (NumberFormatException ignored) { } // e.g. ignore "EasyTown"
                            }
                        }
                        filename = filename.concat(Integer.toString(maxNum + 1));
                    }
                    showSaveDialog();
                }
            }
        });
        //endregion

        //region Start Clock
        clock_tv = (TextView)rootView.findViewById(R.id.clock);
        clock_tv.setText(board.getTime());
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                clockThread.start();
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //endregion

        //region "Paused" Alert
        paused_alert = new AlertDialog.Builder(rootView.getContext()).create();
        paused_alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paused = false;
            }
        });
        View pausedAlertView = inflater.inflate(R.layout.pause_dialog_layout, container, false);
        pausedAlertView.findViewById(R.id.pause_resume_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused_alert.cancel();
            }
        });
        paused_alert.setView(pausedAlertView);
        //endregion

        return rootView;
    }

    public void setCurrentPosition(int _currentPosition, View _currentTile) {
        this.currentPosition = _currentPosition;
        this.currentTile = _currentTile;
    }

    public void showSaveDialog() {
        save_dialog_view = inflater.inflate(R.layout.save_dialog_layout, container, false);
        ((EditText)(save_dialog_view.findViewById(R.id.save_textbox))).setText(filename);
        save_dialog_view.findViewById(R.id.save_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = false;
                save_alert.cancel();
            }
        });
        save_dialog_view.findViewById(R.id.save_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = ((EditText) (save_dialog_view.findViewById(R.id.save_textbox))).getText().toString();
                activity.loadFiles();
                files = activity.getFiles();
                boolean found = false;
                for (File f : files)
                    if (f.getName().replace(".txt", "").equals(filename)) {
                        found = true;
                        break;
                    }
                save_alert.cancel();
                if (found) {
                    InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(save_dialog_view.findViewById(R.id.save_textbox).getWindowToken(), 0);
                    String message = String.format("%s already exists. Overwrite it?", filename);
                    ((TextView)overwrite_dialog_view.findViewById(R.id.confirm_dialog_text)).setText(message);
                    overwrite_alert.show();
                } else {
                    saveGame(activity.getSaveDir(), filename);
                    paused = false;
                }
            }
        });
        save_alert.setView(save_dialog_view);
        save_alert.show();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void handleAutoSave() {
        try {
            clockThread.interrupt();
            File autoSaveFile = activity.getAutoSaveFile();
            if (!gameOver) {
                File saveFile = new File(activity.getFilesDir(), getResources().getString(R.string.autosave_filename));
                BufferedWriter buff = new BufferedWriter(new FileWriter(saveFile, false));
                JSONObject jsonObject = board.save((String) clock_tv.getText());
                buff.write(jsonObject.toString());
                buff.flush();
                buff.close();
                activity.enableResumeGameButton(true);
                activity.loadFiles();
                files = activity.getFiles();
            } else if (autoSaveFile != null) {
                activity.enableResumeGameButton(false);
                autoSaveFile.delete();
                activity.loadFiles();
                files = activity.getFiles();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if(!paused) {
            paused = true;
            paused_alert.show();
        }
    }

    /*
     * Write game state to file.
     * Update loadGamesJSON with this JSON.
     */
    public void saveGame(File saveDir, String _filename) {
        try {
            String clock_text = (String) clock_tv.getText();
            // Write game state to file
            File saveFile = new File(saveDir, _filename.concat(".txt"));
            BufferedWriter buff = new BufferedWriter(new FileWriter(saveFile, false));
            JSONObject jsonObject = board.save(clock_text);
            buff.write(jsonObject.toString());
            buff.flush();
            buff.close();
            // Update loadGamesJSON with this JSON
            JSONObject loadGamesJSON = activity.getLoadGamesJSON();
            if(loadJSON == null || !loadJSON.getString(getResources().getString(R.string.json_filename_id)).equals(_filename)) {
                loadJSON = new JSONObject();
                loadJSON.put(getResources().getString(R.string.json_filename_id), _filename);
                loadJSON.put(getResources().getString(R.string.json_time_id), clock_text);
                int length = loadGamesJSON.getInt(getResources().getString(R.string.json_length_id));
                loadGamesJSON.put(Integer.toString(length), loadJSON);
                ++length;
                loadGamesJSON.put(getResources().getString(R.string.json_length_id), Integer.toString(length));
            }
            else {
                loadJSON.put(getResources().getString(R.string.json_time_id), clock_text);
                loadGamesJSON.put(Integer.toString(loadJSONPosition), loadJSON);
            }
            // Write loadGamesJSON to loadGamesFile
            buff = new BufferedWriter(new FileWriter(activity.getLoadGamesFile(), false));
            buff.write(loadGamesJSON.toString());
            buff.flush();
            buff.close();
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Update high score and save to file
    public void updateHighScore() {
        try {
            JSONObject highScoresJSON = activity.getHighScoresJSON();
            JSONArray jsonScores;
            switch (difficulty) {
                case 1:
                    jsonScores = highScoresJSON.getJSONArray(getResources().getString(R.string.easy));
                    break;
                case 2:
                    jsonScores = highScoresJSON.getJSONArray(getResources().getString(R.string.medium));
                    break;
                default:
                    jsonScores = highScoresJSON.getJSONArray(getResources().getString(R.string.hard));
                    break;
            }

            String currentTime = clock_tv.getText().toString();
            int index = currentTime.indexOf(':');
            int minutes = Integer.parseInt(currentTime.substring(0, index));
            int seconds = Integer.parseInt(currentTime.substring(index+1));
            String scoreTime;
            int scoreMinutes, scoreSeconds;
            ArrayList<String> scores = new ArrayList<>();

            //region Find Current Score Place
            boolean inserted = false;
            for(int i = 0; i < 10 && !jsonScores.getString(i).isEmpty(); ++i) {
                scoreTime = jsonScores.getString(i);
                index = scoreTime.indexOf(':');
                scoreMinutes = Integer.parseInt(scoreTime.substring(0, index));
                scoreSeconds = Integer.parseInt(scoreTime.substring(index + 1));
                if(!inserted && scoreMinutes > minutes) {
                    scores.add(currentTime);
                    inserted = true;
                }
                else if(!inserted && scoreMinutes == minutes && scoreSeconds > seconds) {
                    scores.add(currentTime);
                    inserted = true;
                }
                scores.add(scoreTime);
            }
            if(!inserted && scores.size() < 10)
                scores.add(currentTime);
            //endregion

            //region Update High Scores File
            for(int i = 0; i < 10 && i < scores.size(); ++i)
                jsonScores.put(i, scores.get(i));
            switch (difficulty) {
                case 1:
                    highScoresJSON.put(getResources().getString(R.string.easy), jsonScores);
                    break;
                case 2:
                    highScoresJSON.put(getResources().getString(R.string.medium), jsonScores);
                    break;
                default:
                    highScoresJSON.put(getResources().getString(R.string.hard), jsonScores);
                    break;
            }
            BufferedWriter buff = new BufferedWriter(new FileWriter(activity.getHighScoresFile(), false));
            buff.write(highScoresJSON.toString());
            buff.flush();
            buff.close();
            //endregion
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
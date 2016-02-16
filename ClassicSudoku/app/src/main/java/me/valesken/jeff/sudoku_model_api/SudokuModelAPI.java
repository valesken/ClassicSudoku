package me.valesken.jeff.sudoku_model_api;

import org.json.JSONObject;

import java.util.LinkedList;

import me.valesken.jeff.sudoku_model.*;

/**
 * Created by jeff on 2/8/2016.
 * Last updated on 2/8/2016.
 */
public class SudokuModelAPI {

    public static Board board;

    public static void initializeNewBoard(int houseSize) {
        board = new Board(houseSize);
        board.initializeHouses();
        board.initializeTiles();
    }

    public static int newGame(int difficulty) {
        if(board == null) {
            return -1;
        }
        return board.newGame(difficulty);
    }

    public static int loadGame(JSONObject jsonObject) {
        if(board == null) {
            return -1;
        }
        return board.loadGame(jsonObject);
    }

    public static JSONObject save(String currentTime) {
        if(board == null) {
            return null;
        }
        return board.save(currentTime);
    }

    public static String getTime() {
        if(board == null) {
            return null;
        }
        return board.getTime();
    }

    public static LinkedList[] getBoard() {
        if(board == null) {
            return null;
        }
        return board.getBoard();
    }

    public static boolean tileIsNoteMode(int position) {
        return board != null && board.tileIsNoteMode(position);
    }

    public static boolean tileIsOrig(int position) {
        return board != null && board.tileIsOrig(position);
    }

    public static void updateTile(int position, int value) {
        if(board != null) {
            board.updateTile(position, value);
        }
    }

    public static LinkedList getTile(int position) {
        if(board == null) {
            return null;
        }
        return board.getTileNotesOrValue(position);
    }

    public static boolean isGameOver() {
        return board != null && board.isGameOver();
    }

    public static void clearTile(int position) {
        if(board != null) {
            board.clearTile(position);
        }
    }

    public static void toggleMode(int position) {
        if(board != null) {
            board.toggleMode(position);
        }
    }

    public static int getHint() {
        if(board == null) {
            return -1;
        }
        return board.useHint();
    }

    public static void solve() {
        if(board != null) {
            board.solve();
        }
    }
}

package me.valesken.jeff.sudoku_model;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by jeff on 2/8/2016.
 * Last updated on 2/8/2016.
 */
public class ModelProxy {

    protected static Board board;

    /**
     * Initialize a new board for a new game.
     *
     * @param houseSize The size of each House (row, column, zone) in the board.
     */
    public static void initializeNewBoard(int houseSize) {
        board = new Board(houseSize);
        board.initializeHouses();
        board.initializeTiles();
    }

    /**
     * Create a new game.
     *
     * @param difficulty The difficulty level for the new game.
     * @return The actual difficulty level of the game. Should be the same (unless random, which case this tells you
     * the selected difficulty level). -1 if the board has not yet been initialized.
     */
    public static int newGame(int difficulty) {
        if (board == null) {
            return -1;
        }
        return board.newGame(difficulty);
    }

    /**
     * Load a saved game.
     *
     * @param jsonObject The JSON Object representing your saved game.
     * @return The difficulty level of the saved game. -1 if the JSON is badly formatted or the board has not yet
     * been initialized.
     */
    public static int loadGame(JSONObject jsonObject) {
        if (board == null) {
            return -1;
        }
        return board.loadGame(jsonObject);
    }

    /**
     * Save the game that you are currently playing.
     *
     * @param currentTime The amount of time you have spent playing the current game.
     * @return The JSON Object representing your saved game. Null if there is no current game.
     */
    public static JSONObject save(String currentTime) {
        if (board == null) {
            return null;
        }
        return board.save(currentTime);
    }

    /**
     * @return The amount of time you have spent playing the current game. Null if there is no current game.
     */
    public static String getTime() {
        if (board == null) {
            return null;
        }
        return board.getTime();
    }

    /**
     * @return An array of LinkedLists representing the notes/values for every Tile in the current game. Null if
     * there is no current game.
     */
    public static LinkedList[] getBoard() {
        if (board == null) {
            return null;
        }
        return board.getBoard();
    }

    /**
     * Check if a given Tile is in Note mode or Value mode.
     *
     * @param position The 0 - 80 index of the Tile you want to check.
     * @return True if the Tile is in Note mode. False if the Tile is in Value mode, the provided index is out of
     * bounds, or there is no current game.
     */
    public static boolean tileIsNoteMode(int position) {
        return board != null && board.tileIsNoteMode(position);
    }

    /**
     * Check if a given Tile is an "original" Tile (meaning its value cannot be changed).
     *
     * @param position The 0 - 80 index of the Tile you want to check.
     * @return True if the Tile is an "original" Tile. False if the Tile is not "original", the provided index is out
     * of bounds, or there is no current game.
     */
    public static boolean tileIsOrig(int position) {
        return board != null && board.tileIsOrig(position);
    }

    /**
     * Update the value or note for a given Tile.
     *
     * @param position The 0 - 80 index of the Tile you want to update.
     * @param value    The value you want to update it with or note you want to add or remove.
     * @return A LinkedList of Integers representing the current Value/Notes associated with the selected Tile. Null
     * if the provided index is out of bounds, the value is out of bounds, or there is no current game.
     */
    public static LinkedList updateTile(int position, int value) {
        if (board == null) {
            return null;
        }
        return board.updateTile(position, value);
    }

    /**
     * Retrieve a LinkedList of Integers representing the current Value/Notes associated with the selected Tile.
     *
     * @param position The 0 - 80 index of the Tile you want.
     * @return A LinkedList of Integers representing the current Value/Notes associated with the selected Tile. Null
     * if the provided index is out of bounds or there is no current game.
     */
    public static LinkedList getTile(int position) {
        if (board == null) {
            return null;
        }
        return board.getTileNotesOrValue(position);
    }

    /**
     * @return True if the current game has finished. False if it is still ongoing or there is no current game.
     */
    public static boolean isGameOver() {
        return board != null && board.isGameOver();
    }

    /**
     * Clear the current value and all stored notes on a given Tile.
     *
     * @param position The 0 - 80 index of the Tile you want to clear.
     * @return A LinkedList of Integers representing the current Value/Notes associated with the selected Tile. Null
     * if the provided index is out of bounds or there is no current game.
     */
    public static LinkedList clearTile(int position) {
        if (board == null) {
            return null;
        }
        return board.clearTile(position);
    }

    /**
     * Switch a Tile between Note mode and Value mode. If it's currently in Note mode, then it will switch to Value
     * mode. If it's currently in Value mode, then it will switch to Note mode.
     *
     * @param position The 0 - 80 index of the Tile you want to toggle.
     * @return True if the toggle succeeds. False if it does not, if the provided index is out of bounds, or if there
     * is no current game.
     */
    public static boolean toggleNoteMode(int position) {
        return board != null && board.toggleNoteMode(position);
    }

    /**
     * Ask the game to provide you with a hint. This means selecting a random Tile that is unsolved (or incorrectly
     * solved) and setting it to its solved value. The solved Tile will be marked as an "original", meaning its value
     * is now unchangeable.
     *
     * @return The index of the Tile that was solved for you. -1 if the game is already solved or there is no current
     * game.
     */
    public static int getHint() {
        if (board == null) {
            return -1;
        }
        return board.useHint();
    }

    /**
     * Solve the game.
     *
     * @return True if the game is now solved, False if the game has already been solved or there is no current game.
     */
    public static boolean solve() {
        return board != null && board.solve();
    }
}

package me.valesken.jeff.sudoku_model;

/**
 * Use this interface to implement a new Technique to make a change in the board.
 *
 * Created by jeff on 5/31/16.
 */
interface Technique {
    /**
     * @return true if Technique makes a change to the board, false otherwise
     */
    boolean execute();
}

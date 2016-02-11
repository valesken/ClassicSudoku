package me.valesken.jeff.sudoku_model;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jeff on 1/8/16.
 * Last updated on 1/15/2016.
 *
 * Simple AI to solve the Board using a variety of techniques for different difficulty levels
 * Uses the Command Pattern to manage the Techniques
 *
 * Easy: Remainder, Single Candidate, Single Position
 * Medium: Candidate Line, Double Pair, Multi-Line
 * Hard: Naked Pairs/Triples, Hidden Pairs/Triples
 * Master: X-Wing, Swordfish, Forcing Chains
 * Obscene: Nishio, Guessing
 * https://www.sudokuoftheday.com/techniques/
 */
public class Solver {

    //region Technique interface
    /**
     * Use this interface to implement a new Technique to make a change in the board.
     */
    private interface Technique {
        /**
         * @return true if Technique makes a change to the board, false otherwise
         */
        boolean execute();
    }
    //endregion

    private Board board;
    private List<House> houses;

    public Solver(Board _board) {
        this.board = _board;
        houses = Arrays.asList(board.getRows());
        houses.addAll(Arrays.asList(board.getColumns()));
        houses.addAll(Arrays.asList(board.getZones()));
    }

    /**
     * Call this method to have the Solver attempt to solve its board like a human player using
     * an appropriate set of strategies.
     *
     * @param difficulty 1 = easy, 2 = medium, 3 = hard
     */
    public void solve(int difficulty) {
        while(!isSolvable(difficulty)) {
            Log.d("Debug Info", "Board not solvable, getting a hint.");
            board.useHint();
            board.clearBoard();
        }
        Log.d("Debug Info", "Solved.");
    }

    /**
     * @param difficulty 1 = easy, 2 = medium, 3 = hard
     * @return Whether or not the board is solvable at the desired difficulty level
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private boolean isSolvable(int difficulty) {
        // Get a list of techniques corresponding to the difficulty level
        LinkedList<Technique> techniques = new LinkedList<>(Arrays.asList(
                new Remainder(),
                new SingleCandidate(),
                new SinglePosition()));
        if(difficulty > 1) {
            techniques.add(new CandidateLine());
            techniques.add(new DoublePair());
            techniques.add(new MultiLine());
        }
        if(difficulty > 2) {
            techniques.add(new NakedPairsAndTriples());
            techniques.add(new HiddenPairsAndTriples());
        }
        // Iterate over selected techniques, resetting to simplest technique when possible, and
        // stopping when no technique succeeds.
        ListIterator<Technique> it = techniques.listIterator();
        while(it.hasNext()) {
            Technique technique = it.next();
            if(technique.execute())
                it = techniques.listIterator();
        }
        return board.isGameOver();
    }

    //region Easy Techniques
    /**
     * This Technique examines all houses to see if only 1 unassigned tile remains. If so, it will
     * assign the appropriate value to that tile.
     */
    private class Remainder implements Technique {
        @Override
        public boolean execute() {
            int targetTotal = 45; // Sum of 1 through 9
            for (House row : houses) {
                Tile remainder = null; // Holds empty Tile (if there is any)
                int value; // Tile's value
                int total = 0; // Sum of house's values
                boolean success = false;
                // Check if row contains only 1 empty Tile
                for(Tile tile : row) {
                    value = tile.getValue();
                    if(value == 0) {
                        success = (remainder == null);
                        if(!success)
                            break;
                        remainder = tile;
                    }
                    total += value;
                }
                // If row contains only 1 empty Tile, solve it (value = targetTotal - total)
                if(success) {
                    value = targetTotal - total;
                    remainder.getRow().clearValueInHouse(value);
                    remainder.getColumn().clearValueInHouse(value);
                    remainder.getZone().clearValueInHouse(value);
                    board.updateTile(remainder.getIndex(), value);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * This Technique examines each tile in the board and checks to see if there is only one value
     * that can be applied to that tile.
     */
    private class SingleCandidate implements Technique {
        @Override
        public boolean execute() {
            int targetTotal = 45; // Sum of 1 through 9
            int realTotal; // For each tile, sum of values unavailable in that tile
            int counter; // For each tile, number of values unavailable in that tile
            boolean inRow, inColumn, inZone;
            for(Tile candidate : board.getTiles()) {
                if(candidate.getValue() == 0) {
                    realTotal = 0;
                    counter = 0;
                    for (int value = 1; value < 10; ++value) {
                        inRow = candidate.getRow().hasValue(value);
                        inColumn = candidate.getColumn().hasValue(value);
                        inZone = candidate.getZone().hasValue(value);
                        if (inRow || inColumn || inZone) {
                            realTotal += value;
                            counter++;
                        }
                    }
                    if(counter == 8) {
                        int value = targetTotal - realTotal;
                        candidate.getRow().clearValueInHouse(value);
                        candidate.getColumn().clearValueInHouse(value);
                        candidate.getZone().clearValueInHouse(value);
                        board.updateTile(candidate.getIndex(), value);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * For each value 1 through 9, this Technique will check each house to see if it contains only
     * one tile which can hold that value.
     */
    private class SinglePosition implements Technique {
        @Override
        public boolean execute() {
            int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            boolean success, rowValue, columnValue, zoneValue;
            for(int value : values) {
                for (House house : houses) {
                    Tile singlePosition = null;
                    success = false;
                    for (Tile tile : house) {
                        rowValue = !tile.getRow().hasValue(value);
                        columnValue = !tile.getColumn().hasValue(value);
                        zoneValue = !tile.getZone().hasValue(value);
                        if(rowValue && columnValue && zoneValue) {
                            if(singlePosition == null) {
                                success = true;
                                singlePosition = tile;
                            }
                            else {
                                success = false;
                                break;
                            }
                        }
                    }
                    if(success) {
                        singlePosition.getRow().clearValueInHouse(value);
                        singlePosition.getColumn().clearValueInHouse(value);
                        singlePosition.getZone().clearValueInHouse(value);
                        board.updateTile(singlePosition.getIndex(), value);
                        return true;
                    }
                }
            }
            return false;
        }
    }
    //endregion

    //region Medium Techniques
    private class CandidateLine implements Technique {
        @Override
        public boolean execute() {
            return false;
        }
    }

    private class DoublePair implements Technique {
        @Override
        public boolean execute() {
            boolean success = false;
            return success;
        }
    }

    private class MultiLine implements Technique {
        @Override
        public boolean execute() {
            boolean success = false;
            return success;
        }
    }
    //endregion

    //region Hard Techniques
    private class NakedPairsAndTriples implements Technique {
        @Override
        public boolean execute() {
            boolean success = false;
            return success;
        }
    }

    private class HiddenPairsAndTriples implements Technique {
        @Override
        public boolean execute() {
            boolean success = false;
            return success;
        }
    }
    //endregion
}

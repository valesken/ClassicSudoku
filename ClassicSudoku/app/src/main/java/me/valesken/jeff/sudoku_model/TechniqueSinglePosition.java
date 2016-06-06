package me.valesken.jeff.sudoku_model;

/**
 * For each value 1 through 9, this Technique will check each house to see if it contains only
 * one tile which can hold that value.
 *
 * Created by jeff on 5/31/2016.
 * Last updated on 6/2/2016.
 */
public class TechniqueSinglePosition implements Technique {

    protected static final int[] VALUES = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    protected Solver solver;
    protected boolean success;

    protected TechniqueSinglePosition(Solver solver) {
        this.solver = solver;
    }


    @Override
    public boolean execute() {

        // Cycle through every possible value 1 through 9
        for(int value : VALUES) {
            Tile singlePosition = checkHousesForValue(value);
            if(singlePosition != null) {
                solver.solveTile(singlePosition, value);
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks all Houses to see if they have a single position for a specific 1 to 9 value.
     * Note: Separated from execute() function for testing purposes.
     *
     * @param value The 1 to 9 value to check for.
     * @return The single Tile if one exists, else null.
     */
    protected Tile checkHousesForValue(int value) {

        // Check that the value is within appropriate bounds
        if(value < 1 || value > 9) {
            return null;
        }

        // Check if this house has only 1 Tile which can have the current value
        for (House house : solver.houses) {

            Tile singlePosition = null;
            success = false;

            // Check every Tile in the House to see if it can have the current value
            for (Tile tile : house) {

                if (!(tile.getRow().hasValue(value) ||
                        tile.getColumn().hasValue(value) ||
                        tile.getZone().hasValue(value))) {

                    // If the Tile can have the value and it's the first Tile that can have the value, then save it
                    if (singlePosition == null) {
                        success = true;
                        singlePosition = tile;
                    } else {
                        success = false;
                        break;
                    }
                }
            }

            // If 1 and only 1 singlePosition was detected, solve it and exit
            if (success) {
                return singlePosition;
            }
        }
        return null;
    }
}

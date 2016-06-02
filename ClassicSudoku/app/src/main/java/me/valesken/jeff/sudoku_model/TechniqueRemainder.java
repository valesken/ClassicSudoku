package me.valesken.jeff.sudoku_model;

/**
 * This Technique examines all houses to see if only 1 unassigned tile remains in any House. If so, it will assign the
 * appropriate value to that tile. Note that this technique only works (right now) on Houses of length 9.
 * TODO: Write an integration test
 *
 * Created by jeff on 5/31/16.
 */
class TechniqueRemainder implements Technique {

    protected static final int TARGET_TOTAL = 45; // Sum of 1 to 9
    protected Solver solver;

    protected TechniqueRemainder(Solver solver) {
        this.solver = solver;
    }

    @Override
    public boolean execute() {
        for (House house : solver.houses) {
            Tile emptyTile = null; // Holds empty Tile (if there is any)
            int value; // Tile's value
            int total = 0; // Sum of house's values
            boolean success = false;

            // Check if this House contains only 1 empty Tile
            for (Tile tile : house) {
                value = tile.getValue();

                // If value is 0, record this Tile as empty, else add value to the total value of this House
                if (value == 0) {

                    // If this is not the first empty Tile detected, stop examining this House
                    success = (emptyTile == null);
                    if(!success) {
                        break;
                    }

                    // Else, continue
                    emptyTile = tile;
                } else {
                    total += value;
                }
            }

            // If row contains only 1 empty Tile, solve it (value = targetTotal - total)
            if (success) {
                value = TARGET_TOTAL - total;
                solver.solveTile(emptyTile, value);
                return true;
            }
        }
        return false;
    }
}

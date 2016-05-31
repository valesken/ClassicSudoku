package me.valesken.jeff.sudoku_model;

/**
 * This Technique examines all houses to see if only 1 unassigned tile remains. If so, it will
 * assign the appropriate value to that tile.
 *
 * Created by jeff on 5/31/16.
 */
class TechniqueRemainder implements Technique {

    protected Solver solver;

    protected TechniqueRemainder(Solver solver) {
        this.solver = solver;
    }

    @Override
    public boolean execute() {
        int targetTotal = 45; // Sum of 1 through 9
        for (House row : solver.houses) {
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
                solver.board.updateTile(remainder.getIndex(), value);
                return true;
            }
        }
        return false;
    }
}

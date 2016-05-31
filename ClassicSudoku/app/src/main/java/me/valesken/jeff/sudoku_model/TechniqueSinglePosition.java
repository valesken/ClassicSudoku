package me.valesken.jeff.sudoku_model;

/**
 * For each value 1 through 9, this Technique will check each house to see if it contains only
 * one tile which can hold that value.
 *
 * Created by jeff on 5/31/16.
 */
public class TechniqueSinglePosition implements Technique {

    protected Solver solver;

    protected TechniqueSinglePosition(Solver solver) {
        this.solver = solver;
    }


    @Override
    public boolean execute() {
        int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        boolean success, rowValue, columnValue, zoneValue;
        for(int value : values) {
            for (House house : solver.houses) {
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
                    solver.board.updateTile(singlePosition.getIndex(), value);
                    return true;
                }
            }
        }
        return false;
    }
}

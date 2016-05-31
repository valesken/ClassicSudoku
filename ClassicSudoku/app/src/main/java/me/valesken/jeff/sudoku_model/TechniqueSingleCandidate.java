package me.valesken.jeff.sudoku_model;

/**
 * This Technique examines each tile in the board and checks to see if there is only one value
 * that can be applied to that tile.
 *
 * Created by jeff on 5/31/16.
 */
class TechniqueSingleCandidate implements Technique {

    protected Solver solver;

    protected TechniqueSingleCandidate(Solver solver) {
        this.solver = solver;
    }

    @Override
    public boolean execute() {
        int targetTotal = 45; // Sum of 1 through 9
        int realTotal; // For each tile, sum of values unavailable in that tile
        int counter; // For each tile, number of values unavailable in that tile
        boolean inRow, inColumn, inZone;
        for(Tile candidate : solver.board.getTiles()) {
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
                    solver.board.updateTile(candidate.getIndex(), value);
                    return true;
                }
            }
        }
        return false;
    }
}

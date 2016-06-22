package me.valesken.jeff.sudoku_model;

/**
 * Use this abstract class to create new Techniques to make a change in the board.
 *
 * Created by jeff on 5/31/16.
 * Last updated on 6/14/2016.
 */
abstract class Technique {

    /**
     * @return true if Technique makes a change to the board, false otherwise
     */
    protected abstract boolean execute();

    /**
     * Detect whether or not a specific Tile is a candidate for a given value. It bases this on whether or not the Tile
     * has already been assigned the specified value within its Houses, or if the Tile's Houses already contain the
     * specified value. It does not care what other values might be assigned to that Tile.
     *
     * This also means that if you assign a value to one Tile in a House, then this method will return false for all
     * other Tiles in that House unless they already had that value assigned to them. Therefore, it is best to examine
     * a House fully before making any updates to it.
     *
     * @param candidate The Tile to examine.
     * @param value     The value to look for candidacy of.
     * @return True if the Tile is a candidate for the given value, otherwise False.
     */
    protected boolean tileIsCandidate(Tile candidate, int value) {
        House row = candidate.getRow();
        House column = candidate.getColumn();
        House zone = candidate.getZone();
        boolean valueAlreadyAssigned = row.hasAssignedValueToTile(value, candidate) ||
                column.hasAssignedValueToTile(value, candidate) ||
                zone.hasAssignedValueToTile(value, candidate);
        return valueAlreadyAssigned || !(row.hasValue(value) || column.hasValue(value) || zone.hasValue(value));
    }
}

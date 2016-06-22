package me.valesken.jeff.sudoku_model;

import java.util.HashSet;
import java.util.Set;

/**
 * This Technique will detect one new Candidate Line, if one exists. A Candidate Line is when all the Tiles in a Zone
 * which are candidates for a value are also all in the same Row or Column. If a Candidate Line is detected, then no
 * other Tile in that shared Row or Column can have that value.
 *
 * See https://www.sudokuoftheday.com/techniques/candidate-lines/
 *
 * Created by jeff on 5/31/2016.
 * Last updated on 6/21/2016.
 */
class TechniqueCandidateLine extends Technique {

    protected Solver solver;

    protected TechniqueCandidateLine(Solver solver) {
        this.solver = solver;
    }

    @Override
    protected boolean execute() {
        // Iterate through possible values 1 to 9
        for (int value = 1; value < 10; ++value) {

            // Iterate through each zone
            for (House zone : solver.zones) {

                Set<Tile> candidates = findCandidates(zone, value);

                // Look through candidates to see if they are all in the same row or column
                int size = candidates.size();
                if (size > 0 && size < 4) {

                    House commonHouse = getCommonRowOrColumn(candidates, true);
                    if (commonHouse != null && isNewLine(commonHouse, candidates, value)) {
                        setCandidateLine(commonHouse, candidates, value);
                        return true;
                    } else {
                        commonHouse = getCommonRowOrColumn(candidates, false);
                        if (commonHouse != null && isNewLine(commonHouse, candidates, value)) {
                            setCandidateLine(commonHouse, candidates, value);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Search through all Tiles in the zone to see if they are a candidate for the value
     *
     * @param value The 1 to 9 value to look for candidates of
     * @return A Set of the candidates. Empty if no candidates.
     */
    protected Set<Tile> findCandidates(House zone, int value) {
        Set<Tile> candidates = new HashSet<>();
        for (Tile tile : zone) {
            if  (tileIsCandidate(tile, value)) {
                candidates.add(tile);
            }
        }
        return candidates;
    }

    /**
     * Return the Row or Column that all the Candidates are in, if they are all in a line.
     *
     * @param candidates The Tiles to examine.
     * @param lookForRow Whether to look for a Row or a Column.
     * @return Return the common Row/Column, if it exists. Otherwise, return null.
     */
    protected House getCommonRowOrColumn(Set<Tile> candidates, boolean lookForRow) {
        House house = null;
        for (Tile candidate : candidates) {
            House tempHouse = lookForRow ? candidate.getRow() : candidate.getColumn();
            if (house == null || tempHouse.equals(house)) {
                house = tempHouse;
            } else {
                return null;
            }
        }
        return house;
    }

    /**
     * Check if this candidate line provides any new information. New information would be if any of the candidates have
     * not yet had the target value assigned to them, or if there are tiles in the line that are not candidates but do
     * have the target value assigned to them (they will need the value removed).
     *
     * @param line       The row or column that will be the candidate line.
     * @param candidates The candidates that will make up this candidate line.
     * @param value      The value that this candidate line will take on.
     * @return True if the candidates in this line were not already known or values will be cleared by this line.
     *         Otherwise, false.
     */
    protected boolean isNewLine(House line, Set<Tile> candidates, int value) {
        for (Tile candidate : candidates) {
            if (!line.hasAssignedValueToTile(value, candidate)) {
                return true;
            }
        }
        for (Tile tile : line.getValueTiles()) {
            if (!candidates.contains(tile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Empty the provided House of the given value. Then set the value to each of the candidates in the provided House.
     * Note: it does not set the value is all of the candidates' Houses because those Houses would then think the value
     * has been claimed when in fact it has not.
     *
     * @param line      The House to update.
     * @param candidates The candidates for a given value.
     * @param value      The value to update in the House.
     */
    protected void setCandidateLine(House line, Set<Tile> candidates, int value) {
        if (line != null && candidates != null && value > 0 && value < 10) {
            line.clearValueInHouse(value);
            for (Tile candidate : candidates) {
                line.setValueInHouse(value, true, candidate);
            }
        }
    }
}

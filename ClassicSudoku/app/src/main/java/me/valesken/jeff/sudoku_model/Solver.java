package me.valesken.jeff.sudoku_model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by jeff on 1/8/2016.
 * Last updated on 6/9/2016.
 *
 * Simple AI to solve the Board using a variety of techniques for different difficulty levels
 * Uses the Command Pattern to manage the SolvingTechniques
 *
 * Easy: Remainder, Single Candidate, Single Position
 * Medium: Candidate Line, Double Pair, Multi-Line
 * Hard: Naked Pairs/Triples, Hidden Pairs/Triples
 * Master: X-Wing, Swordfish, Forcing Chains
 * Obscene: Nishio, Guessing
 * https://www.sudokuoftheday.com/techniques/
 */
class Solver {

    protected Board board;
    protected List<House> houses;
    protected List<House> zones;
    protected Set<Technique> techniques;

    public Solver(Board board) {
        this.board = board;
        houses = new LinkedList<>(Arrays.asList(this.board.getRows()));
        houses.addAll(Arrays.asList(this.board.getColumns()));
        zones = Arrays.asList(this.board.getZones());
        houses.addAll(zones);
    }

    /**
     * Call this method to have the Solver attempt to solve its board like a human player using
     * an appropriate set of strategies.
     *
     * @param difficulty 1 = easy, 2 = medium, 3 = hard
     */
    protected void solve(int difficulty) {
        if (difficulty > 0 && difficulty < 4) {
            setTechniques(difficulty);
            while (!isSolvable()) {
                board.useHint();
                board.clearBoard();
            }
        }
    }

    /**
     * Set the Techniques list to appropriate list for the difficulty level
     *
     * @param difficulty 1 = easy, 2 = medium, 3 = hard
     */
    protected void setTechniques(int difficulty) {
        if (difficulty > 0 && difficulty < 4) {
            techniques = new HashSet<>(Arrays.asList(
                    new TechniqueRemainder(this),
                    new TechniqueSingleCandidate(this),
                    new TechniqueSinglePosition(this)));
            if (difficulty > 1) {
                techniques.add(new TechniqueCandidateLine(this));
                techniques.add(new TechniqueDoublePair(this));
                techniques.add(new TechniqueMultiLine(this));
            }
            if (difficulty > 2) {
                techniques.add(new TechniqueNakedPairsAndTriples(this));
                techniques.add(new TechniqueHiddenPairsAndTriples(this));
            }
        }
    }

    /**
     * Iterate over selected techniques, resetting to simplest technique when possible, and stopping when no technique
     * succeeds.
     *
     * @return Whether or not the board is solvable at the desired difficulty level
     */
    @SuppressWarnings("StatementWithEmptyBody")
    protected boolean isSolvable() {
        Technique[] techniqueArray = techniques.toArray(new Technique[techniques.size()]);
        for (int i = 0; i < techniqueArray.length; ) {
            if (techniqueArray[i].execute()) {
                i = 0;
            } else {
                ++i;
            }
        }
        return board.isGameOver();
    }

    /**
     * Given a Tile and a value, set the Tile to the value. Clear all instances of the value from the Tile's parent
     * Houses before setting the Tile's value. This is to ensure that there are no contradictions in any Houses.
     *
     * @param tile The Tile to solve for.
     * @param value The 1 to 9 value that you want the Tile to have.
     */
    protected void solveTile(Tile tile, int value) {
        if (tile != null && value > 0 && value < 10) {
            tile.getRow().clearValueInHouse(value);
            tile.getColumn().clearValueInHouse(value);
            tile.getZone().clearValueInHouse(value);
            board.updateTile(tile.getIndex(), value);
        }
    }
}

package me.valesken.jeff.sudoku_model;

/**
 * This Technique examines each tile in the board and checks to see if there is only one value that can be applied to
 * that tile.
 *
 * See https://www.sudokuoftheday.com/techniques/single-candidate/
 *
 * Created by jeff on 5/31/2016.
 * Last updated on 6/14/2016.
 */
class TechniqueSingleCandidate extends Technique {

    protected Solver solver;

    protected TechniqueSingleCandidate(Solver solver) {
        this.solver = solver;
    }

    @Override
    protected boolean execute() {

        // Iterate over every Tile in the board
        for(Tile candidate : solver.board.getTiles()) {

            // If the Tile doesn't already have a value, give it a closer look
            if(candidate.getValue() == 0) {

                int candidateValue = 0; // The possible value available to this candidate Tile
                int counter = 0; // Number of possible values available to this candidate Tile

                // For each value 1 - 9, examine the candidate's Houses to see if it's already taken
                for (int value = 1; value < 10; ++value) {

                    // If the value is not taken in one of the candidate's Houses, record it for later
                    if (tileIsCandidate(candidate, value)) {

                        candidateValue = value;
                        ++counter;

                        // If there's already a candidate value, abort because there's too many candidates
                        if(counter > 1) {
                            break;
                        }
                    }
                }

                // If there's 1 and only 1 candidate value, assign it
                if(counter == 1) {
                    solver.solveTile(candidate, candidateValue);
                    return true;
                }

            }

        }

        return false;
    }
}

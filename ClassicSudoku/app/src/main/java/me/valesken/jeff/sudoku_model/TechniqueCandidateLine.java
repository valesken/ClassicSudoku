package me.valesken.jeff.sudoku_model;

/**
 * Created by jeff on 5/31/16.
 */
class TechniqueCandidateLine implements Technique {

    protected Solver solver;

    protected TechniqueCandidateLine(Solver solver) {
        this.solver = solver;
    }

    @Override
    public boolean execute() {
        boolean success = false;
        return success;
    }
}

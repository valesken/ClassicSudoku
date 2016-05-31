package me.valesken.jeff.sudoku_model;

/**
 * Created by jeff on 5/31/16.
 */
public class TechniqueMultiLine implements Technique {

    protected Solver solver;

    protected TechniqueMultiLine(Solver solver) {
        this.solver = solver;
    }

    @Override
    public boolean execute() {
        boolean success = false;
        return success;
    }
}

package me.valesken.jeff.benchmark;

import org.junit.Test;

import me.valesken.jeff.sudoku_model.ModelProxy;

/**
 * Created by jeff on 6/5/2016.
 * Last Updated on 6/5/2016.
 */
public class SimpleBenchMarker {

    private final int HOUSE_SIZE = 9;

    @Test
    public void runSimpleBenchMarker_NoAI() {
        boolean useAI = false;
        int trials = 1000;
        for(int difficulty = 1; difficulty < 4; ++difficulty) {
            long sum = 0;
            long max = 0;
            long[] times = new long[trials];
            for (int i = 0; i < trials; ++i) {
                long start = System.currentTimeMillis();
                ModelProxy.newGame(HOUSE_SIZE, difficulty, null, useAI);
                long end = System.currentTimeMillis();
                times[i] = end - start;
                sum += times[i];
                if (times[i] > max) {
                    max = times[i];
                }
            }
            System.out.println("Difficulty level: " + difficulty);
            System.out.println("Average build time is " + sum / trials + " ms");
            System.out.println("Longest build time is " + max + " ms\n");
        }
    }

    // TODO: Only testing Easy right now... add Medium & Hard as the Techniques get finished
    @Test
    public void runSimpleBenchMarker_WithAI() {
        boolean useAI = true;
        int trials = 1000;
        int difficulty = 1;

        long sum = 0;
        long max = 0;
        long[] times = new long[trials];
        for (int i = 0; i < trials; ++i) {
            long start = System.currentTimeMillis();
            ModelProxy.newGame(HOUSE_SIZE, difficulty, null, useAI);
            long end = System.currentTimeMillis();
            times[i] = end - start;
            sum += times[i];
            if (times[i] > max) {
                max = times[i];
            }
        }
        System.out.println("Difficulty level: " + difficulty);
        System.out.println("Average build time is " + sum / trials + " ms");
        System.out.println("Longest build time is " + max + " ms\n");
    }
}

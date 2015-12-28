package me.valesken.jeff.sudoku_structure;

//import android.util.SparseArray;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 7/12/2015
 *
 * House contains a row, column, or zone of 9 Tiles.
 * House can check if it already contains a tile with a given value.
 * House can check if there are contradictions in it and say which Tiles contradict.
 * House can return a tile at the specified position in the House.
 * House does not need to do anything else;
 */
public class House {

    private int boardSize;
    private ArrayList<Tile> members;
    //private SparseArray<LinkedList<Tile>> values; // For contradiction checking

    public House(int mBoardSize) {
        boardSize = mBoardSize;
        members = new ArrayList<>(mBoardSize);
        //values = new SparseArray<LinkedList<Tile>>();
        //for(int i = 1; i <= boardSize; ++i)
        //    values.put(i, new LinkedList<Tile>());
    }

    /*
     * Setters
     */
    // The order in which Tiles are added corresponds to the position of the Tiles in the House.
    public void addMember(Tile t)
    {
        if(members.size() <= boardSize)
            members.add(t); // Add to end - O(1) insert to end, maintains position in House
    }

    /*
     * Getters
     */
    public Tile getMember(int position) { return members.get(position); }

    // Checks if the house already has a member with a specific value
    public boolean hasValue(int v) {
        for(Tile t : members)
            if(t.getValue() == v)
                return true;
        return false;
    }

    /*
    // Checks if contradictions exist in the house
    public boolean hasContradiction() { return hasContradiction(new LinkedList<Tile>()); }

    // Checks if contradictions exist in the house
    public boolean hasContradiction(LinkedList<Tile> contradictions) {
        // Initialize values
        int value;
        boolean result = false;
        for(int i = 1; i <= boardSize; ++i)
            values.get(i).clear();

        // Get the values from the members of the house
        for(Tile t : members) {
            value = t.getValue();
            if(value != 0)
                values.get(value).add(t);
        }

        // Check if any values contain multiples
        for(int i = 1; i < boardSize; ++i) {
            if(values.get(i).size() > 1) {
                contradictions.addAll(values.get(i));
                result = true;
            }
        }

        // Return the result
        return result;
    }*/

    public int getValueCount()
    {
        int count = 0;
        for(Tile t : members)
            if(t.getValue() > 0)
                ++count;
        return count;
    }

    public LinkedList<Tile> getValueTiles()
    {
        LinkedList<Tile> valueTiles = new LinkedList<>();
        for(Tile t : members)
            if(t.getValue() > 0)
                valueTiles.add(t);
        return valueTiles;
    }
}

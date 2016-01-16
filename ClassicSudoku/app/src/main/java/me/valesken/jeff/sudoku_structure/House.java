package me.valesken.jeff.sudoku_structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 1/15/2016
 *
 * House contains a row, column, or zone of 9 Tiles.
 * House can check if it already contains a tile with a given value.
 * House can check if there are contradictions in it and say which Tiles contradict.
 * House can return a tile at the specified position in the House.
 * House does not need to do anything else;
 */
public class House implements Iterable<Tile> {

    @SuppressWarnings("unchecked")
    private HashSet<Tile>[] valueOwners = new HashSet[9];
    private int boardSize;
    private ArrayList<Tile> members;

    public House(int mBoardSize) {
        boardSize = mBoardSize;
        members = new ArrayList<>(mBoardSize);
        for(int i = 0; i < 9; ++i)
            valueOwners[i] = new HashSet<>();
    }

    //region Setters
    /**
     * This function adds a Tile to this House. The order in which Tiles are added corresponds to
     * the position of the Tiles in the House, so order is important.
     *
     * @param t The next tile to add to this House.
     */
    public void addMember(Tile t) {
        if(members.size() <= boardSize)
            members.add(t); // Add to end - O(1) insert to end, maintains position in House
    }

    /**
     * This function will assign (or remove assignment) to a particular value in this House.
     * E.g. This House will think Tile t claims 4.
     *
     * @param value The 1-9 value to assign (or remove assignment) to the Tile.
     * @param assign Whether to assign or remove assignment of the value to the Tile.
     * @param tile The Tile that the value will be assigned to.
     */
    public void setValueInHouse(int value, boolean assign, Tile tile) {
        --value; // Move values 1-9 to 0-8
        if(assign)
            valueOwners[value].add(tile);
        else
            valueOwners[value].remove(tile);
    }

    /**
     * For a given value, this function will remove all Tiles that are currently assigned to that
     * value in this house. E.g. This House no longer thinks 4 is claimed by any Tile.
     *
     * @param value The 1-9 value in the House to clear.
     */
    public void clearValueInHouse(int value) {
        valueOwners[value - 1].clear();
    }
    //endregion

    //region Getters
    /**
     * @param position The 0-8 position within this House to get a Tile from.
     * @return The Tile at the corresponding position in the House.
     */
    public Tile getMember(int position) {
        return members.get(position);
    }

    /**
     * Checks if the house already has a member with a specific value.
     *
     * @param value The 1-9 value to check for.
     * @return True if this House contains a Tile to which the value is assigned, otherwise False.
     */
    public boolean hasValue(int value) {
        return valueOwners[value - 1].size() > 0;
    }

    /**
     * @return The number of Tiles in this House which have an assigned value.
     */
    public int getValueCount() {
        int count = 0;
        for(Tile t : members)
            if(t.getValue() > 0)
                ++count;
        return count;
    }

    /**
     * @return A LinkedList of Tiles in this House which have an assigned value. If no such Tiles,
     * the LinkedList will simply be an empty list.
     */
    public LinkedList<Tile> getValueTiles() {
        LinkedList<Tile> valueTiles = new LinkedList<>();
        for(Tile t : members)
            if(t.getValue() > 0)
                valueTiles.add(t);
        return valueTiles;
    }
    //endregion

    @Override
    public Iterator<Tile> iterator() {
        return members.iterator();
    }
}

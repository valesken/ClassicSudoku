package me.valesken.jeff.sudoku_model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 6/1/2016.
 *
 * House contains a row, column, or zone of 9 Tiles.
 * House can check if it already contains a tile with a given value.
 * House can check if there are contradictions in it and say which Tiles contradict.
 * House can return a tile at the specified position in the House.
 * House does not need to do anything else;
 */
class House implements Iterable<Tile> {

    protected HashSet<Tile>[] valueToOwnersMap;
    protected ArrayList<Tile> members;
    protected int houseIndex;
    protected int houseSize;
    protected int boardSize;

    @SuppressWarnings("unchecked")
    protected House(int _houseSize, int _houseIndex) {
        houseIndex = _houseIndex;
        houseSize = _houseSize;
        boardSize = houseSize * houseSize;
        members = new ArrayList<>(houseSize);
        valueToOwnersMap = new HashSet[9];
        for (int i = 0; i < 9; ++i) {
            valueToOwnersMap[i] = new HashSet<>();
        }
    }

    //region Setters

    /**
     * This function adds a Tile to this House. The order in which Tiles are added corresponds to
     * the position of the Tiles in the House, so order is important. Cannot add same tile twice.
     *
     * @param tile The next tile to add to this House.
     */
    protected void addMember(Tile tile) {
        if (members.size() < houseSize && !members.contains(tile)) {
            members.add(tile); // Add to end - O(1) insert to end, maintains position in House
        }
    }

    /**
     * This function will assign a Tile (or remove assignment of a Tile from) to a particular value
     * in this House. E.g. This House will think Tile t claims 4. Because of the AI, it is possible
     * to assign multiple tiles to a particular value.
     *
     * @param value     The 1-9 value to assign (or remove assignment) to the Tile.
     * @param assign    Whether to assign or remove assignment of the value to the Tile.
     * @param tile      The Tile that the value will be assigned to.
     * @return true if successful, false otherwise
     */
    protected boolean setValueInHouse(int value, boolean assign, Tile tile) {
        if (value > 0 && value <= houseSize) {
            if (assign) {
                return valueToOwnersMap[value - 1].add(tile);
            } else {
                return valueToOwnersMap[value - 1].remove(tile);
            }
        }
        return false;
    }

    /**
     * For a given value, this function will remove all Tiles that are currently assigned to that value in this house.
     * It also propagates this down to the Tile itself. E.g. This House no longer thinks 4 is claimed by any Tile and
     * any Tiles which previously had 4 as their value or note no longer do.
     *
     * @param value The 1-9 value in the House to clear.
     * @return true if successful, false otherwise
     */
    protected boolean clearValueInHouse(int value) {
        if (value > 0 && value <= houseSize) {
            HashSet<Tile> claimants = new HashSet<>(valueToOwnersMap[value - 1]);
            for(Tile claimant : claimants) {
                valueToOwnersMap[value - 1].remove(claimant);
                claimant.clearValue(value);
            }
            return true;
        }
        return false;
    }
    //endregion

    //region Getters

    /**
     * @param position The 0-8 position within this House to get a Tile from.
     * @return The Tile at the corresponding position in the House, null if no Tile at that position.
     */
    protected Tile getMember(int position) {
        if (position < 0 || position >= members.size()) {
            return null;
        }
        return members.get(position);
    }

    /**
     * Checks if the house already has a member with a specific value.
     *
     * @param value The 1-9 value to check for.
     * @return True if this House contains a Tile to which the value is assigned, otherwise False.
     */
    protected boolean hasValue(int value) {
        return (value > 0 && value <= houseSize && valueToOwnersMap[value - 1].size() > 0);
    }

    /**
     * @return The number of Tiles in this House which have an assigned value.
     */
    protected int getValueCount() {
        int count = 0;
        for (Tile t : members) {
            if (t.getValue() > 0) {
                ++count;
            }
        }
        return count;
    }

    /**
     * @return A LinkedList of Tiles in this House which have an assigned value. If no such Tiles,
     * the LinkedList will simply be an empty list.
     */
    protected LinkedList<Tile> getValueTiles() {
        LinkedList<Tile> valueTiles = new LinkedList<>();
        for (Tile t : members) {
            if (t.getValue() > 0) {
                valueTiles.add(t);
            }
        }
        return valueTiles;
    }

    /**
     * @return The index (0 - 8) for this house.
     */
    protected int getHouseIndex() {
        return houseIndex;
    }
    //endregion

    @Override
    public Iterator<Tile> iterator() {
        return members.iterator();
    }
}

package me.valesken.jeff.sudoku_structure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 1/15/2016.
 */

public class Tile {
    static public String jsonIndexId = "index";
    static public String jsonRowId = "row";
    static public String jsonColumnId = "column";
    static public String jsonZoneId = "zone";
    static public String jsonNoteModeId = "noteMode";
    static public String jsonOrigId = "orig";
    static public String jsonValuesId = "notesOrValue";

    public int houseSize;
    public int index;
    public int rowNumber;
    public House row;
    public int columnNumber;
    public House column;
    public int zoneNumber;
    public House zone;
    public int value;
    public boolean[] notes;
    public boolean noteMode; // true = notes, false = value
    public boolean orig; // true = Tile's value & noteMode are unchangeable


    public Tile(int _houseSize, int _index) {
        index = _index;
        houseSize = _houseSize;
        int zoneWidth = (int) Math.sqrt(houseSize);
        rowNumber = index / houseSize;
        columnNumber = index % houseSize;
        zoneNumber = 3 * (rowNumber / zoneWidth) + columnNumber / zoneWidth;
        noteMode = false;
        orig = false;
        notes = new boolean[houseSize];
        clear();

        visited = false;
        lastTried = 0;
    }

    //region Setters

    /**
     * @param r The row that this tile belongs to.
     * @param c The column that this tile belongs to.
     * @param z The zone that this tile belongs to.
     * @throws IllegalArgumentException If any House is null or has the wrong index for this Tile.
     */
    public void setHouses(House r, House c, House z) throws IllegalArgumentException {
        if (!orig) {
            if (r == null || c == null || z == null) {
                throw new IllegalArgumentException("You must not set Houses in the Tile class to null.");
            }
            if (r.getHouseIndex() != rowNumber) {
                throw new IllegalArgumentException("You are passing the wrong row to this Tile");
            }
            if (c.getHouseIndex() != columnNumber) {
                throw new IllegalArgumentException("You are passing the wrong column to this Tile");
            }
            if (z.getHouseIndex() != zoneNumber) {
                throw new IllegalArgumentException("You are passing the wrong zone to this Tile");
            }
            row = r;
            column = c;
            zone = z;
        }
    }

    /**
     * To be used internally by Tile only! (Or in tests)
     *
     * @param value   The value you want to assign to or remove from this tile in this tile's houses
     * @param inHouse True - assign to the desired value; False - remove from the desired value
     */
    public void setValueInHouses(int value, boolean inHouse) {
        if (!orig && value > 0 && value < houseSize) {
            row.setValueInHouse(value, inHouse, index);
            column.setValueInHouse(value, inHouse, index);
            zone.setValueInHouse(value, inHouse, index);
        }
    }

    /**
     * This will add the value v as a note if this tile is in note mode (or remove the note if it is
     * in note mode and the note has already been added). If it is not in note mode, it will set the
     * value v as the tile's current value (or, if this tile's current value is already v, then it
     * will clear the tile).
     *
     * @param v The value you want to update this tile with.
     */
    public void update(int v) {
        if ((v > 0 && v <= houseSize) && !orig) {
            if (noteMode)
                notes[v - 1] = !notes[v - 1];
            else if (value == v) {
                value = 0;
                setValueInHouses(v, false);
            } else {
                if (value > 0)
                    setValueInHouses(value, false);
                value = v;
                setValueInHouses(v, true);
            }
        }
    }

    /**
     * This will remove this tile's current value from its houses, will clear its current value,
     * and will remove all its current notes.
     */
    public void clear() {
        if (!orig) {
            if (value > 0)
                setValueInHouses(value, false);
            value = 0;
            for (int i = 0; i < houseSize; ++i) {
                notes[i] = false;
            }
        }
    }

    /**
     * This will switch the tile between note mode and value mode. If it is in one, it will switch
     * to the other.
     */
    public void toggleMode() {
        if (!orig) {
            if (noteMode) { // Switch from notes to value
                // If only one hint recorded, make it the new value
                int v = 0;
                for (int i = 0; i < houseSize; ++i) {
                    if (notes[i]) {
                        v = (v == 0) ? (i + 1) : -1; // 0 if no hints, -1 if multiple hints
                        notes[i] = false;
                    }
                }
                if (v > 0) {
                    value = v;
                    setValueInHouses(v, true);
                }
            } else { // Switch from value to notes
                // If there's a value recorded, make it a hint
                if (value > 0)
                    notes[value - 1] = true;
                value = 0;
            }

            noteMode = !noteMode;
        }
    }
    //endregion

    //region Getters

    /**
     * @return A LinkedList containing the current notes in this tile or, if this tile is not in
     * note mode, a single value which is the current value of this tile (0 if empty).
     */
    public LinkedList<Integer> getNotesOrValue() {
        if (noteMode)
            return getNotes();
        LinkedList<Integer> list = new LinkedList<>();
        list.add(getValue());
        return list;
    }

    /**
     * @return The current value of the Tile. 0 if no current value or the Tile is in Node mode.
     */
    public int getValue() {
        return value;
    }

    /**
     * @return A LinkedList containing the current notes in this tile. If this tile is not in note
     * mode, it will return null.
     */
    public LinkedList<Integer> getNotes() {
        LinkedList<Integer> notesList = new LinkedList<>();
        if (noteMode) {
            for (int i = 0; i < houseSize; ++i) {
                if (notes[i])
                    notesList.add(i + 1);
            }
        }
        return notesList;
    }

    /**
     * @return The 0-80 index of this Tile in the Board.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The row that this Tile belongs to.
     */
    public House getRow() {
        return row;
    }

    /**
     * @return The 0-9 index of the row that this Tile belongs to.
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * @return The column that this Tile belongs to.
     */
    public House getColumn() {
        return column;
    }

    /**
     * @return The 0-9 index of the column that this Tile belongs to.
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return The zone that this Tile belongs to.
     */
    public House getZone() {
        return zone;
    }

    /**
     * @return The 0-9 index of the zone that this Tile belongs to.
     */
    public int getZoneNumber() {
        return zoneNumber;
    }

    /**
     * @return True if this Tile is currently marked to accept notes, otherwise False.
     */
    public boolean isNoteMode() {
        return noteMode;
    }

    /**
     * @return True if this Tile is a starting, original, unchangeable Tile, otherwise False.
     */
    public boolean isOrig() {
        return this.orig;
    }

    /**
     * Only to be used when saving the game.
     *
     * @return a JSON representation of this Tile's current state.
     */
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(jsonIndexId, this.getIndex());
            jsonObject.put(jsonRowId, this.getRowNumber());
            jsonObject.put(jsonColumnId, this.getColumnNumber());
            jsonObject.put(jsonZoneId, this.getZoneNumber());
            jsonObject.put(jsonNoteModeId, this.isNoteMode());
            jsonObject.put(jsonOrigId, this.isOrig());
            JSONArray notesOrValueArray = new JSONArray(this.getNotesOrValue());
            jsonObject.put(jsonValuesId, notesOrValueArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //endregion

    //region Initialization

    public boolean visited;
    public int lastTried;

    /**
     * Mark this Tile as NOT visited in the current initialization path.
     */
    public void unVisit() {
        visited = false;
    }

    /**
     * @return True if this Tile has already been visited in the current initialization path,
     * otherwise False.
     */
    public boolean hasBeenVisited() {
        return visited;
    }

    /**
     * This function is a slightly faster way of trying initial values than calling tryInitValue()
     * with the values 1 - 9.
     *
     * @return True if this Tile successfully picked a possible initial value, otherwise False
     */
    public boolean tryInitialize() {
        for (int initValue = lastTried + 1; initValue <= houseSize; ++initValue) {
            lastTried = initValue;
            if (!(row.hasValue(initValue) || column.hasValue(initValue) || zone.hasValue(initValue))) {
                update(initValue);
                visited = true;
                return true;
            }
        }
        return false;
    }

    /**
     * @param initValue The value to try as an initial value for this Tile (1 - 9)
     * @return true if the value doesn't contradict anything in any of the houses, false otherwise
     */
    public boolean tryInitValue(int initValue) {
        if (initValue < 1 || initValue > houseSize)
            return false;
        if (row.hasValue(initValue) || column.hasValue(initValue) || zone.hasValue(initValue))
            return false;
        visited = true;
        update(initValue);
        return true;
    }

    /**
     * Call when board initialization has reached a contradiction and needs to clear Tiles.
     */
    public void resetInitializationState() {
        if (value > 0)
            setValueInHouses(value, false);
        value = 0;
        visited = false;
        lastTried = 0;
    }

    /**
     * Set this Tile as an original, unchangeable, starting Tile on the Board.
     *
     * @param _orig Whether or not to set this Tile as an original, starting Tile.
     */
    public void setOrig(boolean _orig) {
        orig = _orig;
    }

    /**
     * Only to be used for loading a previous game. Will not load any data if the provided json
     * is corrupted or otherwise incorrect.
     *
     * @param jsonObject A JSON representation of this Tile's state.
     */
    public void loadTileState(JSONObject jsonObject) throws JSONException {
        // Get JSON Data
        int loadedIndex = jsonObject.getInt(jsonIndexId);
        int loadedRowNumber = jsonObject.getInt(jsonRowId);
        int loadedColumnNumber = jsonObject.getInt(jsonColumnId);
        int loadedZoneId = jsonObject.getInt(jsonZoneId);
        boolean loadedNoteMode = jsonObject.getBoolean(jsonNoteModeId);
        boolean loadedOrig = jsonObject.getBoolean(jsonOrigId);
        JSONArray jsonArray = jsonObject.getJSONArray(jsonValuesId);
        // If no problems, load the data into this Tile
        this.index = loadedIndex;
        this.rowNumber = loadedRowNumber;
        this.columnNumber = loadedColumnNumber;
        this.zoneNumber = loadedZoneId;
        this.noteMode = loadedNoteMode;
        this.orig = loadedOrig;
        if (noteMode) {
            value = 0;
            for (int i = 0; i < jsonArray.length(); ++i) {
                notes[jsonArray.getInt(i) - 1] = true;
            }
        } else {
            value = jsonArray.getInt(0);
        }
    }
    //endregion
}

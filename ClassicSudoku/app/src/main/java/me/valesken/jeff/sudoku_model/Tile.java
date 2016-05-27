package me.valesken.jeff.sudoku_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 1/15/2016.
 */

class Tile {
    static final protected String JSON_INDEX_ID = "index";
    static final protected String JSON_ROW_ID = "row";
    static final protected String JSON_COLUMN_ID = "column";
    static final protected String JSON_ZONE_ID = "zone";
    static final protected String JSON_NOTE_MODE_ID = "noteMode";
    static final protected String JSON_ORIG_ID = "orig";
    static final protected String JSON_VALUES_ID = "notesOrValue";

    protected int houseSize;
    protected int index;
    protected int rowNumber;
    protected House row;
    protected int columnNumber;
    protected House column;
    protected int zoneNumber;
    protected House zone;
    protected int value;
    protected boolean[] notes;
    protected boolean noteMode; // true = notes, false = value
    protected boolean orig; // true = Tile's value & noteMode are unchangeable

    //region Constructors
    protected Tile(int houseSize, int index) {
        this.index = index;
        this.houseSize = houseSize;
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

    protected Tile(int houseSize, JSONObject loadState) throws JSONException {
        this.houseSize = houseSize;
        loadTileState(loadState);
    }
    //endregion

    //region Setters

    /**
     * @param r The row that this tile belongs to.
     * @param c The column that this tile belongs to.
     * @param z The zone that this tile belongs to.
     * @throws IllegalArgumentException If any House is null or has the wrong index for this Tile.
     */
    protected void setHouses(House r, House c, House z) throws IllegalArgumentException {
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
     * @param value   The 1 - 9 value you want to assign to or remove from this tile in this tile's houses
     * @param inHouse True - assign to the desired value; False - remove from the desired value
     */
    protected void setValueInHouses(int value, boolean inHouse) {
        if (!orig && value > 0 && value <= houseSize) {
            row.setValueInHouse(value, inHouse, index);
            column.setValueInHouse(value, inHouse, index);
            zone.setValueInHouse(value, inHouse, index);
        }
    }

    /**
     * This will add the value v as a note if this tile is in note mode (or remove the note if it is in note mode and
     * the note has already been added). If it is not in note mode, it will set the value v as the tile's current
     * value (or, if this tile's current value is already v, then it will clear the tile).
     *
     * @param v The 1 - 9 value you want to update this tile with.
     */
    protected void update(int v) {
        if ((v > 0 && v <= houseSize) && !orig) {
            if (noteMode) {
                notes[v - 1] = !notes[v - 1];
            } else if (value == v) {
                value = 0;
                setValueInHouses(v, false);
            } else {
                if (value > 0) {
                    setValueInHouses(value, false);
                }
                value = v;
                setValueInHouses(v, true);
            }
        }
    }

    /**
     * This will remove this tile's current value from its houses, will clear its current value, and will remove all
     * its current notes. This will NOT clear the "lastTried" value.
     */
    protected void clear() {
        if (!orig) {
            if (value > 0) {
                setValueInHouses(value, false);
            }
            value = 0;
            for (int i = 0; i < houseSize; ++i) {
                notes[i] = false;
            }
        }
    }

    /**
     * This will switch the tile between note mode and value mode. If it is in one, it will switch to the other.
     */
    protected void toggleMode() {
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
                if (value > 0) {
                    notes[value - 1] = true;
                }
                value = 0;
            }

            noteMode = !noteMode;
        }
    }
    //endregion

    //region Getters

    /**
     * @return A LinkedList containing the current notes in this tile or, if this tile is not in note mode, a single
     * value which is the current value of this tile (0 if empty).
     */
    protected LinkedList<Integer> getNotesOrValue() {
        if (noteMode) {
            return getNotes();
        }
        LinkedList<Integer> list = new LinkedList<>();
        list.add(getValue());
        return list;
    }

    /**
     * @return The current value of the Tile. 0 if no current value or the Tile is in Node mode.
     */
    protected int getValue() {
        return value;
    }

    /**
     * @return A LinkedList containing the current notes in this tile. If this tile is not in note mode, it will
     * return null.
     */
    protected LinkedList<Integer> getNotes() {
        LinkedList<Integer> notesList = new LinkedList<>();
        if (noteMode) {
            for (int i = 0; i < houseSize; ++i) {
                if (notes[i]) {
                    notesList.add(i + 1);
                }
            }
        }
        return notesList;
    }

    /**
     * @return The 0-80 index of this Tile in the Board.
     */
    protected int getIndex() {
        return index;
    }

    /**
     * @return The row that this Tile belongs to.
     */
    protected House getRow() {
        return row;
    }

    /**
     * @return The 0-9 index of the row that this Tile belongs to.
     */
    protected int getRowNumber() {
        return rowNumber;
    }

    /**
     * @return The column that this Tile belongs to.
     */
    protected House getColumn() {
        return column;
    }

    /**
     * @return The 0-9 index of the column that this Tile belongs to.
     */
    protected int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return The zone that this Tile belongs to.
     */
    protected House getZone() {
        return zone;
    }

    /**
     * @return The 0-9 index of the zone that this Tile belongs to.
     */
    protected int getZoneNumber() {
        return zoneNumber;
    }

    /**
     * @return True if this Tile is currently marked to accept notes, otherwise False.
     */
    protected boolean isNoteMode() {
        return noteMode;
    }

    /**
     * @return True if this Tile is a starting, original, unchangeable Tile, otherwise False.
     */
    protected boolean isOrig() {
        return this.orig;
    }

    /**
     * Only to be used when saving the game.
     *
     * @return a JSON representation of this Tile's current state.
     */
    protected JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_INDEX_ID, this.getIndex());
            jsonObject.put(JSON_ROW_ID, this.getRowNumber());
            jsonObject.put(JSON_COLUMN_ID, this.getColumnNumber());
            jsonObject.put(JSON_ZONE_ID, this.getZoneNumber());
            jsonObject.put(JSON_NOTE_MODE_ID, this.isNoteMode());
            jsonObject.put(JSON_ORIG_ID, this.isOrig());
            JSONArray notesOrValueArray = new JSONArray(this.getNotesOrValue());
            jsonObject.put(JSON_VALUES_ID, notesOrValueArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //endregion

    //region Initialization

    protected boolean visited;
    protected int lastTried;

    /**
     * Mark this Tile as NOT visited in the current initialization path. It also clears its current value so that it
     * is primed for the next initialization attempt.
     */
    protected void unVisit() {
        clear();
        visited = false;
    }

    /**
     * @return True if this Tile has already been visited in the current initialization path, otherwise False.
     */
    protected boolean hasBeenVisited() {
        return visited;
    }

    /**
     * This function is a slightly faster way of trying initial values than calling seedInitialValue() with the values 1
     * to 9. It keeps track of the last tried value and simply goes to the next one in the series rather than trying
     * every value from 1 through 9. If the last value was 9, it simply aborts with a return value of false (meaning it
     * failed to find any possible initial value).
     *
     * @return True if this Tile successfully picked a possible initial value, otherwise False
     */
    protected boolean tryInitialize() {
        for (int initValue = lastTried + 1; initValue <= houseSize; ++initValue) {
            lastTried = initValue; // Save most recently tried value
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
    protected boolean seedInitialValue(int initValue) {
        if (initValue < 1 || initValue > houseSize) {
            return false;
        }
        if (row.hasValue(initValue) || column.hasValue(initValue) || zone.hasValue(initValue)) {
            return false;
        }
        update(initValue);
        visited = true;
        return true;
    }

    /**
     * Call when board initialization has reached a contradiction and needs to clear Tiles.
     */
    protected void resetInitializationState() {
        if (value > 0) {
            setValueInHouses(value, false);
        }
        value = 0;
        visited = false;
        lastTried = 0;
    }

    /**
     * Set this Tile as an original, unchangeable, starting Tile on the Board.
     *
     * @param _orig Whether or not to set this Tile as an original, starting Tile.
     */
    protected void setOrig(boolean _orig) {
        orig = _orig;
    }

    /**
     * Only to be used for loading a previous game. Will not load any data if the provided json is corrupted or
     * otherwise incorrect.
     *
     * @param jsonObject A JSON representation of this Tile's state.
     */
    protected void loadTileState(JSONObject jsonObject) throws JSONException {
        // Get JSON Data
        int loadedIndex = jsonObject.getInt(JSON_INDEX_ID);
        int loadedRowNumber = jsonObject.getInt(JSON_ROW_ID);
        int loadedColumnNumber = jsonObject.getInt(JSON_COLUMN_ID);
        int loadedZoneId = jsonObject.getInt(JSON_ZONE_ID);
        boolean loadedNoteMode = jsonObject.getBoolean(JSON_NOTE_MODE_ID);
        boolean loadedOrig = jsonObject.getBoolean(JSON_ORIG_ID);
        JSONArray jsonArray = jsonObject.getJSONArray(JSON_VALUES_ID);
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

package me.valesken.jeff.sudoku_structure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 7/12/2015
 */
public class Tile {
    private int boardSize;
    private int index;
    private int rowNumber;
    private House row;
    private int columnNumber;
    private House column;
    private int zoneNumber;
    private House zone;

    private int value;
    private boolean [] notes;

    private boolean noteMode; // true = notes, false = value
    private boolean orig; // true means this Tile's value & noteMode are unchangeable

    public Tile(int mBoardSize, int in) {
        boardSize = mBoardSize;
        index = in;
        rowNumber = index/boardSize;
        columnNumber = index%boardSize;
        zoneNumber = (rowNumber/((int)Math.sqrt(boardSize)))*3 + columnNumber/((int)Math.sqrt(boardSize));
        noteMode = false;
        orig = false;
        notes = new boolean[boardSize];
        clear();

        visited = false;
        initValues = new boolean[mBoardSize];
    }

    //region Setters
    public void setHouses(House r, House c, House z) {
        row = r;
        column = c;
        zone = z;
    }

    public void update(int v) {
        if((v > 0 && v <= boardSize) && !orig)
        {
            if (noteMode)
                notes[v - 1] = !notes[v - 1];
            else
                value = (value == v) ? 0 : v;
        }
    }

    public void clear() {
        if(!orig)
        {
            value = 0;
            for (int i = 0; i < boardSize; ++i)
                notes[i] = false;
        }
    }

    public void toggleMode() {
        if(!orig)
        {
            if (noteMode) // Switch from notes to value
            {
                // If only one hint recorded, make it the new value
                int v = 0;
                for (int i = 0; i < boardSize; ++i) {
                    if (notes[i]) {
                        if (v == 0)
                            v = i + 1; // single/first hint
                        else
                            v = -1; // multiple hints
                        notes[i] = false;
                    }
                }
                if (v > -1)
                    value = v;
            }
            else // Switch from value to notes
            {
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
    public LinkedList<Integer> getNotesOrValue()
    {
        if(noteMode)
            return getNotes();
        else {
            LinkedList<Integer> list = new LinkedList<Integer>();
            list.add(getValue());
            return list;
        }
    }

    public int getValue() { return value; }

    public LinkedList<Integer> getNotes()
    {
        if(noteMode) {
            LinkedList<Integer> notesList = new LinkedList<Integer>();
            for (int i = 0; i < boardSize; ++i)
                if(notes[i])
                    notesList.add(i+1);
            return  notesList;
        }
        return null;
    }

    public int getIndex() { return index; }

    public int getRowNumber() { return rowNumber; }

    public int getColumnNumber() { return columnNumber; }

    public int getZoneNumber() { return zoneNumber; }

    public boolean isNoteMode() { return noteMode; }

    public boolean isOrig() { return this.orig; }

    public JSONObject getJSON()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("index", this.getIndex());
            jsonObject.put("row", this.getRowNumber());
            jsonObject.put("column", this.getColumnNumber());
            jsonObject.put("zone", this.getZoneNumber());
            jsonObject.put("noteMode", this.isNoteMode());
            jsonObject.put("orig", this.isOrig());

            JSONArray notesOrValueArray = new JSONArray();
            LinkedList<Integer> notesOrValue = this.getNotesOrValue();
            for(int i = 0; i < notesOrValue.size(); ++i)
                notesOrValueArray.put(notesOrValue.get(i));
            jsonObject.put("notesOrValue", notesOrValueArray);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //endregion

    /**********************************
     * INITIALIZATION SECTION - START *
     **********************************/

    //region Initialization

    private boolean visited;
    private boolean[] initValues;

    public void unVisit() { visited = false; }

    public boolean hasBeenVisited() { return visited; }

    // true - succeeded (value not yet tried)
    // false - did not succeed (value already tried or contradicts house)
    public boolean tryInitValue(int initValue)
    {
        if(initValues[initValue-1])
            return false;
        if(row.hasValue(initValue) || column.hasValue(initValue) || zone.hasValue(initValue)) {
            initValues[initValue -1] = true;
            return false;
        }
        visited = true;
        initValues[initValue-1] = true;
        this.update(initValue);
        return true;
    }

    // true - all values have been tried (probably need to undo all)
    // false - there is still an untried value
    public boolean allInitValuesTried()
    {
        for(boolean b : initValues)
            if(!b)
                return false;
        return true;
    }

    // Call when board initilization has reached a contradiction and needs to clear Tiles
    public void resetInitializationState()
    {
        for(int i = 0; i < initValues.length; ++i)
            initValues[i] = false;
        value = 0;
        visited = false;
    }

    public void setOrig(boolean b) { orig = b; }

    public void loadTileState(JSONObject jsonObject)
    {
        try {
            this.index = jsonObject.getInt("index");
            this.rowNumber = jsonObject.getInt("row");
            this.columnNumber = jsonObject.getInt("column");
            this.zoneNumber = jsonObject.getInt("zone");
            this.noteMode = jsonObject.getBoolean("noteMode");
            this.orig = jsonObject.getBoolean("orig");

            JSONArray jsonArray = jsonObject.getJSONArray("notesOrValue");
            if(noteMode)
                for (int i = 0; i < jsonArray.length(); ++i)
                    notes[jsonArray.getInt(i)-1] = true;
            else
                value = jsonArray.getInt(0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //endregion
}

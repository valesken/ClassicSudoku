package com.jeffvk.classicsudoku.game;

/* 
 * This object represents a particular tile in on the sudoku board.
 * It holds a value, and belongs to a row, a column and a zone, and an id number from 0 to 80.
 * The following public functions are available:
 *    Tile(int i)
 *    Tile(int i, int val)
 *    int getId()
 *    void setValue(int)
 *    void removeValue(int)
 *    void reset()
 *    boolean hasValue()
 *    int getValue()
 *    boolean hasTried(int)
 *    void setTried(int)
 *    void setRow(int)
 *    void setCol(int)
 *    void setZone(int)
 *    int getRow()
 *    int getCol()
 *    int getZone()
 *    void setOrig(boolean)
 *    boolean isOrig()
 *    void addNote(int)
 *    void removeNote(int)
 *    String getNotes()
 *    int numNotes()
 */

public class Tile
{
    // Member Variables
    private int value; // automatically initializes to 0
    private final int MAX_ROW = 9;
    private int id;
    private int row;
    private int col;
    private int zone;
    private boolean orig = false; // true only if part of the original configuration
    private boolean [] tried = new boolean[MAX_ROW];
    private boolean [] notes = new boolean[MAX_ROW];

    // Member functions.
    /*
     * This constructor sets this Tile's id, but not the value of this Tile.
     */
    public Tile(int i)
    {
        id = i;
        for(int j = 0; j < MAX_ROW; j++)
        {
            tried[j] = false;
			notes[j] = false;
        }
    }
        
    /*
     * This constructor sets this Tile's id and the value of this Tile.
     */
    public Tile(int i, int val)
    {
        id = i;
        for(int j = 0; j < MAX_ROW; j++)
        {
            tried[j] = false;
			notes[j] = false;
        }
        setValue(val);
    }

    /*
     * This function gets this Tile's id.
     */
    public int getId()
    {
        return id;
    }

    /*
     * This function sets the value of this Tile.
     */
    public void setValue (int val)
    {
		// If the value being "set" is already present (as a note, tried, or value), remove it
    	if (val == 0)
    	{
    		for(int i = 0; i < MAX_ROW; i++)
    		{
    			notes[i] = false;
    			tried[i] = false;
    		}
    		value = 0;
    	}
    	else if(notes[val-1])
		{
			notes[val-1] = false;
			tried[val-1] = false;
			if(value == val)
				value = 0;
			 // If removing the value as a note left only 1 note, make that 1 note the new value
			else if (numNotes() == 1)
			{
				for(int i = 0; i < MAX_ROW; i++)
				{
					if(notes[i])
					{
						value = i+1;
						break;
					}
				}
			}
		}
		// Otherwise, it's not present, so make it the new value and add it as a note
		else
		{
			if(numNotes() == 0)
				value = val;
			else
				value = 0;
			if(val > 0)
			{
				setTried(val);
				addNote(val);
			}
		}
    }

    /*
     * This function removes the value of this Tile and the note for the same value.
     * This function should be used when the value of the Tile is known.
     */
    public void removeValue (int val)
    {
        if(value != 0)
			value = 0;
		removeNote(val);
		if(numNotes() == 1)
		{
			for(int i = 0; i < MAX_ROW; i++)
			{
				if(notes[i])
				{
					value = i+1;
					break;
				}
			}
		}
    }
        
    /*
     * This function removes the value of this Tile, its tried values, and its notes.
     * This function should be used when the value of the Tile is unknown.
     */
    public void reset()
    {
        value = 0;
        for(int i = 0; i < MAX_ROW; i++)
		{
            tried[i] = false; // necessary for backtracking in generation
			notes[i] = false;
		}
    }

    /*
     * This function discovers if this Tile has a non-zero value assigned to it.
     */
    public boolean hasValue()
    {
        boolean result = false;
        if(value != 0)
            result = true;
        return result;
    }

    /*
     * This function returns the current value of this Tile.
	 * Used only in generating the board.
     */
    public int getValue()
    {
        return value;
    }

	/*
	 * This function will report whether a value has already been tried.
	 * Used only in generating the board.
	 */
    public boolean hasTried(int val)
    {
        return tried[val-1];
    }

	/*
	 * This function will record a value as having already been tried.
	 */
    public void setTried(int val)
    {
        tried[val-1] = true;
    }

    /*
     * This function tells this Tile what row it belongs to, so long as it is valid.
     */
    public void setRow(int r)
    {
        if(!(r < 0 || r > 8))
            row = r;
    }

    /*
     * This function tells this Tile what column it belongs to, so long as it is valid.
     */
    public void setCol(int c)
    {
        if(!(c < 0 || c > 8))
            col = c;
    }

    /*
     * This function tells this Tile what zone it belongs to, so long as it is valid.
     */
    public void setZone(int z)
    {
        if(!(z < 0 || z > 8))
            zone = z;
    }

    /*
     * This function returns the row to which this Tile belongs.
     */
    public int getRow()
    {
        return row;
    }

    /*
     * This function returns the column to which this Tile belongs.
     */
    public int getCol()
    {
        return col;
    }

    /*
     * This function returns the zone to which this Tile belongs.
     */
    public int getZone()
    {
        return zone;
    }

	/*
	 * This function sets the Tile as an original seed Tile.
	 */
    public void setOrig(boolean o)
    {
        orig = o;
    }

	/*
	 * This function reports whether or not this Tile is an original seed Tile.
	 */
    public boolean isOrig()
    {
        return orig;
    }

	/*
	 * This function adds a new note to this Tile.
	 */
    public void addNote(int val)
    {
        notes[val-1] = true;
    }

	/*
	 * This function removes a note from this Tile.
	 */
    public void removeNote(int val)
    {
        notes[val-1] = false;
    }

	/*
	 * This function returns a String containing any notes this Tile has.
	 */
    public String getNotes()
    {
		String n = "";
        for(int i = 0; i < MAX_ROW; i++)
        {
			if(notes[i])
			{
				n += Integer.toString(i+1);
				n += " ";
			}
		}
		return n;
    }

	/*
	 * This function tells you how many notes this Tile has.
	 */
    public int numNotes()
    {
		int num = 0;
        for(int i = 0; i < MAX_ROW; i++)
        {
			if(notes[i])
				num++;
		}
		return num;
    }
}
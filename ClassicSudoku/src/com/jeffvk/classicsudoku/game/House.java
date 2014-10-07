package com.jeffvk.classicsudoku.game;

/*
 * This object holds a group of 9 Tiles in an array called "members."
 * It also has a type, which can be 'r' for row, 'c' for column, or 'z' for zone.
 * The following public functions are available:
 *    House(char)
 *    void setMember(int index, Tile memb)
 *    boolean hasMember(int index)
 *    Tile getMember(int index)
 *    void setMemberValue(int index, int value)
 *    void removeMemberValue(int index, int value)
 *    int getMemberValue(int index)
 *    boolean hasMemberValue(int value)
 *    char getType()
 *    int length()
 */

class House
{
    // Member Variables
	private final int MAX_ROW = 9;
    private Tile [] members = new Tile[MAX_ROW]; // Default sets each Tile to NULL;
    private char type = ' ';
        
    // Member Functions.
    /*
     * This constructor will set the value of 'type.'
     * Acceptable values are 'r' for row, 'c' for column, and 'z' for zone.
     * If anything else is passed, the type will be ' '.
     */
    public House(char t)
    {
        if (t == 'r' || t == 'c' || t == 'z')
            type = t;
    }
        
    /*
     * This function will assign an index in the "members" array to point at a particular tile.
     */
    public void setMember(int index, Tile memb)
    {
        members[index] = memb;
    }
        
    /*
     * This function will return true if the member at the input index has a non-zero value.
     */
    public boolean hasMember(int index)
    {
        if (members[index].hasValue())
            return true;
        return false;
    }
        
    /*
     * This function will returns a pointer to the Tile object at the input index in "members".
     */
    public Tile getMember(int index)
    {
        return members[index];
    }
        
    /*
     * This function will set the value of a tile at the input index.
     */
    public void setMemberValue(int index, int value)
    {
        members[index].setValue(value);
    }
        
    /*
     * This function will remove the value of a tile at the input index.
     */
    public void removeMemberValue(int index, int value)
    {
        members[index].removeValue(value);
    }
        
    /*
     * This function will return the value of a tile at the input index.
     */
    public int getMemberValue(int index)
    {
        return members[index].getValue();
    }
        
    /*
     * This function returns true if the passed value is already contained in the House
     */
    public boolean hasMemberValue(int value)
    {
        for(int i = 0; i < MAX_ROW; i++)
        {
            if(members[i].getValue() == value)
                return true;
        }
        return false;
    }
		
    /*
     * This function will return the type of the House.
     */
    public char getType()
    {
        return type;
    }
        
    /*
     * This function will return the number of tiles in this House which have a value.
     */
    public int length()
    {
        int leng = 0; // number of tiles in the house with a value
        for(int i = 0; i < MAX_ROW; i++)
        {
            if(members[i].hasValue())
                leng++;
        }

        return leng;
    }
}
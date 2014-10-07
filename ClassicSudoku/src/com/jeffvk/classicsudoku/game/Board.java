package com.jeffvk.classicsudoku.game;

/*
 * This object generates a sudoku game by randomly seeding 9 tiles and then filling in the rest of
 * the tiles in the board in a consistent manner. This means there are 9^4 (6561) possible boards.
 * It can create easy, medium, and hard games, or can generate a game of random difficulty.
 * It then acts as the interface by which the main Sudoku program interacts with the game.
 */

import java.util.Random; // necessary to generate pseudo-random numbers

public class Board
{
	private final int MAX_ROW = 9;
	private final int MAX_SM = (int)(Math.sqrt(MAX_ROW));
	private final int BOARD_SIZE = (int)(MAX_ROW*MAX_ROW);
	
    // Member Variables
    private Tile [] boxes = new Tile[BOARD_SIZE];
    private House [] rows = new House[MAX_ROW];
    private House [] cols = new House[MAX_ROW];
    private House [] zones = new House[MAX_ROW];
	private int [] solution = new int[BOARD_SIZE];
    private Random rand = new Random();
    private int randNum;
	private long start = 0, now; // Used to keep track of time
    private int givens = 30; // This value is just to make sure the program runs, but 30 will never be used
    private int bound = 0; // Defaults to 0, which will only be used in the completely random case

    /*
     * Public functions.
     */
	public Board()
	{
        int rowNum, colNum, zoneNum;
        int zoneIndex;

        // Instantiate each member of the House Arrays
        for (int i = 0; i < MAX_ROW; i++)
        {
            rows[i] = new House('r');
            cols[i] = new House('c');
            zones[i] = new House('z');
        }
		
		for(int i = 0; i < (BOARD_SIZE); i++) // initialize boxes
		{
			rowNum = i/MAX_ROW;
			colNum = i%MAX_ROW;
			zoneNum = 0;
			for(int j = 0; j < rowNum/MAX_SM; j++)
			{
				zoneNum = zoneNum + MAX_SM;
			}
			for(int j = 0; j < colNum/MAX_SM; j++)
			{
				zoneNum++;
			}
			
			boxes[i] = new Tile(i);
			boxes[i].setRow(rowNum);
			rows[rowNum].setMember(colNum, boxes[i]);
			boxes[i].setCol(colNum);
			cols[colNum].setMember(rowNum, boxes[i]);
			zoneIndex = (rowNum%MAX_SM)*MAX_SM + (colNum%MAX_SM);
			boxes[i].setZone(zoneNum);
			zones[zoneNum].setMember(zoneIndex, boxes[i]);
		}
		
	}
	
		
    /*
     * The difficulty level is determined by the number of givens and the bound.
     * givens = the number of starting positions given to the user
     * bound = the minimum number of givens that can be in a row or column (not a zone, though)
     *
     * Case 1: Create a random EASY game. 40 - 49 givens, lower bound of 4 per row/col
     * Case 2: Create a random MEDIUM game. 32 - 39 givens, lower bound of 3 per row/col
     * Case 3: Create a random HARD game. 27 - 31 givens, lower bound of 2 per row/col
     * Case 4: Randomly generate a new game
     * Default: Should never be reached
     */
	public void newBoard(int difficulty)
    {
        switch(difficulty)
        {
            case 1:
                givens = Math.abs(rand.nextInt()) % 10 + 40;
                bound = 4;
                break;
            case 2:
                givens = Math.abs(rand.nextInt()) % 8 + 32;
                bound = 3;
                break;
            case 3:
                givens = Math.abs(rand.nextInt()) % 5 + 27;
                bound = 2;
                break;
            case 4:
                givens = Math.abs(rand.nextInt()) % 23 + 27;
            default:
                break;
        }
        int [] randIndexes = new int[givens]; // Contains which of the BOARD_SIZE boxes will be seeded with a number
        randomizeIndexes(givens, randIndexes); // Fill with random indexes between 0 and 80

		int randRow; // Holds a randomly generated number
		int index;
		int [] path = new int[BOARD_SIZE]; // Keeps track of the moves
		int lastChoice = -1; // points to the index in path of the last choice made
		for(int col = 0; col < MAX_ROW; col++) // Pick first random set of MAX_ROW numbers
		{
			randRow = Math.abs(rand.nextInt()%MAX_ROW);
			index = (MAX_ROW*randRow)+col;
			boxes[index].setValue(col+1);
			path[col] = index;
			lastChoice++;
		}
		
		// Go to start of grid and start filling in boxes
		int row, col, zone;
		index = 0;
		do
		{
			if(!(boxes[index].hasValue())) // if the box is empty, find a good value for that box
			{
				row = boxes[index].getRow();
				col = boxes[index].getCol();
				zone = boxes[index].getZone();
				// to check if a value is good, check each house and its tried values
				for(int i = 1; i <= MAX_ROW; i++)
				{
					if(rows[row].hasMemberValue(i) || cols[col].hasMemberValue(i) || zones[zone].hasMemberValue(i))
						boxes[index].setTried(i);
					else if(!boxes[index].hasTried(i))
					{
						boxes[index].setValue(i);
						lastChoice++;
						path[lastChoice] = index;
						break;
					}
				}
				// if there are no good values, reset this box and try another option in the previous box
				if(!(boxes[index].hasValue()))
				{
					boxes[index].reset();
					index = path[lastChoice];
					path[lastChoice] = 0;
					lastChoice--;
					boxes[index].removeValue(boxes[index].getValue());
					index--;
				}
			}
			if(index < (BOARD_SIZE - 1))
				index++;
			else
				index = 0;
		}while(notFull());
		
		
        getBoard(solution); // Record the solution to the new game.
        pickHoles(givens, randIndexes); // Pick holes in the board to get a starting game.
        checkBound(givens, bound); // Even out board to help control the difficulty level.
        setOrig(true); // Declares starting tiles unchangeable
    }

    /*
     * This function will fill an array of integers with the current values of each tile.
     * It must be public as this is how the rest of the app will display the board.
     * Note: It will fill an array in place when it is called, thus it MUST be called with a BOARD_SIZE member array.
     * If the function is called with a shorter array, it will return an IndexOutOfBounds exception.
     */
    public void getBoard(int[] nums)
    {
        for (int i = 0; i < BOARD_SIZE; i++)
            nums[i] = boxes[i].getValue();
    }
    
    /*
     * This function is for when primitive data types are not usable. E.g. when toString() needs to be called.
     */
    public void getBoard(Integer[] nums)
    {
    	for(int i = 0; i < BOARD_SIZE; i++)
    		nums[i] = (Integer) boxes[i].getValue();
    }

	/* 
	 * This function returns a boolean value indicating if the user's input is valid.
	 * Will return false for invalid rows or columns.
	 * Will also return false if the row or column or zone already contains the guessed value.
	 * Will also update the board if the guess is valid.
	 */
	public boolean updateBoard(int row, int col, int val)
	{
		if(row < 0 || (row > MAX_ROW-1))
			return false;
		if(col < 0 || col > MAX_ROW-1)
			return false;
		if(!checkTile(row, col, val))
			return false;
		setTile(row, col, val);
		return true;
	}
     	
    /*
     * This function tells the main game if it is valid to change the chosen tile in the chosen way.
     */
    public boolean checkTile(int row, int col, int num)
    {
        int zone = rows[row].getMember(col).getZone();
        if(rows[row].getMember(col).hasValue() && rows[row].getMember(col).isOrig())
            return false;
        if(num < 0 || num > MAX_ROW)
            return false;
        if(num != 0 && (rows[row].hasMemberValue(num) || cols[col].hasMemberValue(num) || zones[zone].hasMemberValue(num)))
            return false;
        return true;
    }
    
    public boolean checkTile(int index, int num)
    {
    	return checkTile(boxes[index].getRow(), boxes[index].getCol(), num);
    }
    
    public boolean checkTileIsOrig(int index)
    {
    	return boxes[index].isOrig();
    }
		
    /*
     * This function will allow the user to guess the value of a tile.
     */
    public void setTile(int row, int col, int num)
    {
    	if(!(rows[row].getMember(col).isOrig()))
    	{
    		if(rows[row].getMember(col).hasValue() && rows[row].getMember(col).getValue() != num)
    			rows[row].removeMemberValue(col, rows[row].getMemberValue(col)); // include line to disable notes
    		rows[row].setMemberValue(col, num);
    	}
    }
	
    public void setTile(int index, int num)
    {
    	if(!(boxes[index].isOrig()))
    	{
    		if(boxes[index].hasValue() && boxes[index].getValue() != num)
    			boxes[index].reset(); // include line to disable notes
    		boxes[index].setValue(num);
    	}
    }
    
    public int getTileValue(int index)
    {
    	return boxes[index].getValue();
    }
    
    /*
     * This functions returns true if the user has won.
     * It returns false if the game is not yet over.
     */
    public boolean isWon()
    {
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            if(!boxes[i].hasValue())
                return false;
        }
        return true;
    }
		
    /*
     * This function returns true if the game can still continue.
     * It returns false if there are no more valid guesses.
     */
    public boolean canContinue()
    {
        int row = 0;
        int col = 0;
        int zone = 0;
        // Check all boxes
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            // If a box is empty, check all values in its row, column, and zone
            // If a value is not in its row, column, or zone, then the game can still continue
            if(!(boxes[i].hasValue()))
            {
                row = boxes[i].getRow();
                col = boxes[i].getCol();
                zone = boxes[i].getZone();
                for(int j = 1; j < MAX_ROW+1; j++)
                {
                    if(!(rows[row].hasMemberValue(j)) && !(cols[col].hasMemberValue(j)) && !(zones[zone].hasMemberValue(j)))
                        return true;
                }
            }
        }
        return false;
    }
		
    /*
     * This function will fill in the value of a given spot.
     */
    public void getHint(int row, int col)
    {
        int index = row*MAX_ROW + col;
        int hint = solution[index];
        Tile tile = rows[row].getMember(col);
		tile.reset();
        tile.setValue(hint);
        tile.setOrig(true);
    }
    
    public void getHint(int index)
    {
    	Tile tile = boxes[index];
    	int hint = solution[index];
    	tile.reset();
    	tile.setValue(hint);
    	tile.setOrig(true);
    }
	
	/*
	 * This function will solve the game.
	 */
	public void solve()
	{
		for(int i = 0; i < BOARD_SIZE; i++)
		{
			boxes[i].reset();
			boxes[i].setValue(solution[i]);
		}
	}
	
	public void start()
	{
		start = (System.currentTimeMillis())/1000; // start time in seconds (1 sec = 1000 ms)
	}
	
	public boolean isStarted()
	{
		if(start == 0)
			return false;
		return true;
	}
	
	// Returns the time elapsed since the start of the game as a String in form "min:sec"
	public String getTime()
	{
		if(start == 0)
			return "0:00";
		now = (System.currentTimeMillis())/1000; // current time in seconds
		int seconds = (int)(now - start);
		int minutes = seconds/60;
		seconds = seconds%60;
		String time = minutes + ":";
		time+=(String.format("%02d", seconds)); // for some reason, time.concat() did not display the seconds
		return time;
	}
	
	public String getNotes(int index)
	{
		String notes = "";
		if(boxes[index].numNotes() != 0)
		{
			char row = (char)(index/MAX_ROW + 65);
			int col = index%MAX_ROW + 1;
			notes = notes + row + Integer.toString(col) + ": " + boxes[index].getNotes() + "\n";
		}
		return notes;
	}
	
	// This method returns the current state of the board as a String to be saved to a File.
	public String save()
	{
		String state = "";
		
		// current grid
		for(int i = 0; i < BOARD_SIZE; i++)
			state+=(boxes[i].getValue() + " ");
		
		// original starting locations
		int numOrigs = 0;
		String origs = "";
		for(int i = 0; i < BOARD_SIZE; i++)
		{
			if(boxes[i].isOrig())
			{
				numOrigs++;
				origs += (i + " ");
			}
		}
		state+=(numOrigs + " ");
		state+=(origs);
		
		// solution grid
		for(int i = 0; i < BOARD_SIZE; i++)
			state+=(solution[i] + " ");
		
		// save the current time in seconds
		if(start == 0)
			state += "0";
		else
		{
			now = (System.currentTimeMillis())/1000; // current time in seconds
			int time = (int)(now - start); // time elapsed in seconds
			state+=(time);
		}
		
		
		return state;
	}
	
	public void loadBoard(String state)
	{
		int index = 0, numOrigs = 0, time = 0;
		
		String [] stateStr = state.split(" ");
		int [] stateInt = new int [stateStr.length];
		for(int i = 0 ; i < stateInt.length; i++)
			stateInt[i] = Integer.parseInt(stateStr[i]);
		
		
		for(int i = 0; i < 81; i++)
		{
			boxes[i].setValue(stateInt[index]);
			index++;
		}
		
		numOrigs = stateInt[index];
		index++;
		
		for(int i = 0; i < numOrigs; i++)
		{
			boxes[stateInt[index]].setOrig(true);
			index++;
		}
		
		for(int i = 0; i < 81; i++)
		{
			solution[i] = stateInt[index];
			index++;
		}
		
		time = stateInt[index];
		
		start = (System.currentTimeMillis())/1000 - time;
		now = (System.currentTimeMillis()/1000);
	}

	
	
	
	
	
	
    /*
     * Private functions.
     */
	
	private boolean notFull()
	{
		for(int i = 0; i < BOARD_SIZE; i++)
		{
			if(!(boxes[i].hasValue()))
				return true;
		}
		return false;
	}
        
    /* 
     * This function will pick a number of random boxes based on int givens.
     * randNum will be used to hold an index value.
     */
    private void randomizeIndexes(int givens, int [] randIndexes)
    {
        int counter = 0;
        boolean advance; // Defaults to false
        do
        {
            randNum = Math.abs(rand.nextInt() % BOARD_SIZE);
            advance = true; // only make false if the index already has been picked
            for (int i = counter-1; i >= 0; i--) // Check all the indexes to see if this one has already been picked
            {
                if(randIndexes[i] == randNum) // If it has been picked, then stop checking and make advance false
                {
                    advance = false;
                    break;
                }
            }
            // Whether or not the index has already been picked, set it as a value in the array.
            // If the index has already been picked, the next time through the loop this index will be replaced with a new one.
            randIndexes[counter] = randNum;
            if(advance)
                counter++;
        } while(counter < givens);
    }
        
    /*
     * This function removes a large number of the answers in order to seed a game.
     * If a box's index is not included in the array randIndexes, then remove that box's value.
     * Otherwise, the box gets to keep its value.
     */
    private void pickHoles(int givens, int [] randIndexes)
    {
        boolean reserved = false;
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            reserved = false;
            for (int j = 0; j < randIndexes.length; j++)
            {
                if (i == randIndexes[j])
                    reserved = true;
            }
            if(!reserved)
                boxes[i].reset();
        }
    }
    
	/*
     * This function checks if any rows or columns are too small, then fixes their size accordingly.
     * While doing this, it finds the longest row or column and removes a value from it - this makes sure that
     * there is always the same number of givens.
     * It will stop checking when there are no rows or columns which are too small.
     */
    private void checkBound(int givens, int bound)
    {
        int tooSmallCount = 0; // Tracks how many rows and columns are too small based on the bound
        do
        {
            tooSmallCount = 0;            
            for (int i = 0; i < MAX_ROW; i++)
            {
                // If a row is too small, add a value to it & remove a value from the longest row.
                // Repeat this until the row is no longer too small.
                while (rows[i].length() < bound)
                {
                    tooSmallCount++;
                    addValue(rows[i]);
                    // Next find the row with most values and remove 1 value from that row.
                    int largestLength = 0;
                    int largestIndex = 0;
                    for (int j = 0; j < MAX_ROW; j++)
                    {
                        if (rows[j].length() > largestLength)
                        {
                            largestLength = rows[j].length();
                            largestIndex = j;
                        }
                    }
                    removeValue(rows[largestIndex], bound);
                }
                // Repeat for columns
                while(cols[i].length() < bound)
                {
                    tooSmallCount++;
                    addValue(cols[i]);
                    int largestLength = 0;
                    int largestIndex = 0;
                    for (int j = 0; j < MAX_ROW; j++)
                    {
                        if (cols[j].length() > largestLength)
                        {
                            largestLength = cols[j].length();
                            largestIndex = j;
                        }
                    }
                    removeValue(cols[largestIndex], bound);
                }
            }
        } while(tooSmallCount != 0);
    }
        
    /* 
     * This function will add another value to a house.
     * This function will only be called to make sure each house has at least the minimum number of values (bound).
     */
    private void addValue(House house)
    {
        boolean success = false; // tracks whether a new value was successfully added to the house
        int tileID;
        // Keep trying to add a new value until the function succeeds.
        // It will not succeed if the position in which it is trying to add a new value is already occupied.
        do
        {
            randNum = Math.abs(rand.nextInt()%MAX_ROW);
            if(!(house.hasMember(randNum))) // Check if that position already has a value. If not, add value to it.
            {
                tileID = house.getMember(randNum).getId();
                boxes[tileID].setValue(solution[tileID]);
                success = true;
            }
        } while (!success);
    }
        
    /*
     * This function will remove a value from a house.
     * This function will only be called to make sure the number of starting givens doesn't change (bound).
     */
    private void removeValue(House house, int bound)
    {
        Tile tile;
        for (int i = 0; i < MAX_ROW; i++) // Cycle through members of house until one can be removed.
        {
            tile = house.getMember(i);
            if(tile.hasValue())
            {
                if(house.getType() == 'r' && cols[tile.getCol()].length() > bound)
                {
                    // Only get here if the house is a row and the tile is in a large enough column.
                    tile.removeValue(tile.getValue());
                    break;
                }
                else if(house.getType() == 'c' && cols[tile.getRow()].length() > bound)
                {
                    // Only get here if the house is a column and the tile is in a large enough row.
                    tile.removeValue(tile.getValue());
                    break;
                }
            }
        }
    }
		
	private void setOrig(boolean origValue)
	{
		for(int i = 0; i < BOARD_SIZE; i++)
		{
			if(boxes[i].hasValue())
				boxes[i].setOrig(origValue);
		}
	}
}
package me.valesken.jeff.sudoku_structure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 7/12/2015
 */
public class Board {

    private int boardSize;
    private int difficulty;
    private Tile [] tiles;
    private House[] rows, columns, zones;
    private int[] solution;

    /**
     * Constructor for Board. Initializes all objects to be non-null.
     * @param _boardSize length of one row for this game
     */
    public Board(int _boardSize)
    {
        boardSize = _boardSize;
        solution = new int[boardSize*boardSize];

        /* Initialize houses */
        rows = new House[boardSize];
        columns = new House[boardSize];
        zones = new House[boardSize];
        for(int i = 0; i < boardSize; ++i) {
            rows[i] = new House(boardSize);
            columns[i] = new House(boardSize);
            zones[i] = new House(boardSize);
        }

        /* Initialize tiles and link them up to the houses */
        tiles = new Tile[boardSize*boardSize];
        for(int i = 0; i < (boardSize*boardSize); ++i) {
            tiles[i] = new Tile(boardSize, i);
            rows[tiles[i].getRowNumber()].addMember(tiles[i]);
            columns[tiles[i].getColumnNumber()].addMember(tiles[i]);
            zones[tiles[i].getZoneNumber()].addMember(tiles[i]);
            tiles[i].setHouses(rows[tiles[i].getRowNumber()], columns[tiles[i].getColumnNumber()], zones[tiles[i].getZoneNumber()]);
        }
    }

    /**
     * Getter for whether or not a Tile is in note-mode.
     * @param position
     * @return True: Tile is in note mode. False: Tile is in value mode.
     */
    public boolean tileIsNoteMode(int position) { return tiles[position].isNoteMode(); }

    /**
     * Getter for whether or not a given Tile is an 'original' Tile.
     * @param position
     * @return whether or not a given Tile is an 'original' Tile.
     */
    public boolean isOrig(int position) { return tiles[position].isOrig(); }

    /**
     * Getter for the current board state. Tile value/notes returned as a list of Integers.
     * @return list of value/notes for each Tile in the board
     */
    public LinkedList<Integer>[] getBoard()
    {
        LinkedList<Integer>[] list = new LinkedList[tiles.length];

        for(int i = 0; i < list.length; ++i)
            list[i] = getTile(i);

        return list;
    }

    /**
     * This updates a specific Tile.
     * @param position index of the Tile to update
     * @param value new value/note for Tile
     */
    public void updateTile(int position, int value)
    {
        tiles[position].update(value);
    }

    public void clearTile(int position) {
        tiles[position].clear();
    }

    public void toggleMode(int position)
    {
        tiles[position].toggleMode();
    }

    public LinkedList<Integer> getTile(int position)
    {
        return tiles[position].getNotesOrValue();
    }

    public int getHint()
    {
        LinkedList<Tile> openTiles = new LinkedList<Tile>();
        for(int i = 0; i < tiles.length; ++i)
            if (tiles[i].getValue() == 0 || tiles[i].getValue() != solution[i])
                openTiles.add(tiles[i]);
        if(openTiles.size() > 0) {
            int index = randGen.nextInt(openTiles.size());
            Tile tile = openTiles.get(index);
            if (tile.isNoteMode())
                tile.toggleMode();
            String s = "set ".concat(Integer.toString(tile.getValue())).concat(" to ");
            tile.update(solution[tile.getIndex()]);
            s = s.concat(Integer.toString(tile.getValue()));
            if(tile.getValue() == 0)
                System.out.println(s);
            tile.setOrig(true);
            return tile.getIndex();
        }
        return -1;
    }

    public boolean isGameOver()
    {
        for(int i = 0; i < tiles.length; ++i)
            if(tiles[i].getValue() != solution[i])
                return false;
        return true;
    }

    public void solve()
    {
        for(int i = 0; i < tiles.length; ++i) {
            if(tiles[i].isNoteMode())
                tiles[i].toggleMode();
            if(tiles[i].getValue() != solution[i])
                tiles[i].update(solution[i]);
        }
    }

    // Serializes current time, difficulty, solution board, current Tile states as JSONObject
    public JSONObject save(String currentTime)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time", currentTime);
            jsonObject.put("difficulty", difficulty);

            JSONArray solutionArray = new JSONArray();
            for (int i = 0; i < solution.length; ++i)
                solutionArray.put(solution[i]);
            jsonObject.put("solution", solutionArray);

            JSONArray tilesArray = new JSONArray();
            for (int i = 0; i < tiles.length; ++i)
                tilesArray.put(tiles[i].getJSON());
            jsonObject.put("tiles", tilesArray);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**********************************
     * INITIALIZATION SECTION - START *
     **********************************/

    //region Initialization

    private Random randGen;
    private String timeElapsed;

    /**
     * Returns the actual difficulty level (useful if 'Random'). Entry point to generate a new game.
     * @param _difficulty difficulty level for the game
     * @return difficulty level for the game
     */
    public int NewGame(int _difficulty)
    {
        difficulty = _difficulty;
        randGen = new Random();
        timeElapsed = "00:00";

        /* if 'Random', select between easy, medium, and hard */
        if(difficulty == 0 || difficulty == 4)
            difficulty = randGen.nextInt(3);

        initialize();
        digHoles(difficulty);
        checkBounds(difficulty);
        markOriginals();

        return difficulty;
    }

    public int LoadGame(JSONObject jsonObject)
    {
        int difficulty = 0;
        randGen = new Random();
        try {
            difficulty = jsonObject.getInt("difficulty");
            timeElapsed = jsonObject.getString("time");
            JSONArray jsonArray = jsonObject.getJSONArray("solution");
            for(int i = 0; i < jsonArray.length(); ++i)
                solution[i] = jsonArray.getInt(i);
            jsonArray = jsonObject.getJSONArray("tiles");
            for (int i = 0; i < jsonArray.length(); ++i) {
                tiles[i].loadTileState(jsonArray.getJSONObject(i));
                tiles[i].setHouses(rows[tiles[i].getRowNumber()], columns[tiles[i].getColumnNumber()], zones[tiles[i].getZoneNumber()]);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return difficulty;
    }

    private void initialize()
    {
        Stack<Tile> tileStack = new Stack<Tile>();
        Tile tempTile;
        int count; // to make sure initialization doesn't take TOO long

        do {
            count = 0;

            /* Seed first 9 Tiles */
            for(int i = 0; i < boardSize; ++i) {
                tempTile = columns[i].getMember(randGen.nextInt(9));
                tempTile.tryInitValue(i + 1);
                tileStack.add(tempTile);
            }

            /*
             * Go through remaining tiles
             * Set to next available value if not contradiction
             * If all values contradict, undo previous in stack and try next value
             */
            int startingIndex = tileStack.peek().getIndex();
            int currentIndex = (startingIndex == (tiles.length-1)) ? 0 : (startingIndex+1);
            while(tileStack.size() < tiles.length) {
                if(tileStack.size() == 0) {
                    count = 50000;
                    break;
                }

                for(int j = 1; j < (boardSize+1); ++j) {
                    ++count;
                    if(count > 50000)
                        break;

                    /* Check if all values have already been tried */
                    if(tiles[currentIndex].allInitValuesTried()) {
                        tiles[currentIndex].resetInitializationState();
                        currentIndex = tileStack.pop().getIndex(); /* Pop stack to get previous state */
                        tiles[currentIndex].unVisit(); /* unVisit so we can try to visit again */
                        break;
                    }

                    /*
                     * Get here if not the case that all values have already been tried
                     * Check if tile already visited
                     */
                    while(tiles[currentIndex].hasBeenVisited()) {
                        currentIndex = (currentIndex < (tiles.length-1)) ? currentIndex+1 : 0;
                        break;
                    }

                    /*
                     * Get here if tile not already visited and not the case that all values have already been tried
                     * Try to add next possible value
                     */
                    if(tiles[currentIndex].tryInitValue(j)) {
                        tileStack.push(tiles[currentIndex]); /* Push current tile onto stack (preserve state) */
                        currentIndex = (currentIndex < (tiles.length-1)) ? currentIndex+1 : 0;
                        break;
                    }
                }

                if(count > 50000)
                    break;
            }

            /* Control for boards that take too long to generate */
            if(count > 50000) {
                for(Tile t : tiles)
                    t.resetInitializationState();
                tileStack.clear();
            }

        }while(count > 50000);

        /* Save the current board state as the solution */
        for(int i = 0; i < tiles.length; ++i)
            solution[i] = tiles[i].getValue();
    }

    /* Pick Holes in Board
     * Case 1: Create a random EASY game. 40 - 49 givens, lower bound of 4 per row/col
     * Case 2: Create a random MEDIUM game. 32 - 39 givens, lower bound of 3 per row/col
     * Case 3: Create a random HARD game. 27 - 31 givens, lower bound of 2 per row/col
     * Default: Randomly generate a new game */
    private void digHoles(int difficulty)
    {
        /* calculate number of givens to start game with */
        int numGivens;
        switch(difficulty)
        {
            case 1: /* easy */
                numGivens = Math.abs(randGen.nextInt(10)) + 40;
                break;
            case 2: /* medium */
                numGivens = Math.abs(randGen.nextInt(8)) + 32;
                break;
            default:
                numGivens = Math.abs(randGen.nextInt(5)) + 27;
                break;
        }

        /* randomly select tiles to clear - "dig holes" */
        int indexToDig;
        for(int i = numGivens; i < tiles.length; ++i)
        {
            indexToDig = Math.abs(randGen.nextInt(tiles.length));
            if(tiles[indexToDig].getValue() > 0)
                tiles[indexToDig].clear();
            else
                --i;
        }
    }

    private void checkBounds(int difficulty)
    {
        int bound = (difficulty > 2) ? 2 : ((difficulty == 2) ? 3 : 4);

        Stack<House> highHouses = new Stack<House>();
        Stack<House> lowHouses = new Stack<House>();
        Tile tile;
        House lowHouse;
        House highHouse;
        LinkedList<Tile> highHouseValues;

        /* Check rows */
        for(House row : rows)
        {
            if(row.getValueCount() > bound)
                highHouses.push(row);
            else if(row.getValueCount() < bound)
                lowHouses.push(row);
        }
        highHouse = highHouses.pop();
        highHouseValues = highHouse.getValueTiles();
        while(lowHouses.size() > 0) {
            lowHouse = lowHouses.pop();
            while(lowHouse.getValueCount() < bound) {
                for (Tile t : highHouseValues) {
                    tile = lowHouse.getMember(t.getColumnNumber());
                    if(tile.getValue() == 0) {
                        tile.update(solution[tile.getIndex()]);
                        t.clear();
                        break;
                    }
                }
                if(highHouse.getValueCount() == bound) {
                    highHouse = highHouses.pop();
                    highHouseValues = highHouse.getValueTiles();
                }
            }
        }

        /* Check columns */
        highHouses.clear();
        lowHouses.clear();
        for(House column : columns)
        {
            if(column.getValueCount() > bound)
                highHouses.push(column);
            else if(column.getValueCount() < bound)
                lowHouses.push(column);
        }
        highHouse = highHouses.pop();
        highHouseValues = highHouse.getValueTiles();
        while(lowHouses.size() > 0) {
            lowHouse = lowHouses.pop();
            while(lowHouse.getValueCount() < bound) {
                for(Tile t : highHouseValues) {
                    tile = lowHouse.getMember(t.getRowNumber());
                    if(tile.getValue() == 0) {
                        tile.update(solution[tile.getIndex()]);
                        t.clear();
                        break;
                    }
                }
                if(highHouse.getValueCount() == bound) {
                    highHouse = highHouses.pop();
                    highHouseValues = highHouse.getValueTiles();
                }
            }
        }
    }

    private void markOriginals()
    {
        for(Tile t : tiles)
            if(t.getValue() > 0)
                t.setOrig(true);
    }

    public String getTime()
    {
        return timeElapsed;
    }
    //endregion

}

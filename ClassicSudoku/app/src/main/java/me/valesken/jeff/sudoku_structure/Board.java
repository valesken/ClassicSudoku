package me.valesken.jeff.sudoku_structure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 1/15/2016
 */
public class Board {
    static public String jsonTimeId = "time";
    static public String jsonDifficultyId = "difficulty";
    static public String jsonSolutionId = "solution";
    static public String jsonTilesId = "tiles";

    private int boardSize;
    private int difficulty;
    private Tile [] tiles;
    private House[] rows, columns, zones;
    private int[] solution;

    /**
     * Constructor for Board. Initializes all objects to be non-null.
     *
     * @param _boardSize length of one row for this game
     */
    public Board(int _boardSize) {
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
     *
     * @param position The index (0 - 80) of the Tile to check
     * @return True: Tile is in note mode. False: Tile is in value mode.
     */
    public boolean tileIsNoteMode(int position) {
        return tiles[position].isNoteMode();
    }

    /**
     * Getter for whether or not a given Tile is an 'original' Tile.
     *
     * @param position The index (0 - 80) of the Tile to check
     * @return whether or not a given Tile is an 'original' Tile.
     */
    public boolean isOrig(int position) {
        return tiles[position].isOrig();
    }

    /**
     * Getter for the current board state. Tile value/notes returned as a list of Integers.
     *
     * @return list of value/notes for each Tile in the board
     */
    public LinkedList[] getBoard() {
        LinkedList[] list = new LinkedList[tiles.length];
        for(int i = 0; i < list.length; ++i)
            list[i] = getTile(i);
        return list;
    }

    /**
     * This updates a specific Tile with the desired value/note (depending on its mode).
     *
     * @param position The index (0 - 80) of the Tile to update
     * @param value The new value/note for the Tile
     */
    public void updateTile(int position, int value) {
        tiles[position].update(value);
    }

    /**
     * This removes all values and hints from the selected Tile.
     *
     * @param position The index (0 - 80) of the Tile to clear
     */
    public void clearTile(int position) {
        tiles[position].clear();
    }

    /**
     * This toggles the mode of the selected Tile between Note Mode and Value Mode. If it is one,
     * it will switch to the other.
     *
     * @param position The index (0 - 80) of the Tile to toggle the mode of.
     */
    public void toggleMode(int position) {
        tiles[position].toggleMode();
    }

    /**
     * @param position The index (0 - 80) of the Tile to get.
     * @return A LinkedList of the current hints on the Tile if the Tile is in Note Mode, or a
     * Linked List with only 1 value (which is the Tile's current value) if it is in Value Mode.
     */
    public LinkedList<Integer> getTile(int position) {
        return tiles[position].getNotesOrValue();
    }

    /**
     * @return An Array of all the tiles in the board.
     */
    public Tile[] getTiles() {
        return tiles;
    }

    /**
     * This will randomly select one Tile which does not currently have the correct value and it
     * will assign it the value it should have according to the solution array. It will make that
     * Tile an "original" Tile so that it cannot be changed later.
     *
     * @return The index (0 - 80) of the Tile that was set. -1 if no Tile was set.
     */
    public int getHint() {
        LinkedList<Tile> openTiles = new LinkedList<>();
        for(int i = 0; i < tiles.length; ++i)
            if (tiles[i].getValue() == 0 || tiles[i].getValue() != solution[i])
                openTiles.add(tiles[i]);
        if(openTiles.size() > 0) {
            int index = randGen.nextInt(openTiles.size());
            Tile tile = openTiles.get(index);
            if (tile.isNoteMode())
                tile.toggleMode();
            tile.update(solution[tile.getIndex()]);
            tile.setOrig(true);
            return tile.getIndex();
        }
        return -1;
    }

    /**
     * @return True if every Tile on the board has the correct value, otherwise False.
     */
    public boolean isGameOver() {
        for(int i = 0; i < tiles.length; ++i)
            if(tiles[i].getValue() != solution[i])
                return false;
        return true;
    }

    /**
     * This function will solve the game according to the saved solution.
     */
    public void solve() {
        for(int i = 0; i < tiles.length; ++i) {
            if(tiles[i].isNoteMode())
                tiles[i].toggleMode();
            if(tiles[i].getValue() != solution[i])
                tiles[i].update(solution[i]);
        }
    }

    /**
     * This function will serialize the current time, difficulty, solution board, and Tile states
     * as a single JSONObject.
     *
     * @param currentTime The current time spent on the game in the format of "(m)m:ss"
     * @return A JSON Object representing the current state of the game.
     */
    public JSONObject save(String currentTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(jsonTimeId, currentTime);
            jsonObject.put(jsonDifficultyId, difficulty);

            JSONArray solutionArray = new JSONArray();
            for (int solution_value : solution)
                solutionArray.put(solution_value);
            jsonObject.put(jsonSolutionId, solutionArray);

            JSONArray tilesArray = new JSONArray();
            for (Tile tile : tiles)
                tilesArray.put(tile.getJSON());
            jsonObject.put(jsonTilesId, tilesArray);
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
     * To create a new game, but not to load an old game, call this function before anything else.
     * Returns the actual difficulty level (useful if 'Random').
     *
     * @param _difficulty difficulty level for the game
     * @return difficulty level for the game
     */
    public int NewGame(int _difficulty) {
        difficulty = _difficulty;
        randGen = new Random();
        timeElapsed = "00:00";

        // if 'Random', select between easy, medium, and hard
        if(difficulty == 0 || difficulty == 4)
            difficulty = randGen.nextInt(3);

        initialize();
        digHoles(difficulty);
        checkBounds(difficulty);
        runSolver();
        markOriginals();
        runSolver();

        return difficulty;
    }

    /**
     * To load an old game, call this function before anything else.
     *
     * @param jsonObject JSON representation of the entire Board.
     * @return the difficulty level of the game as determined by the provided JSON Object.
     */
    public int LoadGame(JSONObject jsonObject) {
        int difficulty = 0;
        randGen = new Random();
        try {
            difficulty = jsonObject.getInt(jsonDifficultyId);
            timeElapsed = jsonObject.getString(jsonTimeId);
            JSONArray jsonArray = jsonObject.getJSONArray(jsonSolutionId);
            for(int i = 0; i < jsonArray.length(); ++i)
                solution[i] = jsonArray.getInt(i);
            jsonArray = jsonObject.getJSONArray(jsonTilesId);
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

    /**
     * This function will generate a new, complete, valid board using a DFS algorithm to backtrack
     * when an invalid path is encountered. When finished, it will save the resultant values in the
     * solution array.
     */
    private void initialize() {
        Stack<Tile> tileStack = new Stack<>();
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
                    if(tiles[currentIndex].hasBeenVisited()) {
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

        } while(count > 50000);

        /* Save the current board state as the solution */
        for(int i = 0; i < tiles.length; ++i)
            solution[i] = tiles[i].getValue();
    }

    /**
     * Pick Holes in Board by randomly selecting a set of Tiles as the starting tiles, where the
     * number of Tiles in the set is determined by the difficulty level.
     *
     * DIFFICULTY SELECTION:
     * Case 1: Create a random EASY game. 40 - 49 givens, lower bound of 4 per row/col
     * Case 2: Create a random MEDIUM game. 32 - 39 givens, lower bound of 3 per row/col
     * Case 3: Create a random HARD game. 27 - 31 givens, lower bound of 2 per row/col
     *
     * @param difficulty 1 = Easy, 2 = Medium, otherwise Hard.
     */
    private void digHoles(int difficulty) {
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

    /**
     * This function makes sure that no Row or Column contains less than the boundary for the
     * corresponding difficulty level. E.g. no Row or Column in an Easy game should contain less
     * than 4 original tiles.
     *
     * @param difficulty 3 = Hard, 2 = Medium, otherwise Easy
     */
    private void checkBounds(int difficulty) {
        int bound = (difficulty == 3) ? 2 : ((difficulty == 2) ? 3 : 4);

        Stack<House> highHouses = new Stack<>();
        Stack<House> lowHouses = new Stack<>();
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

    /**
     * For all tiles with a value, mark them as originals. Use only in initialization process and
     * only after boundaries have been checked, but before attempting to solve with the Solver.
     */
    private void markOriginals() {
        for(Tile t : tiles)
            if(t.getValue() > 0)
                t.setOrig(true);
    }

    /**
     * Run Solver and clean up the board when finished. This step makes sure the board is solvable
     * at the desired difficulty level.
     */
    private void runSolver() {
        (new Solver(this)).solve(this.difficulty);
        clearBoard();
    }

    /**
     * Clear every tile that is not an original tile. This essentially resets the board.
     */
    public void clearBoard() {
        for(Tile t : tiles)
            t.clear();
    }

    /**
     * @return The time spent on this game so far. String format will be "(m)m:ss".
     */
    public String getTime() {
        return timeElapsed;
    }

    /**
     * @return An array of all the rows in the board.
     */
    public House[] getRows() {
        return rows;
    }

    /**
     * @return An array of all the columns in the board.
     */
    public House[] getColumns() {
        return columns;
    }

    /**
     * @return An array of all the zones in the board.
     */
    public House[] getZones() {
        return zones;
    }
    //endregion

}

package me.valesken.jeff.sudoku_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import me.valesken.jeff.util.Logger;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 4/14/2016
 */
class Board {
    static final protected String JSON_TIME_ID = "time";
    static final protected String JSON_DIFFICULTY_ID = "difficulty";
    static final protected String JSON_SOLUTION_ID = "solution";
    static final protected String JSON_TILES_ID = "tiles";

    protected Logger logger;
    protected int houseSize;
    protected int boardSize;
    protected int difficulty;
    protected Tile[] tiles;
    protected House[] rows, columns, zones;
    protected Set<Tile> solvedTiles;
    protected int[] solution;
    protected Random randGen;
    protected String timeElapsed;

    //region Construction Methods

    /**
     * Constructor for Board. Initializes all objects to be non-null.
     *
     * @param houseSize Number of Tiles one House can contain in this game.
     */
    protected Board(int houseSize) {
        this.houseSize = houseSize;
        boardSize = houseSize * houseSize;
        solution = new int[boardSize];
        rows = new House[houseSize];
        columns = new House[houseSize];
        zones = new House[houseSize];
        solvedTiles = new HashSet<>();
        tiles = new Tile[boardSize];
        logger = new Logger();
        randGen = new Random();
        timeElapsed = "";
    }

    /**
     * This will populate the House arrays with actual Houses. For use in initialization of the Board.
     * TODO: Make self-healing if error occurs during initialization
     */
    protected void initializeHouses() {
        for (int i = 0; i < houseSize; ++i) {
            rows[i] = buildHouse(i);
            columns[i] = buildHouse(i);
            zones[i] = buildHouse(i);
        }
    }

    /**
     * This will populate the Tile array with the actual Tiles. It will also put the Tile in the appropriate Houses.
     * //TODO: Make self-healing if error occurs during initialization
     */
    protected void initializeTiles() {
        for (int i = 0; i < tiles.length; ++i) {
            Tile tile = buildTile(i);
            tiles[i] = tile;
            addTileToHouses(tile, tile.getRowNumber(), tile.getColumnNumber(), tile.getZoneNumber());
        }
    }

    /**
     * This function puts a Tile into the specified Houses.
     * //TODO: Make self-healing if error occurs during initialization
     *
     * @param tile         The Tile to insert.
     * @param rowNumber    The index of the row to insert the Tile into.
     * @param columnNumber The index of the column to insert the Tile into.
     * @param zoneNumber   The index of the zone to insert the Tile into.
     */
    protected void addTileToHouses(Tile tile, int rowNumber, int columnNumber, int zoneNumber) {
        House row = (rowNumber > -1 && rowNumber < houseSize) ? getRow(rowNumber) : null;
        House column = (columnNumber > -1 && columnNumber < houseSize) ? getColumn(columnNumber) : null;
        House zone = (zoneNumber > -1 && zoneNumber < houseSize) ? getZone(zoneNumber) : null;
        if (row != null && column != null && zone != null) {
            row.addMember(tile);
            column.addMember(tile);
            zone.addMember(tile);
            tile.setHouses(row, column, zone);
        }
    }

    /**
     * This will create a House with the specified index.
     * TODO: Make self-healing if error occurs during initialization
     *
     * @param index The index for the House. E.g. Row 5 has index 5.
     * @return The resultant House.
     */
    protected House buildHouse(int index) {
        return (index > -1 && index < houseSize) ? new House(houseSize, index) : null;
    }

    /**
     * This will create a Tile with the specified index.
     * TODO: Make self-healing if error occurs during initialization
     *
     * @param index The index for the Tile. E.g. Tile 63 has index 63.
     * @return The resultant Tile.
     */
    protected Tile buildTile(int index) {
        if (index > -1 && index < boardSize) {
            return new Tile(houseSize, index);
        }
        return null;
    }

    //endregion

    //region Getters

    /**
     * @param index The 0-8 index of the row you want.
     * @return The desired row (House). Null if index is out of bounds.
     */
    protected House getRow(int index) {
        return (index > -1 && index < houseSize) ? rows[index] : null;
    }

    /**
     * @param index The 0-8 index of the column you want.
     * @return The desired column (House). Null if index is out of bounds.
     */
    protected House getColumn(int index) {
        return (index > -1 && index < houseSize) ? columns[index] : null;
    }

    /**
     * @param index The 0-8 index of the zone you want.
     * @return The desired zone (House). Null if index is out of bounds.
     */
    protected House getZone(int index) {
        return (index > -1 && index < houseSize) ? zones[index] : null;
    }

    /**
     * @param index The 0-80 index of the tile you want.
     * @return The desired Tile. Null if index is out of bounds.
     */
    protected Tile getTile(int index) {
        return (index > -1 && index < boardSize) ? tiles[index] : null;
    }

    /**
     * @param index The 0-80 index of the solution tile you want.
     * @return The desired solution value. -1 if index is out of bounds.
     */
    protected int getSolutionForTile(int index) {
        return (index > -1 && index < boardSize) ? solution[index] : -1;
    }

    /**
     * @param position The index (0 - 80) of the Tile to get.
     * @return A LinkedList of the current hints on the Tile if the Tile is in Note Mode, or a Linked List with only
     * 1 value (which is the Tile's current value) if it is in Value Mode. Return null if the index is invalid.
     */
    protected LinkedList<Integer> getTileNotesOrValue(int position) {
        if (position > -1 && position < boardSize) {
            return getTile(position).getNotesOrValue();
        }
        return null;
    }

    /**
     * Getter for whether or not a Tile is in note-mode.
     *
     * @param index The index (0 - 80) of the Tile to check
     * @return True: Tile is in note mode. False: Tile is in value mode or does not exist.
     */
    protected boolean tileIsNoteMode(int index) {
        Tile tile = getTile(index);
        return tile != null && tile.isNoteMode();
    }

    /**
     * Getter for whether or not a given Tile is an 'original' Tile.
     *
     * @param index The index (0 - 80) of the Tile to check
     * @return True: Tile is an 'original' Tile. False: Tile is open to be changed or does not exist.
     */
    protected boolean tileIsOrig(int index) {
        Tile tile = getTile(index);
        return tile != null && tile.isOrig();
    }

    /**
     * @return An Array of all the tiles in the board.
     */
    protected Tile[] getTiles() {
        return tiles;
    }

    /**
     * @return An array of all the rows in the board.
     */
    protected House[] getRows() {
        return rows;
    }

    /**
     * @return An array of all the columns in the board.
     */
    protected House[] getColumns() {
        return columns;
    }

    /**
     * @return An array of all the zones in the board.
     */
    protected House[] getZones() {
        return zones;
    }

    /**
     * Getter for the current board state. Tile value/notes returned as a list of Integers.
     *
     * @return list of value/notes for each Tile in the board
     */
    protected LinkedList[] getBoard() {
        LinkedList[] list = new LinkedList[tiles.length];
        for (int i = 0; i < list.length; ++i) {
            list[i] = getTileNotesOrValue(i);
        }
        return list;
    }

    /**
     * @return A list of all Tiles that currently are assigned the wrong value in the board.
     */
    protected LinkedList<Tile> getWrongTiles() {
        LinkedList<Tile> wrongTiles = new LinkedList<>();
        for (int i = 0; i < tiles.length; ++i) {
            Tile tile = getTile(i);
            int solvedValue = getSolutionForTile(i);
            if (tile.getValue() == 0 || tile.getValue() != solvedValue) {
                wrongTiles.add(tile);
            }
        }
        return wrongTiles;
    }

    /**
     * @return True if every Tile on the board has the correct value, otherwise False.
     */
    protected boolean isGameOver() {
        return solvedTiles.size() == boardSize;
    }

    /**
     * @return The time spent on this game so far. String format will be "(m)m:ss".
     */
    protected String getTime() {
        return timeElapsed;
    }
    //endregion

    //region Setters

    /**
     * This updates a specific Tile with the desired value/note (depending on its mode).
     *
     * @param position The index (0 - 80) of the Tile to update
     * @param value    The new value/note for the Tile
     * @return The new values of the Tile, or null if the index is < 0 or > 80
     */
    protected LinkedList<Integer> updateTile(int position, int value) {
        if (position > -1 && position < boardSize) {
            Tile tile = getTile(position);
            tile.update(value);
            if (tile.getValue() == getSolutionForTile(position)) {
                solvedTiles.add(tile);
            } else {
                solvedTiles.remove(tile);
            }
            return tile.getNotesOrValue();
        }
        return null;
    }

    /**
     * This removes all values and hints from the selected Tile.
     *
     * @param position The index (0 - 80) of the Tile to clear
     * @return The new values of the Tile, or null if the index is < 0 or > 80
     */
    protected LinkedList<Integer> clearTile(int position) {
        if (position > -1 && position < boardSize) {
            Tile tile = getTile(position);
            getTile(position).clear();
            return tile.getNotesOrValue();
        }
        return null;
    }

    /**
     * This toggles the mode of the selected Tile between Note Mode and Value Mode. If it is one, it will switch to
     * the other.
     *
     * @param position The index (0 - 80) of the Tile to toggle the mode of.
     * @return True if Tile exists (index is valid) and note mode toggled, false otherwise
     */
    protected boolean toggleNoteMode(int position) {
        if (position > -1 && position < boardSize) {
            getTile(position).toggleMode();
            return true;
        }
        return false;
    }

    /**
     * This will randomly select one Tile which does not currently have the correct value and it will assign it the
     * value it should have according to the solution array. It will make that Tile an "original" Tile so that it
     * cannot be changed later.
     *
     * @return The index (0 - 80) of the Tile that was set. -1 if no Tile was set.
     */
    protected int useHint() {
        LinkedList<Tile> wrongTiles = getWrongTiles();
        if (wrongTiles.size() > 0) {
            Tile tile = wrongTiles.get(randGen.nextInt(wrongTiles.size()));
            int index = tile.getIndex();
            if (tile.isNoteMode()) {
                tile.toggleMode();
            }
            tile.update(getSolutionForTile(index));
            tile.setOrig(true);
            return index;
        }
        return -1;
    }

    /**
     * This function will solve the game according to the saved solution.
     *
     * @return True if the game was not already solved and now has been solve, False otherwise.
     */
    protected boolean solve() {
        boolean nowSolved = false;
        for (int i = 0; i < tiles.length; ++i) {
            Tile tile = getTile(i);
            if (tile.isNoteMode()) {
                tile.toggleMode();
            }
            if (tile.getValue() != getSolutionForTile(i)) {
                tile.update(getSolutionForTile(i));
                nowSolved = true;
            }
        }
        return nowSolved;
    }
    //endregion

    //region Save Game Methods

    /**
     * This function will serialize the current time, difficulty, solution board, and Tile states
     * as a single JSONObject.
     *
     * @param currentTime The current time spent on the game in the format of "(m)m:ss"
     * @return A JSON Object representing the current state of the game.
     */
    protected JSONObject save(String currentTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_TIME_ID, currentTime);
            jsonObject.put(JSON_DIFFICULTY_ID, difficulty);

            JSONArray solutionArray = new JSONArray();
            for (int solution_value : solution) {
                solutionArray.put(solution_value);
            }
            jsonObject.put(JSON_SOLUTION_ID, solutionArray);

            JSONArray tilesArray = new JSONArray();
            for (Tile tile : tiles) {
                tilesArray.put(tile.getJSON());
            }
            jsonObject.put(JSON_TILES_ID, tilesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //endregion

    //region Load Game Methods

    /**
     * To load an old game, call this function after initializing the Board.
     *
     * @param jsonObject JSON representation of the entire Board.
     * @return the difficulty level of the game as determined by the provided JSON Object.
     */
    protected int loadGame(JSONObject jsonObject) {
        try {
            // Try to load values from JSON
            int tempDifficulty = jsonObject.getInt(JSON_DIFFICULTY_ID);
            String tempTimeElapsed = jsonObject.getString(JSON_TIME_ID);
            int[] tempSolution = new int[boardSize];
            Tile[] tempTiles = new Tile[boardSize];
            JSONArray jsonSolutionArray = jsonObject.getJSONArray(JSON_SOLUTION_ID);
            JSONArray jsonTileArray = jsonObject.getJSONArray(JSON_TILES_ID);
            for (int i = 0; i < boardSize; ++i) {
                tempSolution[i] = jsonSolutionArray.getInt(i);
                tempTiles[i] = loadTile(jsonTileArray.getJSONObject(i));
            }

            // JSON loading succeeded, so now load values
            difficulty = tempDifficulty;
            timeElapsed = tempTimeElapsed;
            System.arraycopy(tempSolution, 0, solution, 0, boardSize);
            System.arraycopy(tempTiles, 0, tiles, 0, boardSize);
            for (Tile t : tiles) {
                t.setHouses(getRow(t.getRowNumber()), getColumn(t.getColumnNumber()), getZone(t.getZoneNumber()));
            }
            return difficulty;
        } catch (JSONException ignored) {
            return -1;
        }
    }

    /**
     * This function will load a single Tile from a saved JSON state.
     *
     * @param tileState The saved state in JSON format.
     * @return The loaded Tile.
     * @throws JSONException if JSON format is incorrect.
     */
    protected Tile loadTile(JSONObject tileState) throws JSONException {
        return new Tile(houseSize, tileState);
    }

    //endregion

    //region Game Start Methods

    /**
     * To create a new game, but not to load an old game, call this function before anything else.
     * Returns the actual difficulty level (useful if 'Random'). 1 = Easy, 2 = Medium, 3 = Hard.
     *
     * @param _difficulty difficulty level for the game
     * @return difficulty level for the game
     */
    protected int newGame(int _difficulty) {
        difficulty = _difficulty;
        timeElapsed = "00:00";

        // if 'Random', select between easy, medium, and hard
        if (difficulty < 1 || difficulty > 3) {
            difficulty = randGen.nextInt(3) + 1;
        }

        buildCompleteBoard();
        digHoles(getNumberOfGivens(difficulty));
        checkBounds(getBound(difficulty));
        markOriginals();
        //runSolver();

        return difficulty;
    }

    /**
     * This function will generate a new, complete, valid board using a DFS algorithm to backtrack
     * when an invalid path is encountered. When finished, it will save the resultant values in the
     * solution array.
     *
     * @return True if initialization succeeds, false otherwise (i.e. stack becomes empty)
     */
    protected boolean buildCompleteBoard() {
        try {
            Stack<Tile> tileStack = new Stack<>();
            seedFirstTiles(tileStack);
            fillBoard_DFS(tileStack);
            saveBoardToSolution();
            return true;
        } catch (EmptyStackException e) {
            return false;
        }
    }

    /**
     * This function will pick a random Tile from each column and seed it with an initial value. This function should
     * only be used during setup on an empty Board.
     * // TODO: Does not currently check if Board is truly empty.
     *
     * @param tileStack The Stack of Tiles to be used during the DFS portion of initialization. Each Tile seeded in
     *                  this function will be added to the provided Stack.
     */
    protected void seedFirstTiles(Stack<Tile> tileStack) {
        Tile tempTile;
        for (int i = 0; i < houseSize; ++i) {
            tempTile = getColumn(i).getMember(randGen.nextInt(houseSize));
            tempTile.seedInitialValue(i + 1);
            tileStack.add(tempTile);
        }
    }

    /**
     * This function will actually execute the DFS. It finds the starting point (based on the last seeded Tile), then
     * try to initialize the next Tile based on Board index. If the next Tile has already been visited, then it will be
     * skipped over and the search will continue with the next Tile based on Board index again. If the Tile cannot be
     * initialized, then the function will try a different value for the most recently visited Tile before this
     * (uninitializable) one. If there are no more Tiles to visit (i.e. all Tiles have been visited already) then the
     * DFS is finished.
     *
     * @param tileStack The Stack used to keep track of how many Tile have been visited and which was the most
     *                  recently visited Tile.
     */
    protected void fillBoard_DFS(Stack<Tile> tileStack) {
        int currentIndex = incrementIndex_DFS(tileStack.peek().getIndex());
        while (keepSearching_DFS(tileStack)) {
            currentIndex = executeOneStep_DFS(currentIndex, tileStack);
        }
    }

    /**
     * This function examines the tileStack to determine whether or not the DFS should keep searching for Tiles to
     * initialize.
     *
     * @param tileStack The Stack of Tiles to examine.
     * @return True if there are no more Tiles to initialize, False otherwise.
     */
    protected boolean keepSearching_DFS(Stack<Tile> tileStack) {
        return tileStack.size() < getTiles().length;
    }

    /**
     * This function attempts to execute a single step in the DFS that builds the Board. It first retrieves the next
     * Tile to initialize, then tries to initialize it. If the initialization succeeds, then the newly initialized
     * Tile will be added to the Tile Stack. If the initialization fails, then that means the Board is in an invalid
     * state and must try to re-initialize the most recently visited Tile with a new value. Right now, however, in
     * the case of failure, the most recently visited Tile will simply be prepared to be re-initialized.
     *
     * @param index     The index of the most recently visited Tile, which may be either the top of the Stack or, if
     *                  a Tile has been prepped for re-initialization, a Tile recently popped off of the stack.
     * @param tileStack The Tile Stack used throughout the DFS.
     * @return The index of the most recently visited Tile.
     */
    protected int executeOneStep_DFS(int index, Stack<Tile> tileStack) {
        Tile currentTile = getNextTile_DFS(index);
        if (currentTile.tryInitialize()) {
            tileStack.add(currentTile);
            return currentTile.getIndex();
        } else {
            currentTile.resetInitializationState(); // Reset state of Tile that failed to initialize
            Tile previousTile = tileStack.pop(); // Get last visited Tile and prep it to try next value
            previousTile.unVisit();
            return previousTile.getIndex();
        }
    }

    /**
     * This function returns the "next" index in the Board. If the current index corresponds to the last tile in the
     * Board, then the next index will be 0.
     *
     * @param index The current index to find the next from.
     * @return The next index in the Board.
     */
    protected int incrementIndex_DFS(int index) {
        return (index == (tiles.length - 1)) ? 0 : index + 1;
    }

    /**
     * This function will return the next uninitialized Tile in the Board.
     *
     * @param index The index of the Tile to begin looking from (includes the Tile belonging to this index).
     * @return The next uninitialized Tile in the Board.
     */
    protected Tile getNextTile_DFS(int index) {
        while (getTile(index).hasBeenVisited()) {
            index = incrementIndex_DFS(index);
        }
        return getTile(index);
    }

    /**
     * This function will save the current values of all Tiles as the correct solution for the Board. However, if any
     * Tiles are null or their values are empty, then this function will abort and no values will be saved.
     */
    protected void saveBoardToSolution() {
        // Make sure board is complete before saving it to solution
        boolean boardComplete = true;
        for (int i = 0; i < boardSize; ++i) {
            Tile tile = getTile(i);
            if (tile == null || tile.getValue() == 0) {
                boardComplete = false;
                break;
            }
        }
        // If board IS complete, then go ahead and save it
        if (boardComplete) {
            for (int i = 0; i < boardSize; ++i) {
                solution[i] = getTile(i).getValue();
            }
        }
    }

    /**
     * Get the number of givens that the game should start with. (A "given" is an unchangeable Tile whose solution is
     * visible from the beginning of the game.)
     *
     * @param difficulty 1 = Easy, 2 = Medium, otherwise Hard.
     * @return 40 - 49 for Easy, 32 - 39 for Medium, 27 - 31 for Hard
     */
    protected int getNumberOfGivens(int difficulty) {
        switch (difficulty) {
            case 1: // easy
                return Math.abs(randGen.nextInt(10)) + 40;
            case 2: // medium
                return Math.abs(randGen.nextInt(8)) + 32;
            default:
                return Math.abs(randGen.nextInt(5)) + 27;
        }
    }

    /**
     * Pick Holes in Board by randomly selecting a set of Tiles as the starting tiles, where the number of Tiles in
     * the set is determined by the difficulty level.
     *
     * @param numGivens The number of givens to begin the game with.
     */
    protected void digHoles(int numGivens) {
        for (int i = numGivens; i < boardSize; ++i) {
            int indexToDig = Math.abs(randGen.nextInt(boardSize));
            if (getTile(indexToDig).getValue() > 0) {
                getTile(indexToDig).clear();
            } else {
                --i;
            }
        }
    }

    /**
     * This function returns the lower bound on the number of Tiles allowed in a row or column.
     *
     * @param difficulty 3 = Hard, 2 = Medium, otherwise Easy
     * @return 2 for Hard, 3 for Medium, 4 for Easy
     */
    protected int getBound(int difficulty) {
        return (difficulty == 3) ? 2 : ((difficulty == 2) ? 3 : 4);
    }

    /**
     * This function makes sure that no Row or Column contains fewer Tiles than the boundary for the corresponding
     * difficulty level. Easy: boundary = 4. Medium: boundary = 3. Hard: boundary = 2.
     *
     * @param bound 3 = Hard, 2 = Medium, otherwise Easy
     */
    protected void checkBounds(int bound) {
        Stack<House> highHouses = new Stack<>();
        Stack<House> lowHouses = new Stack<>();
        // Check Rows
        fillHighAndLowHouses(rows, highHouses, lowHouses, bound);
        adjustHousesToBounds(highHouses, lowHouses, bound, true);
        // Check Columns
        fillHighAndLowHouses(columns, highHouses, lowHouses, bound);
        adjustHousesToBounds(highHouses, lowHouses, bound, false);
    }

    /**
     * This function will fill the provided "highHouse" Stack with houses (from the provided array) that contain more
     * initialized Tiles than the provided minimum bound, and the "lowHouse" Stack with houses that contain fewer
     * initialized Tiles than the provided minimum bound.
     *
     * @param houses     The houses to divvy up into highHouse and lowHouse Stacks.
     * @param highHouses The Stack to contain Houses with more Tiles than the minimum bound.
     * @param lowHouses  The Stack to contain Houses with fewer Tiles than the minimum bound.
     * @param bound      The minimum bound.
     */
    protected void fillHighAndLowHouses(House[] houses, Stack<House> highHouses, Stack<House> lowHouses, int bound) {
        if (highHouses != null && lowHouses != null) {
            highHouses.clear();
            lowHouses.clear();
            for (House house : houses) {
                if (house.getValueCount() > bound) {
                    highHouses.push(house);
                } else if (house.getValueCount() < bound) {
                    lowHouses.push(house);
                }
            }
        }
    }

    /**
     * This function will raise the number of Tiles in all the Houses in the lowHouses Stack to the minimum bound. It
     * will do this by removing a Tile in one of the Houses in the highHouse Stack and adding a Tile to a House in
     * the lowHouse Stack along the complementary House (e.g. Row 1 is a high House and Row 2 is a low House. A Tile
     * in Column 3 of Row 1 is removed, and a Tile in Column 3 of Row 2 is added.)
     *
     * @param highHouses The Stack that contains Houses with more Tiles than the minimum bound.
     * @param lowHouses  The Stack that contains Houses with fewer Tiles than the minimum bound.
     * @param bound      The minimum bound.
     * @param isRow      Whether the Houses being adjusted are Rows or not.
     */
    protected void adjustHousesToBounds(Stack<House> highHouses, Stack<House> lowHouses, int bound, boolean isRow) {
        House highHouse = highHouses.pop();
        LinkedList<Tile> highHouseTiles = highHouse.getValueTiles();
        // Adjust houses as long as there are any houses with too few starting Tiles
        while (lowHouses.size() > 0) {
            House lowHouse = lowHouses.pop();
            // Add however many starting Tiles the house needs to reach the bound
            while (lowHouse.getValueCount() < bound) {
                // If the House we're taking Tiles from no longer has excess Tiles, use the next House with excess Tiles
                if (highHouse.getValueCount() == bound) {
                    highHouse = highHouses.pop();
                    highHouseTiles = highHouse.getValueTiles();
                }
                swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
            }
        }
    }

    /**
     * Search for an empty Tile in the lowHouse that corresponds to a filled Tile in the highHouse.
     *
     * @param highHouseTiles The Tiles to select a swap from
     * @param lowHouse       The House that is lacking Tiles
     * @param isRow          Whether or not the Houses we're looking at are rows
     */
    protected void swapHighHouseToLowHouseTile(List<Tile> highHouseTiles, House lowHouse, boolean isRow) {
        if(highHouseTiles != null && lowHouse != null) {
            for (Tile t : highHouseTiles) {
                Tile tile = lowHouse.getMember((isRow) ? t.getColumnNumber() : t.getRowNumber());
                if (tile.getValue() == 0) {
                    int tileSolution = getSolutionForTile(tile.getIndex());
                    tile.update(tileSolution);
                    t.clear();
                    break;
                }
            }
        }
    }

    /**
     * For all tiles with a value, mark them as originals. Use only in initialization process and only after
     * boundaries have been checked, but before attempting to solve with the Solver.
     */
    protected void markOriginals() {
        for (Tile t : getTiles()) {
            if (t.getValue() > 0) {
                t.setOrig(true);
            }
        }
    }

    /**
     * Run Solver and clean up the board when finished. This step makes sure the board is solvable at the desired
     * difficulty level.
     */
    protected void runSolver(Solver solver) {
        if(solver != null) {
            solver.solve(difficulty);
            clearBoard();
        }
    }

    /**
     * Clear every tile that is not an original tile. This essentially resets the board.
     */
    protected void clearBoard() {
        for (Tile t : getTiles()) {
            t.clear();
        }
    }

    //endregion

}

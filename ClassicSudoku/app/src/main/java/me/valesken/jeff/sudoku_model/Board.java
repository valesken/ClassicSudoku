package me.valesken.jeff.sudoku_model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Jeff on 2/28/2015.
 * Last updated on 2/10/2016
 */
public class Board {
    static public String jsonTimeId = "time";
    static public String jsonDifficultyId = "difficulty";
    static public String jsonSolutionId = "solution";
    static public String jsonTilesId = "tiles";

    public int houseSize;
    public int boardSize;
    public int difficulty;
    public Tile[] tiles;
    public House[] rows, columns, zones;
    public int[] solution;
    public Random randGen;
    public String timeElapsed;

    //region Construction Methods

    /**
     * Constructor for Board. Initializes all objects to be non-null.
     *
     * @param houseSize Number of Tiles one House can contain in this game.
     */
    public Board(int houseSize) {
        this.houseSize = houseSize;
        boardSize = houseSize * houseSize;
        solution = new int[boardSize];
        rows = new House[houseSize];
        columns = new House[houseSize];
        zones = new House[houseSize];
        tiles = new Tile[boardSize];
    }

    /**
     * This will populate the House arrays with actual Houses. For use in initialization of the Board.
     * TODO: Make self-healing if error occurs during initialization
     */
    public void initializeHouses() {
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
    public void initializeTiles() {
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
    public void addTileToHouses(Tile tile, int rowNumber, int columnNumber, int zoneNumber) {
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
    public House buildHouse(int index) {
        return (index > -1 && index < houseSize) ? new House(houseSize, index) : null;
    }

    /**
     * This will create a Tile with the specified index.
     * TODO: Make self-healing if error occurs during initialization
     *
     * @param index The index for the Tile. E.g. Tile 63 has index 63.
     * @return The resultant Tile.
     */
    public Tile buildTile(int index) {
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
    public House getRow(int index) {
        return (index > -1 && index < houseSize) ? rows[index] : null;
    }

    /**
     * @param index The 0-8 index of the column you want.
     * @return The desired column (House). Null if index is out of bounds.
     */
    public House getColumn(int index) {
        return (index > -1 && index < houseSize) ? columns[index] : null;
    }

    /**
     * @param index The 0-8 index of the zone you want.
     * @return The desired zone (House). Null if index is out of bounds.
     */
    public House getZone(int index) {
        return (index > -1 && index < houseSize) ? zones[index] : null;
    }

    /**
     * @param index The 0-80 index of the tile you want.
     * @return The desired Tile. Null if index is out of bounds.
     */
    public Tile getTile(int index) {
        return (index > -1 && index < boardSize) ? tiles[index] : null;
    }

    /**
     * @param index The 0-80 index of the solution tile you want.
     * @return The desired solution value. -1 if index is out of bounds.
     */
    public int getSolutionTile(int index) {
        return (index > -1 && index < boardSize) ? solution[index] : -1;
    }

    /**
     * @param position The index (0 - 80) of the Tile to get.
     * @return A LinkedList of the current hints on the Tile if the Tile is in Note Mode, or a Linked List with only
     * 1 value (which is the Tile's current value) if it is in Value Mode. Return null if the index is invalid.
     */
    public LinkedList<Integer> getTileNotesOrValue(int position) {
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
    public boolean tileIsNoteMode(int index) {
        Tile tile = getTile(index);
        return tile != null && tile.isNoteMode();
    }

    /**
     * Getter for whether or not a given Tile is an 'original' Tile.
     *
     * @param index The index (0 - 80) of the Tile to check
     * @return True: Tile is an 'original' Tile. False: Tile is open to be changed or does not exist.
     */
    public boolean tileIsOrig(int index) {
        Tile tile = getTile(index);
        return tile != null && tile.isOrig();
    }

    /**
     * @return An Array of all the tiles in the board.
     */
    public Tile[] getTiles() {
        return tiles;
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

    /**
     * Getter for the current board state. Tile value/notes returned as a list of Integers.
     *
     * @return list of value/notes for each Tile in the board
     */
    public LinkedList[] getBoard() {
        LinkedList[] list = new LinkedList[tiles.length];
        for (int i = 0; i < list.length; ++i) {
            list[i] = getTileNotesOrValue(i);
        }
        return list;
    }

    /**
     * @return A list of all Tiles that currently are assigned the wrong value in the board.
     */
    public LinkedList<Tile> getWrongTiles() {
        LinkedList<Tile> wrongTiles = new LinkedList<>();
        for (int i = 0; i < tiles.length; ++i) {
            Tile tile = getTile(i);
            int solvedValue = getSolutionTile(i);
            if (tile.getValue() == 0 || tile.getValue() != solvedValue) {
                wrongTiles.add(tile);
            }
        }
        return wrongTiles;
    }

    /**
     * @return True if every Tile on the board has the correct value, otherwise False.
     */
    public boolean isGameOver() {
        for (int i = 0; i < tiles.length; ++i) {
            if (getTile(i).getValue() != getSolutionTile(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The time spent on this game so far. String format will be "(m)m:ss".
     */
    public String getTime() {
        return timeElapsed;
    }
    //endregion

    //region Setters

    /**
     * This updates a specific Tile with the desired value/note (depending on its mode).
     *
     * @param position The index (0 - 80) of the Tile to update
     * @param value    The new value/note for the Tile
     */
    public void updateTile(int position, int value) {
        if (position > -1 && position < boardSize) {
            getTile(position).update(value);
        }
    }

    /**
     * This removes all values and hints from the selected Tile.
     *
     * @param position The index (0 - 80) of the Tile to clear
     */
    public void clearTile(int position) {
        if (position > -1 && position < boardSize) {
            getTile(position).clear();
        }
    }

    /**
     * This toggles the mode of the selected Tile between Note Mode and Value Mode. If it is one, it will switch to
     * the other.
     *
     * @param position The index (0 - 80) of the Tile to toggle the mode of.
     */
    public void toggleMode(int position) {
        if (position > -1 && position < boardSize) {
            getTile(position).toggleMode();
        }
    }

    /**
     * This will randomly select one Tile which does not currently have the correct value and it will assign it the
     * value it should have according to the solution array. It will make that Tile an "original" Tile so that it
     * cannot be changed later.
     *
     * @return The index (0 - 80) of the Tile that was set. -1 if no Tile was set.
     */
    public int useHint() {
        LinkedList<Tile> wrongTiles = getWrongTiles();
        if (wrongTiles.size() > 0) {
            Tile tile = wrongTiles.get(randGen.nextInt(wrongTiles.size()));
            int index = tile.getIndex();
            if (tile.isNoteMode()) {
                tile.toggleMode();
            }
            tile.update(getSolutionTile(index));
            tile.setOrig(true);
            return index;
        }
        return -1;
    }

    /**
     * This function will solve the game according to the saved solution.
     */
    public void solve() {
        for (int i = 0; i < tiles.length; ++i) {
            Tile tile = getTile(i);
            if (tile.isNoteMode()) {
                tile.toggleMode();
            }
            if (tile.getValue() != getSolutionTile(i)) {
                tile.update(getSolutionTile(i));
            }
        }
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
    public JSONObject save(String currentTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(jsonTimeId, currentTime);
            jsonObject.put(jsonDifficultyId, difficulty);

            JSONArray solutionArray = new JSONArray();
            for (int solution_value : solution) {
                solutionArray.put(solution_value);
            }
            jsonObject.put(jsonSolutionId, solutionArray);

            JSONArray tilesArray = new JSONArray();
            for (Tile tile : tiles) {
                tilesArray.put(tile.getJSON());
            }
            jsonObject.put(jsonTilesId, tilesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    //endregion

    //region Game Start/Load Methods

    /**
     * To create a new game, but not to load an old game, call this function before anything else.
     * Returns the actual difficulty level (useful if 'Random').
     *
     * @param _difficulty difficulty level for the game
     * @return difficulty level for the game
     */
    public int newGame(int _difficulty) {
        Log.d("Debug Info", "Inside newGame().");
        difficulty = _difficulty;
        randGen = new Random();
        timeElapsed = "00:00";

        // if 'Random', select between easy, medium, and hard
        if (difficulty == 0 || difficulty == 4) {
            difficulty = randGen.nextInt(3) + 1;
        }

        initialize();
        digHoles(difficulty);
        checkBounds(difficulty);
        markOriginals();
        //runSolver();

        return difficulty;
    }

    /**
     * To load an old game, call this function before anything else.
     *
     * @param jsonObject JSON representation of the entire Board.
     * @return the difficulty level of the game as determined by the provided JSON Object.
     */
    public int loadGame(JSONObject jsonObject) {
        int difficulty = 0;
        randGen = new Random();
        try {
            difficulty = jsonObject.getInt(jsonDifficultyId);
            timeElapsed = jsonObject.getString(jsonTimeId);
            JSONArray jsonArray = jsonObject.getJSONArray(jsonSolutionId);
            for (int i = 0; i < jsonArray.length(); ++i) {
                solution[i] = jsonArray.getInt(i);
            }
            jsonArray = jsonObject.getJSONArray(jsonTilesId);
            for (int i = 0; i < jsonArray.length(); ++i) {
                tiles[i].loadTileState(jsonArray.getJSONObject(i));
                tiles[i].setHouses(rows[tiles[i].getRowNumber()], columns[tiles[i].getColumnNumber()], zones[tiles[i]
                        .getZoneNumber()]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return difficulty;
    }

    /**
     * This function will generate a new, complete, valid board using a DFS algorithm to backtrack
     * when an invalid path is encountered. When finished, it will save the resultant values in the
     * solution array.
     *
     * @return True if initialization succeeds, false otherwise (i.e. stack becomes empty)
     */
    public boolean initialize() {
        try {
            Log.d("Debug Info", "Inside initialize().");
            Stack<Tile> tileStack = new Stack<>();
            Tile tempTile;

            // Seed first 9 Tiles
            for (int i = 0; i < houseSize; ++i) {
                tempTile = columns[i].getMember(randGen.nextInt(9));
                tempTile.tryInitValue(i + 1);
                tileStack.add(tempTile);
            }

            // DFS to fill up the rest of the board
            int startingIndex = tileStack.peek().getIndex();
            int currentIndex = (startingIndex == (tiles.length - 1)) ? 0 : (startingIndex + 1);
            while (tileStack.size() <= tiles.length) {
                while (tiles[currentIndex].hasBeenVisited()) {
                    currentIndex = (currentIndex == (tiles.length - 1)) ? 0 : (currentIndex + 1);
                }
                if (tiles[currentIndex].tryInitialize()) {
                    tileStack.add(tiles[currentIndex]);
                    currentIndex = (currentIndex == (tiles.length - 1)) ? 0 : (currentIndex + 1);
                } else {
                    tiles[currentIndex].resetInitializationState();
                    currentIndex = tileStack.pop().getIndex();
                    tiles[currentIndex].clear();
                    tiles[currentIndex].unVisit();
                }
            }

            //Save the current board state as the solution
            for (int i = 0; i < tiles.length; ++i) {
                solution[i] = tiles[i].getValue();
            }

            // Success
            return true;
        } catch (EmptyStackException e) {
            return false;
        }
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
    public void digHoles(int difficulty) {
        Log.d("Debug Info", "Inside digHoles().");
        /* calculate number of givens to start game with */
        int numGivens;
        switch (difficulty) {
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
        Log.d("Debug Info", String.format("Number of givens to start with is %d", numGivens));

        /* randomly select tiles to clear - "dig holes" */
        int indexToDig;
        for (int i = numGivens; i < tiles.length; ++i) {
            indexToDig = Math.abs(randGen.nextInt(tiles.length));
            if (tiles[indexToDig].getValue() > 0) {
                tiles[indexToDig].clear();
            } else {
                --i;
            }
        }
        Log.d("Debug Info", "Finished in digHoles()");
    }

    /**
     * This function makes sure that no Row or Column contains less than the boundary for the
     * corresponding difficulty level. E.g. no Row or Column in an Easy game should contain less
     * than 4 original tiles.
     *
     * @param difficulty 3 = Hard, 2 = Medium, otherwise Easy
     */
    public void checkBounds(int difficulty) {
        Log.d("Debug Info", "Inside checkBounds().");
        int bound = (difficulty == 3) ? 2 : ((difficulty == 2) ? 3 : 4);

        Stack<House> highHouses = new Stack<>();
        Stack<House> lowHouses = new Stack<>();
        Tile tile;
        House lowHouse;
        House highHouse;
        LinkedList<Tile> highHouseValues;

        /* Check rows */
        for (House row : rows) {
            if (row.getValueCount() > bound) {
                highHouses.push(row);
            } else if (row.getValueCount() < bound) {
                lowHouses.push(row);
            }
        }
        highHouse = highHouses.pop();
        highHouseValues = highHouse.getValueTiles();
        while (lowHouses.size() > 0) {
            lowHouse = lowHouses.pop();
            while (lowHouse.getValueCount() < bound) {
                for (Tile t : highHouseValues) {
                    tile = lowHouse.getMember(t.getColumnNumber());
                    if (tile.getValue() == 0) {
                        tile.update(solution[tile.getIndex()]);
                        t.clear();
                        break;
                    }
                }
                if (highHouse.getValueCount() == bound) {
                    highHouse = highHouses.pop();
                    highHouseValues = highHouse.getValueTiles();
                }
            }
        }

        /* Check columns */
        highHouses.clear();
        lowHouses.clear();
        for (House column : columns) {
            if (column.getValueCount() > bound) {
                highHouses.push(column);
            } else if (column.getValueCount() < bound) {
                lowHouses.push(column);
            }
        }
        highHouse = highHouses.pop();
        highHouseValues = highHouse.getValueTiles();
        while (lowHouses.size() > 0) {
            lowHouse = lowHouses.pop();
            while (lowHouse.getValueCount() < bound) {
                for (Tile t : highHouseValues) {
                    tile = lowHouse.getMember(t.getRowNumber());
                    if (tile.getValue() == 0) {
                        tile.update(solution[tile.getIndex()]);
                        t.clear();
                        break;
                    }
                }
                if (highHouse.getValueCount() == bound) {
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
    public void markOriginals() {
        Log.d("Debug Info", "Inside markOriginals().");
        for (Tile t : tiles) {
            if (t.getValue() > 0) {
                t.setOrig(true);
            }
        }
    }

    /**
     * Run Solver and clean up the board when finished. This step makes sure the board is solvable
     * at the desired difficulty level.
     */
    public void runSolver() {
        (new Solver(this)).solve(this.difficulty);
        clearBoard();
    }

    /**
     * Clear every tile that is not an original tile. This essentially resets the board.
     */
    public void clearBoard() {
        for (Tile t : tiles) {
            t.clear();
        }
    }
    //endregion

}

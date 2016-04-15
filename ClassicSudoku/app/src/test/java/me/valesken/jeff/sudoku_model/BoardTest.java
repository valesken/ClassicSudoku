package me.valesken.jeff.sudoku_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import me.valesken.jeff.util.Logger;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by jeff on 2/8/2016.
 * Last Updated on 4/14/2016.
 */
public class BoardTest {

    private int houseSize = 9;
    private int boardSize = 81;
    private Board board;
    private House mockedHouse;
    private Tile mockedTile;

    //region rules
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    //endregion

    //region setup
    @Before
    public void setUp() {
        board = new Board(houseSize);
        mockedHouse = mock(House.class);
        mockedTile = mock(Tile.class);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        assertEquals(houseSize, board.houseSize);
        assertEquals(boardSize, board.boardSize);
        assertNotNull(board.solution);
        assertEquals(boardSize, board.solution.length);
        assertNotNull(board.rows);
        assertEquals(houseSize, board.rows.length);
        assertNotNull(board.columns);
        assertEquals(houseSize, board.columns.length);
        assertNotNull(board.zones);
        assertEquals(houseSize, board.zones.length);
        assertNotNull(board.logger);
        assertNotNull(board.randGen);
        assertEquals("", board.timeElapsed);
    }
    //endregion

    //region initializeHouses() tests
    @Test
    public void testInitializeHousesPass() {
        // Pre-Check
        for (int i = 0; i < houseSize; ++i) {
            assertNull(board.rows[i]);
            assertNull(board.columns[i]);
            assertNull(board.zones[i]);
        }
        // Execute
        board.initializeHouses();
        // Post-Check
        for (int i = 0; i < houseSize; ++i) {
            assertNotNull(board.rows[i]);
            assertNotNull(board.columns[i]);
            assertNotNull(board.zones[i]);
        }
    }
    //endregion

    //region initializeTiles() tests
    @Test
    public void testInitializeTilesPass() {
        // Set Up
        Tile[] mockTiles = new Tile[houseSize];
        Board spy = spy(board);
        for (int i = 0; i < houseSize; ++i) {
            spy.rows[i] = mock(House.class);
            spy.columns[i] = mock(House.class);
            spy.zones[i] = mock(House.class);
            mockTiles[i] = mock(Tile.class);
            when(mockTiles[i].getRowNumber()).thenReturn(i);
            when(mockTiles[i].getColumnNumber()).thenReturn(i);
            when(mockTiles[i].getZoneNumber()).thenReturn(i);
        }
        for (int i = 0; i < boardSize; ++i) {
            doReturn(mockTiles[i % houseSize]).when(spy).buildTile(i);
        }
        // Pre-Check
        for (int i = 0; i < boardSize; ++i) {
            assertNull(spy.tiles[i]);
        }
        // Run
        spy.initializeTiles();
        // Post-Check
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(spy.tiles[i]);
        }
    }
    //endregion

    //region addTileToHouses() tests
    @Test
    public void testAddTileToHousesLowIndexPass() {
        int index = 0;
        Board spy = spy(board);
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        spy.addTileToHouses(mockedTile, index, index, index);
        verify(mockedHouse, times(3)).addMember(mockedTile);
        verify(mockedTile).setHouses(mockedHouse, mockedHouse, mockedHouse);
    }

    @Test
    public void testAddTileToHousesHighIndexPass() {
        int index = houseSize - 1;
        Board spy = spy(board);
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        spy.addTileToHouses(mockedTile, index, index, index);
        verify(mockedHouse, times(3)).addMember(mockedTile);
        verify(mockedTile).setHouses(mockedHouse, mockedHouse, mockedHouse);
    }

    @Test
    public void testAddTileToHousesNegativeIndexFail() {
        int index = -1;
        Board spy = spy(board);
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        spy.addTileToHouses(mockedTile, index, index, index);
        verify(mockedHouse, never()).addMember(argThat(is(any(Tile.class))));
        verify(mockedTile, never()).setHouses(argThat(is(any(House.class))), argThat(is(any(House.class))), argThat
                (is(any(House.class))));
    }

    @Test
    public void testAddTileToHousesHugeIndexFail() {
        int index = houseSize;
        Board spy = spy(board);
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        spy.addTileToHouses(mockedTile, index, index, index);
        verify(mockedHouse, never()).addMember(argThat(is(any(Tile.class))));
        verify(mockedTile, never()).setHouses(argThat(is(any(House.class))), argThat(is(any(House.class))), argThat
                (is(any(House.class))));
    }
    //endregion

    //region buildHouse() tests
    @Test
    public void testBuildHousePass() {
        for (int i = 0; i < houseSize; ++i) {
            assertNotNull(board.buildHouse(i));
        }
    }

    @Test
    public void testBuildHouseNegativeIndexFail() {
        assertNull(board.buildHouse(-1));
    }

    @Test
    public void testBuildHouseLargeIndexFail() {
        assertNull(board.buildHouse(houseSize));
    }
    //endregion

    //region buildTile() tests
    @Test
    public void testBuildTilePass() {
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(board.buildTile(i));
        }
    }

    @Test
    public void testBuildTileNegativeIndexFail() {
        assertNull(board.buildTile(-1));
    }

    @Test
    public void testBuildTileLargeIndexFail() {
        assertNull(board.buildTile(boardSize));
    }
    //endregion

    //region getRow() tests
    @Test
    public void testGetRowPass() {
        board.rows[0] = mockedHouse;
        board.rows[houseSize - 1] = mockedHouse;
        assertEquals(mockedHouse, board.getRow(0));
        assertEquals(mockedHouse, board.getRow(houseSize - 1));
    }

    @Test
    public void testGetRowNegativeIndexFail() {
        assertNull(board.getRow(-1));
    }

    @Test
    public void testGetRowLargeIndexFail() {
        assertNull(board.getRow(houseSize));
    }
    //endregion

    //region getColumn() tests
    @Test
    public void testGetColumnPass() {
        board.columns[0] = mockedHouse;
        board.columns[houseSize - 1] = mockedHouse;
        assertEquals(mockedHouse, board.getColumn(0));
        assertEquals(mockedHouse, board.getColumn(houseSize - 1));
    }

    @Test
    public void testGetColumnNegativeIndexFail() {
        assertNull(board.getColumn(-1));
    }

    @Test
    public void testGetColumnLargeIndexFail() {
        assertNull(board.getColumn(houseSize));
    }
    //endregion

    //region getZone() tests
    @Test
    public void testGetZonePass() {
        board.zones[0] = mockedHouse;
        board.zones[houseSize - 1] = mockedHouse;
        assertEquals(mockedHouse, board.getZone(0));
        assertEquals(mockedHouse, board.getZone(houseSize - 1));
    }

    @Test
    public void testGetZoneNegativeIndexFail() {
        assertNull(board.getZone(-1));
    }

    @Test
    public void testGetZoneLargeIndexFail() {
        assertNull(board.getZone(houseSize));
    }
    //endregion

    //region getTile() tests
    @Test
    public void testGetTilePass() {
        board.tiles[0] = mockedTile;
        board.tiles[boardSize - 1] = mockedTile;
        assertEquals(mockedTile, board.getTile(0));
        assertEquals(mockedTile, board.getTile(boardSize - 1));
    }

    @Test
    public void testGetTileNegativeIndexFail() {
        assertNull(board.getTile(-1));
    }

    @Test
    public void testGetTileLargeIndexFail() {
        assertNull(board.getTile(boardSize));
    }
    //endregion

    //region getSolutionTile() tests
    @Test
    public void testGetSolutionTileMinIndexPass() {
        int index = 0;
        board.solution[index] = 5;
        assertEquals(5, board.getSolutionTile(index));
    }

    @Test
    public void testGetSolutionTileMaxIndexPass() {
        int index = boardSize - 1;
        board.solution[index] = 5;
        assertEquals(5, board.getSolutionTile(index));
    }

    @Test
    public void testGetSolutionTileNegativeIndexFail() {
        assertEquals(-1, board.getSolutionTile(-1));
    }

    @Test
    public void testGetSolutionTileLargeIndexFail() {
        assertEquals(-1, board.getSolutionTile(boardSize));
    }
    //endregion

    //region getTileNotesOrValue() tests
    @Test
    public void testGetTileNotesOrValueMinIndexPass() {
        int index = 0;
        LinkedList<Integer> list = new LinkedList<>();
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        when(mockedTile.getNotesOrValue()).thenReturn(list);
        assertEquals(list, spy.getTileNotesOrValue(index));
    }

    @Test
    public void testGetTileNotesOrValueMaxIndexPass() {
        int index = boardSize - 1;
        LinkedList<Integer> list = new LinkedList<>();
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        when(mockedTile.getNotesOrValue()).thenReturn(list);
        assertEquals(list, spy.getTileNotesOrValue(index));
    }

    @Test
    public void testGetTileNotesOrValueNegativeIndexFail() {
        assertNull(board.getTileNotesOrValue(-1));
    }

    @Test
    public void testGetTileNotesOrValueLargeIndexFail() {
        assertNull(board.getTileNotesOrValue(boardSize));
    }
    //endregion

    //region tileIsNoteMode() tests
    @Test
    public void testTileIsNoteModeTruePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsNoteMode(index));
    }

    @Test
    public void testTileIsNoteModeFalsePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(false);
        doReturn(mockedTile).when(spy).getTile(index);
        assertFalse(spy.tileIsNoteMode(index));
    }

    @Test
    public void testTileIsNoteModeMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsNoteMode(index));
    }

    @Test
    public void testTileIsNoteModeMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsNoteMode(index));
    }

    @Test
    public void testTileIsNoteModeNegativeIndexFail() {
        assertFalse(board.tileIsNoteMode(-1));
    }

    @Test
    public void testTileIsNoteModeLargeIndexFail() {
        assertFalse(board.tileIsNoteMode(boardSize));
    }
    //endregion

    //region tileIsOrig() tests
    @Test
    public void testTileIsOrigTruePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsOrig(index));
    }

    @Test
    public void testTileIsOrigFalsePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(false);
        doReturn(mockedTile).when(spy).getTile(index);
        assertFalse(spy.tileIsOrig(index));
    }

    @Test
    public void testTileIsOrigMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsOrig(index));
    }

    @Test
    public void testTileIsOrigMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsOrig(index));
    }

    @Test
    public void testTileIsOrigNegativeIndexFail() {
        assertFalse(board.tileIsOrig(-1));
    }

    @Test
    public void testTileIsOrigLargeIndexFail() {
        assertFalse(board.tileIsOrig(boardSize));
    }
    //endregion

    //region getTiles() tests
    @Test
    public void testGetTilesDefaultPass() {
        assertArrayEquals(board.tiles, board.getTiles());
    }

    @Test
    public void testGetTilesInitializedPass() {
        board.initializeHouses();
        board.initializeTiles();
        assertArrayEquals(board.tiles, board.getTiles());
    }
    //endregion

    //region getRows() tests
    @Test
    public void testGetRowsDefaultPass() {
        assertArrayEquals(board.rows, board.getRows());
    }

    @Test
    public void testGetRowsInitializedPass() {
        board.initializeHouses();
        board.initializeTiles();
        assertArrayEquals(board.rows, board.getRows());
    }
    //endregion

    //region getColumns() tests
    @Test
    public void testGetColumnsDefaultPass() {
        assertArrayEquals(board.columns, board.getColumns());
    }

    @Test
    public void testGetColumnsInitializedPass() {
        board.initializeHouses();
        board.initializeTiles();
        assertArrayEquals(board.columns, board.getColumns());
    }
    //endregion

    //region getZones() tests
    @Test
    public void testGetZonesDefaultPass() {
        assertArrayEquals(board.zones, board.getZones());
    }

    @Test
    public void testGetZonesInitializedPass() {
        board.initializeHouses();
        board.initializeTiles();
        assertArrayEquals(board.zones, board.getZones());
    }
    //endregion

    //region getBoard() tests
    @Test
    public void testGetBoardAllValuesPass() {
        // Setup
        Board spy = spy(board);
        for (int i = 0; i < boardSize; ++i) {
            LinkedList<Integer> list = new LinkedList<>();
            list.add(i % 9);
            doReturn(list).when(spy).getTileNotesOrValue(i);
        }
        // Execute
        LinkedList[] gameState = spy.getBoard();
        // Verify
        assertEquals(boardSize, gameState.length);
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(gameState[i]);
            assertEquals(1, gameState[i].size());
            assertEquals(i % 9, gameState[i].get(0));
        }
    }

    @Test
    public void testGetBoardAllHintsPass() {
        // Setup
        Board spy = spy(board);
        LinkedList<Integer> list = new LinkedList<>();
        list.add(4);
        list.add(7);
        doReturn(list).when(spy).getTileNotesOrValue(anyInt());
        // Execute
        LinkedList[] gameState = spy.getBoard();
        // Verify
        assertEquals(boardSize, gameState.length);
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(gameState[i]);
            assertEquals(2, gameState[i].size());
            assertEquals(4, gameState[i].get(0));
            assertEquals(7, gameState[i].get(1));
        }
    }

    @Test
    public void testGetBoardMixedHintsValuesPass() {
        // Setup
        Board spy = spy(board);
        for (int i = 0; i < boardSize; ++i) {
            LinkedList<Integer> list = new LinkedList<>();
            list.add(i % 9);
            if (i % 2 == 0) {
                list.add(i / 9);
            }
            doReturn(list).when(spy).getTileNotesOrValue(i);
        }
        // Execute
        LinkedList[] gameState = spy.getBoard();
        // Verify
        assertEquals(boardSize, gameState.length);
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(gameState[i]);
            if (i % 2 == 0) {
                assertEquals(2, gameState[i].size());
                assertEquals(i % 9, gameState[i].get(0));
                assertEquals(i / 9, gameState[i].get(1));
            } else {
                assertEquals(1, gameState[i].size());
                assertEquals(i % 9, gameState[i].get(0));
            }
        }
    }

    @Test
    public void testGetBoardWithEmptiesPass() {
        // Setup
        Board spy = spy(board);
        for (int i = 0; i < boardSize; ++i) {
            LinkedList<Integer> list = new LinkedList<>();
            if (i % 2 == 0) {
                list.add(i % 9);
            }
            doReturn(list).when(spy).getTileNotesOrValue(i);
        }
        // Execute
        LinkedList[] gameState = spy.getBoard();
        // Verify
        assertEquals(boardSize, gameState.length);
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(gameState[i]);
            if (i % 2 == 0) {
                assertEquals(1, gameState[i].size());
                assertEquals(i % 9, gameState[i].get(0));
            } else {
                assertEquals(0, gameState[i].size());
            }
        }
    }
    //endregion

    //region getWrongTiles() tests
    @Test
    public void testGetWrongTilesOneTilePass() {
        // Setup
        int index = 0;
        int tileValue = 5;
        int badSolution = 3;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(tileValue);
        doReturn(badSolution).when(spy).getSolutionTile(index);
        doReturn(tileValue).when(spy).getSolutionTile(intThat(is(not(index))));
        // Execute
        LinkedList<Tile> wrongTiles = spy.getWrongTiles();
        // Verify
        assertNotNull(wrongTiles);
        assertEquals(1, wrongTiles.size());
        assertEquals(mockedTile, wrongTiles.get(0));
    }

    @Test
    public void testGetWrongTilesMultipleTilesPass() {
        // Setup
        int index0 = 0;
        int index1 = 1;
        int tileValue = 5;
        int badSolution = 3;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(tileValue);
        doReturn(badSolution).when(spy).getSolutionTile(intThat(either(is(index0)).or(is(index1))));
        doReturn(tileValue).when(spy).getSolutionTile(intThat(is(not(either(is(index0)).or(is(index1))))));
        // Execute
        LinkedList<Tile> wrongTiles = spy.getWrongTiles();
        // Verify
        assertNotNull(wrongTiles);
        assertEquals(2, wrongTiles.size());
        assertEquals(mockedTile, wrongTiles.get(0));
        assertEquals(mockedTile, wrongTiles.get(1));
    }

    @Test
    public void testGetWrongTilesZeroTilesPass() {
        // Setup
        int tileValue = 5;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(tileValue);
        doReturn(tileValue).when(spy).getSolutionTile(anyInt());
        // Execute
        LinkedList<Tile> wrongTiles = spy.getWrongTiles();
        // Verify
        assertNotNull(wrongTiles);
        assertEquals(0, wrongTiles.size());
    }
    //endregion

    //region isGameOver() tests
    @Test
    public void testIsGameOverTruePass() {
        int value = 1;
        Board spy = spy(board);
        doReturn(value).when(spy).getSolutionTile(anyInt());
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(value);
        assertTrue(spy.isGameOver());
    }

    @Test
    public void testIsGameOverFalsePass() {
        int solutionValue = 1;
        int tileValue = 5;
        Board spy = spy(board);
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(tileValue);
        assertFalse(spy.isGameOver());
    }
    //endregion

    //region getTime() tests
    @Test
    public void testGetTimePass() {
        board.timeElapsed = "0:12";
        assertEquals("0:12", board.getTime());
        board.timeElapsed = "0:13";
        assertEquals("0:13", board.getTime());
    }
    //endregion

    //region updateTile() tests
    @Test
    public void testUpdateTileMinIndexPass() {
        // Set up
        int index = 0;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        // Execute & Verify
        assertNotNull(spy.updateTile(index, value));
        verify(mockedTile).update(value);
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testUpdateTileMaxIndexPass() {
        // Set up
        int index = boardSize - 1;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        // Execute & Verify
        assertNotNull(spy.updateTile(index, value));
        verify(mockedTile).update(value);
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testUpdateTileNegativeIndexIgnore() {
        // Set up
        int index = -1;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        // Execute & Verify
        assertNull(spy.updateTile(index, value));
        verify(mockedTile, never()).update(anyInt());
        verify(mockedTile, never()).getNotesOrValue();
    }

    @Test
    public void testUpdateTileLargeIndexIgnore() {
        // Set up
        int index = boardSize;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        // Execute & Verify
        assertNull(spy.updateTile(index, value));
        verify(mockedTile, never()).update(anyInt());
        verify(mockedTile, never()).getNotesOrValue();
    }
    //endregion

    //region clearTile() tests
    @Test
    public void testClearTileMinIndexPass() {
        // Set up
        int index = 0;
        Board spy = spy(board);
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        doReturn(mockedTile).when(spy).getTile(index);
        // Execute & Verify
        assertNotNull(spy.clearTile(index));
        verify(mockedTile).clear();
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testClearTileMaxIndexPass() {
        // Set up
        int index = boardSize - 1;
        Board spy = spy(board);
        doReturn(new LinkedList<>()).when(mockedTile).getNotesOrValue();
        doReturn(mockedTile).when(spy).getTile(index);
        // Execute & Verify
        assertNotNull(spy.clearTile(index));
        verify(mockedTile).clear();
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testClearTileNegativeIndexIgnore() {
        // Set up
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        // Execute & Verify
        assertNull(spy.clearTile(-1));
        verify(mockedTile, never()).clear();
        verify(mockedTile, never()).getNotesOrValue();
    }

    @Test
    public void testClearTileLargeIndexIgnore() {
        // Set up
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        // Execute & Verify
        assertNull(spy.clearTile(boardSize));
        verify(mockedTile, never()).clear();
        verify(mockedTile, never()).getNotesOrValue();
    }
    //endregion

    //region toggleNoteMode() tests
    @Test
    public void testToggleModeMinIndexPass() {
        // Set up
        int index = 0;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        // Execute & Verify
        assertTrue(spy.toggleNoteMode(index));
        verify(mockedTile).toggleMode();
    }

    @Test
    public void testToggleModeMaxIndexPass() {
        // Set up
        int index = boardSize - 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        // Execute & Verify
        assertTrue(spy.toggleNoteMode(index));
        verify(mockedTile).toggleMode();
    }

    @Test
    public void testToggleModeNegativeIndexIgnore() {
        // Set up
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        // Execute & Verify
        assertFalse(spy.toggleNoteMode(-1));
        verify(mockedTile, never()).toggleMode();
    }

    @Test
    public void testToggleModeLargeIndexIgnore() {
        // Set up
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        // Execute & Verify
        assertFalse(spy.toggleNoteMode(boardSize));
        verify(mockedTile, never()).toggleMode();
    }
    //endregion

    //region useHint() tests
    @Test
    public void testUseHintOneOpenTileValueModePass() {
        // Setup
        int index = 0;
        int solutionValue = 1;
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        LinkedList<Tile> list = new LinkedList<>();
        list.add(mockedTile);
        doReturn(list).when(spy).getWrongTiles();
        doReturn(solutionValue).when(spy).getSolutionTile(index);
        when(spy.randGen.nextInt(anyInt())).thenReturn(0);
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getIndex()).thenReturn(index);
        // Execute
        assertEquals(index, spy.useHint());
        // Verify
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile).update(solutionValue);
        verify(mockedTile).setOrig(true);
    }

    @Test
    public void testUseHintMultipleOpenTilesPass() {
        // Setup
        int index = 1; // index of tile that Random will "choose"
        int solutionValue = 1;
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        LinkedList<Tile> list = new LinkedList<>();
        list.add(mock(Tile.class));
        list.add(mockedTile);
        doReturn(list).when(spy).getWrongTiles();
        doReturn(solutionValue).when(spy).getSolutionTile(index);
        when(spy.randGen.nextInt(anyInt())).thenReturn(1);
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getIndex()).thenReturn(index);
        // Execute
        assertEquals(index, spy.useHint());
        // Verify
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile).update(solutionValue);
        verify(mockedTile).setOrig(true);
    }

    @Test
    public void testUseHintZeroOpenTilesFail() {
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        doReturn(new LinkedList<>()).when(spy).getWrongTiles();
        assertEquals(-1, spy.useHint());
    }
    //endregion

    //region solve() tests
    @Test
    public void testSolveEmptyBoardNoNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(0);
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(2 * boardSize)).getSolutionTile(anyInt());
        verify(mockedTile, times(boardSize)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, times(boardSize)).update(solutionValue);
    }

    @Test
    public void testSolveEmptyBoardSomeNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        Tile mockedTile2 = mock(Tile.class);
        doReturn(mockedTile).when(spy).getTile(gt(1));
        doReturn(mockedTile2).when(spy).getTile(lt(2));
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(0);
        when(mockedTile2.isNoteMode()).thenReturn(true);
        when(mockedTile2.getValue()).thenReturn(0);
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(2 * boardSize)).getSolutionTile(anyInt());
        verify(mockedTile, times(boardSize - 2)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, times(boardSize - 2)).update(solutionValue);
        verify(mockedTile2, times(2)).isNoteMode();
        verify(mockedTile2, times(2)).toggleMode();
        verify(mockedTile2, times(2)).update(solutionValue);
    }

    @Test
    public void testSolveEmptyBoardAllNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(true);
        when(mockedTile.getValue()).thenReturn(0);
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(2 * boardSize)).getSolutionTile(anyInt());
        verify(mockedTile, times(boardSize)).isNoteMode();
        verify(mockedTile, times(boardSize)).toggleMode();
        verify(mockedTile, times(boardSize)).update(solutionValue);
    }

    @Test
    public void testSolvePartialBoardNoNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        Tile mockedTile2 = mock(Tile.class);
        doReturn(mockedTile).when(spy).getTile(gt(1));
        doReturn(mockedTile2).when(spy).getTile(lt(2));
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(0); // mockedTile IS NOT solved
        when(mockedTile2.isNoteMode()).thenReturn(false);
        when(mockedTile2.getValue()).thenReturn(solutionValue); // mockedTile2 IS solved
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(2 * boardSize - 2)).getSolutionTile(anyInt()); // Check every Tile, update only mockedTile
        verify(mockedTile, times(boardSize - 2)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, times(boardSize - 2)).update(solutionValue);
        verify(mockedTile2, times(2)).isNoteMode();
        verify(mockedTile2, never()).toggleMode();
        verify(mockedTile2, never()).update(anyInt());
    }

    @Test
    public void testSolvePartialBoardSomeNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        Tile mockedTile2 = mock(Tile.class);
        doReturn(mockedTile).when(spy).getTile(gt(1));
        doReturn(mockedTile2).when(spy).getTile(lt(2));
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(0); // mockedTile has no value, is in Value Mode
        when(mockedTile2.isNoteMode()).thenReturn(true);
        when(mockedTile2.getValue()).thenReturn(solutionValue); // mockedTile2 has value, is in Note Mode
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(2 * boardSize - 2)).getSolutionTile(anyInt()); // Check every Tile, update only mockedTile
        verify(mockedTile, times(boardSize - 2)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, times(boardSize - 2)).update(solutionValue);
        verify(mockedTile2, times(2)).isNoteMode();
        verify(mockedTile2, times(2)).toggleMode();
        verify(mockedTile2, never()).update(anyInt());
    }

    @Test
    public void testSolveSolvedBoardNoNotesPass() {
        // Setup
        int solutionValue = 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(solutionValue);
        // Execute & Verify
        assertFalse(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(boardSize)).getSolutionTile(anyInt());
        verify(mockedTile, times(boardSize)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, never()).update(anyInt());
    }

    @Test
    public void testSolveFullBoardPartiallyRightNoNotesPass() {
        // Setup
        int solutionValue = 1;
        int badValue = 2;
        Board spy = spy(board);
        Tile mockedTile2 = mock(Tile.class);
        doReturn(mockedTile).when(spy).getTile(gt(1));
        doReturn(mockedTile2).when(spy).getTile(lt(2));
        doReturn(solutionValue).when(spy).getSolutionTile(anyInt());
        when(mockedTile.isNoteMode()).thenReturn(false);
        when(mockedTile.getValue()).thenReturn(solutionValue);
        when(mockedTile2.isNoteMode()).thenReturn(false);
        when(mockedTile2.getValue()).thenReturn(badValue);
        // Execute & Verify
        assertTrue(spy.solve());
        verify(spy, times(boardSize)).getTile(anyInt());
        verify(spy, times(boardSize + 2)).getSolutionTile(anyInt());
        verify(mockedTile, times(boardSize - 2)).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile, never()).update(anyInt());
        verify(mockedTile2, times(2)).isNoteMode();
        verify(mockedTile2, never()).toggleMode();
        verify(mockedTile2, times(2)).update(solutionValue);
    }
    //endregion

    //region save() tests
    @Test
    public void testSavePass() throws JSONException {
        // Setup
        String currentTime = "0:12";
        board.difficulty = 1;
        JSONObject object = new JSONObject();
        when(mockedTile.getJSON()).thenReturn(object);
        for (int i = 0; i < boardSize; ++i) {
            board.solution[i] = i % 9;
            board.tiles[i] = mockedTile;
        }
        // Execute
        JSONObject savedGame = board.save(currentTime);
        // Verify
        assertNotNull(savedGame);
        assertEquals(board.difficulty, savedGame.getInt(Board.jsonDifficultyId));
        assertEquals(currentTime, savedGame.getString(Board.jsonTimeId));
        JSONArray solution = savedGame.getJSONArray(Board.jsonSolutionId);
        assertNotNull(solution);
        assertEquals(boardSize, solution.length());
        JSONArray tiles = savedGame.getJSONArray(Board.jsonTilesId);
        assertNotNull(tiles);
        assertEquals(boardSize, tiles.length());
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(i % 9, solution.getInt(i));
            assertEquals(object, tiles.getJSONObject(i));
        }
    }
    //endregion

    //region loadGame() tests
    @Test
    public void testLoadGamePass() throws JSONException {
        // Setup
        Board spy = spy(board);
        String savedTime = "11:11";
        int difficulty = 1;
        int solutionValue = 5;
        JSONObject mockLoadGame = mock(JSONObject.class);
        JSONObject mockTileObject = mock(JSONObject.class);
        JSONArray mockSolutionArray = mock(JSONArray.class);
        JSONArray mockTileArray = mock(JSONArray.class);
        when(mockLoadGame.getInt(Board.jsonDifficultyId)).thenReturn(difficulty);
        when(mockLoadGame.getString(Board.jsonTimeId)).thenReturn(savedTime);
        when(mockLoadGame.getJSONArray(Board.jsonSolutionId)).thenReturn(mockSolutionArray);
        when(mockLoadGame.getJSONArray(Board.jsonTilesId)).thenReturn(mockTileArray);
        when(mockSolutionArray.getInt(anyInt())).thenReturn(solutionValue);
        when(mockTileArray.getJSONObject(anyInt())).thenReturn(mockTileObject);
        doReturn(mockedTile).when(spy).loadTile(argThat(is(any(JSONObject.class))));
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        when(mockedTile.getRowNumber()).thenReturn(0);
        when(mockedTile.getColumnNumber()).thenReturn(0);
        when(mockedTile.getZoneNumber()).thenReturn(0);
        // Execute
        assertEquals(difficulty, spy.loadGame(mockLoadGame));
        // Verify
        assertEquals(difficulty, spy.difficulty);
        assertEquals(savedTime, spy.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(solutionValue, spy.solution[i]);
            assertEquals(mockedTile, spy.tiles[i]);
        }
    }

    @Test
    public void testLoadGameDifficultyRaisesFlag() {
        // Setup
        JSONObject savedGame = new JSONObject();
        board.difficulty = 1;
        // Execute
        assertEquals(-1, board.loadGame(savedGame));
        // Verify
        assertEquals(1, board.difficulty);
        assertEquals("", board.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, board.solution[i]);
            assertNull(board.tiles[i]);
        }
    }

    @Test
    public void testLoadGameTimeRaisesFlag() throws JSONException {
        // Setup
        JSONObject savedGame = new JSONObject();
        savedGame.put(Board.jsonDifficultyId, 2);
        board.difficulty = 1;
        // Execute
        assertEquals(-1, board.loadGame(savedGame));
        // Verify
        assertEquals(1, board.difficulty);
        assertEquals("", board.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, board.solution[i]);
            assertNull(board.tiles[i]);
        }
    }

    @Test
    public void testLoadGameSolutionArrayRaisesFlag() throws JSONException {
        // Setup
        JSONObject savedGame = new JSONObject();
        savedGame.put(Board.jsonDifficultyId, 2);
        savedGame.put(Board.jsonTimeId, "11:11");
        board.difficulty = 1;
        // Execute
        assertEquals(-1, board.loadGame(savedGame));
        // Verify
        assertEquals(1, board.difficulty);
        assertEquals("", board.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, board.solution[i]);
            assertNull(board.tiles[i]);
        }
    }

    @Test
    public void testLoadGameSolutionValueRaisesFlag() throws JSONException {
        // Setup
        JSONObject savedGame = new JSONObject();
        savedGame.put(Board.jsonDifficultyId, 2);
        savedGame.put(Board.jsonTimeId, "11:11");
        JSONArray solutionArray = new JSONArray();
        solutionArray.put(5); // Only 1 value in array!
        savedGame.put(Board.jsonSolutionId, solutionArray);
        board.difficulty = 1;
        // Execute
        assertEquals(-1, board.loadGame(savedGame));
        // Verify
        assertEquals(1, board.difficulty);
        assertEquals("", board.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, board.solution[i]);
            assertNull(board.tiles[i]);
        }
    }

    @Test
    public void testLoadGameTileArrayRaisesFlag() throws JSONException {
        // Setup
        JSONObject savedGame = new JSONObject();
        savedGame.put(Board.jsonDifficultyId, 2);
        savedGame.put(Board.jsonTimeId, "11:11");
        JSONArray solutionArray = new JSONArray();
        for (int i = 0; i < boardSize; ++i) {
            solutionArray.put(5);
        }
        savedGame.put(Board.jsonSolutionId, solutionArray);
        board.difficulty = 1;
        // Execute
        assertEquals(-1, board.loadGame(savedGame));
        // Verify
        assertEquals(1, board.difficulty);
        assertEquals("", board.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, board.solution[i]);
            assertNull(board.tiles[i]);
        }
    }

    @Test
    public void testLoadGameTileValueRaisesFlag() throws JSONException {
        // Setup
        Board spy = spy(board);
        JSONObject savedGame = new JSONObject();
        savedGame.put(Board.jsonDifficultyId, 2);
        savedGame.put(Board.jsonTimeId, "11:11");
        JSONArray solutionArray = new JSONArray();
        JSONArray tilesArray = new JSONArray();
        for (int i = 0; i < boardSize; ++i) {
            solutionArray.put(5);
            tilesArray.put(mock(JSONObject.class));
        }
        savedGame.put(Board.jsonSolutionId, solutionArray);
        savedGame.put(Board.jsonTilesId, tilesArray);
        spy.difficulty = 1;
        doThrow(new JSONException("Test")).when(spy).loadTile(argThat(is(any(JSONObject.class))));
        // Execute
        assertEquals(-1, spy.loadGame(savedGame));
        // Verify
        assertEquals(1, spy.difficulty);
        assertEquals("", spy.timeElapsed);
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, spy.solution[i]);
            assertNull(spy.tiles[i]);
        }
    }
    //endregion

    //region newGame() tests
    @Test
    public void testNewGameEasyPass() {
        // Setup
        int difficulty = 1;
        int numGivens = 40;
        int bound = 4;
        Board spy = spy(board);
        doReturn(true).when(spy).buildCompleteBoard();
        doReturn(numGivens).when(spy).getNumberOfGivens(difficulty);
        doReturn(bound).when(spy).getBound(difficulty);
        doNothing().when(spy).digHoles(anyInt());
        doNothing().when(spy).checkBounds(anyInt());
        doNothing().when(spy).markOriginals();
        spy.randGen = mock(Random.class);
        spy.logger = mock(Logger.class);
        // Execute
        assertEquals(difficulty, spy.newGame(difficulty));
        // Verify
        assertEquals(difficulty, spy.difficulty);
        assertEquals("00:00", spy.timeElapsed);
        verify(spy.logger).logDebugMessage("Inside newGame().");
        verify(spy).buildCompleteBoard();
        verify(spy).getNumberOfGivens(difficulty);
        verify(spy).digHoles(numGivens);
        verify(spy).getBound(difficulty);
        verify(spy).checkBounds(bound);
        verify(spy).markOriginals();
    }

    @Test
    public void testNewGameMediumPass() {
        // Setup
        int difficulty = 2;
        int numGivens = 32;
        int bound = 3;
        Board spy = spy(board);
        doReturn(true).when(spy).buildCompleteBoard();
        doReturn(numGivens).when(spy).getNumberOfGivens(difficulty);
        doReturn(bound).when(spy).getBound(difficulty);
        doNothing().when(spy).digHoles(anyInt());
        doNothing().when(spy).checkBounds(anyInt());
        doNothing().when(spy).markOriginals();
        spy.randGen = mock(Random.class);
        spy.logger = mock(Logger.class);
        // Execute
        assertEquals(difficulty, spy.newGame(difficulty));
        // Verify
        assertEquals(difficulty, spy.difficulty);
        assertEquals("00:00", spy.timeElapsed);
        verify(spy.logger).logDebugMessage("Inside newGame().");
        verify(spy).buildCompleteBoard();
        verify(spy).getNumberOfGivens(difficulty);
        verify(spy).digHoles(numGivens);
        verify(spy).getBound(difficulty);
        verify(spy).checkBounds(bound);
        verify(spy).markOriginals();
    }

    @Test
    public void testNewGameHardPass() {
        // Setup
        int difficulty = 3;
        int numGivens = 27;
        int bound = 2;
        Board spy = spy(board);
        doReturn(true).when(spy).buildCompleteBoard();
        doReturn(numGivens).when(spy).getNumberOfGivens(difficulty);
        doReturn(bound).when(spy).getBound(difficulty);
        doNothing().when(spy).digHoles(anyInt());
        doNothing().when(spy).checkBounds(anyInt());
        doNothing().when(spy).markOriginals();
        spy.randGen = mock(Random.class);
        spy.logger = mock(Logger.class);
        // Execute
        assertEquals(difficulty, spy.newGame(difficulty));
        // Verify
        assertEquals(difficulty, spy.difficulty);
        assertEquals("00:00", spy.timeElapsed);
        verify(spy.logger).logDebugMessage("Inside newGame().");
        verify(spy).buildCompleteBoard();
        verify(spy).getNumberOfGivens(difficulty);
        verify(spy).digHoles(numGivens);
        verify(spy).getBound(difficulty);
        verify(spy).checkBounds(bound);
        verify(spy).markOriginals();
    }

    @Test
    public void testNewGameRandomPass() {
        // Setup
        int difficulty = 4;
        int resultantDifficulty = 2;
        int numGivens = 32;
        int bound = 3;
        Board spy = spy(board);
        doReturn(true).when(spy).buildCompleteBoard();
        doReturn(numGivens).when(spy).getNumberOfGivens(resultantDifficulty);
        doReturn(bound).when(spy).getBound(resultantDifficulty);
        doNothing().when(spy).digHoles(anyInt());
        doNothing().when(spy).checkBounds(anyInt());
        doNothing().when(spy).markOriginals();
        spy.randGen = mock(Random.class);
        when(spy.randGen.nextInt(3)).thenReturn(resultantDifficulty - 1);
        spy.logger = mock(Logger.class);
        // Execute
        assertEquals(resultantDifficulty, spy.newGame(difficulty));
        // Verify
        assertEquals(resultantDifficulty, spy.difficulty);
        assertEquals("00:00", spy.timeElapsed);
        verify(spy.logger).logDebugMessage("Inside newGame().");
        verify(spy).buildCompleteBoard();
        verify(spy).getNumberOfGivens(resultantDifficulty);
        verify(spy).digHoles(numGivens);
        verify(spy).getBound(resultantDifficulty);
        verify(spy).checkBounds(bound);
        verify(spy).markOriginals();
    }
    //endregion

    //region buildCompleteBoard() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testBuildCompleteBoardPass() {
        // Setup
        Board spy = spy(board);
        spy.logger = mock(Logger.class);
        doNothing().when(spy).seedFirstTiles(argThat(is(any(Stack.class))));
        doNothing().when(spy).fillBoard_DFS(argThat(is(any(Stack.class))));
        doNothing().when(spy).saveBoardToSolution();
        // Execute
        assertTrue(spy.buildCompleteBoard());
        // Verify
        verify(spy.logger).logDebugMessage("Inside buildCompleteBoard().");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildCompleteBoardException() {
        // Setup
        Board spy = spy(board);
        spy.logger = mock(Logger.class);
        doNothing().when(spy).seedFirstTiles(argThat(is(any(Stack.class))));
        doThrow(new EmptyStackException()).when(spy).fillBoard_DFS(argThat(is(any(Stack.class))));
        doNothing().when(spy).saveBoardToSolution();
        // Execute
        assertFalse(spy.buildCompleteBoard());
        // Verify
        verify(spy.logger).logDebugMessage("Inside buildCompleteBoard().");
    }
    //endregion

    //region seedFirstTiles() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testSeedFirstTilesPass() {
        // Setup
        Stack<Tile> mockStack = (Stack<Tile>) (mock(Stack.class));
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        when(spy.randGen.nextInt(anyInt())).thenReturn(0); // return value doesn't actually matter
        when(mockedHouse.getMember(anyInt())).thenReturn(mockedTile);
        // Execute
        spy.seedFirstTiles(mockStack);
        // Verify
        verify(mockStack, times(houseSize)).add(mockedTile);
    }
    //endregion

    //region fillBoard_DFS() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testFillBoardDFS_LoopAndExit_Pass() {
        // Setup
        int index = 8;
        Board spy = spy(board);
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.peek()).thenReturn(mockedTile);
        when(mockedTile.getIndex()).thenReturn(index);
        doReturn(mockedTile).when(spy).getTile(index);
        doReturn(true).doReturn(false).when(spy).keepSearching_DFS(mockStack);
        doReturn(index + 2).when(spy).executeOneStep_DFS(index + 1, mockStack);
        // Execute
        spy.fillBoard_DFS(mockStack);
        // Verify
        verify(spy, times(2)).keepSearching_DFS(mockStack);
        verify(spy).executeOneStep_DFS(index + 1, mockStack);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFillBoardDFS_ThrowException() {
        // Setup
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.peek()).thenThrow(new EmptyStackException());
        // Expect
        expectedException.expect(EmptyStackException.class);
        // Execute
        board.fillBoard_DFS(mockStack);
    }
    //endregion

    //region keepSearching_DFS() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testKeepSearchingDFS_MidSize_Pass() {
        // Setup
        int size = 35;
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.size()).thenReturn(size);
        // Execute & Verify
        assertTrue(board.keepSearching_DFS(mockStack));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeepSearchingDFS_MinSize_Pass() {
        // Setup
        int size = 0;
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.size()).thenReturn(size);
        // Execute & Verify
        assertTrue(board.keepSearching_DFS(mockStack));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeepSearchingDFS_MaxSize_Pass() {
        // Setup
        int size = boardSize;
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.size()).thenReturn(size);
        // Execute & Verify
        assertTrue(board.keepSearching_DFS(mockStack));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeepSearchingDFS_TooLargeSize_Pass() {
        // Setup
        int size = boardSize + 1;
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        when(mockStack.size()).thenReturn(size);
        // Execute & Verify
        assertFalse(board.keepSearching_DFS(mockStack));
    }
    //endregion

    //region executeOneStep_DFS() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOneStepDFS_SuccessfulInitialize_Pass() {
        // Setup
        int index = 9;
        Board spy = spy(board);
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        doReturn(mockedTile).when(spy).getNextTile_DFS(index);
        when(mockedTile.tryInitialize()).thenReturn(true);
        when(mockedTile.getIndex()).thenReturn(index + 1);
        // Execute & Verify
        assertEquals(index + 1, spy.executeOneStep_DFS(index, mockStack));
        verify(mockStack).add(mockedTile);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOneStepDFS_FailedInitialize_Pass() {
        // Setup
        int index = 9;
        Board spy = spy(board);
        Stack<Tile> mockStack = (Stack<Tile>) mock(Stack.class);
        Tile mockTile1 = mock(Tile.class);
        Tile mockTile2 = mock(Tile.class);
        doReturn(mockTile2).when(spy).getNextTile_DFS(index);
        when(mockTile2.getIndex()).thenReturn(index + 1);
        when(mockTile2.tryInitialize()).thenReturn(false);
        when(mockStack.pop()).thenReturn(mockTile1);
        when(mockTile1.getIndex()).thenReturn(index);
        // Execute & Verify
        assertEquals(index, spy.executeOneStep_DFS(index, mockStack));
        verify(mockTile2).resetInitializationState();
        verify(mockStack).pop();
        verify(mockTile1).unVisit();
    }
    //endregion

    //region incrementIndex_DFS() tests
    @Test
    public void testIncrementIndexDFS_MidIndex_Pass() {
        int index = boardSize / 2;
        assertEquals(index + 1, board.incrementIndex_DFS(index));
    }

    @Test
    public void testIncrementIndexDFS_MaxIndex_Pass() {
        int index = boardSize - 1;
        assertEquals(0, board.incrementIndex_DFS(index));
    }
    //endregion

    //region getNextTile_DFS() tests
    @Test
    public void testGetNextTileDFS_NotVisited_Pass() {
        // Setup
        int index = 0;
        Board spy = spy(board);
        when(mockedTile.hasBeenVisited()).thenReturn(false);
        doReturn(mockedTile).when(spy).getTile(index);
        // Execute & Verify
        assertEquals(mockedTile, spy.getNextTile_DFS(index));
    }

    @Test
    public void testGetNextTileDFS_VisitedThenNotVisited_Pass() {
        // Setup
        int index = 0;
        Board spy = spy(board);
        Tile mockTile1 = mock(Tile.class);
        Tile mockTile2 = mock(Tile.class);
        when(mockTile1.hasBeenVisited()).thenReturn(true);
        when(mockTile2.hasBeenVisited()).thenReturn(false);
        doReturn(mockTile1).when(spy).getTile(index);
        doReturn(mockTile2).when(spy).getTile(index + 1);
        doReturn(index + 1).when(spy).incrementIndex_DFS(index);
        // Execute & Verify
        assertEquals(mockTile2, spy.getNextTile_DFS(index));
    }

    @Test
    public void testGetNextTileDFS_MaxIndexVisitedThenWrapAround_Pass() {
        // Setup
        int index = boardSize - 1;
        Board spy = spy(board);
        Tile mockTileMax = mock(Tile.class);
        Tile mockTile0 = mock(Tile.class);
        when(mockTileMax.hasBeenVisited()).thenReturn(true);
        when(mockTile0.hasBeenVisited()).thenReturn(false);
        doReturn(mockTileMax).when(spy).getTile(index);
        doReturn(mockTile0).when(spy).getTile(0);
        doReturn(0).when(spy).incrementIndex_DFS(index);
        // Execute & Verify
        assertEquals(mockTile0, spy.getNextTile_DFS(index));
    }
    //endregion

    //region saveBoardToSolution() tests
    @Test
    public void testSaveBoardToSolution_BoardCompleted_Pass() {
        // Setup
        int value = 5;
        Board spy = spy(board);
        when(mockedTile.getValue()).thenReturn(value);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        // Execute
        spy.saveBoardToSolution();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(value, spy.solution[i]);
        }
    }

    @Test
    public void testSaveBoardToSolution_BoardIncomplete() {
        // Setup
        int value = 5;
        Board spy = spy(board);
        Tile mockIncompleteTile = mock(Tile.class);
        when(mockedTile.getValue()).thenReturn(value);
        when(mockIncompleteTile.getValue()).thenReturn(0);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        doReturn(mockIncompleteTile).when(spy).getTile(boardSize - 1);
        // Execute
        spy.saveBoardToSolution();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            assertEquals(0, spy.solution[i]);
        }
    }
    //endregion

    //region getNumberOfGivens() tests
    @Test
    public void testGetNumberOfGivens_Easy_Pass() {
        // Setup
        int randInt = 0;
        int difficulty = 1;
        board.randGen = mock(Random.class);
        when(board.randGen.nextInt(anyInt())).thenReturn(randInt);
        // Execute & Verify
        assertEquals(randInt + 40, board.getNumberOfGivens(difficulty));
        verify(board.randGen).nextInt(10);
    }

    @Test
    public void testGetNumberOfGivens_Medium_Pass() {
        // Setup
        int randInt = 0;
        int difficulty = 2;
        board.randGen = mock(Random.class);
        when(board.randGen.nextInt(anyInt())).thenReturn(randInt);
        // Execute & Verify
        assertEquals(randInt + 32, board.getNumberOfGivens(difficulty));
        verify(board.randGen).nextInt(8);
    }

    @Test
    public void testGetNumberOfGivens_Hard_Pass() {
        // Setup
        int randInt = 0;
        int difficulty = 3;
        board.randGen = mock(Random.class);
        when(board.randGen.nextInt(anyInt())).thenReturn(randInt);
        // Execute & Verify
        assertEquals(randInt + 27, board.getNumberOfGivens(difficulty));
        verify(board.randGen).nextInt(5);
    }
    //endregion

    //region digHoles() tests
    @Test
    public void testDigHoles_NoTilesAlreadyCleared_Pass() {
        // Setup
        int numGivens = boardSize - 2;
        int value = 5; // value of Tiles to be reset
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        Tile mockTile1 = mock(Tile.class);
        Tile mockTile2 = mock(Tile.class);
        doReturn(mockTile1).when(spy).getTile(1);
        doReturn(mockTile2).when(spy).getTile(2);
        when(spy.randGen.nextInt(boardSize)).thenReturn(1).thenReturn(2);
        when(mockTile1.getValue()).thenReturn(value);
        when(mockTile2.getValue()).thenReturn(value);
        // Execute & Verify
        spy.digHoles(numGivens);
        verify(mockTile1).clear();
        verify(mockTile2).clear();
    }

    @Test
    public void testDigHoles_OneTileAlreadyCleared_Pass() {
        // Setup
        int numGivens = boardSize - 2;
        int value = 5; // value of Tiles to be reset
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        Tile mockTile1 = mock(Tile.class);
        Tile mockTile2 = mock(Tile.class);
        Tile mockTile3 = mock(Tile.class);
        doReturn(mockTile1).when(spy).getTile(1);
        doReturn(mockTile2).when(spy).getTile(2);
        doReturn(mockTile3).when(spy).getTile(3);
        when(spy.randGen.nextInt(boardSize)).thenReturn(1).thenReturn(2).thenReturn(3);
        when(mockTile1.getValue()).thenReturn(value);
        when(mockTile2.getValue()).thenReturn(0); // Simulating that this Tile has already been cleared
        when(mockTile3.getValue()).thenReturn(value);
        // Execute & Verify
        spy.digHoles(numGivens);
        verify(mockTile1).clear();
        verify(mockTile2, never()).clear();
        verify(mockTile3).clear();
    }
    //endregion

    //region getBound() tests
    @Test
    public void testGetBounds_Easy_Pass() {
        int difficulty = 1;
        int expectedBound = 4;
        assertEquals(expectedBound, board.getBound(difficulty));
    }

    @Test
    public void testGetBounds_Medium_Pass() {
        int difficulty = 2;
        int expectedBound = 3;
        assertEquals(expectedBound, board.getBound(difficulty));
    }

    @Test
    public void testGetBounds_Hard_Pass() {
        int difficulty = 3;
        int expectedBound = 2;
        assertEquals(expectedBound, board.getBound(difficulty));
    }
    //endregion

    //region checkBounds() tests
    @Test
    @SuppressWarnings("unchecked")
    public void testCheckBounds_Pass() {
        // Setup
        int bound = 3;
        Board spy = spy(board);
        doNothing().when(spy).fillHighAndLowHouses(argThat(either(is(spy.rows)).or(is(spy.columns))), argThat(is(any
                (Stack.class))), argThat(is(any(Stack.class))), anyInt());
        doNothing().when(spy).adjustHousesToBounds(argThat(is(any(Stack.class))), argThat(is(any(Stack.class))),
                anyInt(), anyBoolean());
        // Execute
        spy.checkBounds(bound);
        // Verify
        verify(spy, times(2)).fillHighAndLowHouses(argThat(is(any(House[].class))), argThat(is(any(Stack.class))),
                argThat(is(any(Stack.class))), intThat(is(bound)));
        verify(spy).adjustHousesToBounds(argThat(is(any(Stack.class))), argThat(is(any(Stack.class))), anyInt(),
                booleanThat(is(true)));
        verify(spy).adjustHousesToBounds(argThat(is(any(Stack.class))), argThat(is(any(Stack.class))), anyInt(),
                booleanThat(is(false)));
    }
    //endregion

    //region fillHighAndLowHouses() tests
    @SuppressWarnings("unchecked")
    @Test
    public void testFillHighAndLowHouses_pushLowHigh_Pass() {
        // Setup
        int bound = 3;
        House highHouse = mock(House.class);
        doReturn(4).when(highHouse).getValueCount();
        House lowHouse = mock(House.class);
        doReturn(2).when(lowHouse).getValueCount();
        House[] houses = {highHouse, lowHouse};
        Stack<House> highHouses = mock(Stack.class);
        Stack<House> lowHouses = mock(Stack.class);
        // Execute
        board.fillHighAndLowHouses(houses, highHouses, lowHouses, bound);
        // Verify
        verify(highHouses).push(highHouse);
        verify(highHouses).push(argThat(is(any(House.class))));
        verify(lowHouses).push(lowHouse);
        verify(lowHouses).push(argThat(is(any(House.class))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFillHighAndLowHouses_pushNone_Pass() {
        // Setup
        int bound = 3;
        House highHouse = mock(House.class);
        doReturn(3).when(highHouse).getValueCount();
        House lowHouse = mock(House.class);
        doReturn(3).when(lowHouse).getValueCount();
        House[] houses = {highHouse, lowHouse};
        Stack<House> highHouses = mock(Stack.class);
        Stack<House> lowHouses = mock(Stack.class);
        // Execute
        board.fillHighAndLowHouses(houses, highHouses, lowHouses, bound);
        // Verify
        verify(highHouses, never()).push(argThat(is(any(House.class))));
        verify(lowHouses, never()).push(argThat(is(any(House.class))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFillHighAndLowHouses_nullStack_ignored() {
        // Setup
        int bound = 3;
        House highHouse = mock(House.class);
        House lowHouse = mock(House.class);
        House[] houses = {highHouse, lowHouse};
        Stack<House> mockStack = mock(Stack.class);
        // Execute
        board.fillHighAndLowHouses(houses, null, null, bound);
        board.fillHighAndLowHouses(houses, null, mockStack, bound);
        board.fillHighAndLowHouses(houses, mockStack, null, bound);
        // Verify
        verify(highHouse, never()).getValueCount();
        verify(lowHouse, never()).getValueCount();
        verify(mockStack, never()).push(argThat(is(any(House.class))));
    }
    //endregion

    //region adjustHousesToBounds() tests
    @Test
    public void testAdjustHousesToBounds_row_Pass() {
        // Set up
        int bound = 3;
        boolean isRow = true;
        Board spy = spy(board);
        Stack highHouses = mock(Stack.class); // Populate High Houses
        doReturn(1).when(highHouses).size();
        House highHouse = mock(House.class);
        List highHouseTiles = mock(LinkedList.class);
        doReturn(highHouse).when(highHouses).pop();
        doReturn(highHouseTiles).when(highHouse).getValueTiles();
        doReturn(bound + 1).when(highHouse).getValueCount();
        Stack lowHouses = mock(Stack.class); // Populate Low Houses
        when(lowHouses.size())
                .thenReturn(1)
                .thenReturn(0);
        House lowHouse = mock(House.class);
        doReturn(lowHouse).when(lowHouses).pop();
        when(lowHouse.getValueCount())
                .thenReturn(bound - 1)
                .thenReturn(bound);
        doNothing().when(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow); // Stub out other methods
        // Execute
        spy.adjustHousesToBounds(highHouses, lowHouses, bound, isRow);
        // Verify
        verify(highHouses).pop();
        verify(lowHouses).pop();
        verify(lowHouse, times(2)).getValueCount();
        verify(highHouse).getValueTiles();
        verify(highHouse).getValueCount();
        verify(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
    }

    @Test
    public void testAdjustHousesToBounds_column_Pass() {
        // Set up
        int bound = 3;
        boolean isRow = false;
        Board spy = spy(board);
        Stack highHouses = mock(Stack.class); // Populate High Houses
        doReturn(1).when(highHouses).size();
        House highHouse = mock(House.class);
        List highHouseTiles = mock(LinkedList.class);
        doReturn(highHouse).when(highHouses).pop();
        doReturn(highHouseTiles).when(highHouse).getValueTiles();
        doReturn(bound + 1).when(highHouse).getValueCount();
        Stack lowHouses = mock(Stack.class); // Populate Low Houses
        when(lowHouses.size())
                .thenReturn(1)
                .thenReturn(0);
        House lowHouse = mock(House.class);
        doReturn(lowHouse).when(lowHouses).pop();
        when(lowHouse.getValueCount())
                .thenReturn(bound - 1)
                .thenReturn(bound);
        doNothing().when(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow); // Stub out other methods
        // Execute
        spy.adjustHousesToBounds(highHouses, lowHouses, bound, isRow);
        // Verify
        verify(highHouses).pop();
        verify(lowHouses).pop();
        verify(lowHouse, times(2)).getValueCount();
        verify(highHouse).getValueTiles();
        verify(highHouse).getValueCount();
        verify(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
    }

    @Test
    public void testAdjustHousesToBounds_multipleLowHouses_Pass() {
        // Set up
        int bound = 3;
        boolean isRow = true;
        Board spy = spy(board);
        Stack highHouses = mock(Stack.class); // Populate High Houses
        doReturn(1).when(highHouses).size();
        House highHouse = mock(House.class);
        List highHouseTiles = mock(LinkedList.class);
        doReturn(highHouse).when(highHouses).pop();
        doReturn(highHouseTiles).when(highHouse).getValueTiles();
        doReturn(bound + 1).when(highHouse).getValueCount();
        Stack lowHouses = mock(Stack.class); // Populate Low Houses
        when(lowHouses.size())
                .thenReturn(2)
                .thenReturn(1)
                .thenReturn(0);
        House lowHouse = mock(House.class);
        doReturn(lowHouse).when(lowHouses).pop();
        when(lowHouse.getValueCount())
                .thenReturn(bound - 1)
                .thenReturn(bound)
                .thenReturn(bound - 1)
                .thenReturn(bound);
        doNothing().when(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow); // Stub out other methods
        // Execute
        spy.adjustHousesToBounds(highHouses, lowHouses, bound, isRow);
        // Verify
        verify(highHouses).pop();
        verify(lowHouses, times(2)).pop();
        verify(lowHouse, times(4)).getValueCount();
        verify(highHouse).getValueTiles();
        verify(highHouse, times(2)).getValueCount();
        verify(spy, times(2)).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
    }

    @Test
    public void testAdjustHousesToBounds_multipleLowAndHighHouses_Pass() {
        // Set up
        int bound = 3;
        boolean isRow = true;
        Board spy = spy(board);
        Stack highHouses = mock(Stack.class); // Populate High Houses
        doReturn(1).when(highHouses).size();
        House highHouse = mock(House.class);
        List highHouseTiles = mock(LinkedList.class);
        doReturn(highHouse).when(highHouses).pop();
        doReturn(highHouseTiles).when(highHouse).getValueTiles();
        when(highHouse.getValueCount())
                .thenReturn(bound + 1)
                .thenReturn(bound)
                .thenReturn(bound + 1);
        Stack lowHouses = mock(Stack.class); // Populate Low Houses
        when(lowHouses.size())
                .thenReturn(2)
                .thenReturn(1)
                .thenReturn(0);
        House lowHouse = mock(House.class);
        doReturn(lowHouse).when(lowHouses).pop();
        when(lowHouse.getValueCount())
                .thenReturn(bound - 1)
                .thenReturn(bound)
                .thenReturn(bound - 1)
                .thenReturn(bound);
        doNothing().when(spy).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow); // Stub out other methods
        // Execute
        spy.adjustHousesToBounds(highHouses, lowHouses, bound, isRow);
        // Verify
        verify(highHouses, times(2)).pop();
        verify(lowHouses, times(2)).pop();
        verify(lowHouse, times(4)).getValueCount();
        verify(highHouse, times(2)).getValueTiles();
        verify(highHouse, times(2)).getValueCount();
        verify(spy, times(2)).swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
    }
    //endregion

    //region swapHighHouseToLowHouseTile() tests
    @Test
    public void testSwapHighHouseToLowHouseTile_row_pass() {
        // Setup
        boolean isRow = true;
        int column = 5;
        int lowTileIndex = 45;
        int tileSolution = 9;
        // Setup board spy
        Board spy = spy(board);
        doReturn(tileSolution).when(spy).getSolutionTile(lowTileIndex);
        // Setup high house
        List<Tile> highHouseTiles = spy(new LinkedList<Tile>());
        Tile highTile = mock(Tile.class);
        highHouseTiles.add(highTile);
        doReturn(column).when(highTile).getColumnNumber();
        // Setup low house
        House lowHouse = mock(House.class);
        Tile lowTile = mock(Tile.class);
        doReturn(lowTile).when(lowHouse).getMember(column);
        doReturn(lowTileIndex).when(lowTile).getIndex();
        // Execute
        spy.swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
        // Verify
        verify(lowTile).update(tileSolution);
        verify(highTile).clear();
        verify(highTile).getColumnNumber();
        verify(highTile, never()).getRowNumber();
        verify(lowHouse).getMember(column);
    }

    @Test
    public void testSwapHighHouseToLowHouseTile_column_pass() {
        // Setup
        boolean isRow = false;
        int row = 5;
        int lowTileIndex = 45;
        int tileSolution = 9;
        // Setup board spy
        Board spy = spy(board);
        doReturn(tileSolution).when(spy).getSolutionTile(lowTileIndex);
        // Setup high house
        List<Tile> highHouseTiles = spy(new LinkedList<Tile>());
        Tile highTile = mock(Tile.class);
        highHouseTiles.add(highTile);
        doReturn(row).when(highTile).getRowNumber();
        // Setup low house
        House lowHouse = mock(House.class);
        Tile lowTile = mock(Tile.class);
        doReturn(lowTile).when(lowHouse).getMember(row);
        doReturn(lowTileIndex).when(lowTile).getIndex();
        // Execute
        spy.swapHighHouseToLowHouseTile(highHouseTiles, lowHouse, isRow);
        // Verify
        verify(lowTile).update(tileSolution);
        verify(highTile).clear();
        verify(highTile, never()).getColumnNumber();
        verify(highTile).getRowNumber();
        verify(lowHouse).getMember(row);
    }

    @Test
    public void testSwapHighHouseToLowHouseTile_emptyHighHouse_ignore() {
        // Setup
        boolean isRow = true;
        int column = 5;
        int lowTileIndex = 45;
        int tileSolution = 9;
        // Setup board spy
        Board spy = spy(board);
        doReturn(tileSolution).when(spy).getSolutionTile(lowTileIndex);
        // Setup low house
        House lowHouse = mock(House.class);
        Tile lowTile = mock(Tile.class);
        doReturn(lowTile).when(lowHouse).getMember(column);
        doReturn(lowTileIndex).when(lowTile).getIndex();
        // Execute
        spy.swapHighHouseToLowHouseTile(new LinkedList<Tile>(), lowHouse, isRow);
        // Verify
        verify(lowTile, never()).update(tileSolution);
        verify(lowHouse, never()).getMember(column);
    }

    @Test
    public void testSwapHighHouseToLowHouseTile_nullHighHouse_ignore() {
        // Setup
        boolean isRow = true;
        int column = 5;
        int lowTileIndex = 45;
        int tileSolution = 9;
        // Setup board spy
        Board spy = spy(board);
        doReturn(tileSolution).when(spy).getSolutionTile(lowTileIndex);
        // Setup low house
        House lowHouse = mock(House.class);
        Tile lowTile = mock(Tile.class);
        doReturn(lowTile).when(lowHouse).getMember(column);
        doReturn(lowTileIndex).when(lowTile).getIndex();
        // Execute
        spy.swapHighHouseToLowHouseTile(null, lowHouse, isRow);
        // Verify
        verify(lowTile, never()).update(tileSolution);
        verify(lowHouse, never()).getMember(column);
    }

    @Test
    public void testSwapHighHouseToLowHouseTile_nullLowHouse_ignore() {
        // Setup
        boolean isRow = true;
        int column = 5;
        int lowTileIndex = 45;
        int tileSolution = 9;
        // Setup board spy
        Board spy = spy(board);
        doReturn(tileSolution).when(spy).getSolutionTile(lowTileIndex);
        // Setup high house
        List<Tile> highHouseTiles = spy(new LinkedList<Tile>());
        Tile highTile = mock(Tile.class);
        highHouseTiles.add(highTile);
        doReturn(column).when(highTile).getRowNumber();
        // Execute
        spy.swapHighHouseToLowHouseTile(highHouseTiles, null, isRow);
        // Verify
        verify(highTile, never()).clear();
        verify(highTile, never()).getColumnNumber();
        verify(highTile, never()).getRowNumber();
    }
    //endregion

    //region markOriginals() tests
    @Test
    public void test_markOriginals_allOrig_pass() {
        // Setup
        Board spy = spy(board);
        Tile[] mockTiles = new Tile[boardSize];
        for(int i = 0; i < boardSize; ++i) {
            Tile mockTile = mock(Tile.class);
            doReturn(1).when(mockTile).getValue();
            mockTiles[i] = mockTile;
        }
        doReturn(mockTiles).when(spy).getTiles();
        // Execute
        spy.markOriginals();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            verify(mockTiles[i]).setOrig(true);
        }
    }

    @Test
    public void test_markOriginals_noOrig_pass() {
        // Setup
        Board spy = spy(board);
        Tile[] mockTiles = new Tile[boardSize];
        for(int i = 0; i < boardSize; ++i) {
            Tile mockTile = mock(Tile.class);
            doReturn(0).when(mockTile).getValue();
            mockTiles[i] = mockTile;
        }
        doReturn(mockTiles).when(spy).getTiles();
        // Execute
        spy.markOriginals();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            verify(mockTiles[i], never()).setOrig(anyBoolean());
        }
    }

    @Test
    public void test_markOriginals_someOrig_pass() {
        // Setup
        Board spy = spy(board);
        Tile[] mockTiles = new Tile[boardSize];
        for(int i = 0; i < boardSize; ++i) {
            Tile mockTile = mock(Tile.class);
            doReturn((i < 40) ? 0 : 1).when(mockTile).getValue();
            mockTiles[i] = mockTile;
        }
        doReturn(mockTiles).when(spy).getTiles();
        // Execute
        spy.markOriginals();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            if(i < 40) {
                verify(mockTiles[i], never()).setOrig(anyBoolean());
            } else {
                verify(mockTiles[i]).setOrig(true);
            }
        }
    }
    //endregion

    //region runSolver() tests
    @Test
    public void test_runSolver_pass() {
        // Setup
        Board spy = spy(board);
        Solver mockSolver = mock(Solver.class);
        spy.difficulty = 1;
        doNothing().when(spy).clearBoard();
        // Execute
        spy.runSolver(mockSolver);
        // Verify
        verify(mockSolver).solve(spy.difficulty);
        verify(spy).clearBoard();
    }

    @Test
    public void test_runSolver_null() {
        // Setup
        Board spy = spy(board);
        spy.difficulty = 1;
        doNothing().when(spy).clearBoard();
        // Execute
        spy.runSolver(null);
        // Verify
        verify(spy, never()).clearBoard();
    }
    //endregion

    //region clearBoard() tests
    @Test
    public void test_clearBoard_pass() {
        // Setup
        Board spy = spy(board);
        Tile[] mockTiles = new Tile[81];
        for(int i = 0; i < boardSize; ++i) {
            mockTiles[i] = mock(Tile.class);
        }
        doReturn(mockTiles).when(spy).getTiles();
        // Execute
        spy.clearBoard();
        // Verify
        for(int i = 0; i < boardSize; ++i) {
            verify(mockTiles[i]).clear();
        }
    }
    //endregion

}

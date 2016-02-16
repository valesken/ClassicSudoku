package me.valesken.jeff.sudoku_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by jeff on 2/8/2016.
 * Last Updated on 2/10/2016.
 */
public class BoardTest {

    private int houseSize = 9;
    private int boardSize = 81;
    private Board board;
    private House mockedHouse;
    private Tile mockedTile;

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
    }
    //endregion

    //region initializeHouses() tests
    @Test
    public void testInitializeHousesPass() {
        Board spy = spy(board);
        spy.initializeHouses();
        for (int i = 0; i < houseSize; ++i) {
            verify(spy, times(3)).buildHouse(i);
            assertNotNull(spy.rows[i]);
            assertNotNull(spy.columns[i]);
            assertNotNull(spy.zones[i]);
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
        // Run
        spy.initializeTiles();
        // Verify
        for (int i = 0; i < boardSize; ++i) {
            assertNotNull(spy.tiles[i]);
            verify(spy).buildTile(i);
            verify(spy, times(houseSize)).addTileToHouses(spy.tiles[i], i % houseSize, i % houseSize, i % houseSize);
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
        verify(spy).getRow(index);
        verify(spy).getColumn(index);
        verify(spy).getZone(index);
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
        verify(spy).getRow(index);
        verify(spy).getColumn(index);
        verify(spy).getZone(index);
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
        verify(spy, never()).getRow(anyInt());
        verify(spy, never()).getColumn(anyInt());
        verify(spy, never()).getZone(anyInt());
        verify(mockedHouse, never()).addMember(any(Tile.class));
        verify(mockedTile, never()).setHouses(any(House.class), any(House.class), any(House.class));
    }

    @Test
    public void testAddTileToHousesHugeIndexFail() {
        int index = houseSize;
        Board spy = spy(board);
        doReturn(mockedHouse).when(spy).getRow(anyInt());
        doReturn(mockedHouse).when(spy).getColumn(anyInt());
        doReturn(mockedHouse).when(spy).getZone(anyInt());
        spy.addTileToHouses(mockedTile, index, index, index);
        verify(spy, never()).getRow(anyInt());
        verify(spy, never()).getColumn(anyInt());
        verify(spy, never()).getZone(anyInt());
        verify(mockedHouse, never()).addMember(any(Tile.class));
        verify(mockedTile, never()).setHouses(any(House.class), any(House.class), any(House.class));
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
        int index = -1;
        assertEquals(-1, board.getSolutionTile(index));
    }

    @Test
    public void testGetSolutionTileLargeIndexFail() {
        int index = boardSize;
        assertEquals(-1, board.getSolutionTile(index));
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
        verify(spy).getTile(index);
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testGetTileNotesOrValueMaxIndexPass() {
        int index = boardSize - 1;
        LinkedList<Integer> list = new LinkedList<>();
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        when(mockedTile.getNotesOrValue()).thenReturn(list);
        assertEquals(list, spy.getTileNotesOrValue(index));
        verify(spy).getTile(index);
        verify(mockedTile).getNotesOrValue();
    }

    @Test
    public void testGetTileNotesOrValueNegativeIndexFail() {
        Board spy = spy(board);
        assertNull(spy.getTileNotesOrValue(-1));
        verify(spy, never()).getTile(anyInt());
    }

    @Test
    public void testGetTileNotesOrValueLargeIndexFail() {
        Board spy = spy(board);
        assertNull(spy.getTileNotesOrValue(boardSize));
        verify(spy, never()).getTile(anyInt());
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
        verify(spy).getTile(index);
        verify(mockedTile).isNoteMode();
    }

    @Test
    public void testTileIsNoteModeFalsePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(false);
        doReturn(mockedTile).when(spy).getTile(index);
        assertFalse(spy.tileIsNoteMode(index));
        verify(spy).getTile(index);
        verify(mockedTile).isNoteMode();
    }

    @Test
    public void testTileIsNoteModeMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsNoteMode(index));
        verify(spy).getTile(index);
        verify(mockedTile).isNoteMode();
    }

    @Test
    public void testTileIsNoteModeMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        when(mockedTile.isNoteMode()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsNoteMode(index));
        verify(spy).getTile(index);
        verify(mockedTile).isNoteMode();
    }

    @Test
    public void testTileIsNoteModeNegativeIndexFail() {
        int index = -1;
        Board spy = spy(board);
        assertFalse(spy.tileIsNoteMode(index));
        verify(spy).getTile(index);
    }

    @Test
    public void testTileIsNoteModeLargeIndexFail() {
        Board spy = spy(board);
        assertFalse(spy.tileIsNoteMode(boardSize));
        verify(spy).getTile(boardSize);
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
        verify(spy).getTile(index);
        verify(mockedTile).isOrig();
    }

    @Test
    public void testTileIsOrigFalsePass() {
        int index = 10;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(false);
        doReturn(mockedTile).when(spy).getTile(index);
        assertFalse(spy.tileIsOrig(index));
        verify(spy).getTile(index);
        verify(mockedTile).isOrig();
    }

    @Test
    public void testTileIsOrigMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsOrig(index));
        verify(spy).getTile(index);
        verify(mockedTile).isOrig();
    }

    @Test
    public void testTileIsOrigMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        when(mockedTile.isOrig()).thenReturn(true);
        doReturn(mockedTile).when(spy).getTile(index);
        assertTrue(spy.tileIsOrig(index));
        verify(spy).getTile(index);
        verify(mockedTile).isOrig();
    }

    @Test
    public void testTileIsOrigNegativeIndexFail() {
        int index = -1;
        Board spy = spy(board);
        assertFalse(spy.tileIsOrig(index));
        verify(spy).getTile(index);
    }

    @Test
    public void testTileIsOrigLargeIndexFail() {
        Board spy = spy(board);
        assertFalse(spy.tileIsOrig(boardSize));
        verify(spy).getTile(boardSize);
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
            verify(spy).getTileNotesOrValue(i);
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
            verify(spy).getTileNotesOrValue(i);
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
            verify(spy).getTileNotesOrValue(i);
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
            verify(spy).getTileNotesOrValue(i);
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
        verify(mockedTile, times(boardSize)).getValue();
        for (int i = 0; i < boardSize; ++i) {
            verify(spy).getTile(i);
            verify(spy).getSolutionTile(i);
        }
    }

    @Test
    public void testIsGameOverFalsePass() {
        Board spy = spy(board);
        doReturn(1).when(spy).getSolutionTile(anyInt());
        doReturn(mockedTile).when(spy).getTile(anyInt());
        when(mockedTile.getValue()).thenReturn(5);
        assertFalse(spy.isGameOver());
        verify(mockedTile).getValue();
        verify(spy).getTile(anyInt()); // Should stop after trying index 0
        verify(spy).getSolutionTile(anyInt()); // Should stop after trying index 0
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
        int index = 0;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.updateTile(index, value);
        verify(spy).getTile(index);
        verify(mockedTile).update(value);
    }

    @Test
    public void testUpdateTileMaxIndexPass() {
        int index = boardSize - 1;
        int value = 4;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.updateTile(index, value);
        verify(spy).getTile(index);
        verify(mockedTile).update(value);
    }

    @Test
    public void testUpdateTileNegativeIndexIgnore() {
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        spy.updateTile(-1, 4);
        verify(spy, never()).getTile(anyInt());
        verify(mockedTile, never()).update(anyInt());
    }

    @Test
    public void testUpdateTileLargeIndexIgnore() {
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(anyInt());
        spy.updateTile(boardSize, 4);
        verify(spy, never()).getTile(anyInt());
        verify(mockedTile, never()).update(anyInt());
    }
    //endregion

    //region clearTile() tests
    @Test
    public void testClearTileMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.clearTile(index);
        verify(spy).getTile(index);
        verify(mockedTile).clear();
    }

    @Test
    public void testClearTileMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.clearTile(index);
        verify(spy).getTile(index);
        verify(mockedTile).clear();
    }

    @Test
    public void testClearTileNegativeIndexIgnore() {
        Board spy = spy(board);
        spy.clearTile(-1);
        verify(spy, never()).getTile(anyInt());
    }

    @Test
    public void testClearTileLargeIndexIgnore() {
        Board spy = spy(board);
        spy.clearTile(boardSize);
        verify(spy, never()).getTile(anyInt());
    }
    //endregion

    //region toggleMode() tests
    @Test
    public void testToggleModeMinIndexPass() {
        int index = 0;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.toggleMode(index);
        verify(spy).getTile(index);
        verify(mockedTile).toggleMode();
    }

    @Test
    public void testToggleModeMaxIndexPass() {
        int index = boardSize - 1;
        Board spy = spy(board);
        doReturn(mockedTile).when(spy).getTile(index);
        spy.toggleMode(index);
        verify(spy).getTile(index);
        verify(mockedTile).toggleMode();
    }

    @Test
    public void testToggleModeNegativeIndexIgnore() {
        Board spy = spy(board);
        spy.toggleMode(-1);
        verify(spy, never()).getTile(anyInt());
    }

    @Test
    public void testToggleModeLargeIndexIgnore() {
        Board spy = spy(board);
        spy.toggleMode(boardSize);
        verify(spy, never()).getTile(anyInt());
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
        verify(spy).getWrongTiles();
        verify(spy).getSolutionTile(index);
        verify(spy.randGen).nextInt(1);
        verify(mockedTile).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile).update(solutionValue);
        verify(mockedTile).setOrig(true);
        verify(mockedTile).getIndex();
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
        verify(spy).getWrongTiles();
        verify(spy).getSolutionTile(index);
        verify(spy.randGen).nextInt(2);
        verify(mockedTile).isNoteMode();
        verify(mockedTile, never()).toggleMode();
        verify(mockedTile).update(solutionValue);
        verify(mockedTile).setOrig(true);
        verify(mockedTile).getIndex();
    }

    @Test
    public void testUseHintZeroOpenTilesFail() {
        // Setup
        Board spy = spy(board);
        spy.randGen = mock(Random.class);
        LinkedList<Tile> list = new LinkedList<>();
        doReturn(list).when(spy).getWrongTiles();
        // Execute
        assertEquals(-1, spy.useHint());
        // Verify
        verify(spy).getWrongTiles();
        verify(spy.randGen, never()).nextInt(anyInt());
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
        // Verify
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
        // Execute
        spy.solve();
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
        verify(mockedTile, times(boardSize)).getJSON();
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

}

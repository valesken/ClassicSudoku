package me.valesken.jeff.sudoku_structure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 1/25/2016.
 * Last updated on 1/26/2016.
 */
public class TileTest {

    private int boardsize = 9;
    private int tileIndex = 0;
    private Tile tile;
    private House mockedHouse;

    //region setUp
    @Before
    public void setUp() throws Exception {
        mockedHouse = mock(House.class);
        tile = new Tile(boardsize, tileIndex);
        tile.setHouses(mockedHouse, mockedHouse, mockedHouse);
    }
    //endregion

    //region setHouses() tests
    @Test
    public void testSetHouses() throws Exception {
        House row = new House(boardsize);
        House column = new House(boardsize);
        House zone = new House(boardsize);
        tile.setHouses(row, column, zone);
        assertEquals(tile.getRow(), row);
        assertEquals(tile.getColumn(), column);
        assertEquals(tile.getZone(), zone);
    }
    //endregion

    //region setValueInHouses() tests
    @Test
    public void testSetValueInHouses() {
        int value = 1;
        tile.setValueInHouses(value, true);
        verify(mockedHouse, times(3)).setValueInHouse(value, true, tileIndex);
    }
    //endregion

    //region toggleNoteMode() tests
    @Test
    public void testToggleNoteModeOn() throws Exception {
        tile.toggleMode();
        assertTrue(tile.isNoteMode());
    }

    @Test
    public void testToggleNoteModeOff() throws Exception {
        tile.toggleMode();
        assertTrue(tile.isNoteMode());
        tile.toggleMode();
        assertFalse(tile.isNoteMode());
    }
    //endregion

    //region update() tests
    @Test
    public void testUpdateValue() throws Exception {
        int value = 1;
        Tile spy = spy(tile);
        spy.update(value);
        verify(spy).setValueInHouses(value, true);
        assertEquals(value, spy.getValue());
    }
    //endregion
}
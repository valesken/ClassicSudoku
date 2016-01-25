package me.valesken.jeff.sudoku_structure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jeff on 1/25/16.
 */
public class TileTest {

    private int boardsize = 9;
    private Tile tile;

    @Before
    public void setUp() throws Exception {
        tile = new Tile(boardsize, 0);
    }

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

    @Test
    public void testUpdate() throws Exception {

    }
}
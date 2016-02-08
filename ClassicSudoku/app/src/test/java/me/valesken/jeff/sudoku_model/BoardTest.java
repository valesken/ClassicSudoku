package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


/**
 * Created by jeff on 2/8/2016.
 * Last Updated on 2/8/2016.
 */
public class BoardTest {

    private int houseSize = 9;
    private int boardSize = 81;
    private Board board;
    private House mockedRow;
    private House mockedColumn;
    private House mockedZone;
    private Tile mockedTile;

    //region setup
    @Before
    public void setUp() {
        mockedTile = mock(Tile.class);
        mockedRow = mock(House.class);
        mockedColumn = mock(House.class);
        mockedZone = mock(House.class);
        board = new Board(houseSize, mockedTile, mockedRow, mockedColumn, mockedZone);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        Board testBoard = new Board(houseSize);
        assertEquals(houseSize, testBoard.houseSize);
        assertEquals(boardSize, testBoard.boardSize);
        assertNotNull(testBoard.solution);
        assertEquals(boardSize, testBoard.solution.length);
        assertNotNull(testBoard.rows);
        assertEquals(houseSize, testBoard.rows.length);
        assertNotNull(testBoard.columns);
        assertEquals(houseSize, testBoard.columns.length);
        assertNotNull(testBoard.zones);
        assertEquals(houseSize, testBoard.zones.length);
    }
    //endregion

}

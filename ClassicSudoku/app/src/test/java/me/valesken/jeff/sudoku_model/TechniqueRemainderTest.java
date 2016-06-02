package me.valesken.jeff.sudoku_model;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/1/2016.
 * Last Updated on 6/1/2016.
 */
public class TechniqueRemainderTest {

    private int targetTotal = 45;
    private TechniqueRemainder remainder;
    private Solver mockSolver;
    private List<House> houses;
    private List<Tile> tiles;
    private Board mockBoard;
    private House mockHouse;
    private Tile mockTile;

    //region setup
    @Before
    public void setUp() throws Exception {
        mockSolver = mock(Solver.class);
        houses = new LinkedList<>();
        tiles = new LinkedList<>();
        mockBoard = mock(Board.class);
        mockHouse = mock(House.class);
        mockTile = mock(Tile.class);
        remainder = new TechniqueRemainder(mockSolver);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructor() {
        TechniqueRemainder tr = new TechniqueRemainder(mockSolver);
        assertNotNull(tr);
        assertEquals(tr.solver, mockSolver);
        assertEquals(TechniqueRemainder.TARGET_TOTAL, targetTotal);
    }
    //endregion

    //region execute() tests
    @Test
    public void testOneHouseHasRemainderPass_lastTileInHouse() {

        // Set up Tiles in House
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(0).when(mockTile).getValue();
        tiles.add(mockTile);
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up everything else
        houses.add(mockHouse);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertTrue(remainder.execute());
        verify(mockSolver).solveTile(mockTile, solution);
    }

    @Test
    public void testOneHouseHasRemainderPass_firstTileInHouse() {

        // Set up Tiles in House
        doReturn(0).when(mockTile).getValue();
        tiles.add(mockTile);
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up everything else
        houses.add(mockHouse);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertTrue(remainder.execute());
        verify(mockSolver).solveTile(mockTile, solution);
    }

    @Test
    public void testOneHouseHasRemainderPass_middleTileInHouse() {

        // Set up Tiles in House
        for(int i = 0; i < 4; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(0).when(mockTile).getValue();
        tiles.add(mockTile);
        for(int i = 4; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up everything else
        houses.add(mockHouse);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertTrue(remainder.execute());
        verify(mockSolver).solveTile(mockTile, solution);
    }

    @Test
    public void testTwoHousesOneHasRemainderPass() {

        // Set up Tiles in first House
        for(int i = 0; i < 9; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up Tiles in second House
        List<Tile> moreTiles = new LinkedList<>();
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            moreTiles.add(tempMockTile);
        }
        doReturn(0).when(mockTile).getValue();
        moreTiles.add(mockTile);
        House mockHouse2 = mock(House.class);
        doReturn(moreTiles.iterator()).when(mockHouse2).iterator();

        // Set up everything else
        houses.add(mockHouse);
        houses.add(mockHouse2);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertTrue(remainder.execute());
        verify(mockSolver).solveTile(mockTile, solution);
    }

    @Test
    public void testTwoHouseBothHaveRemainderPass() {

        // Set up Tiles in first House
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(0).when(mockTile).getValue();
        tiles.add(mockTile);
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up Tiles in second House
        List<Tile> moreTiles = new LinkedList<>();
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            moreTiles.add(tempMockTile);
        }
        Tile mockTile2 = mock(Tile.class);
        doReturn(0).when(mockTile2).getValue();
        moreTiles.add(mockTile2);
        House mockHouse2 = mock(House.class);
        doReturn(moreTiles.iterator()).when(mockHouse2).iterator();

        // Set up everything else
        houses.add(mockHouse);
        houses.add(mockHouse2);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertTrue(remainder.execute());
        verify(mockSolver).solveTile(mockTile, solution);
        verify(mockSolver, never()).solveTile(mockTile2, solution);
    }

    @Test
    public void testOneHouseHasMultipleRemaindersFail() {

        // Set up Tiles in House
        for(int i = 0; i < 7; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(0).when(mockTile).getValue();
        tiles.add(mockTile);
        Tile mockTile2 = mock(Tile.class);
        doReturn(0).when(mockTile2).getValue();
        tiles.add(mockTile2);
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up everything else
        houses.add(mockHouse);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;
        int solution = 9;

        // Execute & Verify
        assertFalse(remainder.execute());
        verify(mockSolver, never()).solveTile(mockTile, solution);
        verify(mockSolver, never()).solveTile(mockTile2, solution);
    }

    @Test
    public void testOneHouseHasNoRemaindersFail() {

        // Set up Tiles in House
        for(int i = 0; i < 9; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(i + 1).when(tempMockTile).getValue();
            tiles.add(tempMockTile);
        }
        doReturn(tiles.iterator()).when(mockHouse).iterator();

        // Set up everything else
        houses.add(mockHouse);
        mockSolver.houses = houses;
        mockSolver.board = mockBoard;

        // Execute & Verify
        assertFalse(remainder.execute());
        verify(mockSolver, never()).solveTile(argThat(is(CoreMatchers.any(Tile.class))), anyInt());
    }
    //endregion
}

package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/2/2016.
 * Last updated on 6/2/2016.
 */
public class TechniqueSinglePositionTest {

    private int solution = 9;
    private List<House> housesSpy;
    private ArrayList<Tile> tilesSpy;
    private Tile mockTile;
    private House mockRow, mockColumn, mockZone;
    private Solver mockSolver;
    private TechniqueSinglePosition singlePosition;

    //region setup
    @Before
    public void setUp() {
        LinkedList<House> houses = new LinkedList<>();
        housesSpy = spy(houses);
        ArrayList<Tile> tiles = new ArrayList<>();
        tilesSpy = spy(tiles);
        mockTile = mock(Tile.class);
        mockRow = mock(House.class);
        mockColumn = mock(House.class);
        mockZone = mock(House.class);
        mockSolver = mock(Solver.class);
        mockSolver.houses = housesSpy;
        singlePosition = new TechniqueSinglePosition(mockSolver);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        TechniqueSinglePosition tsp = new TechniqueSinglePosition(mockSolver);
        assertNotNull(tsp);
        assertEquals(tsp.solver, mockSolver);
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(values, TechniqueSinglePosition.VALUES);
    }
    //endregion

    //region execute() tests
    @Test
    public void testExecuteHasSinglePositionPass() {
        // Set up
        TechniqueSinglePosition spy = spy(singlePosition);
        doReturn(null).when(spy).checkHousesForValue(anyInt());
        doReturn(mockTile).when(spy).checkHousesForValue(solution);
        // Execute & Verify
        assertTrue(spy.execute());
        verify(spy, times(9)).checkHousesForValue(anyInt());
        verify(mockSolver).solveTile(mockTile, solution);
    }

    @Test
    public void testExecuteDoesNotHaveSinglePositionFail() {
        // Set up
        TechniqueSinglePosition spy = spy(singlePosition);
        doReturn(null).when(spy).checkHousesForValue(anyInt());
        // Execute & Verify
        assertFalse(spy.execute());
        verify(spy, times(9)).checkHousesForValue(anyInt());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
    }
    //endregion

    //region checkHousesForValue() tests
    @Test
    public void testCheckHousesForValuePass() {
        // Build first house
        doReturn(false).when(mockRow).hasValue(solution);
        doReturn(false).when(mockColumn).hasValue(solution);
        doReturn(false).when(mockZone).hasValue(solution);
        for(int i = 0; i < 8; ++i) {
            Tile tempMockTile = mock(Tile.class);
            House tempMockColumn = mock(House.class);
            House tempMockZone = mock(House.class);
            doReturn(true).when(tempMockColumn).hasValue(solution);
            doReturn(true).when(tempMockZone).hasValue(solution);
            doReturn(mockRow).when(tempMockTile).getRow();
            doReturn(tempMockColumn).when(tempMockTile).getColumn();
            doReturn(tempMockZone).when(tempMockTile).getZone();
            tilesSpy.add(tempMockTile);
        }
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        tilesSpy.add(mockTile);
        final Iterator<Tile> iter = tilesSpy.iterator();
        doReturn(iter).when(mockRow).iterator();
        housesSpy.add(mockRow);

        // Execute
        Tile solutionTile = singlePosition.checkHousesForValue(solution);

        // Verify
        verify(mockRow).iterator();
        verify(tilesSpy).iterator();
        verify(mockRow, times(9)).hasValue(solution);
        verify(mockColumn).hasValue(solution);
        verify(mockZone).hasValue(solution);
        assertNotNull(solutionTile);
        assertEquals(solutionTile, mockTile);
    }

    @Test
    public void testCheckHousesForValueNoPositionFail() {
        // Build first house
        doReturn(true).when(mockRow).hasValue(solution);
        doReturn(true).when(mockColumn).hasValue(solution);
        doReturn(true).when(mockZone).hasValue(solution);
        for(int i = 0; i < 9; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow).when(tempMockTile).getRow();
            doReturn(mockColumn).when(tempMockTile).getColumn();
            doReturn(mockZone).when(tempMockTile).getZone();
            doReturn(i).when(tempMockTile).getIndex();
            tilesSpy.add(tempMockTile);
        }
        final Iterator<Tile> iter = tilesSpy.iterator();
        doReturn(iter).when(mockRow).iterator();
        housesSpy.add(mockRow);

        // Execute
        Tile solutionTile = singlePosition.checkHousesForValue(solution);

        // Verify
        verify(mockRow).iterator();
        verify(tilesSpy).iterator();
        verify(mockRow, times(9)).hasValue(solution);
        verify(mockColumn, never()).hasValue(solution);
        verify(mockZone, never()).hasValue(solution);
        assertNull(solutionTile);
    }

    @Test
    public void testCheckHousesForValueTwoPositionsFail() {
        // Build first house
        doReturn(false).when(mockRow).hasValue(solution);
        doReturn(false).when(mockColumn).hasValue(solution);
        doReturn(false).when(mockZone).hasValue(solution);
        for(int i = 0; i < 7; ++i) {
            Tile tempMockTile = mock(Tile.class);
            House tempMockColumn = mock(House.class);
            House tempMockZone = mock(House.class);
            doReturn(true).when(tempMockColumn).hasValue(solution);
            doReturn(true).when(tempMockZone).hasValue(solution);
            doReturn(mockRow).when(tempMockTile).getRow();
            doReturn(tempMockColumn).when(tempMockTile).getColumn();
            doReturn(tempMockZone).when(tempMockTile).getZone();
            tilesSpy.add(tempMockTile);
        }

        // Add first "successful" Tile
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        tilesSpy.add(mockTile);

        // Add second "successful" Tile
        Tile mockTile2 = mock(Tile.class);
        doReturn(mockRow).when(mockTile2).getRow();
        doReturn(mockColumn).when(mockTile2).getColumn();
        doReturn(mockZone).when(mockTile2).getZone();
        tilesSpy.add(mockTile2);
        final Iterator<Tile> iter = tilesSpy.iterator();
        doReturn(iter).when(mockRow).iterator();
        housesSpy.add(mockRow);

        // Execute
        Tile solutionTile = singlePosition.checkHousesForValue(solution);

        // Verify
        verify(mockRow).iterator();
        verify(tilesSpy).iterator();
        verify(mockRow, times(9)).hasValue(solution);
        verify(mockColumn, times(2)).hasValue(solution);
        verify(mockZone, times(2)).hasValue(solution);
        assertNull(solutionTile);
    }

    @Test
    public void testCheckHousesForValueNegativeValueFail() {
        assertNull(singlePosition.checkHousesForValue(-1));
        verify(housesSpy, never()).iterator();
    }

    @Test
    public void testCheckHousesForValueLargeValueFail() {
        assertNull(singlePosition.checkHousesForValue(1000));
        verify(housesSpy, never()).iterator();
    }
    //endregion
}

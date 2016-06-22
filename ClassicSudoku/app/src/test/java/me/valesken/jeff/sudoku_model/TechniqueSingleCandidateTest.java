package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/1/2016.
 * Last updated on 6/8/2016.
 */
public class TechniqueSingleCandidateTest {

    private int targetValue = 4;
    private Tile[] tiles = new Tile[81];
    private Tile mockTile;
    private Solver mockSolver;
    private TechniqueSingleCandidate singleCandidate;

    //region setup
    @Before
    public void setUp() {
        Board mockBoard = mock(Board.class);
        doReturn(tiles).when(mockBoard).getTiles();
        mockTile = mock(Tile.class);
        mockSolver = mock(Solver.class);
        mockSolver.board = mockBoard;
        singleCandidate = new TechniqueSingleCandidate(mockSolver);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        TechniqueSingleCandidate tsc = new TechniqueSingleCandidate(mockSolver);
        assertNotNull(tsc);
        assertEquals(tsc.solver, mockSolver);
    }
    //endregion

    //region execute() tests
    @Test
    public void testOneCandidatePass_FirstInBoard() {
        TechniqueSingleCandidate spy = spy(singleCandidate);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());

        // Set up Candidate
        doReturn(true).when(spy).tileIsCandidate(mockTile, targetValue);
        tiles[0] = mockTile;

        // Set up the rest of the Tiles
        for (int i = 1; i < 81; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Execute & Verify
        assertTrue(spy.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
    }

    @Test
    public void testOneCandidatePass_LastInBoard() {
        TechniqueSingleCandidate spy = spy(singleCandidate);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());

        // Set up all Tiles except Candidate
        for (int i = 0; i < 80; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Set up Candidate
        doReturn(true).when(spy).tileIsCandidate(mockTile, targetValue);
        tiles[80] = mockTile;

        // Execute & Verify
        assertTrue(spy.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
    }

    @Test
    public void testOneCandidatePass_MiddleOfBoard() {
        TechniqueSingleCandidate spy = spy(singleCandidate);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());

        // Set up half Tiles
        for (int i = 0; i < 40; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Set up Candidate
        doReturn(true).when(spy).tileIsCandidate(mockTile, targetValue);
        tiles[40] = mockTile;

        // Set up remainder of Tiles
        for (int i = 41; i < 80; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Execute & Verify
        assertTrue(spy.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
    }

    @Test
    public void testMultipleCandidatesFail() {
        TechniqueSingleCandidate spy = spy(singleCandidate);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());

        // Set up Candidate
        doReturn(true).when(spy).tileIsCandidate(mockTile, targetValue);
        doReturn(true).when(spy).tileIsCandidate(mockTile, targetValue + 1);
        tiles[0] = mockTile;

        // Set up the rest of the Tiles
        for (int i = 1; i < 81; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Execute & Verify
        assertFalse(spy.execute());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
    }

    @Test
    public void testNoCandidatesFail_HousesHaveAllValues() {
        TechniqueSingleCandidate spy = spy(singleCandidate);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());

        // Set up the Tiles
        for (int i = 0; i < 81; ++i) {
            tiles[i] = mock(Tile.class);
        }

        // Execute & Verify
        assertFalse(spy.execute());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
    }

    @Test
    public void testNoCandidatesFail_TilesAllHaveValues() {
        TechniqueSingleCandidate spy = spy(singleCandidate);

        // Set up the Tiles
        for (int i = 0; i < 81; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(targetValue).when(tempMockTile).getValue();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertFalse(spy.execute());
        verify(spy, never()).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
        for(int i = 0; i < 81; ++i) {
            verify(tiles[i]).getValue();
        }
    }
    //endregion
}

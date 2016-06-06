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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/1/2016.
 * Last updated on 6/1/2016.
 */
public class TechniqueSingleCandidateTest {

    private int targetValue = 4;
    private Tile[] tiles = new Tile[81];
    private House mockRow1, mockRow2, mockColumn1, mockColumn2, mockZone1, mockZone2;
    private Tile mockTile;
    private Solver mockSolver;
    private TechniqueSingleCandidate singleCandidate;

    //region setup
    @Before
    public void setUp() {
        Board mockBoard = mock(Board.class);
        doReturn(tiles).when(mockBoard).getTiles();
        mockRow1 = mock(House.class);
        mockRow2 = mock(House.class);
        mockColumn1 = mock(House.class);
        mockColumn2 = mock(House.class);
        mockZone1 = mock(House.class);
        mockZone2 = mock(House.class);
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

        // Set up Candidate
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(false).when(mockRow1).hasValue(targetValue);
        doReturn(mockRow1).when(mockTile).getRow();
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(false).when(mockColumn1).hasValue(targetValue);
        doReturn(mockColumn1).when(mockTile).getColumn();
        doReturn(true).when(mockZone1).hasValue(anyInt());
        doReturn(false).when(mockZone1).hasValue(targetValue);
        doReturn(mockZone1).when(mockTile).getZone();
        tiles[0] = mockTile;

        // Set up the rest of the Tiles
        doReturn(true).when(mockRow2).hasValue(anyInt());
        doReturn(true).when(mockColumn2).hasValue(anyInt());
        doReturn(true).when(mockZone2).hasValue(anyInt());
        for (int i = 1; i < 81; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow2).when(tempMockTile).getRow();
            doReturn(mockColumn2).when(tempMockTile).getColumn();
            doReturn(mockZone2).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertTrue(singleCandidate.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
        verify(mockRow1, times(9)).hasValue(anyInt());
        verify(mockColumn1).hasValue(anyInt()); // Will only be called once because row will be False first
        verify(mockZone1).hasValue(anyInt()); // Will only be called once because row will be False first
        verify(mockRow2, never()).hasValue(anyInt());
        verify(mockColumn2, never()).hasValue(anyInt());
        verify(mockZone2, never()).hasValue(anyInt());
    }

    @Test
    public void testOneCandidatePass_LastInBoard() {
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(true).when(mockZone1).hasValue(anyInt());

        // Set up all Tiles except Candidate
        for (int i = 0; i < 80; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow1).when(tempMockTile).getRow();
            doReturn(mockColumn1).when(tempMockTile).getColumn();
            doReturn(mockZone1).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Set up Candidate
        doReturn(true).when(mockRow2).hasValue(anyInt());
        doReturn(false).when(mockRow2).hasValue(targetValue);
        doReturn(true).when(mockColumn2).hasValue(anyInt());
        doReturn(false).when(mockColumn2).hasValue(targetValue);
        doReturn(true).when(mockZone2).hasValue(anyInt());
        doReturn(false).when(mockZone2).hasValue(targetValue);
        doReturn(mockRow2).when(mockTile).getRow();
        doReturn(mockColumn2).when(mockTile).getColumn();
        doReturn(mockZone2).when(mockTile).getZone();
        tiles[80] = mockTile;

        // Execute & Verify
        assertTrue(singleCandidate.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
        verify(mockRow1, times(9 * 80)).hasValue(anyInt());
        verify(mockColumn1, never()).hasValue(anyInt()); // Will never be called because row will be True first
        verify(mockZone1, never()).hasValue(anyInt()); // Will never be called because row will be True first
        verify(mockRow2, times(9)).hasValue(anyInt());
        verify(mockColumn2).hasValue(anyInt()); // Will only be called once because row will be True first
        verify(mockZone2).hasValue(anyInt()); // Will only be called once because row will be True first
    }

    @Test
    public void testOneCandidatePass_MiddleOfBoard() {
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(true).when(mockZone1).hasValue(anyInt());

        // Set up half Tiles
        for (int i = 0; i < 40; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow1).when(tempMockTile).getRow();
            doReturn(mockColumn1).when(tempMockTile).getColumn();
            doReturn(mockZone1).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Set up Candidate
        doReturn(true).when(mockRow2).hasValue(anyInt());
        doReturn(false).when(mockRow2).hasValue(targetValue);
        doReturn(true).when(mockColumn2).hasValue(anyInt());
        doReturn(false).when(mockColumn2).hasValue(targetValue);
        doReturn(true).when(mockZone2).hasValue(anyInt());
        doReturn(false).when(mockZone2).hasValue(targetValue);
        doReturn(mockRow2).when(mockTile).getRow();
        doReturn(mockColumn2).when(mockTile).getColumn();
        doReturn(mockZone2).when(mockTile).getZone();
        tiles[40] = mockTile;

        // Set up remainder of Tiles
        for (int i = 41; i < 80; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow1).when(tempMockTile).getRow();
            doReturn(mockColumn1).when(tempMockTile).getColumn();
            doReturn(mockZone1).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertTrue(singleCandidate.execute());
        verify(mockSolver).solveTile(mockTile, targetValue);
        verify(mockRow1, times(9 * 40)).hasValue(anyInt()); // Only first half of Tiles will be examined
        verify(mockColumn1, never()).hasValue(anyInt()); // Will never be called because row will be True first
        verify(mockZone1, never()).hasValue(anyInt()); // Will never be called because row will be True first
        verify(mockRow2, times(9)).hasValue(anyInt());
        verify(mockColumn2).hasValue(anyInt()); // Will only be called once because row will be True first
        verify(mockZone2).hasValue(anyInt()); // Will only be called once because row will be True first
    }

    @Test
    public void testMultipleCandidatesFail() {

        // Set up Candidate
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(false).when(mockRow1).hasValue(targetValue);
        doReturn(false).when(mockRow1).hasValue(targetValue + 1);
        doReturn(mockRow1).when(mockTile).getRow();
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(false).when(mockColumn1).hasValue(targetValue);
        doReturn(false).when(mockColumn1).hasValue(targetValue + 1);
        doReturn(mockColumn1).when(mockTile).getColumn();
        doReturn(true).when(mockZone1).hasValue(anyInt());
        doReturn(false).when(mockZone1).hasValue(targetValue);
        doReturn(false).when(mockZone1).hasValue(targetValue + 1);
        doReturn(mockZone1).when(mockTile).getZone();
        tiles[0] = mockTile;

        // Set up the rest of the Tiles
        doReturn(true).when(mockRow2).hasValue(anyInt());
        doReturn(true).when(mockColumn2).hasValue(anyInt());
        doReturn(true).when(mockZone2).hasValue(anyInt());
        for (int i = 1; i < 81; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow2).when(tempMockTile).getRow();
            doReturn(mockColumn2).when(tempMockTile).getColumn();
            doReturn(mockZone2).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertFalse(singleCandidate.execute());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
        verify(mockRow1, times(5)).hasValue(anyInt()); // Will only be called 5 times because will abort after second
                                                       // candidate value is detected
        verify(mockColumn1, times(2)).hasValue(anyInt()); // Will only be called twice because row will be False first
        verify(mockZone1, times(2)).hasValue(anyInt()); // Will only be called once because row will be False first
        verify(mockRow2, times(9 * 80)).hasValue(anyInt());
        verify(mockColumn2, never()).hasValue(anyInt());
        verify(mockZone2, never()).hasValue(anyInt());
    }

    @Test
    public void testNoCandidatesFail_HousesHaveAllValues() {

        // Set up the rest of the Tiles
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(true).when(mockZone1).hasValue(anyInt());
        for (int i = 0; i < 81; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(mockRow1).when(tempMockTile).getRow();
            doReturn(mockColumn1).when(tempMockTile).getColumn();
            doReturn(mockZone1).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertFalse(singleCandidate.execute());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
        verify(mockRow1, times(9 * 81)).hasValue(anyInt());
        verify(mockColumn1, never()).hasValue(anyInt());
        verify(mockZone1, never()).hasValue(anyInt());
    }

    @Test
    public void testNoCandidatesFail_TilesAllHaveValues() {

        // Set up the rest of the Tiles
        doReturn(true).when(mockRow1).hasValue(anyInt());
        doReturn(true).when(mockColumn1).hasValue(anyInt());
        doReturn(true).when(mockZone1).hasValue(anyInt());
        for (int i = 0; i < 81; ++i) {
            Tile tempMockTile = mock(Tile.class);
            doReturn(targetValue).when(tempMockTile).getValue();
            doReturn(mockRow1).when(tempMockTile).getRow();
            doReturn(mockColumn1).when(tempMockTile).getColumn();
            doReturn(mockZone1).when(tempMockTile).getZone();
            tiles[i] = tempMockTile;
        }

        // Execute & Verify
        assertFalse(singleCandidate.execute());
        verify(mockSolver, never()).solveTile(argThat(is(any(Tile.class))), anyInt());
        for(int i = 0; i < 81; ++i) {
            verify(tiles[i]).getValue();
        }
        verify(mockRow1, never()).hasValue(anyInt());
        verify(mockColumn1, never()).hasValue(anyInt());
        verify(mockZone1, never()).hasValue(anyInt());
    }
    //endregion
}

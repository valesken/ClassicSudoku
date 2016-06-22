package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/14/2016.
 * Last updated on 6/21/2016.
 */
public class TechniqueCandidateLineTest {

    private int targetValue = 4;
    private Tile[] tiles = new Tile[81];
    private Tile mockTile;
    private Tile mockTile2;
    private Solver mockSolver;
    private TechniqueCandidateLine candidateLine;

    //region setup
    @Before
    public void setUp() {
        Board mockBoard = mock(Board.class);
        doReturn(tiles).when(mockBoard).getTiles();
        mockTile = mock(Tile.class);
        mockTile2 = mock(Tile.class);
        mockSolver = mock(Solver.class);
        mockSolver.board = mockBoard;
        candidateLine = new TechniqueCandidateLine(mockSolver);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        TechniqueCandidateLine tcl = new TechniqueCandidateLine(mockSolver);
        assertNotNull(tcl);
        assertEquals(tcl.solver, mockSolver);
    }
    //endregion

    //region execute() tests
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute_RowTrue() {
        // Set up Zone
        TechniqueCandidateLine spy = spy(candidateLine);
        House mockZone = mock(House.class);
        List<House> zones = new LinkedList<>();
        zones.add(mockZone);
        mockSolver.zones = zones;
        // Set up Candidates
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        doReturn(new HashSet<>()).when(spy).findCandidates(argThat(is(any(House.class))), anyInt());
        doReturn(candidates).when(spy).findCandidates(mockZone, targetValue);
        // Set up spy interactions
        House mockCommonHouse = mock(House.class);
        doReturn(null).when(spy).getCommonRowOrColumn(argThat(is(any(Set.class))), anyBoolean());
        doReturn(mockCommonHouse).when(spy).getCommonRowOrColumn(candidates, true);
        doReturn(false).when(spy).isNewLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        doReturn(true).when(spy).isNewLine(mockCommonHouse, candidates, targetValue);
        doNothing().when(spy).setCandidateLine(mockCommonHouse, candidates, targetValue);

        // Execute
        boolean result = spy.execute();

        // Verify
        verify(spy, times(targetValue)).findCandidates(argThat(is(equalTo(mockZone))), anyInt());
        verify(spy).isNewLine(mockCommonHouse, candidates, targetValue);
        verify(spy).setCandidateLine(mockCommonHouse, candidates, targetValue);
        assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute_ColumnTrue() {
        // Set up Zone
        TechniqueCandidateLine spy = spy(candidateLine);
        House mockZone = mock(House.class);
        List<House> zones = new LinkedList<>();
        zones.add(mockZone);
        mockSolver.zones = zones;
        // Set up Candidates
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        doReturn(new HashSet<>()).when(spy).findCandidates(argThat(is(any(House.class))), anyInt());
        doReturn(candidates).when(spy).findCandidates(mockZone, targetValue);
        // Set up spy interactions
        House mockCommonHouse = mock(House.class);
        doReturn(null).when(spy).getCommonRowOrColumn(argThat(is(any(Set.class))), anyBoolean());
        doReturn(mockCommonHouse).when(spy).getCommonRowOrColumn(candidates, false);
        doReturn(false).when(spy).isNewLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        doReturn(true).when(spy).isNewLine(mockCommonHouse, candidates, targetValue);
        doNothing().when(spy).setCandidateLine(mockCommonHouse, candidates, targetValue);

        // Execute
        boolean result = spy.execute();

        // Verify
        verify(spy, times(targetValue)).findCandidates(argThat(is(equalTo(mockZone))), anyInt());
        verify(spy).isNewLine(mockCommonHouse, candidates, targetValue);
        verify(spy).setCandidateLine(mockCommonHouse, candidates, targetValue);
        assertTrue(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute_NoCandidatesFalse() {
        // Set up
        TechniqueCandidateLine spy = spy(candidateLine);
        House mockZone = mock(House.class);
        List<House> zones = new LinkedList<>();
        zones.add(mockZone);
        mockSolver.zones = zones;
        doReturn(new HashSet<>()).when(spy).findCandidates(argThat(is(any(House.class))), anyInt());

        // Execute
        boolean result = spy.execute();

        // Verify
        verify(spy, times(9)).findCandidates(argThat(is(equalTo(mockZone))), anyInt());
        verify(spy, never()).isNewLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        verify(spy, never()).setCandidateLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecute_TooManyCandidatesFalse() {
        // Set up Zone
        TechniqueCandidateLine spy = spy(candidateLine);
        House mockZone = mock(House.class);
        List<House> zones = new LinkedList<>();
        zones.add(mockZone);
        mockSolver.zones = zones;
        // Set up Candidates
        Set<Tile> candidates = new HashSet<>();
        for(int i = 0; i < 4; ++i) {
            candidates.add(mock(Tile.class));
        }
        doReturn(new HashSet<>()).when(spy).findCandidates(argThat(is(any(House.class))), anyInt());
        doReturn(candidates).when(spy).findCandidates(mockZone, targetValue);

        // Execute
        boolean result = spy.execute();

        // Verify
        verify(spy, times(9)).findCandidates(argThat(is(equalTo(mockZone))), anyInt());
        verify(spy, never()).isNewLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        verify(spy, never()).setCandidateLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        assertFalse(result);
    }
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute_CandidatesNotInLineFalse() {
        // Set up Zone
        TechniqueCandidateLine spy = spy(candidateLine);
        House mockZone = mock(House.class);
        List<House> zones = new LinkedList<>();
        zones.add(mockZone);
        mockSolver.zones = zones;
        // Set up Candidates
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        doReturn(new HashSet<>()).when(spy).findCandidates(argThat(is(any(House.class))), anyInt());
        doReturn(candidates).when(spy).findCandidates(mockZone, targetValue);
        // Set up spy interactions
        House mockCommonHouse = mock(House.class);
        doReturn(mockCommonHouse).when(spy).getCommonRowOrColumn(argThat(is(any(Set.class))), anyBoolean());
        doReturn(false).when(spy).isNewLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());

        // Execute
        boolean result = spy.execute();

        // Verify
        verify(spy, times(9)).findCandidates(argThat(is(equalTo(mockZone))), anyInt());
        verify(spy, times(2)).isNewLine(mockCommonHouse, candidates, targetValue); // 1x see if row, 1x see if column
        verify(spy, never()).setCandidateLine(argThat(is(any(House.class))), argThat(is(any(Set.class))), anyInt());
        assertFalse(result);
    }
    //endregion

    //region findCandidates() tests
    @Test
    public void testFindCandidates_Pass() {
        // Set up
        TechniqueCandidateLine spy = spy(candidateLine);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());
        doReturn(true).when(spy).tileIsCandidate(argThat(either(is(equalTo(mockTile))).or(is(equalTo(mockTile2)))),
                intThat(is(equalTo(targetValue))));
        List<Tile> tiles = new LinkedList<>();
        tiles.add(mockTile);
        tiles.add(mockTile2);
        for (int i = 2; i < 9; ++i) {
            tiles.add(mock(Tile.class));
        }
        House mockZone = mock(House.class);
        doReturn(tiles.iterator()).when(mockZone).iterator();

        // Execute
        Set<Tile> candidates = spy.findCandidates(mockZone, targetValue);

        // Verify
        assertNotNull(candidates);
        assertThat(candidates, hasItem(mockTile));
        assertThat(candidates, hasItem(mockTile2));
    }

    @Test
    public void testFindCandidates_EmptySet_Pass() {
        // Set up
        TechniqueCandidateLine spy = spy(candidateLine);
        doReturn(false).when(spy).tileIsCandidate(argThat(is(any(Tile.class))), anyInt());
        List<Tile> tiles = new LinkedList<>();
        for (int i = 0; i < 9; ++i) {
            tiles.add(mock(Tile.class));
        }
        House mockZone = mock(House.class);
        doReturn(tiles.iterator()).when(mockZone).iterator();

        // Execute
        Set<Tile> candidates = spy.findCandidates(mockZone, targetValue);

        // Verify
        assertNotNull(candidates);
        assertEquals(0, candidates.size());
    }
    //endregion

    //region getCommonRowOrColumn() tests
    @Test
    public void testGetCommonRowOrColumn_OneCandidateInRow_Pass() {
        // Set up
        House mockRow = mock(House.class);
        doReturn(mockRow).when(mockTile).getRow();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);

        // Verify
        assertNotNull(commonRow);
        assertEquals(commonRow, mockRow);
    }

    @Test
    public void testGetCommonRowOrColumn_OneCandidateInColumn_Pass() {
        // Set up
        House mockColumn = mock(House.class);
        doReturn(mockColumn).when(mockTile).getColumn();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);

        // Execute
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNotNull(commonColumn);
        assertEquals(commonColumn, mockColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_TwoCandidatesInRow_Pass() {
        // Set up
        House mockRow = mock(House.class);
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockRow).when(mockTile2).getRow();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);

        // Verify
        assertNotNull(commonRow);
        assertEquals(commonRow, mockRow);
    }

    @Test
    public void testGetCommonRowOrColumn_TwoCandidatesInColumn_Pass() {
        // Set up
        House mockColumn = mock(House.class);
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockColumn).when(mockTile2).getColumn();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);

        // Execute
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNotNull(commonColumn);
        assertEquals(commonColumn, mockColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_ThreeCandidatesInRow_Pass() {
        // Set up
        House mockRow = mock(House.class);
        Tile mockTile3 = mock(Tile.class);
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockRow).when(mockTile2).getRow();
        doReturn(mockRow).when(mockTile3).getRow();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        candidates.add(mockTile3);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);

        // Verify
        assertNotNull(commonRow);
        assertEquals(commonRow, mockRow);
    }

    @Test
    public void testGetCommonRowOrColumn_ThreeCandidatesInColumn_Pass() {
        // Set up
        House mockColumn = mock(House.class);
        Tile mockTile3 = mock(Tile.class);
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockColumn).when(mockTile2).getColumn();
        doReturn(mockColumn).when(mockTile3).getColumn();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        candidates.add(mockTile3);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNotNull(commonRow);
        assertEquals(commonRow, mockColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_TwoCandidatesNotSameRow_Fail() {
        // Set up
        doReturn(mock(House.class)).when(mockTile).getRow();
        doReturn(mock(House.class)).when(mockTile2).getRow();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);

        // Verify
        assertNull(commonRow);
    }

    @Test
    public void testGetCommonRowOrColumn_TwoCandidatesNotSameColumn_Fail() {
        // Set up
        doReturn(mock(House.class)).when(mockTile).getColumn();
        doReturn(mock(House.class)).when(mockTile2).getColumn();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);

        // Execute
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNull(commonColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_ThreeCandidatesNotSameRow_Fail() {
        // Set up
        House mockRow = mock(House.class);
        Tile mockTile3 = mock(Tile.class);
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockRow).when(mockTile2).getRow();
        doReturn(mock(House.class)).when(mockTile3).getRow();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        candidates.add(mockTile3);

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);

        // Verify
        assertNull(commonRow);
    }

    @Test
    public void testGetCommonRowOrColumn_ThreeCandidatesNotSameColumn_Fail() {
        // Set up
        House mockColumn = mock(House.class);
        Tile mockTile3 = mock(Tile.class);
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockColumn).when(mockTile2).getColumn();
        doReturn(mock(House.class)).when(mockTile3).getColumn();
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        candidates.add(mockTile3);

        // Execute
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNull(commonColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_FourOrMoreCandidates_Fail() {
        // Set up
        Set<Tile> candidates = new HashSet<>();
        for(int i = 0; i < 4; ++i) {
            candidates.add(mock(Tile.class));
        }

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNull(commonRow);
        assertNull(commonColumn);
    }

    @Test
    public void testGetCommonRowOrColumn_ZeroCandidates_Fail() {
        // Set up
        Set<Tile> candidates = new HashSet<>();

        // Execute
        House commonRow = candidateLine.getCommonRowOrColumn(candidates, true);
        House commonColumn = candidateLine.getCommonRowOrColumn(candidates, false);

        // Verify
        assertNull(commonRow);
        assertNull(commonColumn);
    }
    //endregion

    //region isNewLine() tests
    @Test
    public void testIsNewLine_candidateNotAssignedInLine_True() {
        // Set up
        House mockLine = mock(House.class);
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mock(Tile.class));
        doReturn(false).when(mockLine).hasAssignedValueToTile(anyInt(), argThat(is(any(Tile.class))));
        doReturn(true).when(mockLine).hasAssignedValueToTile(targetValue, mockTile);

        // Execute
        boolean result = candidateLine.isNewLine(mockLine, candidates, targetValue);

        // Verify
        assertTrue(result);
        verify(mockLine, never()).getValueTiles();
    }

    @Test
    public void testIsNewLine_tileInLineNeedsToBeCleared_True() {
        // Set up
        House mockLine = mock(House.class);
        List<Tile> lineTiles = new LinkedList<>();
        lineTiles.add(mockTile);
        lineTiles.add(mockTile2);
        lineTiles.add(mock(Tile.class));
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        doReturn(true).when(mockLine).hasAssignedValueToTile(intThat(is(equalTo(targetValue))), argThat(isIn(candidates)));
        doReturn(lineTiles).when(mockLine).getValueTiles();

        // Execute
        boolean result = candidateLine.isNewLine(mockLine, candidates, targetValue);

        // Verify
        assertTrue(result);
        verify(mockLine).getValueTiles();
    }

    @Test
    public void testIsNewLine_False() {
        // Set up
        House mockLine = mock(House.class);
        List<Tile> lineTiles = new LinkedList<>();
        lineTiles.add(mockTile);
        lineTiles.add(mockTile2);
        Set<Tile> candidates = new HashSet<>();
        candidates.add(mockTile);
        candidates.add(mockTile2);
        doReturn(true).when(mockLine).hasAssignedValueToTile(intThat(is(equalTo(targetValue))), argThat(isIn(candidates)));
        doReturn(lineTiles).when(mockLine).getValueTiles();

        // Execute
        boolean result = candidateLine.isNewLine(mockLine, candidates, targetValue);

        // Verify
        assertFalse(result);
        verify(mockLine).getValueTiles();
    }
    //endregion

    //region setCandidateLine() tests
    @Test
    public void testSetCandidateLine_Pass() {
        // Set Up
        House mockLine = mock(House.class);
        Set<Tile> candidates = new HashSet<>();
        Set<Tile> candidatesSpy = spy(candidates);
        candidatesSpy.add(mockTile);
        candidatesSpy.add(mockTile2);

        // Execute
        candidateLine.setCandidateLine(mockLine, candidatesSpy, targetValue);

        // Verify
        verify(candidatesSpy).iterator();
        InOrder order = inOrder(mockLine);
        order.verify(mockLine).clearValueInHouse(targetValue);
        order.verify(mockLine, times(2)).setValueInHouse(anyInt(), anyBoolean(), argThat(is(any(Tile.class))));
        verify(mockLine).setValueInHouse(targetValue, true, mockTile);
        verify(mockLine).setValueInHouse(targetValue, true, mockTile2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetCandidateLine_NullHouse_Ignored() {
        // Set Up
        Set<Tile> candidates = mock(Set.class);

        // Execute
        candidateLine.setCandidateLine(null, candidates, targetValue);

        // Verify
        verify(candidates, never()).iterator();
    }

    @Test
    public void testSetCandidateLine_NullCandidates_Ignored() {
        // Set Up
        House mockLine = mock(House.class);

        // Execute
        candidateLine.setCandidateLine(mockLine, null, targetValue);

        // Verify
        verify(mockLine, never()).clearValueInHouse(anyInt());
        verify(mockLine, never()).setValueInHouse(anyInt(), anyBoolean(), argThat(is(any(Tile.class))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetCandidateLine_LowValue_Ignored() {
        // Set Up
        House mockLine = mock(House.class);
        Set<Tile> candidates = mock(Set.class);

        // Execute
        candidateLine.setCandidateLine(mockLine, candidates, 0);

        // Verify
        verify(mockLine, never()).clearValueInHouse(anyInt());
        verify(mockLine, never()).setValueInHouse(anyInt(), anyBoolean(), argThat(is(any(Tile.class))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetCandidateLine_HighValue_Ignored() {
        // Set Up
        House mockLine = mock(House.class);
        Set<Tile> candidates = mock(Set.class);

        // Execute
        candidateLine.setCandidateLine(mockLine, candidates, 10);

        // Verify
        verify(mockLine, never()).clearValueInHouse(anyInt());
        verify(mockLine, never()).setValueInHouse(anyInt(), anyBoolean(), argThat(is(any(Tile.class))));
    }
    //endregion
}

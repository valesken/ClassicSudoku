package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/5/2016.
 * Last updated on 6/5/2016.
 */
public class SolverTest {

    private Solver solver;
    private Board mockBoard;

    //region setup
    @Before
    public void setUp() {
        mockBoard = mock(Board.class);
        doReturn(new House[9]).when(mockBoard).getRows();
        doReturn(new House[9]).when(mockBoard).getColumns();
        doReturn(new House[9]).when(mockBoard).getZones();
        solver = new Solver(mockBoard);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructor() {
        // Already constructed in setUp, so just verify results here
        assertEquals(solver.board, mockBoard);
        verify(mockBoard).getRows();
        verify(mockBoard).getColumns();
        verify(mockBoard).getZones();
        assertNotNull(solver.houses);
        assertNull(solver.techniques);
    }
    //endregion

    //region test solve()
    @Test
    public void testSolveOnFirstAttemptPass() {
        // Set up
        Solver spy = spy(solver);
        int difficulty = 1;
        doNothing().when(spy).setTechniques(anyInt());
        doReturn(true).when(spy).isSolvable();
        // Execute
        spy.solve(difficulty);
        // Verify
        verify(spy).setTechniques(difficulty);
        verify(spy).isSolvable();
        verify(mockBoard, never()).useHint();
        verify(mockBoard, never()).clearBoard();
    }

    @Test
    public void testSolveOnLaterAttemptPass() {
        // Set up
        Solver spy = spy(solver);
        int difficulty = 1;
        doNothing().when(spy).setTechniques(anyInt());
        doReturn(false).doReturn(true).when(spy).isSolvable();
        // Execute
        spy.solve(difficulty);
        // Verify
        verify(spy).setTechniques(difficulty);
        verify(spy, times(2)).isSolvable();
        verify(mockBoard).useHint();
        verify(mockBoard).clearBoard();
    }

    @Test
    public void testSolveDifficultyTooLowFail() {
        Solver spy = spy(solver);
        spy.solve(0);
        verify(spy, never()).setTechniques(anyInt());
    }

    @Test
    public void testSolveDifficultyTooHighFail() {
        Solver spy = spy(solver);
        spy.solve(4);
        verify(spy, never()).setTechniques(anyInt());
    }
    //endregion

    //region test setTechniques()
    @Test
    public void testSetTechniquesEasyPass() {
        // Set up
        Solver spy = spy(solver);
        int difficulty = 1; // Easy
        // Execute
        spy.setTechniques(difficulty);
        // Verify
        assertNotNull(spy.techniques);
        assertEquals(3, spy.techniques.size());
        // Verify all Easy techniques present
        Technique[] techniques = spy.techniques.toArray(new Technique[spy.techniques.size()]);
        Class[] techniqueClasses = { TechniqueRemainder.class, TechniqueSingleCandidate.class,
                TechniqueSinglePosition.class };
        for(Class clazz : techniqueClasses) {
            boolean found = false;
            for(Technique technique : techniques) {
                if (technique.getClass().equals(clazz)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void testSetTechniquesMediumPass() {
        // Set up
        Solver spy = spy(solver);
        int difficulty = 2; // Medium
        // Execute
        spy.setTechniques(difficulty);
        // Verify
        assertNotNull(spy.techniques);
        assertEquals(6, spy.techniques.size());
        // Verify all Medium techniques present
        Technique[] techniques = spy.techniques.toArray(new Technique[spy.techniques.size()]);
        Class[] techniqueClasses = {
                TechniqueRemainder.class,
                TechniqueSingleCandidate.class,
                TechniqueSinglePosition.class,
                TechniqueCandidateLine.class,
                TechniqueDoublePair.class,
                TechniqueMultiLine.class
        };
        for(Class clazz : techniqueClasses) {
            boolean found = false;
            for(Technique technique : techniques) {
                if (technique.getClass().equals(clazz)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void testSetTechniquesHardPass() {
        // Set up
        Solver spy = spy(solver);
        int difficulty = 3; // Hard
        // Execute
        spy.setTechniques(difficulty);
        // Verify
        assertNotNull(spy.techniques);
        assertEquals(8, spy.techniques.size());
        // Verify all Hard techniques present
        Technique[] techniques = spy.techniques.toArray(new Technique[spy.techniques.size()]);
        Class[] techniqueClasses = {
                TechniqueRemainder.class,
                TechniqueSingleCandidate.class,
                TechniqueSinglePosition.class,
                TechniqueCandidateLine.class,
                TechniqueDoublePair.class,
                TechniqueMultiLine.class,
                TechniqueNakedPairsAndTriples.class,
                TechniqueHiddenPairsAndTriples.class
        };
        for(Class clazz : techniqueClasses) {
            boolean found = false;
            for(Technique technique : techniques) {
                if (technique.getClass().equals(clazz)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void testSetTechniquesDifficultyTooLowFail() {
        Solver spy = spy(solver);
        spy.setTechniques(0);
        assertNull(spy.techniques);
    }

    @Test
    public void testSetTechniquesDifficultyTooHighFail() {
        Solver spy = spy(solver);
        spy.setTechniques(4);
        assertNull(spy.techniques);
    }
    //endregion

    //region test isSolvable()
    @Test
    public void testIsSolvableAlreadySolvedPass() {
        // Set up
        doReturn(true).when(mockBoard).isGameOver();
        solver.techniques = new HashSet<>();
        for (int i = 0; i < 3; ++i) {
            Technique tempMockTechnique = mock(Technique.class);
            doReturn(false).when(tempMockTechnique).execute();
            solver.techniques.add(tempMockTechnique);
        }
        // Execute & Verify
        assertTrue(solver.isSolvable());
        for (Technique technique : solver.techniques) {
            verify(technique).execute();
        }
        verify(mockBoard).isGameOver();
    }

    @Test
    public void testIsSolvableExecuteOncePass() {
        // Set up
        doReturn(true).when(mockBoard).isGameOver();
        solver.techniques = new HashSet<>();
        for (int i = 0; i < 2; ++i) {
            Technique tempMockTechnique = mock(Technique.class);
            doReturn(false).when(tempMockTechnique).execute();
            solver.techniques.add(tempMockTechnique);
        }
        Technique mockTechnique = mock(Technique.class);
        doReturn(true).doReturn(false).when(mockTechnique).execute();
        solver.techniques.add(mockTechnique);
        // Execute & Verify
        assertTrue(solver.isSolvable());
        for (Technique technique : solver.techniques) {
            verify(technique, atLeast(1)).execute();
        }
        verify(mockTechnique, times(2)).execute();
        verify(mockBoard).isGameOver();
    }
    //endregion

    //region test solveTile()
    @Test
    public void testSolveTilePass() {
        // Set up
        int index = 31;
        int value = 4;
        House mockRow = mock(House.class);
        House mockColumn = mock(House.class);
        House mockZone = mock(House.class);
        Tile mockTile = mock(Tile.class);
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(index).when(mockTile).getIndex();
        // Execute
        solver.solveTile(mockTile, value);
        // Verify
        verify(mockRow).clearValueInHouse(value);
        verify(mockColumn).clearValueInHouse(value);
        verify(mockZone).clearValueInHouse(value);
        verify(mockBoard).updateTile(index, value);
    }

    @Test
    public void testSolveTileNullTileIgnored() {
        solver.solveTile(null, 4);
        verify(mockBoard, never()).updateTile(anyInt(), anyInt());
    }

    @Test
    public void testSolveTileLowValueIgnored() {
        solver.solveTile(mock(Tile.class), 0);
        verify(mockBoard, never()).updateTile(anyInt(), anyInt());
    }

    @Test
    public void testSolveTileHighValueIgnored() {
        solver.solveTile(mock(Tile.class), 10);
        verify(mockBoard, never()).updateTile(anyInt(), anyInt());
    }
    //endregion
}

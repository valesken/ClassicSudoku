package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by jeff on 6/8/2016.
 * Last updated on 6/9/2016.
 */
public class TechniqueTest {

    private int targetValue = 4;
    private Technique technique;
    private Tile mockTile;
    private House mockRow, mockColumn, mockZone;

    //region set up
    @Before
    public void setUp() {
        technique = mock(Technique.class, Mockito.CALLS_REAL_METHODS);
        mockTile = mock(Tile.class);
        mockRow = mock(House.class);
        mockColumn = mock(House.class);
        mockZone = mock(House.class);
    }
    //endregion

    //region tileIsCandidate() tests
    @Test
    public void testTileIsCandidate_ValueIsAssignedToTileInRow_Pass() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockRow).hasAssignedValueToTile(targetValue, mockTile);

        // Execute & Verify
        assertTrue(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn, never()).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone, never()).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow, never()).hasValue(targetValue);
        verify(mockColumn, never()).hasValue(targetValue);
        verify(mockZone, never()).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueIsAssignedToTileInColumn_Pass() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockColumn).hasAssignedValueToTile(targetValue, mockTile);

        // Execute & Verify
        assertTrue(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone, never()).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow, never()).hasValue(targetValue);
        verify(mockColumn, never()).hasValue(targetValue);
        verify(mockZone, never()).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueIsAssignedToTileInZone_Pass() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockZone).hasAssignedValueToTile(targetValue, mockTile);

        // Execute & Verify
        assertTrue(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow, never()).hasValue(targetValue);
        verify(mockColumn, never()).hasValue(targetValue);
        verify(mockZone, never()).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueNotInHouses_Pass() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();

        // Execute & Verify
        assertTrue(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow).hasValue(targetValue);
        verify(mockColumn).hasValue(targetValue);
        verify(mockZone).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueInRow_Fail() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockRow).hasValue(targetValue);

        // Execute & Verify
        assertFalse(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow).hasValue(targetValue);
        verify(mockColumn, never()).hasValue(targetValue);
        verify(mockZone, never()).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueInColumn_Fail() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockColumn).hasValue(targetValue);

        // Execute & Verify
        assertFalse(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow).hasValue(targetValue);
        verify(mockColumn).hasValue(targetValue);
        verify(mockZone, never()).hasValue(targetValue);
    }

    @Test
    public void testTileIsCandidate_ValueInZone_Fail() {
        // Set up
        doReturn(mockRow).when(mockTile).getRow();
        doReturn(mockColumn).when(mockTile).getColumn();
        doReturn(mockZone).when(mockTile).getZone();
        doReturn(true).when(mockZone).hasValue(targetValue);

        // Execute & Verify
        assertFalse(technique.tileIsCandidate(mockTile, targetValue));
        verify(mockRow).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockColumn).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockZone).hasAssignedValueToTile(targetValue, mockTile);
        verify(mockRow).hasValue(targetValue);
        verify(mockColumn).hasValue(targetValue);
        verify(mockZone).hasValue(targetValue);
    }
    //endregion
}

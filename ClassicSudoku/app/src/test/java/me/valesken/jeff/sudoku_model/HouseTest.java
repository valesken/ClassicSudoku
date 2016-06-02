package me.valesken.jeff.sudoku_model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jeff on 1/28/2016.
 * Last Updated on 2/8/2016.
 */
public class HouseTest {

    private House house;
    private Tile mockedTile;
    private Tile mockedTile2;
    private int houseSize = 9;
    private int houseIndex = 0;

    //region setup
    @Before
    public void setUp() {
        house = new House(houseSize, houseIndex);
        mockedTile = mock(Tile.class);
        mockedTile2 = mock(Tile.class);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        assertEquals(houseSize, house.houseSize);
        assertEquals(houseIndex, house.houseIndex);
        assertNotNull(house.members);
        assertEquals(0, house.members.size());
        assertNotNull(house.valueToOwnersMap);
        assertEquals(9, house.valueToOwnersMap.length);
        for (HashSet set : house.valueToOwnersMap) {
            assertNotNull(set);
            assertEquals(0, set.size());
        }
    }
    //endregion

    //region addMember() tests
    @Test
    public void testAddFirstMemberPass() {
        house.addMember(mockedTile);
        assertEquals(1, house.members.size());
        assertEquals(mockedTile, house.members.get(0));
    }

    @Test
    public void testCanAddNineMembersPass() {
        Tile[] tiles = new Tile[houseSize];
        for (int i = 0; i < houseSize; ++i) {
            tiles[i] = mock(Tile.class);
            house.addMember(tiles[i]);
        }
        assertEquals(9, house.members.size());
        for (int i = 0; i < houseSize; ++i) {
            assertEquals(tiles[i], house.members.get(i));
        }
    }

    @Test
    public void testCannotAddMoreThanNineMembersFail() {
        Tile[] tiles = new Tile[houseSize + 1];
        for (int i = 0; i < (houseSize + 1); ++i) {
            tiles[i] = mock(Tile.class);
            house.addMember(tiles[i]);
        }
        assertEquals(9, house.members.size());
        for (int i = 0; i < houseSize; ++i) {
            assertEquals(tiles[i], house.members.get(i));
        }
        assertFalse(house.members.contains(tiles[houseSize]));
    }

    @Test
    public void testCannotAddSameTileTwiceFail() {
        house.addMember(mockedTile);
        house.addMember(mockedTile);
        assertEquals(1, house.members.size());
        assertEquals(mockedTile, house.members.get(0));
    }
    //endregion

    //region setValueInHouse() tests
    @Test
    public void testSetValueNoClaimPass() {
        assertTrue(house.setValueInHouse(1, true, mockedTile));
        assertEquals(1, house.valueToOwnersMap[0].size());
        assertTrue(house.valueToOwnersMap[0].contains(mockedTile));
    }

    @Test
    public void testSetValueOneClaimPass() {
        Tile mockedTile2 = mock(Tile.class);
        house.valueToOwnersMap[0].add(mockedTile);
        assertTrue(house.setValueInHouse(1, true, mockedTile2));
        assertEquals(2, house.valueToOwnersMap[0].size());
        assertTrue(house.valueToOwnersMap[0].contains(mockedTile));
        assertTrue(house.valueToOwnersMap[0].contains(mockedTile2));
    }

    @Test
    public void testRemoveValueOneClaimPass() {
        house.valueToOwnersMap[0].add(mockedTile);
        assertTrue(house.setValueInHouse(1, false, mockedTile));
        assertEquals(0, house.valueToOwnersMap[0].size());
        assertFalse(house.valueToOwnersMap[0].contains(mockedTile));
    }

    @Test
    public void testRemoveValueTwoClaimsPass() {
        Tile mockedTile2 = mock(Tile.class);
        house.valueToOwnersMap[0].add(mockedTile);
        house.valueToOwnersMap[0].add(mockedTile2);
        assertTrue(house.setValueInHouse(1, false, mockedTile));
        assertEquals(1, house.valueToOwnersMap[0].size());
        assertFalse(house.valueToOwnersMap[0].contains(mockedTile));
        assertTrue(house.valueToOwnersMap[0].contains(mockedTile2));
    }

    @Test
    public void testAddSameTileTwiceFail() {
        house.valueToOwnersMap[0].add(mockedTile);
        assertFalse(house.setValueInHouse(1, true, mockedTile));
    }

    @Test
    public void testAddZeroValueFail() {
        assertFalse(house.setValueInHouse(0, true, mockedTile));
    }

    @Test
    public void testAddNegativeValueFail() {
        assertFalse(house.setValueInHouse(-1, true, mockedTile));
    }

    @Test
    public void testAddLargeValueFail() {
        assertFalse(house.setValueInHouse(1000, true, mockedTile));
    }

    @Test
    public void testRemoveValueNoClaimsFail() {
        assertFalse(house.setValueInHouse(1, false, mockedTile));
    }

    @Test
    public void testRemoveMissingValueOneClaimFail() {
        Tile mockedTile2 = mock(Tile.class);
        house.valueToOwnersMap[0].add(mockedTile);
        assertFalse(house.setValueInHouse(1, false, mockedTile2));
        assertEquals(1, house.valueToOwnersMap[0].size());
        assertTrue(house.valueToOwnersMap[0].contains(mockedTile));
    }
    //endregion

    //region clearValueInHouse() tests
    @Test
    public void testClearOneValueInHousePass() {
        // Set up
        int value = 1;
        house.valueToOwnersMap[value - 1].add(mockedTile);
        // Execute & Verify
        assertTrue(house.clearValueInHouse(value));
        verify(mockedTile).clearValue(value);
        assertEquals(0, house.valueToOwnersMap[0].size());
    }

    @Test
    public void testClearMultipleValuesInHousePass() {
        // Set up
        int value = 1;
        house.valueToOwnersMap[value - 1].add(mockedTile);
        house.valueToOwnersMap[value - 1].add(mockedTile2);
        // Execute & Verify
        assertTrue(house.clearValueInHouse(value));
        verify(mockedTile).clearValue(value);
        verify(mockedTile2).clearValue(value);
        assertEquals(0, house.valueToOwnersMap[value - 1].size());
    }

    @Test
    public void testClearNoValuesInHousePass() {
        assertTrue(house.clearValueInHouse(1));
        assertEquals(0, house.valueToOwnersMap[0].size());
    }

    @Test
    public void testClearDoesNotAffectOtherValuesPass() {
        // Set up
        int value1 = 1;
        int value2 = 2;
        house.valueToOwnersMap[value1 - 1].add(mockedTile);
        house.valueToOwnersMap[value2 - 1].add(mockedTile2);
        // Execute & Verify
        assertTrue(house.clearValueInHouse(value1));
        verify(mockedTile).clearValue(anyInt());
        verify(mockedTile2, never()).clearValue(anyInt());
        assertEquals(1, house.valueToOwnersMap[value2 - 1].size());
        assertTrue(house.valueToOwnersMap[value2 - 1].contains(mockedTile2));
    }

    @Test
    public void testClearZeroValueFail() {
        assertFalse(house.clearValueInHouse(0));
    }

    @Test
    public void testClearNegativeValueFail() {
        assertFalse(house.clearValueInHouse(-1));
    }

    @Test
    public void testClearLargeValueFail() {
        assertFalse(house.clearValueInHouse(1000));
    }
    //endregion

    //region getMember() tests
    @Test
    public void testGetMemberZeroPass() {
        house.members.add(mockedTile);
        assertEquals(mockedTile, house.getMember(0));
    }

    @Test
    public void testGetMemberLaterPass() {
        house.members.add(new Tile(houseSize, 10));
        house.members.add(mockedTile);
        assertEquals(mockedTile, house.getMember(1));
    }

    @Test
    public void testGetMemberNonExistentFail() {
        assertNull(house.getMember(0));
    }

    @Test
    public void testGetMemberNegativeIndexFail() {
        assertNull(house.getMember(-1));
    }

    @Test
    public void testGetMemberLargeIndexFail() {
        assertNull(house.getMember(1000));
    }
    //endregion

    //region hasValue() tests
    @Test
    public void testOneTileHasValuePass() {
        house.valueToOwnersMap[0].add(mockedTile);
        assertTrue(house.hasValue(1));
    }

    @Test
    public void testMultipleTilesHaveValuePass() {
        Tile mockedTile2 = mock(Tile.class);
        house.valueToOwnersMap[0].add(mockedTile);
        house.valueToOwnersMap[0].add(mockedTile2);
        assertTrue(house.hasValue(1));
    }

    @Test
    public void testNoTileHasValuePass() {
        assertFalse(house.hasValue(1));
    }

    @Test
    public void testHasZeroValueFail() {
        assertFalse(house.hasValue(0));
    }

    @Test
    public void testHasNegativeValueFail() {
        assertFalse(house.hasValue(-1));
    }

    @Test
    public void testHasLargeValueFail() {
        assertFalse(house.hasValue(1000));
    }
    //endregion

    //region getValueCount() tests
    @Test
    public void testDefaultValueCountZeroPass() {
        assertEquals(0, house.getValueCount());
        for (int i = 0; i < houseSize; ++i) {
            house.members.add(mock(Tile.class));
        }
        assertEquals(0, house.getValueCount());
    }

    @Test
    public void testValueCountOnePass() {
        when(mockedTile.getValue()).thenReturn(1);
        house.members.add(mockedTile);
        for (int i = 1; i < houseSize; ++i) {
            house.members.add(mock(Tile.class));
        }
        assertEquals(1, house.getValueCount());
    }

    @Test
    public void testValueCountAllPass() {
        for (int i = 0; i < houseSize; ++i) {
            Tile tempMockTile = mock(Tile.class);
            when(tempMockTile.getValue()).thenReturn(i + 1);
            house.members.add(tempMockTile);
        }
        assertEquals(9, house.getValueCount());
    }
    //endregion

    //region getValueTiles() tests
    @Test
    public void testDefaultValueTilesEmptyPass() {
        assertEquals(0, house.getValueTiles().size());
        for (int i = 0; i < houseSize; ++i) {
            house.members.add(mock(Tile.class));
        }
        assertEquals(0, house.getValueTiles().size());
    }

    @Test
    public void testOneValueGetValueTilesPass() {
        when(mockedTile.getValue()).thenReturn(1);
        house.members.add(mockedTile);
        for (int i = 1; i < houseSize; ++i) {
            house.members.add(mock(Tile.class));
        }
        LinkedList<Tile> values = house.getValueTiles();
        assertEquals(1, values.size());
        assertEquals(mockedTile, values.getFirst());
    }

    @Test
    public void testMultipleValuesGetValueTilesPass() {
        Tile[] tempTiles = new Tile[houseSize];
        for (int i = 0; i < houseSize; ++i) {
            Tile tempMockTile = mock(Tile.class);
            when(tempMockTile.getValue()).thenReturn(i + 1);
            tempTiles[i] = tempMockTile;
            house.members.add(tempMockTile);
        }
        LinkedList<Tile> values = house.getValueTiles();
        assertEquals(9, values.size());
        for (int i = 0; i < houseSize; ++i) {
            assertEquals(tempTiles[i], values.get(i));
        }
    }
    //endregion

    //region getHouseIndex() tests
    @Test
    public void testHouseIndex() {
        assertEquals(houseIndex, house.getHouseIndex());
    }
    //endregion

    //region iterator tests
    @Test
    public void testIterateOverNoTilesPass() {
        int count = 0;
        for (Tile ignored : house) {
            ++count;
        }
        assertEquals(0, count);
    }

    @Test
    public void testIterateOverOneTilePass() {
        int count = 0;
        house.members.add(mockedTile);
        for (Tile tile : house) {
            assertEquals(tile, mockedTile);
            ++count;
        }
        assertEquals(1, count);
    }

    @Test
    public void testIterateOverMultipleTilesPass() {
        int count = 0;
        HashSet<Tile> tempTiles = new HashSet<>();
        for (int i = 0; i < houseSize; ++i) {
            Tile tempMockTile = mock(Tile.class);
            tempTiles.add(tempMockTile);
            house.members.add(tempMockTile);
        }
        for (Tile tile : house) {
            assertTrue(tempTiles.contains(tile));
            ++count;
        }
        assertEquals(9, count);
    }
    //endregion
}

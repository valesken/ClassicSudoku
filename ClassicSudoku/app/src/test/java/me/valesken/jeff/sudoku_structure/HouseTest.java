package me.valesken.jeff.sudoku_structure;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jeff on 1/28/16.
 * Last Updated on 1/29/2016.
 */
public class HouseTest {

    private House house;
    Tile mockedTile;
    private int houseSize = 9;
    private int houseIndex = 0;

    //region setup
    @Before
    public void setUp() {
        house = new House(houseSize, houseIndex);
        mockedTile = mock(Tile.class);
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorPass() {
        assertEquals(houseSize, house.houseSize);
        assertEquals(houseIndex, house.houseIndex);
        assertNotNull(house.members);
        assertEquals(0, house.members.size());
        assertNotNull(house.valueOwners);
        assertEquals(9, house.valueOwners.length);
        for (HashSet set : house.valueOwners) {
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
        assertTrue(house.setValueInHouse(1, true, 0));
        assertEquals(1, house.valueOwners[0].size());
        assertTrue(house.valueOwners[0].contains(0));
    }

    @Test
    public void testSetValueOneClaimPass() {
        house.valueOwners[0].add(0);
        assertTrue(house.setValueInHouse(1, true, 1));
        assertEquals(2, house.valueOwners[0].size());
        assertTrue(house.valueOwners[0].contains(0));
        assertTrue(house.valueOwners[0].contains(1));
    }

    @Test
    public void testRemoveValueOneClaimPass() {
        house.valueOwners[0].add(0);
        assertTrue(house.setValueInHouse(1, false, 0));
        assertEquals(0, house.valueOwners[0].size());
        assertFalse(house.valueOwners[0].contains(0));
    }

    @Test
    public void testRemoveValueTwoClaimsPass() {
        house.valueOwners[0].add(0);
        house.valueOwners[0].add(1);
        assertTrue(house.setValueInHouse(1, false, 0));
        assertEquals(1, house.valueOwners[0].size());
        assertFalse(house.valueOwners[0].contains(0));
        assertTrue(house.valueOwners[0].contains(1));
    }

    @Test
    public void testAddSameTileTwiceFail() {
        house.valueOwners[0].add(0);
        assertFalse(house.setValueInHouse(1, true, 0));
    }

    @Test
    public void testAddZeroValueFail() {
        assertFalse(house.setValueInHouse(0, true, 0));
    }

    @Test
    public void testAddNegativeValueFail() {
        assertFalse(house.setValueInHouse(-1, true, 0));
    }

    @Test
    public void testAddLargeValueFail() {
        assertFalse(house.setValueInHouse(1000, true, 0));
    }

    @Test
    public void testAddNegativeTileIndexFail() {
        assertFalse(house.setValueInHouse(1, true, -1));
    }

    @Test
    public void testAddLargeTileIndexFail() {
        assertFalse(house.setValueInHouse(1, true, 1000));
    }

    @Test
    public void testRemoveValueNoClaimsFail() {
        assertFalse(house.setValueInHouse(1, false, 0));
    }

    @Test
    public void testRemoveMissingValueOneClaimFail() {
        house.valueOwners[0].add(0);
        assertFalse(house.setValueInHouse(1, false, 1));
        assertEquals(1, house.valueOwners[0].size());
        assertTrue(house.valueOwners[0].contains(0));
    }
    //endregion

    //region clearValueInHouse() tests
    @Test
    public void testClearOneValueInHousePass() {
        house.valueOwners[0].add(0);
        assertTrue(house.clearValueInHouse(1));
        assertEquals(0, house.valueOwners[0].size());
    }

    @Test
    public void testClearMultipleValuesInHousePass() {
        house.valueOwners[0].add(0);
        house.valueOwners[0].add(1);
        assertTrue(house.clearValueInHouse(1));
        assertEquals(0, house.valueOwners[0].size());
    }

    @Test
    public void testClearNoValuesInHousePass() {
        assertTrue(house.clearValueInHouse(1));
        assertEquals(0, house.valueOwners[0].size());
    }

    @Test
    public void testClearDoesNotAffectOtherValuesPass() {
        house.valueOwners[0].add(0);
        house.valueOwners[1].add(1);
        assertTrue(house.clearValueInHouse(1));
        assertEquals(1, house.valueOwners[1].size());
        assertTrue(house.valueOwners[1].contains(1));
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
        house.valueOwners[0].add(0);
        assertTrue(house.hasValue(1));
    }

    @Test
    public void testMultipleTilesHaveValuePass() {
        house.valueOwners[0].add(0);
        house.valueOwners[0].add(1);
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

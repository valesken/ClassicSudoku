package me.valesken.jeff.sudoku_structure;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by jeff on 1/28/16.
 * Last Updated on 1/28/2016.
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
        for(HashSet set : house.valueOwners) {
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
        for(int i = 0; i < houseSize; ++i) {
            tiles[i] = mock(Tile.class);
            house.addMember(tiles[i]);
        }
        assertEquals(9, house.members.size());
        for(int i = 0; i < houseSize; ++i)
            assertEquals(tiles[i], house.members.get(i));
    }

    @Test
    public void testCannotAddMoreThanNineMembersFail() {
        Tile[] tiles = new Tile[houseSize + 1];
        for(int i = 0; i < (houseSize + 1); ++i) {
            tiles[i] = mock(Tile.class);
            house.addMember(tiles[i]);
        }
        assertEquals(9, house.members.size());
        for(int i = 0; i < houseSize; ++i)
            assertEquals(tiles[i], house.members.get(i));
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

    @Test
    public void testAddZeroValueFail() {
        assertFalse(house.setValueInHouse(0, true, 0));
    }

    @Test
    public void testAddNegativeValueFail() {
        assertFalse(house.setValueInHouse(-1, true, 0));
    }
    //endregion
}

package me.valesken.jeff.sudoku_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jeff on 1/25/2016.
 * Last updated on 1/28/2016.
 */
public class TileTest {

    private int houseSize = 9;
    private int tileIndex = 0;
    private Tile tile;
    private House mockedHouse;
    private int loadedIndex = 80;
    private int loadedRowIndex = 8;
    private int loadedColumnIndex = 8;
    private int loadedZoneIndex = 8;

    //region rules
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    //endregion

    //region setUp
    @Before
    public void setUp() throws Exception {
        mockedHouse = mock(House.class);
        tile = new Tile(houseSize, tileIndex);
        tile.row = mockedHouse;
        tile.column = mockedHouse;
        tile.zone = mockedHouse;
    }

    private JSONObject setUpTileState() throws JSONException {
        JSONObject tileState = new JSONObject();
        tileState.put(Tile.JSON_INDEX_ID, loadedIndex);
        tileState.put(Tile.JSON_ROW_ID, loadedRowIndex);
        tileState.put(Tile.JSON_COLUMN_ID, loadedColumnIndex);
        tileState.put(Tile.JSON_ZONE_ID, loadedZoneIndex);
        return tileState;
    }
    //endregion

    //region constructor tests
    @Test
    public void testConstructorClearsStatePass() {
        assertEquals(0, tile.value);
        assertFalse(tile.noteMode);
        assertEquals(0, tile.getNotes().size());
    }

    @Test
    public void testConstructorIndicesPass() {
        int rowNumber;
        int columnNumber;
        int zoneNumber;
        int boardSize = 81;
        for (int index = 0; index < boardSize; ++index) {
            tile = new Tile(houseSize, index);
            rowNumber = index / houseSize;
            columnNumber = index % houseSize;
            int zoneWidth = 3;
            zoneNumber = 3 * (rowNumber / zoneWidth) + (columnNumber / zoneWidth);
            assertEquals(tile.index, index);
            assertEquals(tile.rowNumber, rowNumber);
            assertEquals(tile.columnNumber, columnNumber);
            assertEquals(tile.zoneNumber, zoneNumber);
        }
    }

    @Test
    public void testConstructorInitializationVarsPass() {
        assertEquals(0, tile.value);
        assertEquals(0, tile.lastTried);
    }
    //endregion

    //region setHouses() tests
    @Test
    public void testSetHousesPass() {
        House row = new House(houseSize, 0);
        House column = new House(houseSize, 0);
        House zone = new House(houseSize, 0);
        tile.setHouses(row, column, zone);
        assertEquals(tile.getRow(), row);
        assertEquals(tile.getColumn(), column);
        assertEquals(tile.getZone(), zone);
    }

    @Test
    public void testSetHousesRowNullExcept() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You must not set Houses in the Tile class to null.");
        tile.setHouses(null, null, null);
    }

    @Test
    public void testSetHousesColumnNullExcept() {
        House column = new House(houseSize, 0);
        House zone = new House(houseSize, 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You must not set Houses in the Tile class to null.");
        tile.setHouses(null, column, zone);
    }

    @Test
    public void testSetHousesZoneNullExcept() {
        House row = new House(houseSize, 0);
        House zone = new House(houseSize, 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You must not set Houses in the Tile class to null.");
        tile.setHouses(row, null, zone);
    }

    @Test
    public void testSetHousesAllNullExcept() {
        House row = new House(houseSize, 0);
        House column = new House(houseSize, 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You must not set Houses in the Tile class to null.");
        tile.setHouses(row, column, null);
    }

    @Test
    public void testSetHousesBadRowIndexExcept() {
        House row = new House(houseSize, 1); // Should be 0 to be good
        House column = new House(houseSize, 0);
        House zone = new House(houseSize, 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You are passing the wrong row to this Tile");
        tile.setHouses(row, column, zone);
    }

    @Test
    public void testSetHousesBadColumnIndexExcept() {
        House row = new House(houseSize, 0);
        House column = new House(houseSize, 1); // Should be 0 to be good
        House zone = new House(houseSize, 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You are passing the wrong column to this Tile");
        tile.setHouses(row, column, zone);
    }

    @Test
    public void testSetHousesBadZoneIndexExcept() {
        House row = new House(houseSize, 0);
        House column = new House(houseSize, 0);
        House zone = new House(houseSize, 1); // Should be 0 to be good
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You are passing the wrong zone to this Tile");
        tile.setHouses(row, column, zone);
    }

    @Test
    public void testSetHousesOrigTileIgnore() {
        tile.orig = true;
        House origRow = tile.row;
        House origColumn = tile.column;
        House origZone = tile.zone;
        House row = new House(houseSize, 0);
        House column = new House(houseSize, 0);
        House zone = new House(houseSize, 0);
        tile.setHouses(row, column, zone);
        assertEquals(origRow, tile.row);
        assertEquals(origColumn, tile.column);
        assertEquals(origZone, tile.zone);
        assertNotEquals(row, tile.row);
        assertNotEquals(column, tile.column);
        assertNotEquals(zone, tile.zone);
    }
    //endregion

    //region setValueInHouses() tests
    @Test
    public void testSetValueInHousesPass() {
        int value = 4;
        tile.setValueInHouses(value, true);
        verify(mockedHouse, times(3)).setValueInHouse(value, true, tile);
    }

    @Test
    public void testSetValueInHousesMinValuePass() {
        int value = 1;
        tile.setValueInHouses(value, true);
        verify(mockedHouse, times(3)).setValueInHouse(value, true, tile);
    }

    @Test
    public void testSetValueInHousesMaxValuePass() {
        int value = houseSize;
        tile.setValueInHouses(value, true);
        verify(mockedHouse, times(3)).setValueInHouse(value, true, tile);
    }

    @Test
    public void testSetValueInHousesZeroIgnored() {
        tile.setValueInHouses(0, true);
        verify(mockedHouse, never()).setValueInHouse(0, true, tile);
    }

    @Test
    public void testSetValueInHousesNegativeIgnored() {
        tile.setValueInHouses(-1, true);
        verify(mockedHouse, never()).setValueInHouse(-1, true, tile);
    }

    @Test
    public void testSetValueInHousesLargeIgnored() {
        tile.setValueInHouses(1000, true);
        verify(mockedHouse, never()).setValueInHouse(1000, true, tile);
    }

    @Test
    public void testSetValueInHousesOrigTileIgnored() {
        tile.orig = true;
        tile.setValueInHouses(1, true);
        verify(mockedHouse, never()).setValueInHouse(1, true, tile);
    }
    //endregion

    //region update() tests
    @Test
    public void testUpdateValueOncePass() {
        Tile spy = spy(tile);
        spy.update(1);
        verify(spy).setValueInHouses(1, true);
        assertEquals(1, spy.value);
    }

    @Test
    public void testUpdateValueTwicePass() {
        Tile spy = spy(tile);
        spy.update(1);
        spy.update(2);
        verify(spy).setValueInHouses(1, true); // Set first
        verify(spy).setValueInHouses(1, false); // Erase first
        verify(spy).setValueInHouses(2, true); // Set Second
        assertEquals(2, spy.value);
    }

    @Test
    public void testUpdateAndUndoValueTwicePass() {
        Tile spy = spy(tile);
        spy.update(1);
        spy.update(1); // Second Update should "erase" previous
        verify(spy, times(1)).setValueInHouses(1, true);
        verify(spy, times(1)).setValueInHouses(1, false);
        assertEquals(0, spy.value);
    }

    @Test
    public void testUpdateNoteOncePass() {
        tile.noteMode = true;
        tile.update(1);
        assertTrue(tile.notes[0]); // note '1'
    }

    @Test
    public void testUpdateNoteAndUndoPass() {
        tile.noteMode = true;
        tile.update(1);
        tile.update(1);
        assertFalse(tile.notes[0]); // note '1'
    }

    @Test
    public void testUpdateNoteMultiplePass() {
        tile.noteMode = true;
        tile.update(1);
        tile.update(2);
        assertTrue(tile.notes[0]); // note '1'
        assertTrue(tile.notes[1]); // note '2'
    }

    @Test
    public void testUpdateNegativeIgnore() {
        int value = tile.value;
        tile.update(-1);
        assertEquals(value, tile.value);
    }

    @Test
    public void testUpdateLargeIgnore() {
        int value = tile.value;
        tile.update(1000);
        assertEquals(value, tile.value);
    }

    @Test
    public void testUpdateOrigTileIgnore() {
        int value = tile.value;
        tile.orig = true;
        tile.update(1);
        assertEquals(value, tile.value);
    }
    //endregion

    //region clearValue() tests
    @Test
    public void testClearValueExistsAsNotePass() {
        // Set up
        int value = 4;
        Tile spy = spy(tile);
        doReturn(0).when(spy).getValue();
        spy.notes[value - 1] = true;
        // Execute
        spy.clearValue(value);
        // Verify
        assertFalse(spy.notes[value - 1]);
    }

    @Test
    public void testClearValueExistsAsValuePass() {
        // Set up
        int value = 4;
        Tile spy = spy(tile);
        doReturn(value).when(spy).getValue();
        spy.notes[value - 1] = false;
        // Execute
        spy.clearValue(value);
        // Verify
        assertEquals(0, spy.value);
    }

    @Test
    public void testClearValueNotInTilePass() {
        // Set up
        int value = 4;
        Tile spy = spy(tile);
        spy.value = value + 1;
        spy.notes[value - 1] = false;
        // Execute
        spy.clearValue(value);
        // Verify
        assertEquals(value + 1, spy.value);
        assertFalse(spy.notes[value - 1]);
    }

    @Test
    public void testClearValueNegativeFail() {
        // Set up
        Tile spy = spy(tile);
        int value = 2;
        spy.value = value;
        for(int i = 0; i < spy.notes.length; ++i) {
            spy.notes[i] = true;
        }
        // Execute
        spy.clearValue(-1);
        // Verify
        verify(spy, never()).getValue();
        assertEquals(value, spy.value);
        for(int i = 0; i < spy.notes.length; ++i) {
            assertTrue(spy.notes[i]);
        }
    }

    @Test
    public void testClearValueLargeFail() {
        // Set up
        Tile spy = spy(tile);
        int value = 2;
        spy.value = value;
        for(int i = 0; i < spy.notes.length; ++i) {
            spy.notes[i] = true;
        }
        // Execute
        spy.clearValue(1000);
        // Verify
        verify(spy, never()).getValue();
        assertEquals(value, spy.value);
        for(int i = 0; i < spy.notes.length; ++i) {
            assertTrue(spy.notes[i]);
        }
    }

    @Test
    public void testClearValueOriginalFail() {
        // Set up
        Tile spy = spy(tile);
        int value = 2;
        spy.value = value;
        for(int i = 0; i < spy.notes.length; ++i) {
            spy.notes[i] = true;
        }
        spy.orig = true;
        // Execute
        spy.clearValue(value);
        // Verify
        verify(spy, never()).getValue();
        assertEquals(value, spy.value);
        for(int i = 0; i < spy.notes.length; ++i) {
            assertTrue(spy.notes[i]);
        }
    }
    //endregion

    //region clear() tests
    @Test
    public void textClearFromNothingPass() {
        tile.clear();
        assertEquals(0, tile.value);
        assertEquals(0, tile.getNotes().size());
    }

    @Test
    public void testClearFromValuePass() {
        Tile spy = spy(tile);
        spy.value = 1;
        spy.clear();
        verify(spy).setValueInHouses(Mockito.anyInt(), eq(false));
        assertEquals(0, spy.value);
        assertEquals(0, spy.getNotes().size());
    }

    @Test
    public void testClearFromNotesPass() {
        tile.noteMode = true;
        tile.notes[0] = true; // note '0'
        tile.notes[1] = true; // note '1'
        tile.clear();
        assertEquals(0, tile.value);
        assertEquals(0, tile.getNotes().size());
    }

    @Test
    public void testClearFromEmptyNoteModePass() {
        tile.noteMode = true;
        tile.clear();
        assertEquals(0, tile.value);
        assertEquals(0, tile.getNotes().size());
    }

    @Test
    public void testClearValueOrigTileIgnore() {
        tile.value = 1;
        tile.orig = true;
        tile.clear();
        assertEquals(1, tile.value);
        assertNotEquals(0, tile.value);
    }

    @Test
    public void testClearNotesOrigTileIgnore() {
        tile.noteMode = true;
        tile.notes[0] = true;
        tile.orig = true;
        tile.clear();
        assertTrue(tile.notes[0]);
    }
    //endregion

    //region toggleNoteMode() tests
    @Test
    public void testToggleNoteModeOnPass() {
        tile.toggleMode();
        assertTrue(tile.noteMode);
    }

    @Test
    public void testToggleNoteModeOnOneValue() {
        tile.value = 1;
        tile.toggleMode();
        assertTrue(tile.noteMode);
        assertEquals(0, tile.value);
        assertTrue(tile.notes[0]);
    }

    @Test
    public void testToggleNoteModeOffPass() {
        tile.toggleMode();
        assertTrue(tile.noteMode);
        tile.toggleMode();
        assertFalse(tile.noteMode);
    }

    @Test
    public void testToggleNoteModeOffOneValue() {
        Tile spy = spy(tile);
        spy.toggleMode();
        spy.notes[0] = true; // note '1'
        spy.toggleMode();
        verify(spy).setValueInHouses(1, true);
        assertFalse(spy.noteMode);
        assertEquals(1, spy.value);
        assertFalse(spy.notes[0]);
    }

    @Test
    public void testToggleNoteModeOffMultipleValues() {
        tile.toggleMode();
        tile.notes[0] = true; // note '1'
        tile.notes[1] = true; // note '2'
        tile.toggleMode();
        assertFalse(tile.noteMode);
        assertEquals(0, tile.value);
        assertFalse(tile.notes[0]);
        assertFalse(tile.notes[1]);
    }

    @Test
    public void testToggleNoteModeOnOrigTileIgnore() {
        boolean noteMode = tile.noteMode;
        tile.orig = true;
        tile.toggleMode();
        assertEquals(noteMode, tile.noteMode);
    }
    //endregion

    //region getNotesOrValue() tests
    @Test
    public void testEmptyNotesModePass() {
        Tile spy = spy(tile);
        spy.noteMode = true;
        LinkedList<Integer> notes = spy.getNotesOrValue();
        verify(spy).getNotes();
        assertNotNull(notes);
        assertEquals(0, notes.size());
    }

    @Test
    public void testOneNoteModePass() {
        Tile spy = spy(tile);
        spy.noteMode = true;
        spy.notes[0] = true; // note '1'
        LinkedList<Integer> notes = spy.getNotesOrValue();
        verify(spy).getNotes();
        assertNotNull(notes);
        assertEquals(1, notes.size());
        assertTrue(notes.contains(1));
    }

    @Test
    public void testMultipleNoteModePass() {
        Tile spy = spy(tile);
        spy.noteMode = true;
        spy.notes[0] = true; // note '1'
        spy.notes[1] = true; // note '2'
        LinkedList<Integer> notes = spy.getNotesOrValue();
        verify(spy).getNotes();
        assertNotNull(notes);
        assertEquals(2, notes.size());
        assertTrue(notes.contains(1));
        assertTrue(notes.contains(2));
    }

    @Test
    public void testNoValuePass() {
        Tile spy = spy(tile);
        LinkedList<Integer> values = spy.getNotesOrValue();
        verify(spy).getValue();
        assertNotNull(values);
        assertEquals(1, values.size());
        assertTrue(values.contains(0));
    }

    @Test
    public void testWithValuePass() {
        Tile spy = spy(tile);
        spy.value = 1;
        LinkedList<Integer> values = spy.getNotesOrValue();
        verify(spy).getValue();
        assertNotNull(values);
        assertEquals(1, values.size());
        assertTrue(values.contains(1));
    }
    //endregion

    //region getValue() tests
    @Test
    public void testDefaultGetValuePass() {
        assertEquals(0, tile.getValue());
    }

    @Test
    public void testGetValueAfterUpdatePass() {
        tile.value = 1;
        assertEquals(1, tile.getValue());
    }

    @Test
    public void testGetValueInNoteModePass() {
        tile.noteMode = true;
        assertEquals(0, tile.getValue());
    }

    @Test
    public void testGetValueInNoteModeWithNotePass() {
        tile.noteMode = true;
        tile.notes[0] = true; // note '1'
        assertEquals(0, tile.getValue());
    }
    //endregion

    //region getNotes() tests
    @Test
    public void testGetNotesNotNoteModeNoValuePass() {
        LinkedList<Integer> notes = tile.getNotes();
        assertNotNull(notes);
        assertEquals(0, notes.size());
    }

    @Test
    public void testGetNotesNotNoteModeWithValuePass() {
        tile.value = 1;
        LinkedList<Integer> notes = tile.getNotes();
        assertNotNull(notes);
        assertEquals(0, notes.size());
    }

    @Test
    public void testGetNotesNoteModeNoNotesPass() {
        tile.noteMode = true;
        LinkedList<Integer> notes = tile.getNotes();
        assertNotNull(notes);
        assertEquals(0, notes.size());
    }

    @Test
    public void testGetNotesNoteModeOneNotePass() {
        tile.noteMode = true;
        tile.notes[0] = true; // note '1'
        LinkedList<Integer> notes = tile.getNotes();
        assertNotNull(notes);
        assertEquals(1, (int) (notes.getFirst()));
        assertEquals(1, notes.size());
    }

    @Test
    public void testGetNotesNoteModeMultipleNotesPass() {
        tile.noteMode = true;
        tile.notes[0] = true; // note '1'
        tile.notes[1] = true; // note '2'
        LinkedList<Integer> notes = tile.getNotes();
        assertNotNull(notes);
        assertTrue(notes.contains(1));
        assertTrue(notes.contains(2));
        assertEquals(2, notes.size());
    }
    //endregion

    //region getIndex() tests
    @Test
    public void testGetIndexPass() {
        assertEquals(tileIndex, tile.getIndex());
    }
    //endregion

    //region getHouse/getHouseNumber tests
    @Test
    public void testGetRowPass() {
        assertEquals(mockedHouse, tile.getRow());
    }

    @Test
    public void testGetRowNumberPass() {
        assertEquals(tile.rowNumber, tile.getRowNumber());
    }

    @Test
    public void testGetColumnPass() {
        assertEquals(mockedHouse, tile.getColumn());
    }

    @Test
    public void testGetColumnNumberPass() {
        assertEquals(tile.columnNumber, tile.getColumnNumber());
    }

    @Test
    public void testGetZonePass() {
        assertEquals(mockedHouse, tile.getZone());
    }

    @Test
    public void testGetZoneNumberPass() {
        assertEquals(tile.zoneNumber, tile.getZoneNumber());
    }
    //endregion

    //region isNoteMode() tests
    @Test
    public void testIsNoteModePass() {
        tile.noteMode = true;
        assertTrue(tile.isNoteMode());
    }

    @Test
    public void testIsNotNoteModePass() {
        tile.noteMode = false;
        assertFalse(tile.isNoteMode());
    }
    //endregion

    //region isOrig() tests
    @Test
    public void testIsOrigPass() {
        tile.orig = true;
        assertTrue(tile.isOrig());
    }

    @Test
    public void testIsNotOrigPass() {
        tile.orig = false;
        assertFalse(tile.isOrig());
    }
    //endregion

    //region getJSON() tests
    @Test
    public void testDefaultTileJSONPass() throws JSONException {
        Tile spy = spy(tile);
        JSONObject tileObject = spy.getJSON();
        // Verify Method Calls
        verify(spy).getIndex();
        verify(spy).getRowNumber();
        verify(spy).getColumnNumber();
        verify(spy).getZoneNumber();
        verify(spy).isNoteMode();
        verify(spy).isOrig();
        verify(spy).getNotesOrValue();
        // Verify All Necessary Tags Used
        assertTrue(tileObject.has(Tile.JSON_INDEX_ID));
        assertTrue(tileObject.has(Tile.JSON_ROW_ID));
        assertTrue(tileObject.has(Tile.JSON_COLUMN_ID));
        assertTrue(tileObject.has(Tile.JSON_ZONE_ID));
        assertTrue(tileObject.has(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.has(Tile.JSON_ORIG_ID));
        assertTrue(tileObject.has(Tile.JSON_VALUES_ID));
        // Verify Correct Values
        assertEquals(tileObject.getInt(Tile.JSON_INDEX_ID), spy.index);
        assertEquals(tileObject.getInt(Tile.JSON_ROW_ID), spy.rowNumber);
        assertEquals(tileObject.getInt(Tile.JSON_COLUMN_ID), spy.columnNumber);
        assertEquals(tileObject.getInt(Tile.JSON_ZONE_ID), spy.zoneNumber);
        assertFalse(tileObject.getBoolean(Tile.JSON_NOTE_MODE_ID));
        assertFalse(tileObject.getBoolean(Tile.JSON_ORIG_ID));
        JSONArray jsonValues = tileObject.getJSONArray(Tile.JSON_VALUES_ID);
        assertEquals(1, jsonValues.length());
        assertEquals(0, jsonValues.getInt(0));
    }

    @Test
    public void testTileWithValueJSONPass() throws JSONException {
        Tile spy = spy(tile);
        spy.value = 1;
        JSONObject tileObject = spy.getJSON();
        // Verify Method Calls
        verify(spy).getIndex();
        verify(spy).getRowNumber();
        verify(spy).getColumnNumber();
        verify(spy).getZoneNumber();
        verify(spy).isNoteMode();
        verify(spy).isOrig();
        verify(spy).getNotesOrValue();
        // Verify All Necessary Tags Used
        assertTrue(tileObject.has(Tile.JSON_INDEX_ID));
        assertTrue(tileObject.has(Tile.JSON_ROW_ID));
        assertTrue(tileObject.has(Tile.JSON_COLUMN_ID));
        assertTrue(tileObject.has(Tile.JSON_ZONE_ID));
        assertTrue(tileObject.has(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.has(Tile.JSON_ORIG_ID));
        assertTrue(tileObject.has(Tile.JSON_VALUES_ID));
        // Verify Correct Values
        assertEquals(tileObject.getInt(Tile.JSON_INDEX_ID), spy.index);
        assertEquals(tileObject.getInt(Tile.JSON_ROW_ID), spy.rowNumber);
        assertEquals(tileObject.getInt(Tile.JSON_COLUMN_ID), spy.columnNumber);
        assertEquals(tileObject.getInt(Tile.JSON_ZONE_ID), spy.zoneNumber);
        assertFalse(tileObject.getBoolean(Tile.JSON_NOTE_MODE_ID));
        assertFalse(tileObject.getBoolean(Tile.JSON_ORIG_ID));
        JSONArray jsonValues = tileObject.getJSONArray(Tile.JSON_VALUES_ID);
        assertEquals(1, jsonValues.length());
        assertEquals(1, jsonValues.getInt(0));
    }

    @Test
    public void testTileInNoteModeJSONPass() throws JSONException {
        Tile spy = spy(tile);
        spy.noteMode = true;
        JSONObject tileObject = spy.getJSON();
        // Verify Method Calls
        verify(spy).getIndex();
        verify(spy).getRowNumber();
        verify(spy).getColumnNumber();
        verify(spy).getZoneNumber();
        verify(spy).isNoteMode();
        verify(spy).isOrig();
        verify(spy).getNotesOrValue();
        // Verify All Necessary Tags Used
        assertTrue(tileObject.has(Tile.JSON_INDEX_ID));
        assertTrue(tileObject.has(Tile.JSON_ROW_ID));
        assertTrue(tileObject.has(Tile.JSON_COLUMN_ID));
        assertTrue(tileObject.has(Tile.JSON_ZONE_ID));
        assertTrue(tileObject.has(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.has(Tile.JSON_ORIG_ID));
        assertTrue(tileObject.has(Tile.JSON_VALUES_ID));
        // Verify Correct Values
        assertEquals(tileObject.getInt(Tile.JSON_INDEX_ID), spy.index);
        assertEquals(tileObject.getInt(Tile.JSON_ROW_ID), spy.rowNumber);
        assertEquals(tileObject.getInt(Tile.JSON_COLUMN_ID), spy.columnNumber);
        assertEquals(tileObject.getInt(Tile.JSON_ZONE_ID), spy.zoneNumber);
        assertTrue(tileObject.getBoolean(Tile.JSON_NOTE_MODE_ID));
        assertFalse(tileObject.getBoolean(Tile.JSON_ORIG_ID));
        JSONArray jsonNotes = tileObject.getJSONArray(Tile.JSON_VALUES_ID);
        assertEquals(0, jsonNotes.length());
    }

    @Test
    public void testTileWithNotesJSONPass() throws JSONException {
        Tile spy = spy(tile);
        spy.noteMode = true;
        spy.notes[0] = true;
        spy.notes[1] = true;
        JSONObject tileObject = spy.getJSON();
        // Verify Method Calls
        verify(spy).getIndex();
        verify(spy).getRowNumber();
        verify(spy).getColumnNumber();
        verify(spy).getZoneNumber();
        verify(spy).isNoteMode();
        verify(spy).isOrig();
        verify(spy).getNotesOrValue();
        // Verify All Necessary Tags Used
        assertTrue(tileObject.has(Tile.JSON_INDEX_ID));
        assertTrue(tileObject.has(Tile.JSON_ROW_ID));
        assertTrue(tileObject.has(Tile.JSON_COLUMN_ID));
        assertTrue(tileObject.has(Tile.JSON_ZONE_ID));
        assertTrue(tileObject.has(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.has(Tile.JSON_ORIG_ID));
        assertTrue(tileObject.has(Tile.JSON_VALUES_ID));
        // Verify Correct Values
        assertEquals(tileObject.getInt(Tile.JSON_INDEX_ID), spy.index);
        assertEquals(tileObject.getInt(Tile.JSON_ROW_ID), spy.rowNumber);
        assertEquals(tileObject.getInt(Tile.JSON_COLUMN_ID), spy.columnNumber);
        assertEquals(tileObject.getInt(Tile.JSON_ZONE_ID), spy.zoneNumber);
        assertTrue(tileObject.getBoolean(Tile.JSON_NOTE_MODE_ID));
        assertFalse(tileObject.getBoolean(Tile.JSON_ORIG_ID));
        JSONArray jsonNotes = tileObject.getJSONArray(Tile.JSON_VALUES_ID);
        assertEquals(2, jsonNotes.length());
        HashSet<Integer> notes = new HashSet<>();
        for (int i = 0; i < jsonNotes.length(); ++i) {
            notes.add(jsonNotes.getInt(i));
        }
        assertTrue(notes.contains(1));
        assertTrue(notes.contains(2));
    }

    @Test
    public void testOrigTileJSONPass() throws JSONException {
        Tile spy = spy(tile);
        spy.value = 1;
        spy.orig = true;
        JSONObject tileObject = spy.getJSON();
        // Verify Method Calls
        verify(spy).getIndex();
        verify(spy).getRowNumber();
        verify(spy).getColumnNumber();
        verify(spy).getZoneNumber();
        verify(spy).isNoteMode();
        verify(spy).isOrig();
        verify(spy).getNotesOrValue();
        // Verify All Necessary Tags Used
        assertTrue(tileObject.has(Tile.JSON_INDEX_ID));
        assertTrue(tileObject.has(Tile.JSON_ROW_ID));
        assertTrue(tileObject.has(Tile.JSON_COLUMN_ID));
        assertTrue(tileObject.has(Tile.JSON_ZONE_ID));
        assertTrue(tileObject.has(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.has(Tile.JSON_ORIG_ID));
        assertTrue(tileObject.has(Tile.JSON_VALUES_ID));
        // Verify Correct Values
        assertEquals(tileObject.getInt(Tile.JSON_INDEX_ID), spy.index);
        assertEquals(tileObject.getInt(Tile.JSON_ROW_ID), spy.rowNumber);
        assertEquals(tileObject.getInt(Tile.JSON_COLUMN_ID), spy.columnNumber);
        assertEquals(tileObject.getInt(Tile.JSON_ZONE_ID), spy.zoneNumber);
        assertFalse(tileObject.getBoolean(Tile.JSON_NOTE_MODE_ID));
        assertTrue(tileObject.getBoolean(Tile.JSON_ORIG_ID));
        JSONArray jsonValues = tileObject.getJSONArray(Tile.JSON_VALUES_ID);
        assertEquals(1, jsonValues.length());
        assertEquals(1, jsonValues.getInt(0));
    }
    //endregion

    //region unVisit() tests
    @Test
    public void testUnVisitVisited() {
        tile.visited = true;
        tile.unVisit();
        assertFalse(tile.visited);
    }

    @Test
    public void testUnVisitUnvisited() {
        tile.visited = false;
        tile.unVisit();
        assertFalse(tile.visited);
    }
    //endregion

    //region hasBeenVisited() tests
    @Test
    public void testHasBeenVisitedPass() {
        tile.visited = true;
        assertTrue(tile.hasBeenVisited());
    }

    @Test
    public void testHasNotBeenVisitedPass() {
        tile.visited = false;
        assertFalse(tile.hasBeenVisited());
    }
    //endregion

    //region tryInitialize() tests
    @Test
    public void testTryInitializeFromZeroNoContradictionsPass() {
        Tile spy = spy(tile);
        when(mockedHouse.hasValue(1)).thenReturn(false);
        assertTrue(spy.tryInitialize());
        verify(mockedHouse, times(3)).hasValue(1);
        verify(spy).update(1);
        assertEquals(1, spy.value);
        assertEquals(1, spy.lastTried);
        assertTrue(spy.visited);
    }

    @Test
    public void testTryInitializeFromOneNoContradictionsPass() {
        Tile spy = spy(tile);
        spy.lastTried = 1;
        when(mockedHouse.hasValue(2)).thenReturn(false);
        assertTrue(spy.tryInitialize());
        verify(mockedHouse, times(3)).hasValue(2);
        verify(spy).update(2);
        assertEquals(2, spy.value);
        assertEquals(2, spy.lastTried);
        assertTrue(spy.visited);
    }

    @Test
    public void testTryInitializeFromZeroRowContradictsOnePass() {
        // Setup
        Tile spy = spy(tile);
        House mockedRow = mock(House.class);
        spy.row = mockedRow;
        when(mockedRow.hasValue(1)).thenReturn(true);
        when(mockedRow.hasValue(2)).thenReturn(false);
        when(mockedHouse.hasValue(2)).thenReturn(false);
        assertTrue(spy.tryInitialize());
        // Verify method calls
        verify(mockedRow).hasValue(1);
        verify(mockedHouse, never()).hasValue(1);
        verify(mockedRow).hasValue(2);
        verify(mockedHouse, times(2)).hasValue(2);
        verify(spy, never()).update(1);
        verify(spy).update(2);
        // Verify results
        assertEquals(2, spy.value);
        assertEquals(2, spy.lastTried);
        assertTrue(spy.visited);
    }

    @Test
    public void testTryInitializeFromZeroColumnContradictsOnePass() {
        // Setup
        Tile spy = spy(tile);
        House mockedColumn = mock(House.class);
        spy.column = mockedColumn;
        when(mockedColumn.hasValue(1)).thenReturn(true);
        when(mockedColumn.hasValue(2)).thenReturn(false);
        when(mockedHouse.hasValue(anyInt())).thenReturn(false);
        assertTrue(spy.tryInitialize());
        // Verify method calls
        verify(mockedHouse).hasValue(1);
        verify(mockedColumn).hasValue(1);
        verify(mockedHouse, times(2)).hasValue(2);
        verify(mockedColumn).hasValue(2);
        verify(spy, never()).update(1);
        verify(spy).update(2);
        // Verify results
        assertEquals(2, spy.value);
        assertEquals(2, spy.lastTried);
        assertTrue(spy.visited);
    }

    @Test
    public void testTryInitializeFromZeroZoneContradictsOnePass() {
        // Setup
        Tile spy = spy(tile);
        House mockedZone = mock(House.class);
        spy.zone = mockedZone;
        when(mockedZone.hasValue(1)).thenReturn(true);
        when(mockedZone.hasValue(2)).thenReturn(false);
        when(mockedHouse.hasValue(anyInt())).thenReturn(false);
        assertTrue(spy.tryInitialize());
        // Verify method calls
        verify(mockedHouse, times(2)).hasValue(1);
        verify(mockedZone).hasValue(1);
        verify(mockedHouse, times(2)).hasValue(2);
        verify(mockedZone).hasValue(2);
        verify(spy, never()).update(1);
        verify(spy).update(2);
        // Verify results
        assertEquals(2, spy.value);
        assertEquals(2, spy.lastTried);
        assertTrue(spy.visited);
    }

    @Test
    public void testTryInitializeFromZeroAllContradictFail() {
        // Setup
        Tile spy = spy(tile);
        when(mockedHouse.hasValue(anyInt())).thenReturn(true);
        assertFalse(spy.tryInitialize());
        // Verify method calls
        for (int value = 1; value <= houseSize; ++value) {
            verify(mockedHouse).hasValue(value);
        }
        verify(spy, never()).update(anyInt());
        // Verify results
        assertEquals(0, spy.value);
        assertEquals(9, spy.lastTried);
        assertFalse(spy.visited);
    }

    @Test
    public void testTryInitializeFromOneAllContradictFail() {
        // Setup
        Tile spy = spy(tile);
        spy.lastTried = 1;
        when(mockedHouse.hasValue(anyInt())).thenReturn(true);
        assertFalse(spy.tryInitialize());
        // Verify method calls
        verify(mockedHouse, never()).hasValue(1); // Not ideal, but expected to ignore this
        for (int value = 2; value <= houseSize; ++value) {
            verify(mockedHouse).hasValue(value);
        }
        verify(spy, never()).update(anyInt());
        // Verify results
        assertEquals(0, spy.value);
        assertEquals(9, spy.lastTried);
        assertFalse(spy.visited);
    }
    //endregion

    //region seedInitialValue() tests
    @Test
    public void testSeedInitialValuePass() {
        Tile spy = spy(tile);
        when(mockedHouse.hasValue(1)).thenReturn(false);
        assertTrue(spy.seedInitialValue(1));
        verify(mockedHouse, times(3)).hasValue(1);
        verify(spy).update(1);
        assertEquals(1, spy.value);
        assertTrue(spy.visited);
    }

    @Test
    public void testSeedInitialValueSmallFail() {
        Tile spy = spy(tile);
        assertFalse(spy.seedInitialValue(-1));
        verify(mockedHouse, never()).hasValue(-1);
        verify(spy, never()).update(-1);
        assertNotEquals(-1, spy.value);
        assertFalse(spy.visited);
    }

    @Test
    public void testSeedInitialValueBigFail() {
        Tile spy = spy(tile);
        assertFalse(spy.seedInitialValue(1000));
        verify(mockedHouse, never()).hasValue(1000);
        verify(spy, never()).update(1000);
        assertNotEquals(1000, spy.value);
        assertFalse(spy.visited);
    }

    @Test
    public void testSeedInitialValueRowConflictFail() {
        House mockedRow = mock(House.class);
        Tile spy = spy(tile);
        spy.row = mockedRow;
        when(mockedRow.hasValue(1)).thenReturn(true);
        assertFalse(spy.seedInitialValue(1));
        verify(mockedRow).hasValue(1);
        verify(mockedHouse, never()).hasValue(1);
        verify(spy, never()).update(1);
        assertNotEquals(1, spy.value);
        assertFalse(spy.visited);
    }

    @Test
    public void testSeedInitialValueColumnConflictFail() {
        House mockedColumn = mock(House.class);
        Tile spy = spy(tile);
        spy.column = mockedColumn;
        when(mockedHouse.hasValue(1)).thenReturn(false);
        when(mockedColumn.hasValue(1)).thenReturn(true);
        assertFalse(spy.seedInitialValue(1));
        verify(mockedHouse).hasValue(1);
        verify(mockedColumn).hasValue(1);
        verify(spy, never()).update(1);
        assertNotEquals(1, spy.value);
        assertFalse(spy.visited);
    }

    @Test
    public void testSeedInitialValueZoneConflictFail() {
        House mockedZone = mock(House.class);
        Tile spy = spy(tile);
        spy.zone = mockedZone;
        when(mockedHouse.hasValue(1)).thenReturn(false);
        when(mockedZone.hasValue(1)).thenReturn(true);
        assertFalse(spy.seedInitialValue(1));
        verify(mockedHouse, times(2)).hasValue(1);
        verify(mockedZone).hasValue(1);
        verify(spy, never()).update(1);
        assertNotEquals(1, spy.value);
        assertFalse(spy.visited);
    }
    //endregion

    //region resetInitializationState() tests
    @Test
    public void testResetFromDefaultStatePass() {
        Tile spy = spy(tile);
        spy.resetInitializationState();
        verify(spy, never()).setValueInHouses(anyInt(), eq(false));
        assertEquals(0, spy.value);
        assertEquals(0, spy.lastTried);
        assertFalse(spy.visited);
    }

    @Test
    public void testResetFromNonDefaultStatePass() {
        Tile spy = spy(tile);
        spy.value = 1;
        spy.visited = true;
        spy.lastTried = 1;
        spy.resetInitializationState();
        verify(spy).setValueInHouses(1, false);
        assertEquals(0, spy.value);
        assertEquals(0, spy.lastTried);
        assertFalse(spy.visited);
    }
    //endregion

    //region setOrig() tests
    @Test
    public void testSetOrigTruePass() {
        tile.setOrig(true);
        assertTrue(tile.orig);
    }

    @Test
    public void testSetOrigFalsePass() {
        tile.setOrig(false);
        assertFalse(tile.orig);
    }
    //endregion

    //region loadTileState() tests
    @Test
    public void testLoadTileOrigWithValuePass() throws JSONException {
        // Setup
        tile.value = 3;
        JSONObject tileState = setUpTileState();
        tileState.put(Tile.JSON_ORIG_ID, true);
        tileState.put(Tile.JSON_NOTE_MODE_ID, false);
        JSONArray values = new JSONArray();
        values.put(5);
        tileState.put(Tile.JSON_VALUES_ID, values);
        tile.loadTileState(tileState);
        // Verify results
        assertEquals(5, tile.value);
        assertTrue(tile.orig);
        assertFalse(tile.noteMode);
        assertEquals(loadedIndex, tile.index);
        assertEquals(loadedRowIndex, tile.rowNumber);
        assertEquals(loadedColumnIndex, tile.columnNumber);
        assertEquals(loadedZoneIndex, tile.zoneNumber);
    }

    @Test
    public void testLoadTileNotOrigWithValuePass() throws JSONException {
        // Setup
        tile.value = 3;
        JSONObject tileState = setUpTileState();
        tileState.put(Tile.JSON_ORIG_ID, false);
        tileState.put(Tile.JSON_NOTE_MODE_ID, false);
        JSONArray values = new JSONArray();
        values.put(5);
        tileState.put(Tile.JSON_VALUES_ID, values);
        tile.loadTileState(tileState);
        // Verify results
        assertEquals(5, tile.value);
        assertFalse(tile.orig);
        assertFalse(tile.noteMode);
        assertEquals(loadedIndex, tile.index);
        assertEquals(loadedRowIndex, tile.rowNumber);
        assertEquals(loadedColumnIndex, tile.columnNumber);
        assertEquals(loadedZoneIndex, tile.zoneNumber);
    }

    @Test
    public void testLoadTileWithNotesPass() throws JSONException {
        // Setup
        tile.value = 3;
        JSONObject tileState = setUpTileState();
        tileState.put(Tile.JSON_ORIG_ID, false);
        tileState.put(Tile.JSON_NOTE_MODE_ID, true);
        JSONArray values = new JSONArray();
        values.put(5);
        values.put(6);
        tileState.put(Tile.JSON_VALUES_ID, values);
        tile.loadTileState(tileState);
        // Verify results
        assertEquals(0, tile.value);
        assertFalse(tile.orig);
        assertTrue(tile.noteMode);
        assertTrue(tile.notes[4]);
        assertTrue(tile.notes[5]);
        assertEquals(loadedIndex, tile.index);
        assertEquals(loadedRowIndex, tile.rowNumber);
        assertEquals(loadedColumnIndex, tile.columnNumber);
        assertEquals(loadedZoneIndex, tile.zoneNumber);
    }

    @Test
    public void testLoadTileWithMissingTagExcept() throws JSONException {
        tile.value = 3;
        tile.index = 0;
        tile.rowNumber = 0;
        tile.columnNumber = 0;
        tile.zoneNumber = 0;
        JSONObject tileState = setUpTileState();
        try {
            tile.loadTileState(tileState);
            fail();
        } catch (JSONException e) {
            assertEquals(3, tile.value);
            assertEquals(0, tile.index);
            assertEquals(0, tile.rowNumber);
            assertEquals(0, tile.columnNumber);
            assertEquals(0, tile.zoneNumber);
        }
    }
    //endregion
}
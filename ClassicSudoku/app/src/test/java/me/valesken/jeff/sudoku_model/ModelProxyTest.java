package me.valesken.jeff.sudoku_model;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Jeff on 4/14/16.
 * Last Updated on 6/5/2016.
 */
public class ModelProxyTest {

    private final int BOARD_SIZE = 81;
    private final int HOUSE_SIZE = 9;
    private Board mockBoard;

    //region setup
    @Before
    public void setUp() {
        mockBoard = mock(Board.class);
        ModelProxy.board = mockBoard;
    }
    //endregion

    //region constructor tests
    @Test
    public void test_constructor_pass() {
        assertNotNull(new ModelProxy());
    }
    //endregion

    //region newGame() tests
    @Test
    public void test_newGame_pass() {
        // Set up
        int difficulty = 1;
        int randomDifficulty = 2;
        doReturn(randomDifficulty).when(mockBoard).newGame(anyInt(), booleanThat(is(false)));
        doReturn(difficulty).when(mockBoard).newGame(and(gt(0), lt(4)), booleanThat(is(false)));
        // Execute & Verify
        assertEquals(difficulty, ModelProxy.newGame(9, difficulty, mockBoard, false));
        verify(mockBoard).newGame(difficulty, false);
    }

    @Test
    public void test_newGame_random_pass() {
        // Set up
        int difficulty = 4;
        int randomDifficulty = 2;
        doReturn(randomDifficulty).when(mockBoard).newGame(anyInt(), booleanThat(is(false)));
        doReturn(difficulty).when(mockBoard).newGame(and(gt(0), lt(4)), booleanThat(is(false)));
        // Execute & Verify
        assertEquals(randomDifficulty, ModelProxy.newGame(9, difficulty, mockBoard, false));
        verify(mockBoard).newGame(difficulty, false);
    }
    //endregion

    //region loadGame() tests
    @Test
    public void test_loadGame_pass() {
        // Setup
        int difficulty = 1;
        JSONObject mockJson = mock(JSONObject.class);
        doReturn(difficulty).when(mockBoard).loadGame(mockJson);
        // Execute & Verify
        assertEquals(difficulty, ModelProxy.loadGame(9, mockJson, mockBoard));
    }
    //endregion

    //region save() tests
    @Test
    public void test_save_pass() {
        // Setup
        JSONObject mockJson = mock(JSONObject.class);
        doReturn(mockJson).when(mockBoard).save(anyString());
        // Execute & Verify
        assertEquals(mockJson, ModelProxy.save("12:00"));
    }

    @Test
    public void test_save_null_fail() {
        ModelProxy.board = null;
        assertNull(ModelProxy.save("12:00"));
    }
    //endregion

    //region getTime() tests
    @Test
    public void test_getTime_pass() {
        // Set up
        String time = "1:05";
        doReturn(time).when(mockBoard).getTime();
        // Execute & Verify
        assertEquals(time, ModelProxy.getTime());
    }

    @Test
    public void test_getTime_null_fail() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertNull(ModelProxy.getTime());
    }
    //endregion

    //region getBoard() tests
    @Test
    public void test_getBoard_pass() {
        // Set up
        LinkedList[] lists = new LinkedList[BOARD_SIZE];
        doReturn(lists).when(mockBoard).getBoard();
        // Execute & Verify
        assertArrayEquals(lists, ModelProxy.getBoard());
    }

    @Test
    public void test_getBoard_null_fail() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertNull(ModelProxy.getBoard());
    }
    //endregion

    //region tileIsNoteMode() tests
    @Test
    public void test_tileIsNoteMode_true() {
        // Set up
        int position = 40;
        doReturn(false).when(mockBoard).tileIsNoteMode(anyInt());
        doReturn(true).when(mockBoard).tileIsNoteMode(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertTrue(ModelProxy.tileIsNoteMode(position));
    }

    @Test
    public void test_tileIsNoteMode_false() {
        // Set up
        int position = 40;
        doReturn(false).when(mockBoard).tileIsNoteMode(anyInt());
        // Execute & Verify
        assertFalse(ModelProxy.tileIsNoteMode(position));
    }

    @Test
    public void test_tileIsNoteMode_badIndex_false() {
        // Set up
        int position = -1;
        doReturn(false).when(mockBoard).tileIsNoteMode(anyInt());
        doReturn(true).when(mockBoard).tileIsNoteMode(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertFalse(ModelProxy.tileIsNoteMode(position));
    }

    @Test
    public void test_tileIsNoteMode_nullBoard_false() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertFalse(ModelProxy.tileIsNoteMode(40));
    }
    //endregion

    //region tileIsOrig() tests
    @Test
    public void test_tileIsOrig_true() {
        // Set up
        int position = 40;
        doReturn(false).when(mockBoard).tileIsOrig(anyInt());
        doReturn(true).when(mockBoard).tileIsOrig(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertTrue(ModelProxy.tileIsOrig(position));
    }

    @Test
    public void test_tileIsOrig_false() {
        // Set up
        int position = 40;
        doReturn(false).when(mockBoard).tileIsOrig(anyInt());
        // Execute & Verify
        assertFalse(ModelProxy.tileIsOrig(position));
    }

    @Test
    public void test_tileIsOrig_badIndex_false() {
        // Set up
        int position = -1;
        doReturn(false).when(mockBoard).tileIsOrig(anyInt());
        doReturn(true).when(mockBoard).tileIsOrig(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertFalse(ModelProxy.tileIsOrig(position));
    }

    @Test
    public void test_tileIsOrig_nullBoard_false() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertFalse(ModelProxy.tileIsOrig(40));
    }
    //endregion

    //region updateTile() tests
    @Test
    public void test_updateTile_pass() {
        // Set up
        int position = 40;
        int value = 3;
        doReturn(null).when(mockBoard).updateTile(anyInt(), anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).updateTile(and(gt(0), lt(BOARD_SIZE)), and(gt(0), lt(HOUSE_SIZE)));
        // Execute
        Object obj = ModelProxy.updateTile(position, value);
        // Verify
        assertNotNull(obj);
        assertEquals(LinkedList.class, obj.getClass());
        verify(mockBoard).updateTile(position, value);
    }

    @Test
    public void test_updateTile_badIndex_null() {
        // Set up
        int position = -1;
        int value = 3;
        doReturn(null).when(mockBoard).updateTile(anyInt(), anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).updateTile(and(gt(0), lt(BOARD_SIZE)), and(gt(0), lt(HOUSE_SIZE)));
        // Execute
        Object obj = ModelProxy.updateTile(position, value);
        // Verify
        assertNull(obj);
        verify(mockBoard).updateTile(position, value);
    }

    @Test
    public void test_updateTile_badValue_null() {
        // Set up
        int position = 40;
        int value = -1;
        doReturn(null).when(mockBoard).updateTile(anyInt(), anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).updateTile(and(gt(0), lt(BOARD_SIZE)), and(gt(0), lt(HOUSE_SIZE)));
        // Execute
        Object obj = ModelProxy.updateTile(position, value);
        // Verify
        assertNull(obj);
        verify(mockBoard).updateTile(position, value);
    }

    @Test
    public void test_updateTile_nullBoard_ignore() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertNull(ModelProxy.updateTile(40, 3));
    }
    //endregion

    //region getTile() tests
    @Test
    public void test_getTile_pass() {
        // Set up
        int position = 40;
        doReturn(null).when(mockBoard).getTileNotesOrValue(anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).getTileNotesOrValue(and(gt(0), lt(BOARD_SIZE)));
        // Execute
        Object obj = ModelProxy.getTile(position);
        // Verify
        assertNotNull(obj);
        assertEquals(LinkedList.class, obj.getClass());
        verify(mockBoard).getTileNotesOrValue(position);
    }

    @Test
    public void test_getTile_badIndex_null() {
        // Set up
        int position = -1;
        doReturn(null).when(mockBoard).getTileNotesOrValue(anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).getTileNotesOrValue(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertNull(ModelProxy.getTile(position));
        verify(mockBoard).getTileNotesOrValue(position);
    }

    @Test
    public void test_getTile_nullBoard_null() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertNull(ModelProxy.getTile(40));
    }
    //endregion

    //region isGameOver() tests
    @Test
    public void test_isGameOver_true() {
        // Set up
        doReturn(true).when(mockBoard).isGameOver();
        // Execute & Verify
        assertTrue(ModelProxy.isGameOver());
        verify(mockBoard).isGameOver();
    }

    @Test
    public void test_isGameOver_false() {
        // Set up
        doReturn(false).when(mockBoard).isGameOver();
        // Execute & Verify
        assertFalse(ModelProxy.isGameOver());
        verify(mockBoard).isGameOver();
    }

    @Test
    public void test_isGameOver_nullBoard_false() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertFalse(ModelProxy.isGameOver());
    }
    //endregion

    //region clearTile() tests
    @Test
    public void test_clearTile_pass() {
        // Set up
        int position = 40;
        doReturn(null).when(mockBoard).clearTile(anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).clearTile(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertNotNull(ModelProxy.clearTile(position));
        verify(mockBoard).clearTile(position);
    }

    @Test
    public void test_clearTile_badIndex_null() {
        // Set up
        int position = -1;
        doReturn(null).when(mockBoard).clearTile(anyInt());
        doReturn(new LinkedList<>()).when(mockBoard).clearTile(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertNull(ModelProxy.clearTile(position));
        verify(mockBoard).clearTile(position);
    }

    @Test
    public void test_clearTile_nullBoard_null() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertNull(ModelProxy.clearTile(40));
    }
    //endregion

    //region toggleNoteMode() tests
    @Test
    public void test_toggleNoteMode_true() {
        // Set up
        int position = 40;
        doReturn(false).when(mockBoard).toggleNoteMode(anyInt());
        doReturn(true).when(mockBoard).toggleNoteMode(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertTrue(ModelProxy.toggleNoteMode(position));
        verify(mockBoard).toggleNoteMode(position);
    }

    @Test
    public void test_toggleNoteMode_badIndex_false() {
        // Set up
        int position = -1;
        doReturn(false).when(mockBoard).toggleNoteMode(anyInt());
        doReturn(true).when(mockBoard).toggleNoteMode(and(gt(0), lt(BOARD_SIZE)));
        // Execute & Verify
        assertFalse(ModelProxy.toggleNoteMode(position));
        verify(mockBoard).toggleNoteMode(position);
    }

    @Test
    public void test_toggleNoteMode_nullBoard_false() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertFalse(ModelProxy.toggleNoteMode(40));
    }
    //endregion

    //region getHint() tests
    @Test
    public void test_getHint_pass() {
        // Set up
        int position = 40;
        doReturn(position).when(mockBoard).useHint();
        // Execute & Verify
        assertEquals(position, ModelProxy.getHint());
        verify(mockBoard).useHint();
    }

    @Test
    public void test_getHint_boardSolved_fail() {
        // Set up
        int position = -1;
        doReturn(position).when(mockBoard).useHint();
        // Execute & Verify
        assertEquals(position, ModelProxy.getHint());
        verify(mockBoard).useHint();
    }

    @Test
    public void test_getHint_boardNull_fail() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertEquals(-1, ModelProxy.getHint());
    }
    //endregion

    //region solve() tests
    @Test
    public void test_solve_true() {
        // Set up
        doReturn(true).when(mockBoard).solve();
        // Execute & Verify
        assertTrue(ModelProxy.solve());
        verify(mockBoard).solve();
    }

    @Test
    public void test_solve_false() {
        // Set up
        doReturn(false).when(mockBoard).solve();
        // Execute & Verify
        assertFalse(ModelProxy.solve());
        verify(mockBoard).solve();
    }

    @Test
    public void test_solve_nullBoard_false() {
        // Set up
        ModelProxy.board = null;
        // Execute & Verify
        assertFalse(ModelProxy.solve());
    }
    //endregion
}

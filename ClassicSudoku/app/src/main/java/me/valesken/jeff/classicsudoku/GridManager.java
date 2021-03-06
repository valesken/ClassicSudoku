package me.valesken.jeff.classicsudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.LinkedList;

import me.valesken.jeff.sudoku_model.ModelProxy;

/**
 * Created by Jeff on 7/4/2015.
 * Last updated on 7/12/2015.
 */
public class GridManager {
    private Context context;
    private int boardSize;
    private TableLayout grid;
    private LinkedList[] values;
    private View[] views;
    private GameFragment gameFragment;
    private int currentGridIndex;

    public GridManager(Context _context, int _boardSize, TableLayout _grid, GameFragment _gameFragment) {
        this.context = _context;
        this.grid = _grid;
        this.boardSize = _boardSize;
        this.gameFragment = _gameFragment;
        this.values = ModelProxy.getBoard();
        this.currentGridIndex = -1;
        this.views = new View[values.length];
    }

    @SuppressLint("InflateParams")
    public void initializeGrid() {
        values = ModelProxy.getBoard();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tile;
        int gridIndex;
        for(int row = 0; row < boardSize; ++row) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.grid_row_layout, null, false);
            tableRow.setGravity(Gravity.CENTER);
            for(int column = 0; column < boardSize; ++column) {
                gridIndex = (row * 9) + column;
                tile = inflater.inflate(R.layout.tile_layout, null, false);
                updateItem(gridIndex, tile);
                tile.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // Set up background & borders of tiles
                if (row == 2 || row == 5) {
                    if(column == 8)
                        tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_bottom_no_right));
                    else if (column == 2 || column == 5)
                        tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_bottom_right));
                    else if (column == 3 || column == 6)
                        tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_bottom_left));
                    else
                        tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_bottom));
                }
                else if (column == 2 || column == 5)
                    tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_right));
                else if (column == 3 || column == 6)
                    tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_left));
                else if (column == 8)
                    tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector_no_right));
                else
                    tile.setBackground(context.getResources().getDrawable(R.drawable.tile_selector));
                if(row == 3 || row == 6)
                    tile.findViewById(R.id.tile_top_big_border).setVisibility(View.VISIBLE);

                tile.setId(gridIndex);
                tile.setActivated(false);
                tile.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.setActivated(true);
                        if (currentGridIndex > -1 && v.getId() != currentGridIndex)
                            views[currentGridIndex].setActivated(false);
                        currentGridIndex = v.getId();
                        gameFragment.setCurrentPosition(currentGridIndex, v);
                        return false;
                    }
                });
                views[gridIndex] = tile;
                tableRow.addView(tile);
            }
            grid.addView(tableRow);
        }
    }

    public void updateItem(int gridIndex, View tile) {
        /* set notes invisible*/
        View[] tileNoteViews = new View[boardSize];
        tileNoteViews[0] = tile.findViewById(R.id.tile_note_1);
        tileNoteViews[1] = tile.findViewById(R.id.tile_note_2);
        tileNoteViews[2] = tile.findViewById(R.id.tile_note_3);
        tileNoteViews[3] = tile.findViewById(R.id.tile_note_4);
        tileNoteViews[4] = tile.findViewById(R.id.tile_note_5);
        tileNoteViews[5] = tile.findViewById(R.id.tile_note_6);
        tileNoteViews[6] = tile.findViewById(R.id.tile_note_7);
        tileNoteViews[7] = tile.findViewById(R.id.tile_note_8);
        tileNoteViews[8] = tile.findViewById(R.id.tile_note_9);
        for(View v : tileNoteViews)
            v.setVisibility(View.INVISIBLE);
        // value tile
        if(!ModelProxy.tileIsNoteMode(gridIndex)) {
            TextView textView = (TextView) tile.findViewById(R.id.tile_value_text);
            textView.setVisibility(View.VISIBLE);
            if ((Integer)values[gridIndex].get(0) > 0)
                textView.setText(String.format("%d", (Integer)values[gridIndex].get(0)));
            else
                textView.setText(" ");

            if(ModelProxy.tileIsOrig(gridIndex)) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextColor(Color.BLACK);
            }
        }
        // note tile
        else {
            tile.findViewById(R.id.tile_value_text).setVisibility(View.INVISIBLE);
            for(Object note: values[gridIndex])
                tileNoteViews[(Integer)note-1].setVisibility(View.VISIBLE);
        }
    }

    public boolean updateTile(int gridIndex, int value)
    {
        ModelProxy.updateTile(gridIndex, value);
        values[gridIndex] = ModelProxy.getTile(gridIndex);
        this.updateItem(gridIndex, views[gridIndex]);
        return ModelProxy.isGameOver();
    }

    public void clearTile(int gridIndex)
    {
        ModelProxy.clearTile(gridIndex);
        values[gridIndex] = ModelProxy.getTile(gridIndex);
        this.updateItem(gridIndex, views[gridIndex]);
    }

    public void toggleMode(int gridIndex)
    {
        ModelProxy.toggleNoteMode(gridIndex);
        values[gridIndex] = ModelProxy.getTile(gridIndex);
        this.updateItem(gridIndex, views[gridIndex]);
    }

    public boolean getHint()
    {
        int gridIndex = ModelProxy.getHint();
        if (gridIndex > -1) {
            values[gridIndex] = ModelProxy.getTile(gridIndex);
            this.updateItem(gridIndex, views[gridIndex]);
        }
        return ModelProxy.isGameOver();
    }

    public void solve()
    {
        ModelProxy.solve();
        for(int i = 0; i < values.length; ++i) {
            values[i] = ModelProxy.getTile(i);
            this.updateItem(i, views[i]);
        }
    }
}

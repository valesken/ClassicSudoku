package com.jeffvk.classicsudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.jeffvk.classicsudoku.game.*;

public class NewGameActivity extends Activity {

	private Board game;
	private GridView grid;
	private CheckedTextView lastGridBox;
	private int lastGridBoxIndex;
	private TextView clock;
	private Thread t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		
		int difficulty = 0;
		String content = "";
		game = new Board();
		Intent cameFrom = getIntent();
		content = cameFrom.getStringExtra(MainActivity.EXTRA_LOAD);
		if(content.length() == 1)
		{
			difficulty = (int)(content.charAt(0)-48);
			game.newBoard(difficulty);
		}
		else
		{
			File file = new File(this.getFilesDir()+"/sudokuGame.txt");
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String state = br.readLine();
				br.close();
				System.out.println(state);
				game.loadBoard(state);
			} catch (IOException e) {
				e.printStackTrace();
				Toast
				  .makeText(this, "Can't Read File", Toast.LENGTH_SHORT)
				  .show();
			}
		}
		
		grid = (GridView)findViewById(R.id.grid);
		clock = (TextView) findViewById(R.id.clock);
		t = runClock();
		
		showBoard();
		
		grid.setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View thisGridBox, int position, long id)
					{
						if(lastGridBox != null)
						{
							if(game.checkTileIsOrig(lastGridBoxIndex))
								lastGridBox.setBackgroundColor(Color.LTGRAY);
							else
								lastGridBox.setBackgroundColor(Color.WHITE);
						}
						thisGridBox.setBackgroundColor(Color.CYAN);
						lastGridBox = (CheckedTextView) thisGridBox;
						lastGridBoxIndex = position;
					}
				});
		
		grid.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				for(int i = 0; i < 81; i++)
				{
					if(game.checkTileIsOrig(i))
						grid.getChildAt(i).setBackgroundColor(Color.LTGRAY);
				}
			}
		});
		t.start();
	}
	
	public void save(View button)
	{
		try {
			String path = this.getFilesDir() + "/sudokuGame.txt";
			File file = new File(path);
			if(!(file.exists()))
				file.createNewFile();
			String state = game.save();
			FileOutputStream fOut = openFileOutput("sudokuGame.txt", MODE_PRIVATE);
			fOut.write(state.getBytes());
			fOut.close();
			Toast
			  .makeText(this, "Game Saved!", Toast.LENGTH_SHORT)
			  .show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast
			  .makeText(this, "Save Failed :(", Toast.LENGTH_SHORT)
			  .show();
		}
		
	}
	
	public void hint(View button)
	{
		if(lastGridBox != null)
		{
			if(!game.isStarted())
				game.start();
			game.getHint(lastGridBoxIndex);
			lastGridBox.setText(Integer.toString(game.getTileValue(lastGridBoxIndex)));
		}
	}
	
	public void solve(View button)
	{
		if(!game.isStarted())
			game.start();
		game.solve();
		int [] solution = new int [81];
		game.getBoard(solution);
		for(int i = 0; i < 81; i++)
			((CheckedTextView)grid.getChildAt(i)).setText(Integer.toString(solution[i]));
		Toast
		  .makeText(this, "Game finished!", Toast.LENGTH_LONG)
		  .show();
		t.interrupt();
	}
	
	public void numClick(View button)
	{
		if(lastGridBox != null)
		{
			if(!game.isStarted())
				game.start();
			int value = game.getTileValue(lastGridBoxIndex);
			switch(button.getId())
			{
			case R.id.clear:
				value = 0;
				break;
			case R.id.num1:
				value = 1;
				break;
			case R.id.num2:
				value = 2;
				break;
			case R.id.num3:
				value = 3;
				break;
			case R.id.num4:
				value = 4;
				break;
			case R.id.num5:
				value = 5;
				break;
			case R.id.num6:
				value = 6;
				break;
			case R.id.num7:
				value = 7;
				break;
			case R.id.num8:
				value = 8;
				break;
			case R.id.num9:
				value = 9;
				break;
			default:
				break;
			}
			if(game.checkTile(lastGridBoxIndex, value))
			{
				game.setTile(lastGridBoxIndex, value);
				if(value != 0)
					lastGridBox.setText(Integer.toString(value));
				else
					lastGridBox.setText(" ");
			}
			else
			{
				Toast
				  .makeText(this, "Oops, that can't go there.", Toast.LENGTH_LONG)
				  .show();
			}
		}
		clock.setText(game.getTime());
		if(game.isWon())
		{
			Toast
			  .makeText(this, "Game Finished!", Toast.LENGTH_LONG)
			  .show();
			t.interrupt();
		}
	}
	
	private void showBoard()
	{
		int [] intValues = new int[81];
		String [] stringValues = new String[81];
		game.getBoard(intValues);
		for(int i = 0; i < 81; i++)
		{
			if(intValues[i] == 0)
				stringValues[i] = " ";
			else
				stringValues[i] = Integer.toString(intValues[i]);
		}
		grid.setAdapter(new ArrayAdapter<String>(this, R.layout.grid_text_view, stringValues));
		clock.setText(game.getTime());
	}
	
	private Thread runClock()
	{
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	while(true)
	        	{
	        		try
	        		{
	        			Thread.sleep(1000);
	        		}
	        		catch(InterruptedException e)
	        		{
	        			break;
	        		}
	        		clock.post(new Runnable() {
	        			public void run() {
	        				clock.setText(game.getTime());
	        			}
	        		});
	        	}
	        }
	    });
		return t;
	}

}

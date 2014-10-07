package com.jeffvk.classicsudoku;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_VALUE = "com.jeffvk.classicsudoku.VALUE";
	public final static String EXTRA_LOAD = "com.jeffvk.classicsudoku.LOAD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}
	
	public void toNew(View button)
	{
		final View newGameDialogView = getLayoutInflater().inflate(R.layout.activity_new_game_dialog, null);
		
		new AlertDialog.Builder(this)
		.setTitle("Choose Difficulty")
		.setView(newGameDialogView)
		.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {} // null onClick to just go back
		})
		.setNegativeButton("Play", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup radGroup = (RadioGroup)newGameDialogView.findViewById(R.id.difficultyRadGroup);
				int id = radGroup.getCheckedRadioButtonId(), difficulty = 1;
				switch(id)
				{
				case R.id.mediumRadButton:
					difficulty = 2;
					break;
				case R.id.hardRadButton:
					difficulty = 3;
					break;
				case R.id.randomRadButton:
					difficulty = 4;
					break;
				default:
					break;
				}
				createGame(difficulty);
			}
		})
		.show();
	}
	
	public void toLoad(View button)
	{
		File file = new File(this.getFilesDir()+"/sudokuGame.txt");
		if(file.exists())
		{
			Intent intent = new Intent(this, NewGameActivity.class);
			intent.putExtra(EXTRA_LOAD, "load");
			Toast
			  .makeText(this, "Loading...", Toast.LENGTH_LONG)
			  .show();
			startActivity(intent);
		}
		else
		{
			Toast
			  .makeText(this, "No Saved Game Found", Toast.LENGTH_SHORT)
			  .show();
		}
	}
	
	public void toAbout(View button)
	{
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	private void createGame(int difficulty)
	{
		Intent intent = new Intent(this, NewGameActivity.class);
		intent.putExtra(EXTRA_LOAD, Integer.toString(difficulty));
		startActivity(intent);
	}

}

package com.maguirrem.matchmaker;

/*
 *   Copyright 2010 Miguel Martinez de Aguirre Sutton
 * 
 *   This file is part of GameSetup.
 *
 *   GameSetup is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   GameSetup is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with GameSetup.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.maguirrem.matchmaker.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddMatch extends Activity
{
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmatch);
		context = getBaseContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		((EditText) findViewById(R.id.EditMin)).setText(prefs.getString("min", ""));
		((EditText) findViewById(R.id.EditTime)).setText(prefs.getString("time", ""));
		((EditText) findViewById(R.id.EditLoc)).setText(prefs.getString("loc", ""));
		findViewById(R.id.ButtonDoAdd).setOnClickListener(clickListener);
	}
	
	OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Add switch if more buttons
			Backend be = new Backend(context);
			Intent i = new Intent();
			i.putExtra("code", 
					be.start("start "+((EditText) findViewById(R.id.EditMin)).getText()+" " 
						+ ((EditText) findViewById(R.id.EditTime)).getText()+" "
						+ ((EditText) findViewById(R.id.EditLoc)).getText(),true));
			be.close();
			i.putExtra("min", ((EditText) findViewById(R.id.EditMin)).getText().toString());
			i.putExtra("time", ((EditText) findViewById(R.id.EditTime)).getText().toString());
			i.putExtra("loc", ((EditText) findViewById(R.id.EditLoc)).getText().toString());
			i.putExtra("timestamp", "Now");
			setResult(0,i);
			finish();
		}
		
	};
	
}

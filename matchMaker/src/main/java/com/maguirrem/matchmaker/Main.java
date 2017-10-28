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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * 0  _id
 * 1  thread_id
 * 2  address
 * 3  person
 * 4  date
 * 5  protocol
 * 6  read
 * 7  status
 * 8  type
 * 9  reply_path_present
 * 10 subject
 * 11 body
 * 12 service_center
 */

public class Main extends Activity
{
	
	boolean CleanClicked = false;
	private static Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		((Button)findViewById(R.id.ButtonMatches)).setOnClickListener(clickListener);
		((Button)findViewById(R.id.ButtonPeople)).setOnClickListener(clickListener);
		((Button)findViewById(R.id.ButtonPrefs)).setOnClickListener(clickListener);
		((Button)findViewById(R.id.ButtonAbout)).setOnClickListener(clickListener);
		((Button)findViewById(R.id.ButtonClean)).setOnClickListener(clickListener);
		context = getBaseContext();
		
	}
	
	OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.ButtonMatches:
					startActivity(new Intent(context, Matches.class));
					break;
				case R.id.ButtonPeople:
					startActivity(new Intent(context, People.class));
					break;
				case R.id.ButtonPrefs:
					startActivity(new Intent(context, Prefs.class));
					break;
				case R.id.ButtonAbout:
					startActivity(new Intent(context, About.class));
					break;
				case R.id.ButtonClean:
					runClean();
					break;
			}
		}
	};
	
	
	@SuppressWarnings("deprecation")
	// TODO use activity with OK/CANCEL buttons?
	// Clean start //
	void runClean()
	{
		if (CleanClicked)
		{
			CleanClicked = false;
		/*	 */	
		  Cursor c = managedQuery(Uri.parse("content://sms/"), new String[] {"_id"}, "body LIKE '" +
					PreferenceManager.getDefaultSharedPreferences(context).getString("ucode", "wy") + "%'", null, null);
			int cLen = c.getCount();
			Toast.makeText(context, "Deleting " + Integer.valueOf(cLen).toString() + " messages", Toast.LENGTH_SHORT).show();
			c.moveToFirst();
			for (int i = 0; i < cLen; i++)
			{
				context.getContentResolver().delete(Uri.parse("content://sms/"), "_id == " + c.getString(0), null);
				c.moveToNext();
			}
			Toast.makeText(context, "Finished", Toast.LENGTH_SHORT).show();
		}
		else
		{
			CleanClicked = true;
			Toast.makeText(context, 
					"This will remove all messages from your inbox which start with the current ucode. Click again to continue.",
					Toast.LENGTH_LONG).show();
		}
	}
	// Clean end //
}
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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AddPerson extends Activity
{
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addperson);
		context = getBaseContext();
		findViewById(R.id.ButtonDoAdd).setOnClickListener(clickListener);
	}
	
	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			Backend be = new Backend(context);
			be.join(" join "+((EditText) findViewById(R.id.EditName)).getText(), ((EditText) findViewById(R.id.EditNum)).getText().toString());
			be.close();
			Intent i = new Intent();
			i.putExtra("name", ((EditText) findViewById(R.id.EditName)).getText().toString());
			i.putExtra("number", ((EditText) findViewById(R.id.EditNum)).getText().toString());
			setResult(0,i);
			finish();
		}
		
	};
}

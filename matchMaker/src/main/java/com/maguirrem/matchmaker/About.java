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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class About extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		((TextView)findViewById(R.id.TextViewLicenseTitle)).setOnClickListener(clickListener);
		((TextView)findViewById(R.id.TextViewLicenseName)).setOnClickListener(clickListener);
	}
	
	OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			TextView license = (TextView) findViewById(R.id.TextViewLicense);
			if (license.getVisibility()==View.GONE)
				license.setVisibility(View.VISIBLE);
			else
				license.setVisibility(View.GONE);
		}
	};
	
}

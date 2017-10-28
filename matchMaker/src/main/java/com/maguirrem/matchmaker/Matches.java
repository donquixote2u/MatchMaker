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

import java.util.ArrayList;
import java.util.HashMap;

import com.maguirrem.matchmaker.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Matches extends ListActivity
{
	private static final int ButtonStartMatch = 1;
	private SimpleAdapter adapter;
	private Backend be;
	private static Context context;
	private LinearLayout ll;
	private ArrayList<HashMap<String, String>> matches;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getBaseContext();
		be = new Backend(context);
		matches = be.getAllMatches();
		adapter = new SimpleAdapter(
						context,
						matches,
						R.layout.matchesrows,
						new String[] {"code","yesmin","no","time","timestamp","loc"},
						new int[] {R.id.code,R.id.yesmin,R.id.no,R.id.time,R.id.timestamp,R.id.loc});
		setListAdapter(adapter);
	}
	
	@Override
	protected void onDestroy()
	{
		be.close();
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		if (position==0)
		{
			try
			{
				// No fail if already made and GONE
				((LinearLayout) v).getChildAt(2).setVisibility(View.VISIBLE);
			}
			catch (NullPointerException e)
			{
				// NullPointer as getChildAt(2) returns null if ll not yet created
				//TODO
				final int i = 2; // number of buttons
				ll = new LinearLayout(context);
				((LinearLayout) v).addView(ll);
				Button b1 = new Button(context);
				b1.setText("Add new match");
				b1.setWidth(((LinearLayout) ll.getParent()).getWidth()/i);
				b1.setId(ButtonStartMatch);
				b1.setOnClickListener(clickListener);
				Button b2 = new Button(context);
				b2.setText("Close");
				b2.setWidth(((LinearLayout) ll.getParent()).getWidth()/i);
				b2.setId(R.id.ButtonClose);
				b2.setOnClickListener(clickListener);
				ll.addView(b1);
				ll.addView(b2);
			}
		}
		else
		{
			LinearLayout ll = (LinearLayout) ((LinearLayout) v).getChildAt(1);
			ll.setVisibility(View.VISIBLE);
			((LinearLayout) ll.getChildAt(1)).getChildAt(0).setOnClickListener(clickListener); // Stop
			((LinearLayout) ll.getChildAt(1)).getChildAt(1).setOnClickListener(clickListener); // Close
		}
	}
	
	
	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.ButtonStop:
				String code = (String) ((TextView) ((LinearLayout) ((LinearLayout) v.getParent().getParent().getParent()).getChildAt(0)).getChildAt(0)).getText();
				Toast.makeText(context, be.stop(" stop " + code), Toast.LENGTH_SHORT).show();
				// TODO change matches so no trouble if delete and add without re-opening
				// delete all and merge?
				((LinearLayout) v.getParent().getParent().getParent()).removeAllViews();
				break;
			case R.id.ButtonClose:
				if (v.getParent().equals(ll))
					ll.setVisibility(View.GONE);
				else
	((LinearLayout) v.getParent().getParent()).setVisibility(View.GONE);
				break;
			case ButtonStartMatch:
				startActivityForResult(new Intent(context, AddMatch.class), 0);
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("code", data.getStringExtra("code"));
		item.put("yesmin", "0/"+data.getStringExtra("min"));
		item.put("no", "(0)");
		item.put("time", data.getStringExtra("time"));
		item.put("timestamp", data.getStringExtra("timestamp"));
		item.put("loc", data.getStringExtra("loc"));
		matches.add(item);
//		Log.i("Matches",matches.toString());
		((SimpleAdapter) getListAdapter()).notifyDataSetInvalidated();
		ll.setVisibility(View.GONE);
	}
	
}
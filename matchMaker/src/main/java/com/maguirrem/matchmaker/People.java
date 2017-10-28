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

public class People extends ListActivity
{
	private static final int ButtonAddPerson = 1, ButtonAddGroup = 2;
	private SimpleAdapter adapter;
	private Backend be;
	private static Context context;
	private LinearLayout ll;
	private ArrayList<HashMap<String, String>> people;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getBaseContext();
		be = new Backend(context);
		people = be.getAllPeople();
		adapter = new SimpleAdapter(
						context,
						people,
						R.layout.peoplerows,
						new String[] {"name","number","yes","no","away"},
						new int[] {R.id.name,R.id.number,R.id.yes,R.id.no,R.id.away});
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
				ll.setVisibility(View.VISIBLE);
			}
			catch (NullPointerException e)
			{
				// NullPointer as getChildAt(2) returns null if ll not yet created
				final int i = 3; // number of buttons
				ll = new LinearLayout(context);
				((LinearLayout) v).addView(ll);
				Button b1 = new Button(context);
				b1.setText("Add new person");
				b1.setWidth(((LinearLayout) ll.getParent()).getWidth()/i);
				b1.setId(ButtonAddPerson);
				b1.setOnClickListener(clickListener);
				Button b3 = new Button(context);
				b3.setText("Add Group");
				b3.setWidth(((LinearLayout) ll.getParent()).getWidth()/i);
				b3.setId(ButtonAddGroup);
				b3.setOnClickListener(clickListener);
				Button b2 = new Button(context);
				b2.setText("Close");
				b2.setWidth(((LinearLayout) ll.getParent()).getWidth()/i);
				b2.setId(R.id.ButtonClose);
				b2.setOnClickListener(clickListener);
				ll.addView(b1);
				ll.addView(b3);
				ll.addView(b2);
			}
		}
		else
		{
			// TODO as Matches? clarity, no repetition vs variable assignment
			((LinearLayout) v).getChildAt(1).setVisibility(View.VISIBLE);
			((LinearLayout) ((LinearLayout) ((LinearLayout) v).getChildAt(1)).getChildAt(1)).getChildAt(0).setOnClickListener(clickListener);
			((LinearLayout) ((LinearLayout) ((LinearLayout) v).getChildAt(1)).getChildAt(1)).getChildAt(1).setOnClickListener(clickListener);
		}
	}
	
	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.ButtonDelete:
					// findViewById?
					String result = be.leave(((TextView) ((LinearLayout) ((LinearLayout) v.getParent().getParent().getParent()).getChildAt(0)).getChildAt(1)).getText().toString());
					Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
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
				case ButtonAddPerson:
					startActivityForResult(new Intent(context, AddPerson.class), 0);
					break;
				case ButtonAddGroup:
					startActivityForResult(new Intent(context, AddGroup.class), 0);
					break;
			}
		}
		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", data.getStringExtra("name"));
		item.put("number", data.getStringExtra("number"));
		item.put("yes", "0");
		item.put("no", "0");
		item.put("away", "False");
		people.add(item);
		((SimpleAdapter) getListAdapter()).notifyDataSetInvalidated();
		ll.setVisibility(View.GONE);
	}
}

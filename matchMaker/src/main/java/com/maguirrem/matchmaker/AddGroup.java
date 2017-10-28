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

import java.util.Iterator;

import com.bvw.android_library.ContactDetails;
import com.bvw.android_library.getContactGroups;
import com.bvw.android_library.getContacts;
import com.maguirrem.matchmaker.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddGroup extends Activity 
{
	Context context;
	Spinner groupspinner;
	String GroupName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addgroup);
		getContactGroups cGroup = new getContactGroups(getBaseContext());
	     //ContactGroupAdapter cAdapter = new ContactGroupAdapter(this, cGroup.contactgroups);
		// Apply the adapter to the spinner
	    groupspinner = (Spinner) findViewById(R.id.group_spinner);
	 // Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this,
	    		android.R.layout.simple_spinner_item,  cGroup.getGroupNames());
	    // Specify the layout to use when the list of choices appears
	    cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
		groupspinner.setAdapter(cAdapter);
		findViewById(R.id.ButtonAddGroup).setOnClickListener(clickListener);
	}
	
	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{	Context mcontext = getApplicationContext();
			groupspinner = (Spinner) findViewById(R.id.group_spinner);
			// from supplied Group Name, get group id, then all members with that id
			String GroupName=groupspinner.getSelectedItem().toString();
			Log.d("Group:",GroupName);
	        // getContacts GroupContacts = new getContacts(getApplicationContext(), GroupName);
	        getContacts GroupContacts = new getContacts(mcontext, GroupName);
			Backend be = new Backend(mcontext);
			// add loop here to  get each group member name, number
			Iterator<ContactDetails> contactsindex = GroupContacts.contactList.iterator();
			ContactDetails details = new ContactDetails();
			Intent i = new Intent();
			while (contactsindex.hasNext()) {
			details=contactsindex.next();	
			Log.d("GroupContact:",details.name + ";" + details.phone);
			be.join(" join "+ details.name, details.phone);
			i.putExtra("name", details.name);
			i.putExtra("number", details.phone);
			}
			be.close();
			setResult(0,i);
			finish();
		}
		
	};

}

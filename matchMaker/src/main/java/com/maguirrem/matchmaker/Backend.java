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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maguirrem.matchmaker.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class Backend
{
	public static final String syntax = "start min#people(default4) time(defaultASAP) location(defaultHeybridge)\n" +
	"yes code\n" +
	"no code\n" +
	"stop code\n" +
	"away\n" +
	"back\n" +
	"leave\n" +
	"syntax\n" +
	"where code is the code given as reply to start or in invitation.";
	
	private DbHandler db;
	private SmsManager smsman;
	private Context context;
	
	public Backend(Context ctxt)
	{
		context = ctxt;
		db = new DbHandler(ctxt);
		smsman = SmsManager.getDefault();
	}
	
	public void close()
	{
		db.close();
	}
	
	public Boolean existsPerson(String number) { return db.existsPerson(number); }
	
	public String yes(String parse, String number)
	{
		Matcher yesMatcher = Pattern.compile(" *yes *([a-zA-Z]*)").matcher(parse);
		yesMatcher.find();
		String code = yesMatcher.group(1);
		Cursor yes;
		try
		{
			yes = db.yes(code,number);
		}
		catch (SQLiteException e)
		{
			Log.e("Receiver","yes,"+parse+": "+e.toString());
			return "There seems to be a mistake. That code was not found: "+yesMatcher.group(1);
		}
		int yesCount = yes.getCount();
		if (yesCount == db.getMinMatch(code))
		{
			// Text those who replied yes to confirm
			yes.moveToFirst();
			for (int i = 0; i < yesCount; i++)
			{
				smsman.sendTextMessage(yes.getString(0), null, context.getString(R.string.yesConf)+"("+code+")", null, null);
				yes.moveToNext();
			}
			return context.getString(R.string.yesConfLast);
		}
		if (yesCount > db.getMinMatch(code))
			return context.getString(R.string.yesStarted);
		else
		{
			return context.getString(R.string.yesWait);
		}
	}
	
	public String no(String parse, String number)
	{
		try
		{
			Matcher noMatcher = Pattern.compile(" *no *([a-zA-Z]*)").matcher(parse);
			noMatcher.find();
			db.no(noMatcher.group(1),number);
		}
		catch(SQLiteException e)
		{
			Log.e("Backend",e.toString());
			return context.getString(R.string.errorCodeNotFound);
		}
		return context.getString(R.string.okayThanks);
	}
	
	public String start(String parse) { return start(parse,false); }
	public String start(String parse, Boolean raw)
	{
		Matcher startMatcher = Pattern.compile(" *start *([0-9]*) *([^ ]*) *([a-zA-Z]*)").matcher(parse);
		if (startMatcher.find())
		{
			String[] info = db.addMatch(startMatcher.group(1), startMatcher.group(2), startMatcher.group(3));
			Cursor people = db.getPeople(true);
			int count = people.getCount();
			people.moveToFirst();
			for (int i = 0; i < count; i++)
			{
				smsman.sendTextMessage(people.getString(0), null, context.getString(R.string.startAll)+info[1]+", "+info[2]+". Code: "+info[0], null, null);
				people.moveToNext();
			}
			if (raw)
				return info[0];
			return context.getString(R.string.startReply)+info[0];
		}
		else
		{
			Log.d("Receiver",context.getString(R.string.errorSyntax));
			return context.getString(R.string.errorSyntax);
		}
	}
	
	public String stop(String parse)
	{
		// TODO consider changing to not need start|stop etc. for interior use
		Matcher stopMatcher = Pattern.compile(" *stop *([a-zA-Z]*)").matcher(parse);
		stopMatcher.find();
		db.deleteMatch(stopMatcher.group(1));
		return context.getString(R.string.stopSucc)+stopMatcher.group(1);
	}
	
	public String leave(String number)
	{
		db.deletePerson(number);
		return context.getString(R.string.leaveSucc);
	}
	
	public String away(String number)
	{
		db.awayPerson(number);
		return context.getString(R.string.awaySucc);
	}
	
	public String back(String number)
	{
		db.backPerson(number);
		return context.getString(R.string.backSucc);
	}
	
	public String join(String parse, String number)
	{
		Matcher joinMatcher = Pattern.compile(" *join *([a-zA-Z]*)").matcher(parse);
		joinMatcher.find();
		db.addPerson(number, joinMatcher.group(1));
		return context.getString(R.string.joinSucc);
	}
	
	public ArrayList<HashMap<String, String>> getAllMatches()
	{
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tmp = new HashMap<String, String>();
		tmp.put("code", "Code");
		tmp.put("yesmin", "yes/min");
		tmp.put("no", "(no)");
		tmp.put("time", null);
		tmp.put("datetime", null);
		tmp.put("loc", null);
		list.add(tmp);
		Cursor c = db.getMatches(new String[] {"tablename","min","time","timestamp","loc"});
		int count = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < count; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code", c.getString(0));
			map.put("yesmin", Integer.toString(db.yes(c.getString(0)).getCount())+"/"+c.getString(1));
			map.put("no", "("+Integer.toString(db.no(c.getString(0)).getCount())+")");
			map.put("time", c.getString(2));
			Matcher stampMatch = Pattern.compile("\\d{4}-(\\d{2})-(\\d{2}) (\\d{2}:\\d{2}):\\d{2}").matcher(c.getString(3));
			stampMatch.find();
			map.put("timestamp", stampMatch.group(3)+" "+stampMatch.group(2)+"/"+stampMatch.group(1));
			map.put("loc", c.getString(4));
			list.add(map);
			c.moveToNext();
		}
		return list;
	}
	
	public ArrayList<HashMap<String, String>> getAllPeople()
	{
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tmp = new HashMap<String, String>();
		tmp.put("name", "Name");
		tmp.put("number","Number");
		tmp.put("yes",null);
		tmp.put("no",null);
		tmp.put("away", "Away");
		list.add(tmp);
		Cursor c = db.getPeople(false, new String[] {"name","number","yes","no","away"});
		int count = c.getCount();
		c.moveToFirst();
		for (int i = 0; i < count; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", c.getString(0));
			map.put("number",c.getString(1));
			map.put("yes",c.getString(2));
			map.put("no",c.getString(3));
			map.put("away", new Boolean(c.getInt(4)==1).toString());
			list.add(map);
			c.moveToNext();
		}
		return list;
	}
	
}

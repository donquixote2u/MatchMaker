// TODO let people change their minds

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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class DbHandler
{
	private static final String PEOPLE_CREATE = 
		"CREATE TABLE IF NOT EXISTS people(" +
		" number text primary key not null," +
		" name text not null," +
		" yes int default 0," +
		" no int default 0," +
		" away int default 0" + // bool
		")";
	
	private static final String MATCHES_CREATE =
		"CREATE TABLE IF NOT EXISTS matches(" +
		" _id integer primary key autoincrement," +
		" tablename text not null," +
		" timestamp text default current_timestamp," +
		" min integer not null," +
		" loc text not null," +
		" time text not null" +
		")";
	
	private static final String MATCH_TABLE = "(number primary key not null, coming text not null);";
	
	private static final int PEOPLE_VERSION = 1;
	private static final int MATCHES_VERSION = 1;
	private SQLiteDatabase people;
	private SQLiteDatabase matches;
	private static Context context;
	
	public DbHandler(Context ctxt)
	{
		people = ctxt.openOrCreateDatabase("people", 0, null);
		matches = ctxt.openOrCreateDatabase("matches", 0, null);
		if (people.getVersion() != PEOPLE_VERSION)
			updatePeople();
		if (matches.getVersion() != MATCHES_VERSION)
			updateMatches();
		context = ctxt;
	}
	
	public void close()
	{
		people.close();
		matches.close();
	}
	
	public void addPerson(String number, String name)
	{
		ContentValues initVals = new ContentValues();
		initVals.put("number", number);
		initVals.put("name", name);
		people.insert("people", null, initVals);
	}
	
	public void deletePerson(String number)
	{
		people.delete("people", "number = '" + number + "'", null);
	}
	
	public void awayPerson(String number)
	{
		/** Sets number's away status to true */
		ContentValues awayVal = new ContentValues();
		awayVal.put("away", 1);
		people.update("people", awayVal, "number="+number, null);
	}
	
	public void backPerson(String number)
	{
		/** Sets number's away status to false */
		ContentValues awayVal = new ContentValues();
		awayVal.put("away", 0);
		people.update("people", awayVal, "number="+number, null);
	}
	
	public boolean existsPerson(String number)
	{
		return people.query("people", new String[] {}, "number='"+number+"'", null, null, null, null).getCount() == 1;
	}
	
	public Cursor getPeople() { return getPeople(false, new String[] {"number","name"}); }
	public Cursor getPeople(Boolean filterAway) { return getPeople(filterAway, new String[] {"number","name"}); }
	public Cursor getPeople(Boolean filterAway, String[] cols)
	{
		/** Returns a cursor with number and name of all subscribed people in alphabetical order of names. */
		String where = null;
		if (filterAway)
		{
			where = "away='0'";
		}
		return people.query("people", cols, where, null, null, null, "name ASC");

	}
	
	public int getMinMatch(String code)
	{
		/** Returns minimum number of active match with code <b>code</b> */
		Cursor c = matches.query("matches", new String[] {"min"}, "tablename='"+code+"'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(0);
	}
	
	public Cursor getMatches() { return getMatches(new String[] {"tablename","min"}); }
	public Cursor getMatches(String[] cols)
	{
		/** Returns a cursor with code and minimum number of all active matches. */
		return matches.query("matches", cols, null, null, null, null, null);
	}
	
	public String[] addMatch(String min, String time, String loc)
	{
		/** Initiates a table for new match with first available code and 
		 *  places info in matches table. Returns chosen code.
		 */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (min.length() == 0)
			min = prefs.getString("min", "4");
		if (time.length() == 0)
			time = prefs.getString("time", "asap");
		if (loc.length() == 0)
			loc = prefs.getString("loc", "Heybridge");
		
		// find first available code and create code's table
		// TODO consider using matches table to get code
		String code = null;
		for(int i = 97; i < 123; i++)
		{
			code = new Character((char)i).toString();
			try
			{
				matches.execSQL("CREATE TABLE " + code + MATCH_TABLE);
				break;
			}
			catch(SQLException e)
			{}
		}
		
		// place new match into matches table
		ContentValues values = new ContentValues();
		values.put("tablename", code);
		values.put("min", min);
		values.put("loc", loc);
		values.put("time", time);
		matches.insert("matches", null, values);
		
		return new String[] {code,time,loc};
	}
	
	public void deleteMatch(String code)
	{
		/** Removes code's table and removes code's entry in matches table. */
		matches.execSQL("DROP TABLE IF EXISTS "+code);
		matches.delete("matches", "tablename='"+code+"'", null);
	}
	
	public Cursor yes(String code) { return yes(code,null); }
	public Cursor yes(String code, String number)
	{
		/** Returns a cursor to database query containing all coming for match code code. 
		 *  If number is given, inserts number and yes to code's table.
		 */
		if (number!=null)
		{
			ContentValues values = new ContentValues();
			values.put("number",number);
			values.put("coming","yes");
			matches.insert(code, null, values);
			people.execSQL("UPDATE people SET yes=yes+1 WHERE number='"+number+"'");
		}
		return matches.query(code, new String[] {"number"}, "coming = 'yes'", null, null, null, null);
	}
	
	public Cursor no(String code) { return no(code,null); }
	public Cursor no(String code, String number)
	{
		/** Returns a cursor to database query containing all not coming for match code code. 
		 * If number is given, inserts number and no to code's table.
		 */
		if (number!=null)
		{
			ContentValues values = new ContentValues();
			values.put("number", number);
			values.put("coming", "no");
			matches.insert(code, null, values);
			people.execSQL("UPDATE people SET no=no+1 WHERE number='"+number+"'");
		}
		return matches.query(code, new String[] {"number"}, "coming = 'no'", null, null, null, null);
	}
	
	private void updatePeople()
	{
		/** Upgrade people db to newest version, starting at v0 and in order. */
		if (people.getVersion() == 0)
		{
			people.execSQL("PRAGMA writable_schema = 1;" +
					" delete from sqlite_master where type = 'table';" +
					" PRAGMA writable_schema = 0;");
			people.execSQL(PEOPLE_CREATE);
			people.setVersion(1);
		}
	}
	
	private void updateMatches()
	{
		/** Upgrade matches db to newest version, starting at v0 and in order. */
		if (matches.getVersion() == 0)
		{
			matches.execSQL("PRAGMA writable_schema = 1;" +
					" delete from sqlite_master where type = 'table';" +
					" PRAGMA writable_schema = 0;");
			matches.execSQL(MATCHES_CREATE);
			matches.setVersion(1);
		}
	}
}


/*
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHandler {
    class Row extends Object {
        public long _Id;
        public String code;
        public String name;
        public String gender;
    }

    private static final String DATABASE_CREATE =
        "create table BIODATA(_id integer primary key autoincrement, "
            + "code text not null,"
            + "name text not null"
            +");";

    private static final String DATABASE_NAME = "PERSONALDB";

    private static final String DATABASE_TABLE = "BIODATA";

    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public DbHandler(Context ctx)
    {
        try
        {
            db = ctx.openDatabase(DATABASE_NAME, null);
        }
        catch (FileNotFoundException e) 
        {
            try
            {
                db =
                    ctx.createDatabase(DATABASE_NAME, DATABASE_VERSION, 0,
                        null);
                db.execSQL(DATABASE_CREATE);
            }
            catch (FileNotFoundException e1)
            {
                db = null;
            }
        }
    }

    public void close() {
        db.close();
    }

    public void createRow(String code, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("code", code);
        initialValues.put("name", name);
        db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowId) {
        db.delete(DATABASE_TABLE, "_id=" + rowId, null);
    }

    public List<Row> fetchAllRows() {
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
            Cursor c =
                db.query(DATABASE_TABLE, new String[] {
                    "_id", "code", "name"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
                row._Id = c.getLong(0);
                row.code = c.getString(1);
                row.name = c.getString(2);
                ret.add(row);
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
        return ret;
    }

    public Row fetchRow(long rowId)
    {
        Row row = new Row();
        Cursor c =
            db.query(DATABASE_TABLE, new String[] {
                "_id", "code", "name"}, "_id=" + rowId, null, null,
                null, null);
        if (c.getCount() > 0)
        {
            c.moveToFirst();
            row._Id = c.getLong(0);
            row.code = c.getString(1);
            row.name = c.getString(2);
            return row;
        }
        else
        {
            row._Id = -1;
            row.code = row.name= null;
        }
        return row;
    }

    public void updateRow(long rowId, String code, String name) 
    {
        ContentValues args = new ContentValues();
        args.put("code", code);
        args.put("name", name);
        db.update(DATABASE_TABLE, args, "_id=" + rowId, null);
    }
    public Cursor GetAllRows()
    {
        try
        {
            return db.query(DATABASE_TABLE, new String[] {
                    "_id", "code", "name"}, null, null, null, null, null);
        }
        catch (SQLException e)
        {
            Log.e("Exception on query", e.toString());
            return null;
        }
    }
}*/
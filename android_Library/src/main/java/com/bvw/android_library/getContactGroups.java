package com.bvw.android_library;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.Groups;

@SuppressWarnings("deprecation")
public class  getContactGroups   {
public ArrayList<ContactGroup> contactgroups;
public  getContactGroups(Context c) 
{
	this.contactgroups = new ArrayList<ContactGroup>();
    ContentResolver cr = c.getContentResolver();
    Cursor groupCur = cr.query(Groups.CONTENT_URI, null, null, null, null);
    if (groupCur.getCount() > 0) {
        while (groupCur.moveToNext()) {
            ContactGroup newGroup = new ContactGroup();
            newGroup.Id = groupCur.getString(groupCur.getColumnIndex(Groups._ID));
            newGroup.Name = groupCur.getString(groupCur.getColumnIndex(Groups.NAME));
            contactgroups.add(newGroup);
        }
    }
}
public ArrayList<String> getGroupNames(){
	Iterator<ContactGroup> groupindex = contactgroups.iterator();
	ContactGroup tempstore = new ContactGroup();
	ArrayList<String> GroupNames = new ArrayList<String>(); 
	while (groupindex.hasNext()) {
		tempstore=groupindex.next();
		GroupNames.add(tempstore.Name);
	}
	return GroupNames;
}
}




package com.bvw.android_library;

import java.util.ArrayList;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Contacts.Groups;
import android.util.Log;


@SuppressWarnings("deprecation")
public class getContacts extends Application  {

public ArrayList<ContactDetails> contactList;

public getContacts(Context c,String Group) 
{
Context mContext = c;	
// Log.d("groupname: ",Group);	
String Id=getGroupId(mContext, Group.trim());
this.contactList=getContactList(mContext, Id);
}

public String getGroupId(Context c, String Group){
        ContentResolver gr =  c.getContentResolver();
        // gets any/all groups with like description, but only first used
        Cursor groupCur = gr.query(Groups.CONTENT_URI, null,
       	Groups.NAME + " like \"%" + Group + "%\"", null, null);
        String Id="";
        // Log.d("groupsfound= ",String.valueOf(groupCur.getCount()));
        if (groupCur.moveToLast()) {
             Id = groupCur.getString(groupCur.getColumnIndex(Groups._ID));
             // Log.d("groupid: ",Id);
        	}
        return Id;
    }

public ArrayList<ContactDetails> getContactList(Context c, String groupID) {
    ArrayList<ContactDetails> contactList = new ArrayList<ContactDetails>();

    Uri groupURI = ContactsContract.Data.CONTENT_URI;
    String[] projection = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

    Cursor gCur = c.getContentResolver().query(groupURI, projection,
            ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + groupID, null, null);
    if (gCur.getCount() > 0) {
    	Log.d("groupmembers= ",String.valueOf(gCur.getCount()));
        while (gCur.moveToNext()) {
        String id = gCur.getString(gCur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
        Cursor pCur = c.getContentResolver().query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", 
                new String[] { id }, null);
    
        	if (pCur.getCount() > 0) {
               while (pCur.moveToNext()) {
               ContactDetails data = new ContactDetails();
               data.name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
               data.phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               // filter only mobile numbers = prefix 02
               if (data.phone.startsWith("02")) { contactList.add(data);  }
               }
        	}   
        pCur.close();

        }
    }
gCur.close();    
return contactList;    
}
}


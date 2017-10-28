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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
//import android.util.Log;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class Receiver extends BroadcastReceiver
{
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent)
	{
//		Log.d("Receiver","onReceive called");
		String ucode = PreferenceManager.getDefaultSharedPreferences(context).getString("ucode","wy");
		Log.d("Receiver","ucode:"+ucode);
		SmsMessage[] msgs = getMessagesFromIntent(intent);
		for(int i = 0; i < msgs.length; i++)
		{
			SmsMessage msg = msgs[i];
			if (msg.getMessageBody().length()>ucode.length() && msg.getMessageBody().substring(0,ucode.length()).toLowerCase().equals(ucode))
			{
				Backend be = new Backend(context);
				SmsManager smsman = SmsManager.getDefault();
				String parse = msg.getMessageBody().substring(ucode.length());
				Log.d("Receiver","ucode found:" + parse);
				Log.d("Receiver","from: "+msg.getOriginatingAddress());
				if (be.existsPerson(msg.getOriginatingAddress()))
				{
//					Log.d("Receiver","Person exists");
					if (parse.matches(" *yes *([a-zA-Z]*)"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.yes(parse,msg.getOriginatingAddress()), null, null);
					}
					else if (parse.matches(" *no *([a-zA-Z]*)"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.no(parse,msg.getOriginatingAddress()), null, null);
					}
					else if (parse.matches(" *start.*"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.start(parse), null, null);
					}
					else if (parse.matches(" *stop *([a-zA-Z]*)"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.stop(parse), null, null);
					}
					else if (parse.matches(" *leave.*"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.leave(msg.getOriginatingAddress()), null, null);
					}
					else if (parse.matches(" *away.*"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.away(msg.getOriginatingAddress()), null, null);
					}
					else if (parse.matches(" *back.*"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, be.back(msg.getOriginatingAddress()), null, null);
					}
					else if (parse.matches(" *syntax.*"))
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, Backend.syntax, null, null);
					}
					else
					{
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, "Syntax incorrect. For more info please text "+ucode+" syntax", null, null);
					}
				}
				else // person does not exist
				{
					if (parse.matches(" *join *([a-zA-Z]*)"))
					{
//						Log.d("Receiver","join: "+parse);
						be.join(parse,msg.getOriginatingAddress());
					}
					else
					{
//						Log.d("Receiver","join first");
						smsman.sendTextMessage(msg.getOriginatingAddress(), null, "If that message was meant for footballapp, please join first with "+ucode+" join <name>", null, null);
					}
				}
				be.close();
			}
		}
	}
	
	private SmsMessage[] getMessagesFromIntent(Intent intent)
	{
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try{
			Object pdus[] = (Object [])bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for(int n=0; n < pdus.length; n++)
			{
				byte[] byteData = (byte[])pdus[n];
				retMsgs[n] =
				  SmsMessage.createFromPdu(byteData);
			}		  
		}
		catch(Exception e)
		{
//			Log.e("GetMessages", "fail", e);
		}
		return retMsgs;
	}
}

package com.bvw.android_library;
// Library package for general purpose classes
import java.util.ArrayList;

import com.bvw.android_library.Message_Listview.MessageAdapter.MessageDetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Message_Listview extends Activity {
// example class for a message listview using the MessageAdapter class
// oncreate routine contains example setup to use the class	
// layout must contain the specified textviews	
	ListView msgList;
	ArrayList<MessageDetails> details;
	AdapterView.AdapterContextMenuInfo info;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		msgList = (ListView) findViewById(R.id.MessageList);
		details = new ArrayList<MessageDetails>();
		msgList.setAdapter(new MessageAdapter(details , this));
}
	
		public static class MessageAdapter extends android.widget.BaseAdapter {
	    private ArrayList<MessageDetails> _data;
	    Context _c;
		    
	    public MessageAdapter (ArrayList<MessageDetails> data, Context c){
	        _data = data;
	        _c = c;
		    }
		   
		    public int getCount() {
		        return _data.size();
		    }
		    public Object getItem(int position) {
		        return _data.get(position);
		    }
		    public long getItemId(int position) {
		        return position;
		    }
		   
	    public View getView(final int position, View convertView, ViewGroup parent) {
		    View v = convertView;
		    if (v == null) 
		       {
		       LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		       v = vi.inflate(R.layout.list_element, null);
		       }

		    TextView fromView = (TextView)v.findViewById(R.id.From);
		    TextView subjView = (TextView)v.findViewById(R.id.subject);
		    TextView contView = (TextView)v.findViewById(R.id.content);
  
		    MessageDetails msg = _data.get(position);
		    fromView.setText(msg.from);
		    subjView.setText(msg.subj);
		    contView.setText(msg.cont);
	        
		    return v; 
		}

		  public static class MessageDetails {
		    String from;
		    String subj;
		    String cont;

		    public String getFrom() {
		        return from;
		    }
		    public void setFrom(String from) {
		        this.from = from;
		    }
		    public String getSubj() {
		        return subj;
		    }
		    public void setSubj(String subj) {
		        this.subj = subj;
		    }
		    public String getCont() {
		        return cont;
		    }
		    public void setCont(String cont) {
		        this.cont = cont;
		    }
		
		  }
		}	
}

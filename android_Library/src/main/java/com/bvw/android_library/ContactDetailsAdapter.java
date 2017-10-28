package com.bvw.android_library;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactDetailsAdapter extends BaseAdapter{
	List<ContactDetails> details;
	
    public ContactDetailsAdapter(Context c, List<ContactDetails> cdetails) {
        mContext = c;
       this.details=cdetails;
    }
    public int getCount() {
        return this.details.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater vi = LayoutInflater.from(this.mContext);  
            convertView = vi.inflate(R.layout.two_line_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder); 
        }
        else {
            //Get view holder back
            holder = (ViewHolder) convertView.getTag();
        }
        ContactDetails c = details.get(position);
        if (c != null) {
            //Name
            holder.toptext = (TextView) convertView.findViewById(R.id.text1);
            holder.toptext.setText(c.name);
            //ID
            holder.bottomtext = (TextView) convertView.findViewById(R.id.text2);
            holder.bottomtext.setText(c.phone);
        }
        return convertView;
    }
    private Context mContext;
    
    public class ViewHolder {
        TextView toptext;
        TextView bottomtext;
    }
    
}



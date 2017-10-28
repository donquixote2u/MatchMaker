package com.bvw.android_library;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactGroupAdapter extends BaseAdapter{
	List<ContactGroup> groups;
	
    public ContactGroupAdapter(Context c, List<ContactGroup> cgroups) {
        mContext = c;
       this.groups=cgroups;
    }
    public int getCount() {
        return this.groups.size();
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
        ContactGroup c = groups.get(position);
        if (c != null) {
            //Name
            holder.toptext = (TextView) convertView.findViewById(R.id.text1);
            holder.toptext.setText(c.Name);
            //ID
            holder.bottomtext = (TextView) convertView.findViewById(R.id.text2);
            holder.bottomtext.setText(c.Id);
        }
        return convertView;
    }
    private Context mContext;
    
    public class ViewHolder {
        TextView toptext;
        TextView bottomtext;
    }
    
}



package com.tech42.mari.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by mari on 12/12/16.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private String[] task;
    private String[] date;
    private String[] priority;
    private String[] time;
    private Activity context;

    public CustomAdapter(Activity context, String[] task, String[] date, String[] priority, String[] time) {
        super(context, R.layout.listlayout, task);
        this.context = context;
        this.task = task;
        this.date = date;
        this.priority = priority;
        this.time = time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listviewitem = inflater.inflate(R.layout.listlayout, parent, false);
        TextView texttask = (TextView) listviewitem.findViewById(R.id.task);
        TextView textdate = (TextView) listviewitem.findViewById(R.id.date);
        TextView textpriority = (TextView) listviewitem.findViewById(R.id.priority);
        TextView texttime = (TextView) listviewitem.findViewById(R.id.time);
        texttask.setText(task[position]);
        textdate.setText(date[position]);
        textpriority.setText(priority[position]);
        texttime.setText(time[position]);
        return listviewitem;
    }
}

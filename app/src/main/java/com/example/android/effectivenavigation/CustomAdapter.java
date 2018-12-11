package com.example.android.effectivenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    SharedPreferences prefs;
    Context context;
    List<RowItem> rowItem;
    String location = null;

    CustomAdapter(Context context, List<RowItem> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    @Override
    public int getCount() {

        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {

        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return rowItem.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        prefs = context.getSharedPreferences("tag", Context.MODE_PRIVATE);
        location = prefs.getString("KEY", "KEY");

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_fragment_item_layout, null);
        }

        //ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        final Button button =
                (Button)convertView.findViewById( R.id.button2);

        RowItem row_pos = rowItem.get(position);
        // setting the image resource and title
        //imgIcon.setImageResource(row_pos.getIcon());
        txtTitle.setText(row_pos.getTitle());

        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, button);
                        popup.getMenuInflater()
                                .inflate(R.menu.popup_menu, popup.getMenu());



                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                return true;
                            }});
                        popup.show();
                    }

                });

        return convertView;
    }
}

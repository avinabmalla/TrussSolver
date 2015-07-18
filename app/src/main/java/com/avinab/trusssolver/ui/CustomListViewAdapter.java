package com.avinab.trusssolver.ui;

/**
 * Avinab
 * 10/12/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avinab.trusssolver.R;

import java.util.List;


public class CustomListViewAdapter extends ArrayAdapter<ListViewRowItem>
{

	Context context;

	public CustomListViewAdapter(Context context, int resourceId, List<ListViewRowItem> items)
	{
		super(context, resourceId, items);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		ListViewRowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.list_content);
			holder.imageView = (ImageView) convertView.findViewById(R.id.list_image);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();

		holder.txtTitle.setText(rowItem.getTitle());
		holder.imageView.setImageResource(rowItem.getImageId());

		return convertView;
	}

	/*private view holder class*/
	private class ViewHolder
	{
		ImageView imageView;
		TextView txtTitle;
	}
}
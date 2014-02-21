package com.stano.maph.adapters;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.stano.maph.App;
import com.stano.maph.R;
import com.stano.maph.database.cmn.Photo;

public class ListAdapter extends ArrayAdapter<Photo> {

	private int itemWidth;
	private LayoutInflater inflater;

	public ListAdapter(Context context, List<Photo> objects, int itemWidth) {
		super(context, 0, objects);
		this.itemWidth = itemWidth;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.img_view);
			holder.textView = (TextView) convertView.findViewById(R.id.txt_title);
			LayoutParams params = new LayoutParams(itemWidth, (int) (itemWidth * 0.75));
			holder.imageView.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String filePath = App.getThumbsDirPath() + getItem(position).getFilename();
		holder.imageView.setImageURI(Uri.parse(filePath));
		holder.textView.setText(getItem(position).getTitle());
		
		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
	
}

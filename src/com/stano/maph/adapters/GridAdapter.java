package com.stano.maph.adapters;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.stano.maph.App;
import com.stano.maph.database.cmn.Photo;

public class GridAdapter extends ArrayAdapter<Photo> {

	private int itemWidth;

	public GridAdapter(Context context, List<Photo> objects, int itemWidth) {
		super(context, 0, objects);
		this.itemWidth = itemWidth;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;
		if (convertView == null) {
			imageView = new ImageView(getContext());
			int height = (int) (itemWidth * 0.75);
			LayoutParams params = new LayoutParams(itemWidth, height);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}

		String filePath = App.getThumbsDirPath() + getItem(position).getFilename();
		imageView.setImageURI(Uri.parse(filePath));

		return imageView;
	}
}

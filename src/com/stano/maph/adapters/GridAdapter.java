package com.stano.maph.adapters;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.stano.maph.database.cmn.Photo;
import com.stano.maph.utils.ImageResizer;

public class GridAdapter extends ArrayAdapter<Photo> {

	private int itemWidth;

	public GridAdapter(Context context, List<Photo> objects, int itemWidth) {
		super(context, 0, objects);
		this.itemWidth = itemWidth;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = new ImageView(getContext());
			int widthDp = pxToDp(itemWidth);
			int heightDp = (int) (widthDp * 0.6);
			LayoutParams params = new LayoutParams(widthDp, heightDp);
			convertView.setLayoutParams(params);
			holder.imageView = (ImageView) convertView;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String filePath = getContext().getExternalFilesDir(ImageResizer.TYPE_PHOTOS).getAbsolutePath()
				+ getItem(position).getFilename();
		holder.imageView.setImageURI(Uri.parse(filePath));

		return convertView;
	}

	public int pxToDp(int px) {
	    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	private class ViewHolder {
		public ImageView imageView;
	}

}

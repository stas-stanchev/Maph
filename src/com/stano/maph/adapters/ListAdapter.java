package com.stano.maph.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.stano.maph.database.cmn.Photo;

public class ListAdapter extends ArrayAdapter<Photo> {

	public ListAdapter(Context context, List<Photo> objects) {
		super(context, 0, objects);
	}

}

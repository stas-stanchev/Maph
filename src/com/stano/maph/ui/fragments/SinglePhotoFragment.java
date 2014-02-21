package com.stano.maph.ui.fragments;

import com.stano.maph.App;
import com.stano.maph.R;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SinglePhotoFragment extends Fragment {

	public final static String KEY_PHOTONAME = "photo_name";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.single_photo, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Bundle args = getArguments();
		String path = App.getPhotosDirPath() + args.getString(KEY_PHOTONAME);
		((ImageView) view.findViewById(R.id.img_photo)).setImageURI(Uri.parse(path));
	}

}

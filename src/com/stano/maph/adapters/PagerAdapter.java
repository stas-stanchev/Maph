package com.stano.maph.adapters;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.stano.maph.database.cmn.Photo;
import com.stano.maph.ui.fragments.SinglePhotoFragment;

public class PagerAdapter extends FragmentPagerAdapter {

	private List<Photo> listPhotos;

	public PagerAdapter(FragmentManager fm, List<Photo> listPhotos) {
		super(fm);
		this.listPhotos = listPhotos;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new SinglePhotoFragment();
		Bundle args = new Bundle();
		args.putString(SinglePhotoFragment.KEY_PHOTONAME, listPhotos.get(position).getFilename());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return listPhotos.size();
	}

}

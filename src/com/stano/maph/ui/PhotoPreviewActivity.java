package com.stano.maph.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.stano.maph.R;
import com.stano.maph.adapters.PagerAdapter;
import com.stano.maph.database.PhotoDAO;
import com.stano.maph.database.cmn.Photo;

public class PhotoPreviewActivity extends FragmentActivity {
	
	public final static int CODE_UPDATE = 0x01;
	public final static int CODE_CANCEL = 0x02;
	
	public final static String KEY_STARTING_POSITION = "starting_position";
	public final static String KEY_LIST = "list";
	
	private ViewPager viewPager;
	private int startingPosition;
	private ArrayList<Photo> listPhotos;
	private PagerAdapter pagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_preview);
		
		processIntent();
		initPager();
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		
		if (requestCode == CODE_UPDATE) {
			Photo photo = data.getParcelableExtra(EditPhotoActivity.KEY_ITEM);
			updateList(photo);
		}
	}
	
	private void updateList(Photo photo) {
		for (int i = 0; i < listPhotos.size(); i++) {
			Photo photoItem = listPhotos.get(i);
			if (photoItem.getId() == photo.getId()) {
				listPhotos.remove(i);
				listPhotos.add(i, photo);
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preview, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Photo currentItem = listPhotos.get(viewPager.getCurrentItem());
		switch (item.getItemId()) {
		case R.id.menu_delete:
			deletePhoto(currentItem);
			break;
		case R.id.menu_edit:
			editPhoto(currentItem);
			break;
		case R.id.menu_map:
			showOnMap(currentItem);
			break;
		}
		return true;
	}

	private void showOnMap(Photo currentItem) {
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(MapActivity.KEY_ACTION, MapActivity.SINGLE_PHOTO);
		intent.putExtra(MapActivity.KEY_PHOTO, currentItem);
		startActivity(intent);
	}

	private void editPhoto(Photo currentItem) {
		Intent intent = new Intent(this, EditPhotoActivity.class);
		intent.putExtra(EditPhotoActivity.KEY_ITEM, currentItem);
		startActivityForResult(intent, CODE_UPDATE);
	}

	private void deletePhoto(final Photo currentItem) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(R.string.delete_confirm);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PhotoDAO dao = new PhotoDAO(PhotoPreviewActivity.this);
				dao.deletePhoto(currentItem);
				dao.close();
				listPhotos.remove(currentItem);
				pagerAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}

	private void initPager() {
		pagerAdapter = new PagerAdapter(getSupportFragmentManager(), listPhotos);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(startingPosition);
		viewPager.setOffscreenPageLimit(2);
	}

	private void processIntent() {
		startingPosition = getIntent().getIntExtra(KEY_STARTING_POSITION, 0);
		listPhotos = getIntent().getParcelableArrayListExtra(KEY_LIST);
	}

}

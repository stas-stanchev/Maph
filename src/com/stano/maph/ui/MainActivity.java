package com.stano.maph.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.stano.maph.App;
import com.stano.maph.R;
import com.stano.maph.adapters.GridAdapter;
import com.stano.maph.adapters.ListAdapter;
import com.stano.maph.database.PhotoDAO;
import com.stano.maph.database.cmn.Photo;
import com.stano.maph.utils.ImageResizer;

public class MainActivity extends Activity implements LocationListener {

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	private static final int REQUEST_CODE_PICK_IMAGE = 0;
	private static final int REQUEST_CODE_TAKE_PHOTO = 1;
	private GridView gridView;
	private ListView listView;
	private String lastFilename;
	private ArrayList<Photo> listPhotos;
	private GridAdapter gridAdapter;
	private ListAdapter listAdapter;
	private PhotoDAO photoDao;

	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			displayInfoDialog(listPhotos.get(position));
			return true;
		}
	};
	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
			intent.putExtra(PhotoPreviewActivity.KEY_STARTING_POSITION, position);
			intent.putParcelableArrayListExtra(PhotoPreviewActivity.KEY_LIST, listPhotos);
			startActivity(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hideIndicator();

		initFields();
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		photoDao.open();
		listPhotos.clear();
		listPhotos.addAll(photoDao.getAllPhotos());
		notifyDataChange();
	}

	@Override
	protected void onPause() {
		super.onPause();
		photoDao.close();
	}

	@Override
	public void onContentChanged() {
		gridView = (GridView) findViewById(R.id.grid);
		listView = (ListView) findViewById(R.id.list_view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			Log.d("PICTURE SELECT", "Result was not OK");
			return;
		}

		processImage(requestCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_photo_select:
			displayDialog();
			break;
		case R.id.menu_switch_layout:
			if (gridView.isShown()) {
				gridView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			} else {
				gridView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}
			break;
		case R.id.menu_sort_by_date:
			sortByDate();
			break;
		case R.id.menu_sort_by_title:
			sortByTitle();
			break;
		case R.id.menu_map:
			openMap();
			break;
		}

		return true;
	}

	private void openMap() {
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(MapActivity.KEY_ACTION, MapActivity.SHOW_ALL);
		startActivity(intent);
	}

	private void initViews() {
		gridView = (GridView) findViewById(R.id.grid);
		listView = (ListView) findViewById(R.id.list_view);

		gridView.setColumnWidth(getItemWidth());
		gridView.setNumColumns(2);

		gridView.setAdapter(gridAdapter);
		listView.setAdapter(listAdapter);

		gridView.setOnItemClickListener(clickListener);
		gridView.setOnItemLongClickListener(longClickListener);

		listView.setOnItemClickListener(clickListener);
		listView.setOnItemLongClickListener(longClickListener);
	}

	private void initFields() {
		photoDao = new PhotoDAO(this);
		photoDao.open();
		listPhotos = photoDao.getAllPhotos();
		gridAdapter = new GridAdapter(this, listPhotos, getItemWidth());
		listAdapter = new ListAdapter(this, listPhotos, getItemWidth());
	}

	private int getItemWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		return width / 2;
	}

	private void showIndicator() {
		setProgressBarIndeterminateVisibility(true);
	}

	private void hideIndicator() {
		setProgressBarIndeterminateVisibility(false);
	}

	private void displayInfoDialog(Photo photo) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_info);
		dialog.setTitle(getString(R.string.dialog_title));
		dialog.setCanceledOnTouchOutside(true);

		TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
		TextView txtDescription = (TextView) dialog.findViewById(R.id.txt_description);
		TextView txtDate = (TextView) dialog.findViewById(R.id.txt_date);
		TextView txtLocation = (TextView) dialog.findViewById(R.id.txt_location);


		txtTitle.setText(photo.getTitle());
		txtDescription.setText(photo.getDescription());
		txtDate.setText(photo.getFormattedDate());

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		double latitude = photo.getLatitude();
		double longitude = photo.getLongitude();
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				String address = String.format("%s, %s, %s", 
						addresses.get(0).getAddressLine(0), 
						addresses.get(0).getAddressLine(1), 
						addresses.get(0).getAddressLine(2));
				txtLocation.setText(address);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Button btnClose = (Button) dialog.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	private void sortByTitle() {
		sortList(new Comparator<Photo>() {
			@Override
			public int compare(Photo lhs, Photo rhs) {
				return rhs.getTitle().compareTo(lhs.getTitle());
			}
		});
	}

	private void sortByDate() {
		sortList(new Comparator<Photo>() {
			@Override
			public int compare(Photo lhs, Photo rhs) {
				return (int) (lhs.getTimestamp() - rhs.getTimestamp());
			}
		});
	}

	private void sortList(Comparator<Photo> comparator) {
		Collections.sort(listPhotos, comparator);

		notifyDataChange();
	}

	private void notifyDataChange() {
		listAdapter.notifyDataSetChanged();
		gridAdapter.notifyDataSetChanged();
	}

	private void displayDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		String[] items = new String[] { "Take New Photo", "Select From Gallery" };

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					startCamera();
				} else {
					startGallery();
				}
			}
		});
		builder.create().show();
	}

	protected void startGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_IMAGE);
	}

	protected void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String path = App.getPhotosDirPath() + getLastCreatedFilename();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
	}

	private void processImage(int requestCode, Intent data) {
		String filePath = null;
		if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			Uri imageUri = data.getData();
			filePath = getFilepathFromUri(imageUri);
		} else {
			// we already have it
			filePath = App.getPhotosDirPath() + lastFilename;
		}

		// UI heavy
		new ImageProcesserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filePath);
	}

	private String getFilepathFromUri(Uri imageUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();

		return filePath;
	}

	private String getLastCreatedFilename() {
		lastFilename = ImageResizer.getRandomFilename();
		return lastFilename;
	}

	private class ImageProcesserTask extends AsyncTask<String, Void, Void> {

		private String fileName;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showIndicator();
		}

		@Override
		protected Void doInBackground(String... params) {
			// check where is the photo
			String filePath = params[0];
			boolean isInAppDir = filePath.contains(getExternalFilesDir(ImageResizer.TYPE_PHOTOS).getAbsolutePath());

			fileName = null;
			if (!isInAppDir) {
				fileName = ImageResizer.getRandomFilename();
			} else {
				fileName = lastFilename;
			}

			ImageResizer.resizePhoto(MainActivity.this, filePath, fileName);
			ImageResizer.createThumbnail(MainActivity.this, filePath, fileName);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			PhotoDAO dao = new PhotoDAO(MainActivity.this);
			String location = getCurrentLocationAsString();
			long now = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
			String title = getString(R.string._photo_title_format, format.format(new Date(now)));

			Photo newPhoto = dao.createPhoto(fileName, title, "", now, location);

			addPhoto(newPhoto);
			hideIndicator();
		}
	}

	public void addPhoto(Photo newPhoto) {
		if (listPhotos == null) {
			listPhotos = new ArrayList<Photo>();
		}

		listPhotos.add(0, newPhoto);
		notifyDataChange();
	}

	public String getCurrentLocationAsString() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		String formatedLocation = null;

		// getting GPS status
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetworkEnabled) {
			// no network provider is enabled
		} else {
			Location location = null;
			// First get location from Network Provider
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				Log.d("Network", "Network");
				if (locationManager != null) {
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (location != null) {
						formatedLocation = location.getLatitude() + ":" + location.getLongitude();
					}
				}
			}
			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				if (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							formatedLocation = location.getLatitude() + ":" + location.getLongitude();
						}
					}
				}
			}
		}
		return formatedLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}

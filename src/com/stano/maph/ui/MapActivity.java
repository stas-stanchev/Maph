package com.stano.maph.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stano.maph.App;
import com.stano.maph.R;
import com.stano.maph.database.PhotoDAO;
import com.stano.maph.database.cmn.Photo;

public class MapActivity extends Activity implements LocationListener {

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	public static final String KEY_EXTRA = "extra";
	public final static int CODE_COORDS = 0x3;

	public final static int SHOW_ALL = 1;
	public final static int GET_COORDINATES = 2;
	public final static int SINGLE_PHOTO = 3;

	public final static String KEY_ACTION = "action";
	public static final String KEY_PHOTO = "photo";

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	private GoogleMap map;
	private int action;
	private Photo providedPhoto;

	OnMapClickListener clickListener = new OnMapClickListener() {
		@Override
		public void onMapClick(LatLng coordinates) {
			String location = String.format("%s:%s", coordinates.latitude,
					coordinates.longitude);
			finish(location);
		}
	};

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		initLocationManager();
		processIntent();
		initMap();
	}

	protected void finish(String location) {
		Intent intent = getIntent();
		intent.putExtra(KEY_EXTRA, location);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void initLocationManager() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (isGPSEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		} else if (isNetworkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		}
	}

	private void initMap() {
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		if (action == GET_COORDINATES) {
			map.setOnMapClickListener(clickListener);
		} else {
			loadImagesOnMap();
		} 

		switch (action) {
		case SINGLE_PHOTO:
			centerOnPhoto();
			break;
		case SHOW_ALL:
		case GET_COORDINATES:
			centerOnCurrentPosition();
			break;
		}
	}

	private void centerCamera(LatLng coords) {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));

		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}

	private void centerOnCurrentPosition() {
		centerCamera(getCurrentLocation());
	}

	private void centerOnPhoto() {
		centerCamera(new LatLng(providedPhoto.getLatitude(),
				providedPhoto.getLongitude()));
	}

	private void loadImagesOnMap() {
		new AsyncTask<Void, Object, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				PhotoDAO dao = new PhotoDAO(MapActivity.this);
				ArrayList<Photo> photos = dao.getAllPhotos();
				for (Photo photo : photos) {
					String absolutePath = App.getThumbsDirPath()
							+ photo.getFilename();
					Options options = new Options();
					options.inSampleSize = 4;
					Bitmap thumbBitmap = BitmapFactory.decodeFile(absolutePath,
							options);
					publishProgress(photo, thumbBitmap);
				}

				return null;
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				createMarker((Photo) values[0], (Bitmap) values[1]);
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void createMarker(Photo photo, Bitmap image) {
		LatLng point = new LatLng(photo.getLatitude(), photo.getLongitude());
		map.addMarker(new MarkerOptions().position(point)
				.title(photo.getTitle()).snippet(photo.getDescription())
				.icon(BitmapDescriptorFactory.fromBitmap(image)));
	}

	private void processIntent() {
		action = getIntent().getIntExtra(KEY_ACTION, SHOW_ALL);
		if (action == SINGLE_PHOTO) {
			providedPhoto = getIntent().getParcelableExtra(KEY_PHOTO);
		}
	}

	public LatLng getCurrentLocation() {

		// getting GPS status
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetworkEnabled) {
			// no network provider is enabled
		} else {
			Location location = null;
			// First get location from Network Provider
			if (isNetworkEnabled) {
				Log.d("Network", "Network");
				if (locationManager != null) {
					location = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (location != null) {
						return new LatLng(location.getLatitude(),
								location.getLongitude());
					}
				}
			}
			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				if (location == null) {
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							return new LatLng(location.getLatitude(),
									location.getLongitude());
						}
					}
				}
			}
		}
		return new LatLng(0, 0);
	}

	@Override
	public void onLocationChanged(Location location) {
		centerCamera(new LatLng(location.getLatitude(), location.getLongitude()));
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

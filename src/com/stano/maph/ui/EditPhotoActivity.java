package com.stano.maph.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.stano.maph.R;
import com.stano.maph.database.PhotoDAO;
import com.stano.maph.database.cmn.Photo;
import com.stano.maph.ui.widgets.DateTimePicker;

public class EditPhotoActivity extends Activity {

	public final static String KEY_ITEM = "item";
	private Photo photo;
	private EditText edtTitle;
	private EditText edtDescription;
	private Button btnOpenMap;
	private Button btnEditDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_photo);

		getItem();
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_save) {
			savePhoto();
		}
		return true;
	}

	private void savePhoto() {
		photo.setDescription(edtDescription.getText().toString());
		photo.setTitle(edtTitle.getText().toString());

		PhotoDAO dao = new PhotoDAO(this);
		dao.updatePhoto(photo);
		setResult();
		finish();
	}

	private void initViews() {
		edtDescription = (EditText) findViewById(R.id.edt_description);
		edtTitle = (EditText) findViewById(R.id.edt_title);
		btnEditDate = (Button) findViewById(R.id.btn_edit_date);
		btnOpenMap = (Button) findViewById(R.id.btn_open_map);

		edtDescription.setText(photo.getDescription());
		edtTitle.setText(photo.getTitle());
		btnEditDate.setText(photo.getFormattedDate());

		btnEditDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayDatePicker();
			}
		});
		btnOpenMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openMap();
			}
		});
	}

	private void openMap() {
		// TODO Auto-generated method stub

	}

	/**
	 * not mine, from here http://code.google.com/p/datetimepicker/
	 */
	private void displayDatePicker() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		final String timeS = android.provider.Settings.System.getString(getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimePicker.clearFocus();
				photo.setTimestamp(mDateTimePicker.getDateTimeMillis());
				btnEditDate.setText(photo.getFormattedDate());
				mDateTimeDialog.dismiss();
			}
		});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimeDialog.cancel();
			}
		});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mDateTimePicker.reset();
			}
		});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	private void getItem() {
		photo = getIntent().getParcelableExtra(KEY_ITEM);
	}

	@Override
	public void onBackPressed() {
		setResult();

		super.onBackPressed();
	}

	private void setResult() {
		Intent intent = getIntent();
		intent.putExtra(KEY_ITEM, photo);
		setResult(PhotoPreviewActivity.CODE_CANCEL, intent);
	}

}

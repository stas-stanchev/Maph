package com.stano.maph.ui;

import com.stano.maph.R;
import com.stano.maph.database.PhotoDAO;
import com.stano.maph.database.cmn.Photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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

	private void displayDatePicker() {
		// TODO Auto-generated method stub
		
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

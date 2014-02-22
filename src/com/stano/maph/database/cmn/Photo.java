package com.stano.maph.database.cmn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements  Parcelable {

	private int id;
	private String filename;
	private Long timestamp;
	private String title;
	private String description;
	private String location;

	public Photo() {
		
	}
	
	public Photo(Parcel in) {
		this.id = in.readInt();
		this.filename = in.readString();
		this.timestamp = in.readLong();
		this.title = in.readString();
		this.description = in.readString();
		this.location = in.readString();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Photo)) {
			return false;
		}
		
		Photo rhs = (Photo) o;
		
		if (this == rhs) {
			return true;
		}
		
		return this.id == rhs.id;
	}
	
	@Override
	public String toString() {
		return "Photo id#" + id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(filename);
		dest.writeLong(timestamp);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(location);
	}
	
	public double getLatitude() {
		return getCoordinate(0);
	}
	
	public double getLongitude() {
		return getCoordinate(1);
	}

	private double getCoordinate(int i) {
		if (location == null) {
			return 0d;
		}
		return Double.parseDouble(location.split(":")[i]);
	}
	
	public String getFormattedDate() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM HH:mm:ss", Locale.getDefault());
		return format.format(new Date(timestamp));
	}

}

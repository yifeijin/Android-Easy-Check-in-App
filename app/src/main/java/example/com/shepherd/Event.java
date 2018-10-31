package example.com.shepherd;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable {
    String name;
    String location;
    String description;
    Date startTime;
    Date endTime;

    public Event(String name, String location, Date startTime, Date endTime, String description) {
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    protected Event(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        long tmpStartTime = in.readLong();
        this.startTime = tmpStartTime != -1 ? new Date(tmpStartTime) : null;
        long tmpEndTime = in.readLong();
        this.endTime = tmpEndTime != -1 ? new Date(tmpEndTime) : null;
    }

    //@SuppressWarnings("unused")
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeLong(startTime != null ? startTime.getTime() : -1L);
        parcel.writeLong(endTime != null ? endTime.getTime() : -1L);
    }

    @Override
    public String toString() {
        return name;
    }
}

package example.com.shepherd.data;

import android.provider.BaseColumns;

public class EventContract {
    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_START_TIME = "start_time";
    }
}

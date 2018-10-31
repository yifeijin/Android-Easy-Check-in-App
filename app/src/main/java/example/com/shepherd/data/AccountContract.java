package example.com.shepherd.data;

import android.provider.BaseColumns;

public class AccountContract {
    public static final class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_USER_ID = "userid";
        public static final String COLUMN_PASSWORD = "password";
    }
}

package nd801project.elmasry.thankyou.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.*;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favorites.db";

    public FavoritesDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                COLUMN_VIDEO_TITLE + " TEXT DEFAULT '', " +
                COLUMN_VIDEO_THUMBNAIL_URL + " TEXT DEFAULT ''" +
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

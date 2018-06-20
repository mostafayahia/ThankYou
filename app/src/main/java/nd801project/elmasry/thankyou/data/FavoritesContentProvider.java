package nd801project.elmasry.thankyou.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static nd801project.elmasry.thankyou.data.FavoritesContract.*;
import static nd801project.elmasry.thankyou.data.FavoritesContract.FavoritesEntry.*;

public class FavoritesContentProvider extends ContentProvider {

    public static final int FAVORITES = 500;
    public static final int FAVORITES_WITH_ID = 501;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private SQLiteOpenHelper mFavoritesDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, FAVORITES_PATH, FAVORITES);
        uriMatcher.addURI(AUTHORITY, FAVORITES_PATH + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavoritesDbHelper = new FavoritesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String s1) {
        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match) {
            case FAVORITES:
                returnCursor = db.query(TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                break;
            case FAVORITES_WITH_ID:
                returnCursor = db.query(TABLE_NAME, projection, "_id=?", new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case FAVORITES:
                final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                else
                    throw new SQLException("Failed to insert in: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowDeleted;

        switch (match) {
            case FAVORITES:
                rowDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri: " + uri);
        }

        if (rowDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

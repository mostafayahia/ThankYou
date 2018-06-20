package nd801project.elmasry.thankyou.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;

import nd801project.elmasry.thankyou.R;

public class HelperUtils {

    /**
     * return true if the device is connected to the internet
     * @param context
     * @return
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * show snack bar with short length
     * @param activity
     * @param resId
     */
    public static void showSnackbar(Activity activity, int resId) {
        Snackbar.make(activity.findViewById(android.R.id.content), resId, Snackbar.LENGTH_SHORT).show();
    }

}

package nd801project.elmasry.thankyou.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HelperUtils {

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

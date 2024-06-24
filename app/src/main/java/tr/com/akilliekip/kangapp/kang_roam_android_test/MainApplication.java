package tr.com.akilliekip.kangapp.kang_roam_android_test;

import android.app.Application;

import com.roam.sdk.Roam;

public class MainApplication extends Application {
    // TODO: Step 2 : Initialize SDK
    @Override
    public void onCreate() {
        super.onCreate();
        Roam.initialize(this, "API_KEY");
    }
}

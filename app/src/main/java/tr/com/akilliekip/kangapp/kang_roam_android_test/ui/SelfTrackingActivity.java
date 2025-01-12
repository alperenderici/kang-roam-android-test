package tr.com.akilliekip.kangapp.kang_roam_android_test.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import tr.com.akilliekip.kangapp.kang_roam_android_test.MainApplication;
import tr.com.akilliekip.kangapp.kang_roam_android_test.R;
import tr.com.akilliekip.kangapp.kang_roam_android_test.storage.RoamPreferences;
import com.google.android.material.snackbar.Snackbar;
import com.roam.sdk.Roam;
import com.roam.sdk.RoamTrackingMode;
import com.roam.sdk.callback.RoamLocationCallback;

public class SelfTrackingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private TextView snackBar;
    private EditText edtDistanceFilter, edtGeofenceRadius, edtUpdateInterval;
    private CheckBox ckMock, ckForeground;
    private RadioGroup mRadioGroup;
    private Button btnStartTracking, btnStopTracking,btnLogout;
    RoamTrackingMode trackingMode = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_tracking);

        Roam.disableBatteryOptimization();
        snackBar = findViewById(R.id.snackBar);
        edtUpdateInterval = findViewById(R.id.edtUpdateInterval);
        edtGeofenceRadius = findViewById(R.id.edtGeofenceRadius);
        ckMock = findViewById(R.id.ckMock);
        ckForeground = findViewById(R.id.ckForeground);
        mRadioGroup = findViewById(R.id.radioGroup);
        edtDistanceFilter = findViewById(R.id.edtDistanceFilter);
        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking = findViewById(R.id.btnStopTracking);
        btnLogout = findViewById(R.id.btnLogout);

        ckMock.setOnCheckedChangeListener(this);
        btnStartTracking.setOnClickListener(this);
        btnStopTracking.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        ckForeground.setOnCheckedChangeListener(this);
        Roam.notificationOpenedHandler(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackingStatus();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ckMock:
                if (isChecked) {
                    Roam.allowMockLocation(true);
                } else {
                    Roam.allowMockLocation(false);
                }
                break;

            case R.id.ckForeground:
                if (isChecked) {
                    Roam.setForegroundNotification(true,"Roam Example","Click here to redirect the app",
                            R.drawable.ic_geospark,"tr.com.akilliekip.kangapp.kang_roam_android_test.ui.SelfTrackingActivity","tr.com.akilliekip.kangapp.kang_roam_android_test.service.ForegroundService");
                } else {
                    Roam.setForegroundNotification(false,"Roam SDK","Click here to redirect the app",
                            R.drawable.ic_geospark,"tr.com.akilliekip.kangapp.kang_roam_android_test.ui.SelfTrackingActivity","tr.com.akilliekip.kangapp.kang_roam_android_test.service.ForegroundService");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTracking:
                tracking();
                break;
            case R.id.btnStopTracking:
                stopTracking();
                break;

            case R.id.btnLogout:

                RoamPreferences.setSelfLogin(getApplicationContext(), false);
                Roam.selfUserLogout();
                trackingStatus();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
        }
    }

    private void tracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionsQ();
        } else {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this);
        } else if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this);
        }else if (!Roam.checkActivityPermission()) {
            Roam.requestActivityPermission(this);
        } else {
            startTracking();
        }
    }

    private void checkPermissionsQ() {
        if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this);
        } else if (!Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this);
        } else if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this);
        }else if (!Roam.checkActivityPermission()) {
            Roam.requestActivityPermission(this);
        } else {
            startTracking();
        }
    }

    private void startTracking() {
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.rbOption1) {
            Roam.startTracking(RoamTrackingMode.ACTIVE,null);
            trackingMode = RoamTrackingMode.ACTIVE;
            trackingStatus();
        } else if (selectedId == R.id.rbOption2) {
            Roam.startTracking(RoamTrackingMode.BALANCED,null);
            trackingMode = RoamTrackingMode.BALANCED;
            trackingStatus();
        } else if (selectedId == R.id.rbOption3) {
            Roam.startTracking(RoamTrackingMode.PASSIVE,null);
            trackingMode = RoamTrackingMode.PASSIVE;
            trackingStatus();
        } else if (selectedId == R.id.rbOption4) {
            int updateInterval;
            int distanceFilter;
            int stopDuration;
            if (TextUtils.isEmpty(edtUpdateInterval.getText().toString())) {
                updateInterval = 0;
            } else {
                updateInterval = Integer.parseInt(edtUpdateInterval.getText().toString());
            }
            if (TextUtils.isEmpty(edtDistanceFilter.getText().toString())) {
                distanceFilter = 0;
            } else {
                distanceFilter = Integer.parseInt(edtDistanceFilter.getText().toString());
            }
            if (TextUtils.isEmpty(edtGeofenceRadius.getText().toString())) {
                stopDuration = 0;
            } else {
                stopDuration = Integer.parseInt(edtGeofenceRadius.getText().toString());
            }
            if (updateInterval > 0) {
                RoamTrackingMode roamTrackingMode = new RoamTrackingMode.Builder(updateInterval)
                        .setDesiredAccuracy(RoamTrackingMode.DesiredAccuracy.HIGH)
                        .build();
                Roam.startTracking(roamTrackingMode, null);
                trackingMode = roamTrackingMode;
            } else {
                RoamTrackingMode roamTrackingMode = new RoamTrackingMode.Builder(distanceFilter, stopDuration)
                        .setDesiredAccuracy(RoamTrackingMode.DesiredAccuracy.HIGH)
                        .build();
                Roam.startTracking(roamTrackingMode,null);
                trackingMode = roamTrackingMode;
            }
            trackingStatus();
        } else {
            showMsg("Select tracking option");
        }
    }

    private void stopTracking() {
        Roam.stopTracking(null);
        trackingStatus();
    }

    private void trackingStatus() {
        if (Roam.isLocationTracking()) {
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStartTracking.setEnabled(false);
            btnStopTracking.setEnabled(true);
        } else {
            mRadioGroup.clearCheck();
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStartTracking.setEnabled(true);
            btnStopTracking.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Roam.REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMsg("Location permission required");
                }
                break;
            case Roam.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showMsg("Background Location permission required");
                }
                break;
            case Roam.REQUEST_CODE_ACTIVITY_RECOGNITION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    startTracking();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Roam.REQUEST_CODE_LOCATION_ENABLED) {
            tracking();
        }
    }

    private void showMsg(String msg) {
        Snackbar.make(snackBar, msg, Snackbar.LENGTH_SHORT).show();
    }

}
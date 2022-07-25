package sparta.realm.cschat.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.Realm;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.MainActivity;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.User;
import sparta.realm.cschat.R;
import sparta.realm.cschat.SyncInterface;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.spartautils.svars;

public class Splash extends SpartaAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ChatApplication.Initialize(new SyncInterface() {
            @Override
            public void onSyncCompleted(String service_id) {

            }

            @Override
            public void onInfoUpdated(String info) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions(this)) {
            File f = new File(svars.current_app_config(this).file_path_employee_data);
            f.mkdirs();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (Globals.myself() != null) {
                        if ( Realm.databaseManager.getRecordCount(Member.class) > 0) {

                            start_activity(new Intent(Splash.this, ConversationsVP.class));
                        } else {
//                            start_activity(new Intent(Splash.this, Signup.class));
                            start_activity(new Intent(Splash.this, ReferenceVerification.class));
                        }
                    } else {
                        start_activity(new Intent(Splash.this, ReferenceVerification.class));


                    }
                    finish();
                }
            }, 3000);


        }
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

    }

    public static final int MULTIPLE_PERMISSIONS = 10;
    public static String[] permissions = new String[]{

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };





    public static boolean checkPermissions(Activity context) {
        int result;
        boolean ok = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (listPermissionsNeeded.isEmpty() || (listPermissionsNeeded.size() == 1 & listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {

        } else {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            ok = false;
        }
//        if (!listPermissionsNeeded.isEmpty() && (listPermissionsNeeded.size()!=1&listPermissionsNeeded.get(0).equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION))) {
//
//        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {


            //  openOverlaySettings();
        }
        if (!Settings.canDrawOverlays(context)) {
            openOverlaySettings(context);
            ok = false;
        }

        return ok;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void openOverlaySettings(Activity activity) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName()));
        try {
            activity.startActivityForResult(intent, 6);
        } catch (ActivityNotFoundException e) {
            Log.e("Drawers permission :", e.getMessage());
        }
    }
}
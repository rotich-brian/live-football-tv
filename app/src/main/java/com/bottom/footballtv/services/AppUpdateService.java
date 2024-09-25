package com.bottom.footballtv.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bottom.footballtv.R;
import com.bottom.footballtv.models.AppData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppUpdateService {
    private static final String TAG = "APP_UPDATE_SERVICE_TAG";

    private Context context;
    private String currentVersion;

    public interface NewUpdateService{
        void newUpdate(AppData appData);
        void noNewUpdate();
    }

    public AppUpdateService(Context context) {
        this.context = context;
        currentVersion = context.getResources().getString(R.string.app_Version);
    }

    private String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;  // This will give you the version name as specified in your app's build.gradle file
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
            version = context.getResources().getString(R.string.app_Version);
        }
        return version;
    }

    public void checkUpdate(NewUpdateService newUpdateService) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("update").document("app_data");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        AppData appData = new AppData();
                        appData.setVersion_name(document.getString("version_name"));
                        appData.setVersion(document.getString("version"));
                        appData.setMessage(document.getString("message"));
                        appData.setUpdate_url(document.getString("update_url"));
                        appData.setTelegram_user(document.getString("telegram_user"));
                        appData.setShow_later(Boolean.TRUE.equals(document.getBoolean("show_later")));
                        appData.setFrom_telegram(Boolean.TRUE.equals(document.getBoolean("from_telegram")));
                        appData.setFrom_playStore(Boolean.TRUE.equals(document.getBoolean("from_playStore")));

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        if (!getAppVersion(context).equals(appData.getVersion_name())) {

                            newUpdateService.newUpdate(appData);

//                            displayUpdateDialog(appData.getVersion_name(), appData.getMessage(),
//                                    appData.getUpdate_url(), appData.getTelegram_user(), appData.isFrom_telegram(), appData.isShow_later(), appData.isFrom_playStore());
                        } else {
                            newUpdateService.noNewUpdate();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                        newUpdateService.noNewUpdate();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    newUpdateService.noNewUpdate();
                }
            }
        });
    }

    private void displayUpdateDialog(String versionName, String message, String updateUrl,String telegramUser, boolean fromTelegram,boolean showLater, boolean fromPlayStore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("NEW UPDATE "+versionName)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (fromPlayStore){

                            // Redirect to the app's page in Google Play Store
                            final String appPackageName = context.getPackageName();
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        } else if (fromTelegram){

                            //Redirect to the app's page in TelegramChannel
                            goTelegramLink(context,telegramUser);
                        } else {

                            // Redirect to the app's page in Website
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
                        }
                    }
                });

        if (showLater){
            builder.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void goTelegramLink(Context context,String tg) {

        Intent intent;
        try {
            try {
                context.getPackageManager().getPackageInfo("org.telegram.messenger", 0);
            } catch (PackageManager.NameNotFoundException e) {
                context.getPackageManager().getPackageInfo("org.thunderdog.challegram", 0);
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain="+tg));
            context.startActivity(intent);

        } catch (PackageManager.NameNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/"+tg));
            context.startActivity(intent);
        }
    }
}

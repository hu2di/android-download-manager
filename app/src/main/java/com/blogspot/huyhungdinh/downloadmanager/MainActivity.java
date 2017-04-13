package com.blogspot.huyhungdinh.downloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String LINK = "https://github.com/hu2di/android-download-manager/blob/master/Yasuo.mp3";
    private String path = null;

    private TextView tvSuccess;
    private Button btnDownload;

    private DownloadManager mgr = null;
    private long idDownload = -1;
    private boolean isCancel = false;
    private MaterialDialog dialogDownload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initDownload();
    }

    private void initView() {
        tvSuccess = (TextView) findViewById(R.id.tvSuccess);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
    }

    private void initDownload() {
        mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        dialogDownload = new MaterialDialog.Builder(this)
                .content("Downloading")
                .progress(true, 0)
                .positiveText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mgr.remove(idDownload);

                        isCancel = true;
                        Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                })
                .cancelable(false)
                .build();
    }

    private void download() {
        String folder = "/";
        String name = "Yasuo.mp3";
        Uri fileUri = Uri.parse(LINK);

        idDownload = mgr.enqueue(new DownloadManager.Request(fileUri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Download")
                .setDescription("Downloading " + name)
                .setDestinationInExternalPublicDir(folder, name));

        dialogDownload.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            if (!isCancel) {
                if (dialogDownload != null && dialogDownload.isShowing()) {
                    dialogDownload.dismiss();
                }

                Toast.makeText(MainActivity.this, "Download Success", Toast.LENGTH_SHORT).show();
                tvSuccess.setText("Download Success /sdcard/Yasuo.mp3");
            } else {
                isCancel = false;
            }
        }
    };
}

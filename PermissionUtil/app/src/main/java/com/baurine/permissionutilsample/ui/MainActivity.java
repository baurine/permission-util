package com.baurine.permissionutilsample.ui;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.baurine.permissionutil.PermissionUtil;
import com.baurine.permissionutilsample.R;
import com.baurine.permissionutilsample.presenter.LocationPresenter;
import com.baurine.permissionutilsample.util.CheckPermissionUtil;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MainActivity
        extends BaseActivity
        implements LocationPresenter.LocationView {

    private LocationPresenter locationPresenter;
    private TextView tvLocation, tvSaveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        locationPresenter = new LocationPresenter(this, this);
    }

    private void initViews() {
        tvLocation = (TextView) findViewById(R.id.tv_location);
        showLocationResult("unknown!");

        tvSaveResult = (TextView) findViewById(R.id.tv_save_result);
        showSaveResult("unknown!");
    }

    @Override
    public void showLocationResult(String locationResult) {
        tvLocation.setText(getString(R.string.your_location, locationResult));
    }

    private void showSaveResult(String saveResult) {
        tvSaveResult.setText(getString(R.string.save_result, saveResult));
    }

    ////////////////////////////////////////////
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_location:
                locationPresenter.requestLocation(this);
                break;
            case R.id.btn_save_location:
                saveLocation();
                break;
        }
    }

    private void saveLocation() {
        CheckPermissionUtil.checkWriteSd(this,
                new PermissionUtil.ReqPermissionCallback() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            saveLocationToFile();
                        } else {
                            showSaveResult("disallowed!");
                        }
                    }
                });
    }

    private void saveLocationToFile() {
        String content = tvLocation.getText().toString();
        String filePath =
                Environment.getExternalStorageDirectory().getPath() + "/location.txt";
        PrintWriter out = null;
        try {
            out = new PrintWriter(filePath);
            out.println(content);
            showSaveResult(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showSaveResult(e.getLocalizedMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}

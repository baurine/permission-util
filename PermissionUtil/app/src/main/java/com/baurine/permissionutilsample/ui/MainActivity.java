package com.baurine.permissionutilsample.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baurine.permissionutilsample.R;
import com.baurine.permissionutilsample.presenter.LocationPresenter;

public class MainActivity
        extends BaseActivity
        implements LocationPresenter.LocationView {

    private LocationPresenter locationPresenter;
    private TextView tvLocation;

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
    }

    @Override
    public void showLocationResult(String locationResult) {
        tvLocation.setText(getString(R.string.your_location, locationResult));
    }

    ////////////////////////////////////////////
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_location:
                locationPresenter.requestLocation(this);
                break;
        }
    }
}

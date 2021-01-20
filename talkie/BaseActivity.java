package com.example.talkie;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.talkie.sinch.SinchService;


/**
 * Created by ibra_ on 10/06/2018.
 */

public class BaseActivity extends AppCompatActivity implements ServiceConnection {


    private SinchService.SinchServiceInterface mSinchServiceInterface;
    private SinchService sinchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean binded = getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
        assert binded != false;
    }

    @Override
    public void onBindingDied(ComponentName name) {
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public void onNullBinding(ComponentName name) {
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnect();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        boolean binded = getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnect();
        }
    }

    protected void onServiceConnect() {
        // for subclasses
    }

    protected void onServiceDisconnect() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

}

package com.example.boundservice24112020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    MyBoundService mMyBoundService;
    TextView mTvCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvCount = findViewById(R.id.textView);

        Intent intent = new Intent(this , MyBoundService.class);
        ContextCompat.startForegroundService(this,intent);
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBoundService.LocalIBinder localIBinder = (MyBoundService.LocalIBinder) iBinder;
            mMyBoundService = localIBinder.getService();
            mMyBoundService.setOnDataChange(new OnDataChange() {
                @Override
                public void changeCount(int count) {
                    mTvCount.setText("Đang xử lý : " + count);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
package com.example.momo.bluetooth_temp.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import com.example.momo.bluetooth_temp.APP;
import com.example.momo.bluetooth_temp.R;
import com.example.momo.bluetooth_temp.util.BtReceiver;

import java.io.IOException;


public class BtClientActivity extends Activity implements BtBase.Listener, BtReceiver.Listener, BtDevAdapter.Listener {
    private TextView mTips;
    private TextView mLogs;
    private BtReceiver mBtReceiver;
    private final BtDevAdapter mBtDevAdapter = new BtDevAdapter(this);
    private final BtClient mClient = new BtClient(this);
    MediaPlayer player;
    private int play_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_btclient);
        RecyclerView rv = findViewById(R.id.rv_bt);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mBtDevAdapter);
        mTips = findViewById(R.id.tv_tips);
        mLogs = findViewById(R.id.tv_log);
        mBtReceiver = new BtReceiver(this, this);//注册蓝牙广播
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        player = MediaPlayer.create(this,R.raw.music_warning1);
        player.setVolume(1f,1f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBtReceiver);
        mClient.unListener();
        mClient.close();
    }

    @Override
    public void onItemClick(BluetoothDevice dev) {
        if (mClient.isConnected(dev)) {
            APP.toast("已经连接了", 0);
            return;
        }
        mClient.connect(dev);
        APP.toast("正在连接...", 0);
        mTips.setText("正在连接...");
    }

    @Override
    public void foundDev(BluetoothDevice dev) {
        mBtDevAdapter.add(dev);
    }

    // 重新扫描
    public void reScan(View view) {
        mBtDevAdapter.reScan();
    }




    @Override
    public void socketNotify(int state, final Object obj){
        if (isDestroyed())
            return;
        String msg = null;
        switch (state) {
            case BtBase.Listener.CONNECTED:
                BluetoothDevice dev = (BluetoothDevice) obj;
                msg = String.format("与%s(%s)连接成功", dev.getName(), dev.getAddress());
                mTips.setText(msg);
                break;
            case BtBase.Listener.DISCONNECTED:
                msg = "连接断开";
                mTips.setText(msg);
                break;
            case BtBase.Listener.MSG:
                msg = String.format("\n%s", obj);
                Log.e("蓝牙客户端","发来了消息");
                Log.e("蓝牙客户端",msg);
                if(msg.contains("异常")){
                    if(play_flag == 0){
                        if(player != null){

                            player.start();
                            play_flag = 1;
                        }
                        else {
                            player = MediaPlayer.create(this,R.raw.music_warning1);
                            player.setVolume(1f,1f);
                        }
                    }
                }
                if(msg.contains("正常")){
                    if(player != null){
//                        player.reset();
                        player.pause();
                        player.release();
                        player = null;
                        play_flag = 0;
                    }
                }
                mLogs.setText(msg);
                break;
        }
//        APP.toast(msg, 0);
    }
}
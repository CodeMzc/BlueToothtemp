package com.example.momo.bluetooth_temp.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.momo.bluetooth_temp.util.Util;


/**
 * 客户端，与服务端建立长连接
 */
public class BtClient extends BtBase {
    BtClient(Listener listener) {
        super(listener);
    }

    /**
     * 与远端设备建立长连接
     *
     * @param dev 远端设备
     */
    public void connect(BluetoothDevice dev) {
        close();
        try {
//             final BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(SPP_UUID); //加密传输，Android系统强制配对，弹窗显示配对码
            final BluetoothSocket socket = dev.createInsecureRfcommSocketToServiceRecord(SPP_UUID); //明文传输(不安全)，无需配对
            // 开启子线程
            Util.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    Log.e("BtClient","蓝牙传输线程开启");
                    loopRead(socket); //循环读取
                }
            });
        } catch (Throwable e) {
            close();
        }
    }
}
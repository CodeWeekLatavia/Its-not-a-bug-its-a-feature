package com.inabiaf.monkey;
import android.os.AsyncTask;
import android.util.Log;

import com.inabiaf.monkey.TcpClient;

public class ConnectTask extends AsyncTask<String, String, TcpClient> {
    public TcpClient mTcpClient;
    public MainActivity main;
    @Override
    protected TcpClient doInBackground(String... message) {

        //we create a TCPClient object
        mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        },main);
        mTcpClient.run();

        return mTcpClient;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        PacketHandler.HandlePacket(values[0],main);
        //response received from server
        //Log.d("test", "response " + values[0]);
        //process server response here....

    }
}
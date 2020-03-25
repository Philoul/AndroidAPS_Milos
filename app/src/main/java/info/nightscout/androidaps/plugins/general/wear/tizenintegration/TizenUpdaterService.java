package info.nightscout.androidaps.plugins.general.wear.tizenintegration;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgentV2;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import java.io.IOException;

public class TizenUpdaterService extends SAAgentV2 {
    private static final String TAG = "TizenUpdaterService";
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private final IBinder mBinder = new LocalBinder();
    private ServiceConnection mConnectionHandler = null;
    private Context mContext;

    public TizenUpdaterService(Context context) {
        super(TAG, context, SASOCKET_CLASS);
        mContext = context;

        SA mAccessory = new SA();
        try {
            mAccessory.initialize(mContext);
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e)) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
        }
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            final String message = new String(data);
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
//            closeConnection();
        }
    }

    public class LocalBinder extends Binder {
        public TizenUpdaterService getService() {
            return TizenUpdaterService.this;
        }
    }

    public void findPeers() {
        findPeerAgents();
    }

    public void sendData(final String data) {
        if (mConnectionHandler != null) {
            try {
                mConnectionHandler.send(getServiceChannelId(0), data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean closeConnection() {
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
            return true;
        } else {
            return false;
        }
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

//    @Override
//    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
//        if ((result == SAAgentV2.PEER_AGENT_FOUND) && (peerAgents != null)) {
//            for(SAPeerAgent peerAgent:peerAgents)
//                requestServiceConnection(peerAgent);
//        } else if (result == SAAgentV2.FINDPEER_DEVICE_NOT_CONNECTED) {
//            Toast.makeText(getApplicationContext(), "FINDPEER_DEVICE_NOT_CONNECTED", Toast.LENGTH_LONG).show();
//        } else if (result == SAAgentV2.FINDPEER_SERVICE_NOT_FOUND) {
//            Toast.makeText(getApplicationContext(), "FINDPEER_SERVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "NO_PEERS_FOUND", Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        if (result == SAAgentV2.CONNECTION_SUCCESS) {
            if (socket != null && peerAgent != null) {
                this.mConnectionHandler = (ServiceConnection) socket;
                Log.e(TAG, "connection to gear successful.");

            }
        } else if (result == SAAgentV2.CONNECTION_ALREADY_EXIST) {
            Toast.makeText(mContext, "CONNECTION_ALREADY_EXIST", Toast.LENGTH_LONG).show();
        } else if (result == SAAgentV2.CONNECTION_DUPLICATE_REQUEST) {
            Toast.makeText(mContext, "CONNECTION_DUPLICATE_REQUEST", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "CONNECTIONG_FAILURE", Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    protected void onPeerAgentsUpdated(SAPeerAgent[] peerAgents, int result) {
//        final SAPeerAgent[] peers = peerAgents;
//        final int status = result;
//        mHandler.post(() -> {
//            if (peers != null) {
//                if (status == SAAgentV2.PEER_AGENT_AVAILABLE) {
//                    Toast.makeText(getApplicationContext(), "PEER_AGENT_AVAILABLE", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "PEER_AGENT_UNAVAILABLE", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        Log.d(TAG, "connection was requested");

        if (peerAgent != null) {
            acceptServiceConnectionRequest(peerAgent);
        }
    }
}
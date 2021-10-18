package cabuser.com.rydz.socket;


public interface SocketListener {
    void onSocketConnected();
    void onSocketDisconnected();
    void onSocketConnectionError();
    void onSocketConnectionTimeOut();
}

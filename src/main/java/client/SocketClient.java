package client;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import socket.Request;

public class SocketClient {
    private static volatile SocketClient instance;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Gson gson;

     private SocketClient() {
            gson = new GsonBuilder().serializeNulls().create();
            try {
                socket = new Socket(SocketConfig.SERVER_IP, SocketConfig.SERVER_PORT);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                // Handle exceptions, e.g., connection errors
                Log.e("SocketClient", e.getMessage(), e);
            }
        }
    public class SocketConfig {
        public static final String SERVER_IP = "128.153.177.140";
        public static final int SERVER_PORT = 8000; // Replace with your server's port number
    }



    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public <T> T sendRequest(Request request, Class<T> responseClass) {
        try {
            String jsonRequest = gson.toJson(request);

            outputStream.writeUTF(jsonRequest);
            outputStream.flush();

            String jsonResponse = inputStream.readUTF();

            T responseObject = gson.fromJson(jsonResponse, responseClass);
            return responseObject;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            close();
            return null;
        }
    }
}

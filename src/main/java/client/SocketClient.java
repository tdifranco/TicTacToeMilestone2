package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.Gson;

public class SocketClient {
    private static volatile SocketClient instance;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Gson gson;

     private SocketClient() {
            gson = new Gson();
            try {
                socket = new Socket(SocketConfig.SERVER_IP, SocketConfig.SERVER_PORT);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                // Handle exceptions, e.g., connection errors
                System.err.println(e.getMessage());
            }
        }
    public class SocketConfig {
        public static final String SERVER_IP = "0.0.0.0";
        public static final int SERVER_PORT = 5000; // Replace with your server's port number
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

    public <T> T sendRequest(Object request, Class<T> responseClass) {
        try {
            String jsonRequest = gson.toJson(request);

            byte[] requestBytes = jsonRequest.getBytes();
            outputStream.writeInt(requestBytes.length);
            outputStream.write(requestBytes);
            outputStream.flush();

            int responseLength = inputStream.readInt();
            byte[] responseBytes = new byte[responseLength];
            inputStream.readFully(responseBytes);

            String jsonResponse = new String(responseBytes);
            T responseObject = gson.fromJson(jsonResponse, responseClass);
            return responseObject;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

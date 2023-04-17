package com.example.backend.tcpClient;

import static com.example.backend.utlis.ConstantUtils.LIBRARY_TCP_CLIENT_SUCCESS_RESPONSE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.backend.dto.TcpInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public class TcpClient {

    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private static final String libraryClientIp = "192.168.1.16";
    private static final Integer readSocketPort = 6667;
    private static final Integer sendSocketPort = 6666;

    public static boolean readResponseFromLibrarian() {
        try {
            ServerSocket serverSocket = new ServerSocket(readSocketPort);
            Socket socket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String str = dis.readUTF();
            socket.close();
            serverSocket.close();
            if (str.equals(LIBRARY_TCP_CLIENT_SUCCESS_RESPONSE)){
                return true;
            }
        } catch (IOException aE) {
            throw new RuntimeException(aE);
        }
        return false;
    }

    public static void sendActionOrderToLibrarian(TcpInfoDto aNewOrder) {
        Socket s;
        DataOutputStream dout;
        try {
            s = new Socket(libraryClientIp,sendSocketPort);
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(objectMapper.writeValueAsString(aNewOrder));
            dout.flush();
            dout.close();
            s.close();
        } catch (IOException aE) {
            throw new RuntimeException(aE);
        }
    }
}

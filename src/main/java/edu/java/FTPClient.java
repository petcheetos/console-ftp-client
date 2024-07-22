package edu.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPClient {

    private final String server;
    private final int port = 21;
    private final String login;
    private final String password;
    private Socket controlSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private final String clientIp = "127.0.0.1";

    public FTPClient(String server, String login, String password) {
        this.server = server;
        this.login = login;
        this.password = password;
    }

    public void connect() throws IOException {
        controlSocket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
        readServerResponse();
        readServerResponse();
        sendCommand("USER " + login);
        readServerResponse();
        sendCommand("PASS " + password);
        readServerResponse();
    }

    public void disconnect() throws IOException {
        sendCommand("QUIT");
        readServerResponse();
        controlSocket.close();
    }

    public String getDataFromFile(String remoteFilePath, boolean passiveMode) throws IOException {
        String result;
        if (passiveMode) {
            result = getDataPassiveMode(remoteFilePath);
        } else {
            result = getDataActiveMode(remoteFilePath);
        }
        return result;
    }

    private String getDataPassiveMode(String remoteFilePath) throws IOException {
        Socket dataSocket = enterPassiveMode();
        sendCommand("RETR " + remoteFilePath);
        validateResponse(150, readServerResponse());
        return getString(dataSocket);
    }

    private String getString(Socket dataSocket) throws IOException {
        StringBuilder fileData = new StringBuilder();
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                fileData.append(line).append("\n");
            }
        }
        dataSocket.close();
        readServerResponse();
        return fileData.toString();
    }

    private String getDataActiveMode(String remoteFilePath) throws IOException {
        ServerSocket dataSocket = enterActiveMode();
        sendCommand("RETR " + remoteFilePath);
        Socket socket = dataSocket.accept();
        String result = getString(socket);
        dataSocket.close();
        return result;
    }

    public void saveDataToFile(String data, String remoteFilePath, boolean passiveMode) throws IOException {
        if (passiveMode) {
            saveDataToFilePassive(data, remoteFilePath);
        } else {
            saveDataToFileActive(data, remoteFilePath);
        }
    }

    private void saveDataToFilePassive(String data, String remoteFilePath) throws IOException {
        Socket dataSocket = enterPassiveMode();
        sendCommand("STOR " + remoteFilePath);
        validateResponse(150, readServerResponse());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
            writer.write(data);
            writer.flush();
        }
        dataSocket.close();
        readServerResponse();
    }

    private void saveDataToFileActive(String data, String remoteFilePath) throws IOException {
        ServerSocket serverSocket = enterActiveMode();
        sendCommand("STOR " + remoteFilePath);
        validateResponse(150, readServerResponse());
        Socket dataSocket = serverSocket.accept();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
            writer.write(data);
            writer.flush();
        }
        dataSocket.close();
        readServerResponse();
    }

    private Socket enterPassiveMode() throws IOException {
        sendCommand("PASV");
        String response = readServerResponse();

        if (!response.startsWith("227 ")) {
            throw new IOException("Failed to enter passive mode: " + response);
        }

        try {
            FTPAddress address = FTPAddress.create(response);
            return new Socket(address.host, address.port);
        } catch (IOException ioException) {
            throw new RuntimeException("Invalid response in passive mode");
        }
    }

    private ServerSocket enterActiveMode() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        FTPAddress address = new FTPAddress(clientIp, port);
        sendCommand("PORT " + address);
        validateResponse(200, readServerResponse());
        return serverSocket;
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

    private String readServerResponse() throws IOException {
        return reader.readLine();
    }

    private void validateResponse(int expectedCode, String response) throws IOException {
        int responseCode = Integer.parseInt(response.substring(0, 3));
        if (responseCode != expectedCode) {
            throw new IOException("Expected response code " + expectedCode + " but got " + responseCode);
        }
    }
}

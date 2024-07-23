package edu.java.ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class FTPClient {

    private static final Logger logger = Logger.getLogger(FTPClient.class.getName());
    private final String ip;
    private final int port = 21;
    private final String login;
    private final String password;
    private Socket controlSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private final String clientIPActiveMode = "127.0.0.1";

    public FTPClient(String server, String login, String password) {
        this.ip = server;
        this.login = login;
        this.password = password;
    }

    public boolean connect() {
        try {
            controlSocket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
            readServerResponse();
            readServerResponse();
            sendCommand("USER " + login);
            readServerResponse();
            sendCommand("PASS " + password);
            readServerResponse();
            return true;
        } catch (IOException e) {
            logger.severe("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            sendCommand("QUIT");
            readServerResponse();
        } catch (IOException e) {
            logger.warning("Failed to send QUIT command or read response: " + e.getMessage());
        } finally {
            try {
                if (controlSocket != null && !controlSocket.isClosed()) {
                    controlSocket.close();
                    logger.info("Control socket closed.");
                }
            } catch (IOException e) {
                logger.warning("Failed to close control socket: " + e.getMessage());
            }
        }
    }

    public String getDataFromFile(String remoteFilePath, boolean passiveMode) {
        try {
            if (passiveMode) {
                return getDataPassiveMode(remoteFilePath);
            } else {
                return getDataActiveMode(remoteFilePath);
            }
        } catch (IOException e) {
            logger.severe("Failed to get data from file: " + e.getMessage());
            return null;
        }
    }

    private String getDataPassiveMode(String remoteFilePath) throws IOException {
        Socket dataSocket = null;
        try {
            dataSocket = enterPassiveMode();
            sendCommand("RETR " + remoteFilePath);
            validateResponse(150, readServerResponse());
            return getString(dataSocket);
        } catch (IOException e) {
            logger.severe("Failed in passive mode: " + e.getMessage());
            throw e;
        } finally {
            if (dataSocket != null && !dataSocket.isClosed()) {
                dataSocket.close();
                logger.info("Data socket closed.");
            }
        }
    }

    private String getDataActiveMode(String remoteFilePath) throws IOException {
        ServerSocket serverSocket = null;
        Socket dataSocket = null;
        try {
            serverSocket = enterActiveMode();
            sendCommand("RETR " + remoteFilePath);
            validateResponse(150, readServerResponse());
            dataSocket = serverSocket.accept();
            return getString(dataSocket);
        } catch (IOException e) {
            logger.severe("Failed in active mode: " + e.getMessage());
            throw e;
        } finally {
            if (dataSocket != null && !dataSocket.isClosed()) {
                dataSocket.close();
                logger.info("Data socket closed.");
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logger.info("Server socket closed.");
            }
        }
    }

    public boolean saveDataToFile(String data, String remoteFilePath, boolean passiveMode) {
        try {
            if (passiveMode) {
                saveDataToFilePassive(data, remoteFilePath);
            } else {
                saveDataToFileActive(data, remoteFilePath);
            }
            return true;
        } catch (IOException e) {
            logger.severe("Failed to save data to file: " + e.getMessage());
            return false;
        }
    }

    private void saveDataToFilePassive(String data, String remoteFilePath) throws IOException {
        Socket dataSocket = null;
        try {
            dataSocket = enterPassiveMode();
            sendCommand("STOR " + remoteFilePath);
            validateResponse(150, readServerResponse());
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
                writer.write(data);
                writer.flush();
            }
        } catch (IOException e) {
            logger.severe("Failed in passive mode: " + e.getMessage());
            throw e;
        } finally {
            if (dataSocket != null && !dataSocket.isClosed()) {
                dataSocket.close();
                logger.info("Data socket closed.");
            }
            readServerResponse();
        }
    }

    private void saveDataToFileActive(String data, String remoteFilePath) throws IOException {
        ServerSocket serverSocket = null;
        Socket dataSocket = null;
        try {
            serverSocket = enterActiveMode();
            sendCommand("STOR " + remoteFilePath);
            validateResponse(150, readServerResponse());
            dataSocket = serverSocket.accept();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
                writer.write(data);
                writer.flush();
            }
        } catch (IOException e) {
            logger.severe("Failed in active mode: " + e.getMessage());
            throw e;
        } finally {
            if (dataSocket != null && !dataSocket.isClosed()) {
                dataSocket.close();
                logger.info("Data socket closed.");
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logger.info("Server socket closed.");
            }
            readServerResponse();
        }
    }

    private Socket enterPassiveMode() throws IOException {
        sendCommand("PASV");
        String response = readServerResponse();

        if (!response.startsWith("2")) {
            throw new IOException("Failed to enter passive mode: " + response);
        }

        try {
            FTPAddress address = FTPAddress.create(response);
            return new Socket(address.host, address.port);
        } catch (IOException ioException) {
            logger.severe("Invalid response in passive mode: " + ioException.getMessage());
            throw new RuntimeException("Invalid response in passive mode");
        }
    }

    private ServerSocket enterActiveMode() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        FTPAddress address = new FTPAddress(clientIPActiveMode, port);
        sendCommand("PORT " + address);
        validateResponse(200, readServerResponse());
        return serverSocket;
    }

    private String getString(Socket dataSocket) throws IOException {
        StringBuilder fileData = new StringBuilder();
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                fileData.append(line).append("\n");
            }
        }
        readServerResponse();
        return fileData.toString();
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

    private String readServerResponse() throws IOException {
        String response = reader.readLine();
        logger.info("Response: " + response);
        return response;
    }

    private void validateResponse(int expectedCode, String response) throws IOException {
        int responseCode = Integer.parseInt(response.substring(0, 3));
        if (responseCode != expectedCode) {
            throw new IOException("Expected response code " + expectedCode + " but got " + responseCode);
        }
    }
}

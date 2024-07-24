package edu.java;

import edu.java.commands.CommandHandler;
import edu.java.commands.Commands;
import edu.java.console.ConsoleInterface;
import edu.java.console.ConsoleMessages;
import edu.java.entities.Student;
import edu.java.ftp.FTPClient;
import edu.java.services.StudentService;
import edu.java.utils.JSONHandler;

import java.util.HashSet;

public class ConsoleClientApplication {
    private boolean passiveMode = true;

    private final ConsoleInterface console;

    public ConsoleClientApplication() {
        this.console = new ConsoleInterface();
    }

    public void run() {
        if (passiveMode) {
            console.print(ConsoleMessages.DEFAULT_PASSIVE);
        } else {
            console.print(ConsoleMessages.ACTIVE_MODE);
        }

        console.print(ConsoleMessages.INPUT_IP);
        String ip = console.readLine();

        console.print(ConsoleMessages.INPUT_LOGIN);
        String login = console.readLine();

        console.print(ConsoleMessages.INPUT_PASSWORD);
        String password = console.readLine();

        FTPClient ftpClient = new FTPClient(ip, login, password);
        JSONHandler jsonHandler = new JSONHandler();

        if (ftpClient.connect()) {
            console.print(ConsoleMessages.CONNECTED);
            console.print(ConsoleMessages.INPUT_FILE_NAME);
            String remoteFilePath = console.readLine();
            String data = ftpClient.getDataFromFile(remoteFilePath, passiveMode);
            while (data == null) {
                console.print(ConsoleMessages.INVALID_FILENAME);
                remoteFilePath = console.readLine();
                data = ftpClient.getDataFromFile(remoteFilePath, passiveMode);
            }
            HashSet<Student> students = jsonHandler.parseData(data);
            StudentService studentService = new StudentService(students);
            Commands commands = new Commands(studentService);
            CommandHandler commandHandler = new CommandHandler(console, commands);

            while (true) {
                console.print(commands.help());
                String command = console.readLine();
                if (commandHandler.handleCommand(command)) {
                    String dataToFile = jsonHandler.serializeStudents(students);
                    if (ftpClient.saveDataToFile(dataToFile, remoteFilePath, passiveMode)) {
                        console.print(ConsoleMessages.DATA_SAVED);
                    } else {
                        console.print(ConsoleMessages.NO_DATA_SAVED);
                    }
                    break;
                }
            }
        } else {
            console.printError(ConsoleMessages.NOT_CONNECTED);
        }
        console.close();
    }
}

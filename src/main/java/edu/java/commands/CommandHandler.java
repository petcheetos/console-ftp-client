package edu.java.commands;

import edu.java.console.ConsoleInterface;
import edu.java.console.ConsoleMessages;

public class CommandHandler {
    private final ConsoleInterface console;

    private final Commands commands;

    public CommandHandler(ConsoleInterface console, Commands commands) {
        this.console = console;
        this.commands = commands;
    }

    public boolean handleCommand(String command) {
        long id;
        String name;
        switch (command) {
            case "help":
                console.print(ConsoleMessages.COMMAND_LIST);
                console.print(commands.help());
                break;
            case "show":
                console.print(ConsoleMessages.STUDENT_LIST);
                console.print(commands.show());
                break;
            case "list":
                console.print(ConsoleMessages.ENTER_NAME);
                name = console.readLine();
                console.print(commands.list(name));
                break;
            case "info":
                console.print(ConsoleMessages.ENTER_ID);
                id = Long.parseLong(console.readLine());
                console.print(commands.info(id));
                break;
            case "add":
                console.print(ConsoleMessages.ENTER_NAME);
                name = console.readLine();
                console.print(commands.add(name));
                break;
            case "delete":
                console.print(ConsoleMessages.ENTER_ID);
                id = Long.parseLong(console.readLine());
                console.print(commands.delete(id));
                break;
            case "exit":
                console.print(ConsoleMessages.EXIT_MESSAGE);
                return true;
            default:
                console.print(ConsoleMessages.INVALID_COMMAND);
                break;
        }
        return false;
    }
}
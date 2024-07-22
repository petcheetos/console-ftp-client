package edu.java;

import java.io.IOException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input host");
        String host = scanner.next();

        System.out.println("Input login");
        String login = scanner.next();

        System.out.println("Input password");
        String password = scanner.next();

        FTPClient ftpClient = new FTPClient(host, login, password);

        displayMenu();

        try {
            ftpClient.connect();
        } catch (IOException | RuntimeException e) {

        } finally {
            ftpClient.disconnect();
        }

    }

    public static void displayMenu() {
        System.out.println("Menu:");
        System.out.println("1. Get list of students by name");
        System.out.println("2. Get student information by ID");
        System.out.println("3. Add student");
        System.out.println("4. Delete student by ID");
        System.out.println("5. Exit");
    }
}
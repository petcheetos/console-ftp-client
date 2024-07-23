package edu.java.commands;

import edu.java.console.ConsoleMessages;
import edu.java.entities.Student;
import edu.java.services.StudentService;

import java.util.List;

public class Commands {

    private final StudentService studentService;

    public Commands(StudentService studentService) {
        this.studentService = studentService;
    }

    public String help() {
        return "Команды:\n" +
                "help - показать список команд\n" +
                "show - показать всех студентов\n" +
                "list - показать студентов по имени\n" +
                "info - показать информацию о студенте по id\n" +
                "add - добавить студента\n" +
                "delete - удалить студента\n" +
                "exit - завершение работы программы";
    }

    public String show() {
        List<Student> list = studentService.getListStudentsSortedAlphabetically();

        if (list.isEmpty()) {
            return ConsoleMessages.EMPTY_LIST;
        }

        StringBuilder result = new StringBuilder(ConsoleMessages.ALL_STUDENTS_LIST);
        for (Student student : list) {
            result.append("\nid ").append(student.getId()).append(" - ").append(student.getName());
        }
        return result.toString();
    }

    public String list(String name) {
        List<Student> list = studentService.getListStudentsByName(name);

        if (list.isEmpty()) {
            return ConsoleMessages.EMPTY_LIST;
        }

        StringBuilder result = new StringBuilder(ConsoleMessages.STUDENTS_WITH_NAME_LIST + name + ":");
        for (Student student : list) {
            result.append("\nid ").append(student.getId()).append(" - ").append(student.getName());
        }
        return result.toString();
    }

    public String info(long id) {
        String studentName = studentService.getNameById(id);

        if (studentName == null) {
            return ConsoleMessages.NO_STUDENT_WITH_ID + id;
        }

        return ConsoleMessages.STUDENT_INFO + ConsoleMessages.STUDENT_NAME + studentName;
    }

    public String add(String name) {
        Student student = studentService.addStudent(name);
        return ConsoleMessages.STUDENT_ADDED + student.getId() + ", имя: " + student.getName();
    }

    public String delete(long id) {
        studentService.removeStudentById(id);
        return ConsoleMessages.STUDENT_DELETED;
    }
}

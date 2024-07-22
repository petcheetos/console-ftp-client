package edu.java;

import java.util.List;

public class Commands {

    private final StudentManager studentManager;

    public Commands(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public void list() {
        List<Student> list = studentManager.getListStudentsSortedAlphabetically();

        if (list.isEmpty()) {
            System.out.println("Список пуст");
            return;
        }

        System.out.println("Список всех студентов: ");
        for (Student student : list) {
            System.out.println("id " + student.getId() + " - " + student.getName());
        }
    }

    public void list(String name) {
        List<Student> list = studentManager.getListStudentsByName(name);

        if (list.isEmpty()) {
            System.out.println("Список пуст");
            return;
        }

        System.out.println("Список студентов с именем " + name + ":");
        for (Student student : list) {
            System.out.println("id " + student.getId() + " - " + student.getName());
        }
    }

    public void info(long id) {
        String studentName = studentManager.getNameById(id);

        if (studentName == null) {
            System.err.println("Нет студента с id " + id);
            return;
        }

        System.out.println("Студент с id " + id + ":");
        System.out.println("Имя: " + studentName);
    }

    public void add(String name) {
        Student student = studentManager.addStudent(name);
        System.out.println("Добавлен студент id: " + student.getId() + ", имя: " + student.getName());
    }

    public void delete(long id) {
        studentManager.removeStudentById(id);
        System.out.println("Студент удален");
    }
}

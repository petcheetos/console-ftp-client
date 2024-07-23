package edu.java.services;

import edu.java.entities.Student;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class StudentService {
    private final HashSet<Student> students;

    public StudentService(HashSet<Student> students) {
        this.students = students;
    }

    public Student addStudent(String name) {
        Student student = new Student(generateId(), name);
        students.add(student);
        return student;
    }

    public HashSet<Student> getStudents() {
        return new HashSet<>(students);
    }

    public void removeStudentById(long id) {
        students.removeIf(student -> student.getId() == id);
    }

    public List<Student> getListStudentsSortedAlphabetically() {
        return students.stream()
                .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                .collect(Collectors.toList());
    }

    public List<Student> getListStudentsByName(String name) {
        return students.stream()
                .filter(student -> student.getName().equalsIgnoreCase(name))
                .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                .collect(Collectors.toList());
    }

    public String getNameById(long id) {
        return students.stream()
                .filter(student -> student.getId() == id)
                .map(Student::getName)
                .findFirst()
                .orElse(null);
    }

    public long generateId() {
        return students.stream()
                .mapToLong(Student::getId)
                .max()
                .orElse(0L) + 1;
    }
}

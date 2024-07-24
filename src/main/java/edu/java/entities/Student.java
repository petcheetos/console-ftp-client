package edu.java.entities;

import java.util.Objects;

public class Student implements Comparable<Student> {
    private final long id;
    private final String name;

    public Student(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Student other) {
        return this.name.compareTo(other.name);
    }


    public String toJSONObject() {
        return String.format("{\"id\": %d, \"name\": \"%s\"}", id, name);
    }

    @Override
    public String toString() {
        return "Student (" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        return Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}

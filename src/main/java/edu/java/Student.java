package edu.java;

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
}

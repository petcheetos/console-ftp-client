package edu.java;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONHandler {

    public String serializeStudents(HashSet<Student> students) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        stringBuilder.append("  \"students\": [\n");

        boolean first = true;
        for (Student student : students) {
            if (!first) {
                stringBuilder.append(",\n");
            }
            stringBuilder.append("\t").append(student.toJSONObject());
            first = false;
        }

        stringBuilder.append("\n  ]\n}");
        return stringBuilder.toString();
    }

    public HashSet<Student> parseData(String data) {
        HashSet<Student> students = new HashSet<>();

        Pattern pattern = Pattern.compile("\"id\":\\s*(\\d+),\\s*\"name\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(data);
        long id;

        while (matcher.find()) {
            String idStr = matcher.group(1);
            String name = matcher.group(2);

            if (isNumeric(idStr) && !name.contains("\"name\"") && !name.contains("\"id\"") && !name.contains("}")) {
                id = Long.parseLong(idStr);
                students.add(new Student(id, name));
            }
        }
        return students;
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}

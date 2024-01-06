package it.unipi.m598992;

import it.unipi.m598992.classToTest.City;
import it.unipi.m598992.classToTest.Professor;
import it.unipi.m598992.classToTest.Student;
import it.unipi.m598992.xmlSerializer.XMLSerializer;

public class Main {

    public static void main(String[] args) {
        XMLSerializer.serialize(new Object[]{
                new Student("Luca", "Rizzo", 23),
                new Professor("Mario", "Rossi", "Math", 42),
                new Professor("Paolo", "Rossi", "English", 32),
                new City("Pisa", 90000, false, "notSerialized"),
                new City("Pisa", 90000, false, "notSerialized"),
                new Student("Jane", "Doe", 42),
                new City("Rome", 2873000, true, "notSerialized")},
                "src/main/resources/completeTest");
    }

}
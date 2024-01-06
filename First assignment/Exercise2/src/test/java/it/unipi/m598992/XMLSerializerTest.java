package it.unipi.m598992;


import it.unipi.m598992.classToTest.City;
import it.unipi.m598992.classToTest.Professor;
import it.unipi.m598992.classToTest.Student;
import it.unipi.m598992.xmlSerializer.XMLSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XMLSerializerTest {

    public static final String FILE_NAME = "fileName";

    @Test
    void testStudent() throws IOException {
        XMLSerializer.serialize(new Student[]{new Student("Jane", "Doe", 42)}, FILE_NAME);

        String expected = Files.readString(Path.of("src/test/resources/expectedStudent.xml"));
        String result = Files.readString(Path.of(FILE_NAME + ".xml"));
        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void testProfessor() throws IOException, IllegalAccessException {
        XMLSerializer.serialize(new Professor[]{
                new Professor("Jane", "Doe", "Math", 42)}, FILE_NAME);

        String expected = Files.readString(Path.of("src/test/resources/expectedProfessor.xml"));
        String result = Files.readString(Path.of(FILE_NAME + ".xml"));
        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void testStudentProfessor() throws IOException {
        XMLSerializer.serialize(new Object[]{
                new Student("Jane", "Doe", 42),
                new Professor("Mario", "Rossi", "Math", 42)}, FILE_NAME);

        String expected = Files.readString(Path.of("src/test/resources/expectedStudentProfessor.xml"));
        String result = Files.readString(Path.of(FILE_NAME + ".xml"));
        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void testNoRepetitionClass() throws IOException {
        XMLSerializer.serialize(new Object[]{
                new Student("Luca", "Rizzo", 23),
                new Student("Jane", "Doe", 42),
                new City("Pisa", 90000, false, "notSerialized"),
                new City("Rome", 2873000, true, "notSerialized")}, FILE_NAME);

        String expected = Files.readString(Path.of("src/test/resources/expectedNoRepetition.xml"));
        String result = Files.readString(Path.of(FILE_NAME + ".xml"));
        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void testCityStudentClass() throws IOException {
        XMLSerializer.serialize(new Object[]{
                new City("Rome", 2873000, true, "notSerialized"),
                new Student("Luca", "Rizzo", 23)}, FILE_NAME);

        String expected = Files.readString(Path.of("src/test/resources/expectedCityStudent.xml"));
        String result = Files.readString(Path.of(FILE_NAME + ".xml"));
        assertEquals(expected.trim(), result.trim());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(Path.of(FILE_NAME + ".xml"));
    }
}
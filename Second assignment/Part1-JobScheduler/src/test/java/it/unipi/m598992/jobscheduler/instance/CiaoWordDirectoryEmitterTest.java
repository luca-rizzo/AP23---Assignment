package it.unipi.m598992.jobscheduler.instance;

import it.unipi.m598992.auxfile.AJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CiaoWordDirectoryEmitterTest {

    private CiaoWordDirectoryEmitter sut = new CiaoWordDirectoryEmitter();
    @Test
    void testCorrectFileFound() {
        ByteArrayInputStream in = new ByteArrayInputStream("src/test/resources".getBytes());
        System.setIn(in);

        Stream<AJob<String, String>> emit = sut.emit();

        assertEquals(2, emit.toList().size());
    }
}
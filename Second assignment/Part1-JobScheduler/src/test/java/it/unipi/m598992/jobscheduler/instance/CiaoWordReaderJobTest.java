package it.unipi.m598992.jobscheduler.instance;


import it.unipi.m598992.auxfile.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CiaoWordReaderJobTest {

    private CiaoWordReaderJob sut = new CiaoWordReaderJob("src/test/resources/The_Adventures_of_Sherlock_Holmes.txt");

    @Test
    public void testCorrectExecute() {

        List<Pair<String, String>> result = sut.execute().toList();

        assertEquals(32, result.size());
        assertTrue(result.stream().allMatch(pair -> pair.getKey().equals(sut.toCIAO(pair.getValue()))));
    }

}
package it.unipi.m598992.jobscheduler.instance;

import it.unipi.m598992.auxfile.Pair;
import it.unipi.m598992.jobscheduler.exception.OutputStrategyException;
import it.unipi.m598992.jobscheduler.strategy.OutputStrategy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Implementation of the OutputStrategy interface for writing in a csv file the list of 'CIAO' keys and the number of words
 * associated with each key
 */
public class CiaoWordCsvOutput implements OutputStrategy<String, String> {

    /**
     * Writes the list of 'CIAO' keys and the number of anagrams associated with each key,
     * one per line, in the file count_anagrams.csv
     *
     * @param collectOutput The stream of pairs containing 'CIAO' keys and associated anagrams.
     * @throws OutputStrategyException If there's an issue with file I/O.
     */
    @Override
    public void output(Stream<Pair<String, List<String>>> collectOutput) {
        // Open the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("count_anagrams.csv"))) {
            // For each pair, write to the file in CSV format
            collectOutput.forEach(pair -> writeToFileInCsvFormat(writer, pair));
        } catch (IOException e) {
            // Throw an exception if there's an issue with file I/O
            throw new OutputStrategyException(e);
        }
    }

    private void writeToFileInCsvFormat(BufferedWriter writer, Pair<String, List<String>> pair) {
        try {
            // Create a CSV representation of the Pair and write to the file
            String csvLine = createCsvLine(pair);
            writer.write(csvLine);
        } catch (IOException e) {
            // Throw an exception if there's an issue with writing to the file
            throw new OutputStrategyException(e);
        }
    }

    private String createCsvLine(Pair<String, List<String>> item) {
        return String.format("%s, %d\n", item.getKey(), item.getValue().size());
    }
}

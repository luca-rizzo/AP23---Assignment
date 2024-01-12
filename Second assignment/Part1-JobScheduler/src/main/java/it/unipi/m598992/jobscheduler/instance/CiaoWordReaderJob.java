package it.unipi.m598992.jobscheduler.instance;

import it.unipi.m598992.auxfile.AJob;
import it.unipi.m598992.auxfile.Pair;
import it.unipi.m598992.jobscheduler.exception.JobException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


/**
 * Implementation of a job for reading a file and returning pairs of the form (ciao(w), w)
 */
public class CiaoWordReaderJob extends AJob<String, String> {
    private final Path path;

    public CiaoWordReaderJob(Path path) {
        this.path = path;
    }

    public CiaoWordReaderJob(String filename) {
        this.path = Path.of(filename);
    }

    /**
     * Reads the file and returns a stream containing all pairs of the form (ciao(w), w)
     *
     * @return A stream of pairs representing the 'CIAO' word form and the original word.
     * @throws JobException If there's an issue with reading the file or processing its contents.
     */
    @Override
    public Stream<Pair<String, String>> execute() {
        // Read file line by line
        return getLinesFromFile().stream()
                // Remove all punctuation marks by replacing them with a space
                .map(line -> line.replaceAll("[^\\sa-zA-Z0-9]", " "))
                // Retrieve all words in a line by splitting it by space
                // Use flatMap to flatten Stream<Stream>> to Stream
                .flatMap(line -> Arrays.stream(line.split(" ")))
                // Filter only the words that are requested
                .filter(this::shouldIncludeWord)
                // Map each word to a Pair with its modified form (toCIAO(word))
                .map(word -> new Pair<>(toCIAO(word), word));
    }

    /**
     * Transforms a word to its 'CIAO' form by lexicographically sorting its characters in lowercase.
     *
     * @param word The input word to be transformed.
     * @return The 'CIAO' representation of the input word.
     */
    protected String toCIAO(String word){
        // Lexicographically sort the characters in lowercase
        return sortLexicographically(word.toLowerCase());
    }

    private String sortLexicographically(String input) {
        char[] charArray = input.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }

    private List<String> getLinesFromFile() {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new JobException(e);
        }
    }

    private boolean shouldIncludeWord(String word) {
        return word.length() >= 4 && containsOnlyLetters(word);
    }

    private boolean containsOnlyLetters(String input) {
        return input.chars().allMatch(Character::isLetter);
    }
}
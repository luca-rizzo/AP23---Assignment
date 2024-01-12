package it.unipi.m598992.jobscheduler.instance;

import it.unipi.m598992.auxfile.AJob;
import it.unipi.m598992.jobscheduler.exception.EmitStrategyException;
import it.unipi.m598992.jobscheduler.strategy.EmitStrategy;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;
import java.util.stream.Stream;


/**
 * Implementation of the EmitStrategy interface for reading documents from a user-specified directory.
 */
public class CiaoWordDirectoryEmitter implements EmitStrategy<String, String> {

    /**
     * Visits the directory requested by user and creates a new job for each txt file in that directory.
     *
     * @return A stream of AJob instances, each representing a document processing job.
     * @throws EmitStrategyException If there's an issue with reading the directory or creating jobs.
     */
    @Override
    public Stream<AJob<String, String>> emit() {
        String directoryPath = readFilePathFromUser();
        List<Path> allPath = getAllPathFromDir(directoryPath);
        // for each element of the directory, if it is a txt file, the corresponding
        // Job will be created
        return allPath.stream()
                .filter(this::isTxtFile)
                .map(path -> new CiaoWordReaderJob(path.toAbsolutePath()));
    }

    private List<Path> getAllPathFromDir(String directoryPath) {
        // it is necessary to first collect all paths in a list and not work with the original source,
        // because at the end of the try block the source (Files.list()) will be closed and we will receive an exception
        try (Stream<Path> pathStream = Files.list(Paths.get(directoryPath))) {
            return pathStream.toList();
        } catch (IOException e) {
            throw new EmitStrategyException(e);
        }
    }

    private boolean isTxtFile(Path pathElement) {
        //check if it is a regular file and its extension is txt
        return Files.isRegularFile(pathElement) &&
                pathElement.toString().toLowerCase().endsWith(".txt");
    }


    private String readFilePathFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of a directory: ");
        return scanner.nextLine();
    }
}

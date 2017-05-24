package xyz.marcelo.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

public class Runner
{
    private Runner()
    {
    }

    public static void main(String[] args) throws IOException
    {
        try
        {
            // try to parse command line args
            if (args.length != 2)
            {
                Logger.error("Usage: java -jar FolderSync.jar \"SOURCE\" \"DESTINATION\"");
                throw new IllegalArgumentException();
            }

            // create File references based on the provided args
            final String FROM_PATH = replaceUserHomeIfPresent(args[0]);
            final File FROM_FILE = new File(FROM_PATH);
            final String TO_PATH = replaceUserHomeIfPresent(args[1]);
            final File TO_FILE = new File(TO_PATH);

            // get all files from SOURCE
            List<String> allPathsInSource = null;
            if (!FROM_FILE.exists())
            {
                Logger.error("Source folder does not exist.");
                throw new IOException();
            }
            else
            {
                allPathsInSource = getFiles(FROM_PATH);
                Collections.sort(allPathsInSource);
            }
            Logger.info("Found [{}] files in [{}]", allPathsInSource.size(), FROM_PATH);

            // get all files from DESTINATION
            List<String> allPathsInDestination = null;
            if (!TO_FILE.exists())
            {
                allPathsInDestination = Collections.emptyList();
                Files.createDirectory(Paths.get(TO_PATH));
            }
            else
            {
                allPathsInDestination = getFiles(TO_PATH);
                Collections.sort(allPathsInDestination);
            }
            Logger.info("Found [{}] files in [{}]", allPathsInDestination.size(), TO_PATH);

            // copy non-existent files in DESTINATION from SOURCE
            for (String pathInSource : allPathsInSource)
            {
                String pathInDestination = pathInSource.replace(FROM_PATH, TO_PATH);

                if (!allPathsInDestination.stream().anyMatch(f -> f.equals(pathInDestination)))
                {
                    Logger.debug("Copying [{}] to [{}]", pathInSource, pathInDestination);
                    new File(pathInDestination).getParentFile().mkdirs();
                    Files.copy(Paths.get(pathInSource), Paths.get(pathInDestination));
                }
            }
        }
        catch (IOException e)
        {
            Logger.error("Unexpected exception: {}", e);
            throw e;
        }
    }

    private static String replaceUserHomeIfPresent(String path)
    {
        return path.contains("~") ? path.replace("~", System.getProperty("user.home")) : path;
    }

    // https://stackoverflow.com/questions/2056221/recursively-list-files-in-java
    private static List<String> getFiles(String path) throws IOException
    {
        return Files
                .walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(filePath -> filePath.toAbsolutePath().toString())
                .collect(Collectors.toList());
    }
}

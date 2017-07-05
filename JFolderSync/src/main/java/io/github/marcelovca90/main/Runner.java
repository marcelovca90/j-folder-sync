package io.github.marcelovca90.main;

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
        // try to parse command line args
        if (args.length < 2)
        {
            Logger.error("Usage: java -jar FolderSync.jar \"src\" \"[dest1]\" \"[dest2]\" \"[destN]\"");
            throw new IllegalArgumentException();
        }

        final String filenameInSource = args[0];
        final File fileInSource = new File(filenameInSource);

        // iterate over the destination folders
        for (int i = 1; i < args.length; i++)
        {
            // create File references based on the provided args
            final String filenameInDestination = args[i];
            final File fileInDestination = new File(filenameInDestination);

            // retrieve all files in source folder
            List<String> allPathsInSource = null;
            if (!fileInSource.exists())
            {
                Logger.error("Source folder does not exist.");
                throw new IOException();
            }
            else
            {
                allPathsInSource = getFiles(filenameInSource);
                Collections.sort(allPathsInSource);
            }
            Logger.info("Found [{}] files in [{}]", allPathsInSource.size(), filenameInSource);

            // retrieve all files in destination folder
            List<String> allPathsInDestination = null;
            if (!fileInDestination.exists())
            {
                allPathsInDestination = Collections.emptyList();
                Files.createDirectory(Paths.get(filenameInDestination));
            }
            else
            {
                allPathsInDestination = getFiles(filenameInDestination);
                Collections.sort(allPathsInDestination);
            }
            Logger.info("Found [{}] files in [{}]", allPathsInDestination.size(), filenameInDestination);

            // copy non-existent files in destination folder from source folder
            for (String pathInSource : allPathsInSource)
            {
                String pathInDestination = pathInSource.replace(filenameInSource, filenameInDestination);

                if (!allPathsInDestination.stream().anyMatch(f -> f.equals(pathInDestination)))
                {
                    Logger.debug("Copying [{}] to [{}]", pathInSource, pathInDestination);
                    new File(pathInDestination).getParentFile().mkdirs();
                    Files.copy(Paths.get(pathInSource), Paths.get(pathInDestination));
                }
            }
        }
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

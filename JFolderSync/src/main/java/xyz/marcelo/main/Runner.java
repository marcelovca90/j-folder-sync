package xyz.marcelo.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Runner
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length != 2)
            {
                System.out.println("Usage: java -jar FolderSync.jar \"SOURCE\" \"DESTINATION\"");
                System.exit(-1);
            }

            final String FROM = args[0];
            final String TO = args[1];

            // get all files from SOURCE
            List<String> allPathsInSource = null;
            if (Files.notExists(Paths.get(FROM)))
            {
                System.out.println("Source folder does not exist.");
                System.exit(-1);
            }
            else
            {
                allPathsInSource = getFiles(FROM);
                Collections.sort(allPathsInSource);
                System.out.println(String.format("Found [%d] files in [%s]", allPathsInSource.size(), FROM));
            }

            // get all files from DESTINATION
            List<String> allPathsInDestination = null;
            if (Files.notExists(Paths.get(TO)))
            {
                allPathsInDestination = Collections.emptyList();
                Files.createDirectory(Paths.get(TO));
            }
            else
            {
                allPathsInDestination = getFiles(TO);
                Collections.sort(allPathsInDestination);
                System.out.println(String.format("Found [%d] files in [%s]", allPathsInDestination.size(), TO));
            }

            // copy non-existent files in DESTINATION from SOURCE
            for (String pathInSource : allPathsInSource)
            {
                String pathInDestination = pathInSource.replace(FROM, TO);

                if (!allPathsInDestination.stream().anyMatch(f -> f.equals(pathInDestination)))
                {
                    System.out.println(String.format("Copying [%s] to [%s]", pathInSource, pathInDestination));
                    new File(pathInDestination).getParentFile().mkdirs();
                    Files.copy(Paths.get(pathInSource), Paths.get(pathInDestination));
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Unexpected exception: " + e);
            e.printStackTrace();
        }
    }

    private static List<String> getFiles(String path) throws IOException
    {
        return Files
                .find(Paths.get(path), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
                .map(filePath -> filePath.toAbsolutePath().toString())
                .collect(Collectors.toList());
    }
}

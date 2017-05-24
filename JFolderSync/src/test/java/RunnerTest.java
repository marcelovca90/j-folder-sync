import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xyz.marcelo.main.Runner;

public class RunnerTest
{
    private File sourceFile;
    private File destinationFile;

    @Before
    public void setUp() throws IOException
    {
        sourceFile = new File("./from/some-file.tmp");
        sourceFile.getParentFile().mkdirs();
        sourceFile.createNewFile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void main_withInvalidArgs_shouldThrowException() throws Exception
    {
        Runner.main(new String[0]);
    }

    @Test
    public void main_withValidArgs_shouldSynchronizeFiles() throws IOException
    {
        // given
        String sourceFolder = sourceFile.getParentFile().getAbsolutePath();
        String destinationFolder = "./to/";

        // when
        Runner.main(new String[] { sourceFolder, destinationFolder });

        // then
        destinationFile = new File("./to/some-file.tmp");
        assertTrue(destinationFile.exists());
    }

    @After
    public void tearDown()
    {
        if (sourceFile != null && sourceFile.exists())
            sourceFile.delete();
        if (destinationFile != null && destinationFile.exists())
            destinationFile.delete();
    }
}

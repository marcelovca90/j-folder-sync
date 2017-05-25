import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xyz.marcelo.main.Runner;

public class RunnerTest
{
    private static final String SLASH = File.separator;
    private static final String FILE = "file";
    private static final String FROM = "." + SLASH + "from" + SLASH;
    private static final String TO = "." + SLASH + "to" + SLASH;
    private static final String TO_1 = "." + SLASH + "to1" + SLASH;
    private static final String TO_2 = "." + SLASH + "to2" + SLASH;
    private static final String TO_N = "." + SLASH + "toN" + SLASH;

    private File sourceFile;
    private File[] destinationFiles;

    @Before
    public void setUp() throws IOException
    {
        sourceFile = new File(FROM + FILE);
        sourceFile.getParentFile().mkdirs();
        sourceFile.createNewFile();
    }

    @Test(expected = java.lang.IllegalAccessException.class)
    public void privateConstructor_shouldThrowException() throws Exception
    {
        // given
        Constructor<Runner> constructor = Runner.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // when
        constructor.setAccessible(true);
        constructor.newInstance();
        Runner.class.newInstance();

        // then throw exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void main_withInvalidArgs_shouldThrowException() throws Exception
    {
        // given
        String[] args = new String[0];

        // when
        Runner.main(args);

        // then throw exception
    }

    @Test(expected = IOException.class)
    public void main_withInvalidArgs_shouldThrowException2() throws IOException
    {
        // given
        String[] args = new String[] { FROM + "123", TO + "123" };

        // when
        Runner.main(args);

        // then throw exception
    }

    @Test
    public void main_withValidArgs_shouldSynchronizeFiles() throws IOException
    {
        // given
        String[] args = new String[] { FROM, TO_1, TO_2, TO_N };

        // when
        Runner.main(args);

        // then
        destinationFiles = new File[] { new File(TO_1 + FILE), new File(TO_2 + FILE), new File(TO_N + FILE) };
        Arrays.stream(destinationFiles).forEach(file -> assertTrue(file.exists()));
    }

    @After
    public void tearDown()
    {
        if (sourceFile != null && sourceFile.exists())
        {
            sourceFile.delete();
            sourceFile.getParentFile().delete();
        }

        if (destinationFiles != null)
        {
            Arrays.stream(destinationFiles).forEach(file ->
            {
                file.delete();
                file.getParentFile().delete();
            });
        }
    }
}

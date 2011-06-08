import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * Data structure to hold the words in the dictionary.
 */
@SuppressWarnings("serial")
public class Dictionary extends LinkedHashSet<String>
{
  /**
   * Loads the contents of the specified file into a new dictionary.
   *
   * @param file File to be loaded.
   * @return New dictionary.
   * @throws IOException If an I/O error occurs.
   */
  public static Dictionary load(final File file) throws IOException
  {
    final Dictionary dict = new Dictionary();

    final BufferedReader in = new BufferedReader(new FileReader(file));
    String dictw = null;
    while ((dictw = in.readLine()) != null)
    {
      dict.add(dictw.trim());
    }

    return dict;
  }
}

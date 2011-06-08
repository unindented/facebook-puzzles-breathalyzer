import java.io.File;
import java.util.Scanner;

public class breathalyzer
{
  private static final String DICTIONARY_PATH = "../twl06.txt";

  private static void usage()
  {
    System.err.println("Usage: java " + breathalyzer.class.getName() + " <file>");
    System.exit(1);
  }

  public static void main(final String[] args) throws Exception
  {
    if (args.length != 1 || args[0].trim().length() == 0)
    {
      usage();
    }

    // load the dictionary
    final Dictionary dict = Dictionary.load(new File(DICTIONARY_PATH));

    // read the sentence from the given file
    final Scanner in = new Scanner(new File(args[0]));
    final String line = in.nextLine().trim().toUpperCase();

    // and output the sum of the minimum distances between the words in the
    // sentence and the words in the dictionary
    System.out.println(Levenshtein.getInstance().distanceSentence(line, dict));
  }
}

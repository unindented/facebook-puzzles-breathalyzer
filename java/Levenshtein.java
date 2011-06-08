/**
 * Utility class to calculate Levenshtein distances.
 */
public final class Levenshtein
{
  private Levenshtein()
  {
  }

  /**
   * Calculates the Levenshtein distance between two words.
   *
   * @param s Some word.
   * @param t Some other word.
   * @return Levenshtein between them.
   */
  public static int distance(final String s, final String t)
  {
    if (s.equals(t))
    {
      return 0;
    }

    final int m = s.length();
    final int n = t.length();

    // d[i][j] will hold the levenshtein distance between the first i chars
    // of s and the first j chars of t
    final int[][] d = new int[m + 1][n + 1];

    for (int i = 0; i <= m; i++)
    {
      d[i][0] = i; // the distance of any first string to an empty second string
    }
    for (int j = 0; j <= n; j++)
    {
      d[0][j] = j; // the distance of any second string to an empty first string
    }

    for (int j = 1; j <= n; j++)
    {
      for (int i = 1; i <= m; i++)
      {
        if (s.charAt(i - 1) == t.charAt(j - 1))
        {
          d[i][j] = d[i - 1][j - 1]; // no operation required
        }
        else
        {
          d[i][j] = Math.min(Math.min(
            d[i - 1][j    ] + 1,  // a deletion
            d[i    ][j - 1] + 1), // an insertion
            d[i - 1][j - 1] + 1   // a substitution
          );
        }
      }
    }

    return d[m][n];
  }

  /**
   * Calculates the minimum Levenshtein distance between a word and all the
   * words in a dictionary.
   *
   * @param word Word.
   * @param dict Dictionary.
   * @return Minimum Levenshtein distance.
   */
  public static int distanceWord(final String word, final Dictionary dict)
  {
    // if the dictionary contains the word, there is no need to go any further
    if (dict.contains(word))
    {
      return 0;
    }

    int mindist = word.length();

    for (final String dword : dict)
    {
      final int dist = distance(word, dword);
      if (dist < mindist)
      {
        mindist = dist;

        // if the minimum distance == 0, there is no way to improve it
        if (mindist == 0)
        {
          break;
        }
      }
    }

    return mindist;
  }

  /**
   * Calculates the sum of the minimum Levenshtein distances between the words
   * in a sentence and the words in a dictionary.
   *
   * @param sentence Sentence.
   * @param dict Dictionary.
   * @return Sum of the minimum Levenshtein distances.
   */
  public static int distanceSentence(final String sentence, final Dictionary dict)
  {
    int totaldist = 0;

    for (final String word : sentence.split("\\s+"))
    {
      totaldist += distanceWord(word, dict);
    }

    return totaldist;
  }
}

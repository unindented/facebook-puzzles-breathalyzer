/**
 * Utility class to calculate Levenshtein distances.
 */
public final class Levenshtein
{
  /**
   * Calculates the Levenshtein distance between two words.
   *
   * @param s Some word.
   * @param t Some other word.
   * @return Levenshtein between them.
   */
  public static int distance(final String s, final String t)
  {
    final int m = s.length();
    final int n = t.length();

    if (n == 0)
    {
      return m;
    }
    if (m == 0)
    {
      return n;
    }

    final int[] d = new int[n + 1];
    for (int k = 0; k < d.length; k++)
    {
      d[k] = k;
    }
    int x = -1;

    for (int i = 0; i < m; i++)
    {
      int e = i + 1;
      for (int j = 0; j < n; j++)
      {
        final int c = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
        x = Math.min(Math.min(
          d[j + 1] + 1, // an insertion
          e + 1),       // a deletion
          d[j] + c      // a substitution
        );
        d[j] = e;
        e = x;
      }
      d[n] = x;
    }

    return x;
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

    // start with words of the same length, and move outwards
    final int length = word.length();
    int offset = -1;

    int mindist = Integer.MAX_VALUE;

    do
    {
      for (final String dword : dict.getWordsOfLength(length, ++offset))
      {
        final int dist = distance(word, dword);
        if (dist < mindist)
        {
          mindist = dist;

          // if the minimum distance == 0, there is no way to improve it
          if (mindist <= offset)
          {
            break;
          }
        }
      }

      // if the minimum distance <= 1, we are not going to improve it by looking
      // at the words in the other buckets
      if (mindist <= offset + 1)
      {
        break;
      }
    }
    while (true);

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

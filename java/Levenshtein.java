/**
 * Utility class to calculate Levenshtein distances.
 */
public final class Levenshtein
{
  /** Default rows and columns for the distance matrix. */
  private static final int DEFAULT_SIZE = 16;

  /** Rows in the distance matrix. */
  private int rows;
  /** Columns in the distance matrix. */
  private int cols;
  /** Reusable distance matrix. */
  private int[][] d;

  /**
   * Constructs an instance with a distance matrix of the default size.
   */
  private Levenshtein()
  {
    this(DEFAULT_SIZE, DEFAULT_SIZE);
  }

  /**
   * Constructs an instance with a distance matrix of the specified rows and
   * columns.
   *
   * @param rows Rows.
   * @param cols Columns
   */
  private Levenshtein(final int rows, final int cols)
  {
    this.d = getDistanceMatrix(rows, cols);
  }

  /**
   * Returns a matrix with at least the specified rows and columns.
   *
   * <p>
   * It tries to reuse the existing matrix, to minimize allocations.
   * </p>
   *
   * @param rows Minimum rows.
   * @param cols Minimum columns.
   * @return Matrix with at least the specified rows and columns.
   */
  private int[][] getDistanceMatrix(final int rows, final int cols)
  {
    if (this.rows < rows || this.cols < cols)
    {
      this.rows = Math.max(this.rows, rows);
      this.cols = Math.max(this.cols, cols);
      this.d = new int[this.rows][this.cols];
    }
    return d;
  }

  /**
   * Calculates the Levenshtein distance between two words.
   *
   * @param s Some word.
   * @param t Some other word.
   * @return Levenshtein between them.
   */
  public int distance(final String s, final String t)
  {
    if (s.equals(t))
    {
      return 0;
    }

    final int m = s.length();
    final int n = t.length();

    // d[i][j] will hold the levenshtein distance between the first i chars
    // of s and the first j chars of t
    final int[][] d = getDistanceMatrix(m + 1, n + 1);

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
  public int distanceWord(final String word, final Dictionary dict)
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
  public int distanceSentence(final String sentence, final Dictionary dict)
  {
    int totaldist = 0;

    for (final String word : sentence.split("\\s+"))
    {
      totaldist += distanceWord(word, dict);
    }

    return totaldist;
  }

  /* ------------------------------------------------------------------------ */

  /** Singleton instance. */
  private static final Levenshtein instance = new Levenshtein();

  /**
   * Returns the singleton instance.
   *
   * @return Singleton instance.
   */
  public static Levenshtein getInstance()
  {
    return instance;
  }
}

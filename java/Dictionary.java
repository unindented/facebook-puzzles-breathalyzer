import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Data structure to hold the words in the dictionary.
 *
 * <p>
 * Internally it stores them in a map, grouped by word length:
 * </p>
 *
 * <pre>
 * +---+
 * | 2 | -> [AA,AB,...]
 * | 3 | -> [AAH,AAL,...]
 * | 4 | -> [AAHS,AALS,...]
 * | 5 | -> [AAHED,AALII,...]
 * +---+
 * </pre>
 */
public final class Dictionary
{
  /** Contents of the dictionary. */
  private final Map<Integer, Collection<String>> contents;

  /**
   * Constructor.
   */
  public Dictionary()
  {
    this.contents = new HashMap<Integer, Collection<String>>();
  }

  /**
   * Returns the minimum word length in the dictionary.
   *
   * @return Minimum word length.
   */
  public int getMinWordLength()
  {
    return Collections.min(contents.keySet());
  }

  /**
   * Returns the maximum word length in the dictionary.
   *
   * @return Maximum word length.
   */
  public int getMaxWordLength()
  {
    return Collections.max(contents.keySet());
  }

  /**
   * Returns the words of the specified length.
   *
   * <p>
   * For <code>length = 5</code>:
   * </p>
   *
   * <pre>
   * +---------------------------+
   * | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
   * +-------------^-------------+
   * </pre>
   *
   * @param length Length.
   * @return Words of that length.
   */
  public Collection<String> getWordsOfLength(final int length)
  {
    return Collections.unmodifiableCollection(getBucket(length));
  }

  /**
   * Returns the words of the specified length +- offset.
   *
   * <p>
   * For <code>length = 5</code> and <code>offset = 1</code>:
   * </p>
   *
   * <pre>
   * +---------------------------+
   * | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
   * +---------^-------^---------+
   * </pre>
   *
   * @param length Length.
   * @param offset Offset.
   * @return Words of length +- offset.
   */
  public Collection<String> getWordsOfLength(final int length, final int offset)
  {
    if (offset == 0)
    {
      return getWordsOfLength(length);
    }

    final Collection<String> result = new LinkedHashSet<String>();
    if (length - offset > 0)
    {
      result.addAll(getBucket(length - offset));
    }
    if (length + offset > 0)
    {
      result.addAll(getBucket(length + offset));
    }
    return Collections.unmodifiableCollection(result);
  }

  /**
   * Returns <code>true</code> if this dictionary contains the specified word.
   *
   * @param word Word whose presence in this dictionary is to be tested.
   * @return <code>true</code> if this dictionary contains the specified word.
   */
  public boolean contains(final String word)
  {
    return getBucket(word).contains(word);
  }

  /**
   * Ensures that this dictionary contains the specified word. Returns
   * <code>true</code> if this dictionary changed as a result of the call.
   *
   * @param word Word whose presence in this dictionary is to be ensured.
   * @return <code>true</code> if this dictionary changed as a result of the
   *         call.
   */
  public boolean add(final String word)
  {
    return getOrCreateBucket(word).add(word);
  }

  /**
   * Removes the specified word from this dictionary, if it is present. Returns
   * <code>true</code> if this dictionary changed as a result of the call.
   *
   * @param word Word to be removed from this dictionary, if present.
   * @return <code>true</code> if this dictionary changed as a result of the
   *         call.
   */
  public boolean remove(final String word)
  {
    return getBucket(word).remove(word);
  }

  /**
   * Returns the bucket of words of the same length as the one specified. If
   * there is no bucket for that length, it returns an empty collection.
   *
   * @param word Word.
   * @return Bucket of words of the same length.
   */
  private Collection<String> getBucket(final String word)
  {
    return getBucket((word == null) ? 0 : word.trim().length());
  }

  /**
   * Returns the bucket of words of the specified length. If there is no bucket
   * for that length, it returns an empty collection.
   *
   * @param length Length
   * @return Bucket of words of that length.
   */
  private Collection<String> getBucket(final int length)
  {
    if (length <= 0)
    {
      throw new IllegalArgumentException();
    }

    Collection<String> bucket = contents.get(length);
    // if there is no bucket for that length, return an empty set
    if (bucket == null)
    {
      bucket = Collections.emptySet();
    }
    return bucket;
  }

  /**
   * Returns the bucket of words of the same length as the one specified. If
   * there is no bucket for that length, it creates it.
   *
   * @param word Word.
   * @return Bucket of words of the same length.
   */
  private Collection<String> getOrCreateBucket(final String word)
  {
    return getOrCreateBucket((word == null) ? 0 : word.trim().length());
  }

  /**
   * Returns the bucket of words of the specified length. If there is no bucket
   * for that length, it creates it.
   *
   * @param length Length
   * @return Bucket of words of that length.
   */
  private Collection<String> getOrCreateBucket(final int length)
  {
    if (length <= 0)
    {
      throw new IllegalArgumentException();
    }

    Collection<String> bucket = contents.get(length);
    if (bucket == null)
    {
      bucket = new LinkedHashSet<String>();
      contents.put(length, bucket);
    }

    return bucket;
  }

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

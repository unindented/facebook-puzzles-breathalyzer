require 'singleton'

##
# Utility class to calculate Levenshtein distances.
#
class Levenshtein
  include Singleton

  ##
  # Default rows and columns for the distance matrix.
  #
  DEFAULT_SIZE = 16

  ##
  # Rows in the distance matrix.
  #
  attr_reader :rows

  ##
  # Columns in the distance matrix.
  #
  attr_reader :cols

  ##
  # Reusable distance matrix.
  #
  attr_reader :matrix

  ##
  # Constructs an instance with a distance matrix of the specified rows and
  # columns.
  #
  def initialize(rows = DEFAULT_SIZE, cols = DEFAULT_SIZE)
    @rows = 0
    @cols = 0
    @matrix = distance_matrix(rows, cols)
  end

  ##
  # Returns a matrix with at least the specified rows and columns.
  #
  # It tries to reuse the existing matrix, to minimize allocations.
  #
  def distance_matrix(rows, cols)
    if @rows < rows or @cols < cols
      @rows = [@rows, rows].max
      @cols = [@cols, cols].max
      @matrix = Array.new(@rows) { Array.new(@cols, 0) }
    end
    @matrix
  end

  ##
  # Calculates the Levenshtein distance between two words.
  #
  def distance(s, t)
    return 0 if s == t

    m = s.length
    n = t.length
  
    # d[i][j] will hold the levenshtein distance between the first i chars
    # of s and the first j chars of t
    d = distance_matrix(m + 1, n + 1)
  
    (0 .. m).each do |i|
      d[i][0] = i # the distance of any first string to an empty second string
    end
    (0 .. n).each do |j|
      d[0][j] = j # the distance of any second string to an empty first string
    end
  
    (1 .. n).each do |j|
      (1 .. m).each do |i|
        if s[i - 1] == t[j - 1]
          d[i][j] = d[i - 1][j - 1] # no operation required
        else
          d[i][j] = [
                      d[i - 1][j    ] + 1, # a deletion
                      d[i    ][j - 1] + 1, # an insertion
                      d[i - 1][j - 1] + 1  # a substitution
                    ].min
        end
      end
    end
  
    return d[m][n]
  end

  ##
  # Calculates the minimum Levenshtein distance between a word and all the
  # words in a dictionary.
  #
  def distance_word(word, dict)
    # if the dictionary contains the word, there is no need to go any further
    return 0 if dict.include?(word)

    # start with words of the same length, and move outwards
    length = word.length
    offset = -1

    mindist = 2**31 - 1

    loop do
      offset += 1

      dict.words_of_length_offset(length, offset).each do |dword|
        dist = distance(word, dword)
        if dist < mindist
          mindist = dist

          # if the minimum distance == 0, there is no way to improve it
          break if mindist <= offset
        end
      end

      # if the minimum distance <= 1, we are not going to improve it by looking
      # at the words in the other buckets
      break if mindist <= offset + 1
    end

    return mindist
  end

  ##
  # Calculates the sum of the minimum Levenshtein distances between the words
  # in a sentence and the words in a dictionary.
  #
  def distance_sentence(sentence, dict)
    sentence.split(/\s+/).inject(0) do |totaldist, word|
      totaldist + distance_word(word, dict)
    end
  end

end

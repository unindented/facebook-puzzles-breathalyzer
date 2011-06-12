# Utility class to calculate Levenshtein distances.
#
class Levenshtein

  # Calculates the Levenshtein distance between two words.
  #
  def self.distance(s, t)
    return 0 if s == t

    m = s.length
    n = t.length
  
    # d[i][j] will hold the levenshtein distance between the first i chars
    # of s and the first j chars of t
    d = Array.new(m + 1) { Array.new(n + 1, 0) }
  
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

  # Calculates the minimum Levenshtein distance between a word and all the
  # words in a dictionary.
  #
  def self.distance_word(word, dict)
    # if the dictionary contains the word, there is no need to go any further
    return 0 if dict.include?(word)

    mindist = word.length

    dict.each do |dword|
      dist = distance(word, dword)
      if dist < mindist
        mindist = dist

        # if the minimum distance == 0, there is no way to improve it
        break if mindist == 0
      end
    end

    return mindist
  end

  # Calculates the sum of the minimum Levenshtein distances between the words
  # in a sentence and the words in a dictionary.
  #
  def self.distance_sentence(sentence, dict)
    sentence.split(/\s+/).inject(0) do |totaldist, word|
      totaldist + distance_word(word, dict)
    end
  end

end

##
# Utility class to calculate Levenshtein distances.
#
class Levenshtein

  ##
  # Calculates the Levenshtein distance between two words.
  #
  def self.distance(s, t)
    unpack_rule = ($KCODE =~ /^U/i) ? 'U*' : 'C*'
    s = s.unpack(unpack_rule)
    t = t.unpack(unpack_rule)

    m = s.length
    n = t.length

    return m if n == 0
    return n if m == 0
  
    d = (0 .. n).to_a
    x = -1

    (0 ... m).each do |i|
      e = i + 1
      (0 ... n).each do |j|
        c = (s[i] == t[j]) ? 0 : 1
        x = [
          d[j + 1] + 1, # an insertion
          e + 1,        # a deletion
          d[j] + c      # a substitution
        ].min
        d[j] = e
        e = x
      end
      d[n] = x
    end

    return x
  end

  ##
  # Calculates the minimum Levenshtein distance between a word and all the
  # words in a dictionary.
  #
  def self.distance_word(word, dict)
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
  def self.distance_sentence(sentence, dict)
    sentence.split(/\s+/).inject(0) do |totaldist, word|
      totaldist + distance_word(word, dict)
    end
  end

end

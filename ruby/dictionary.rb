require 'set'

##
# Data structure to hold the words in the dictionary.
#
# Internally it stores them in a map, grouped by word length:
#
#   +---+
#   | 2 | -> [AA,AB,...]
#   | 3 | -> [AAH,AAL,...]
#   | 4 | -> [AAHS,AALS,...]
#   | 5 | -> [AAHED,AALII,...]
#   +---+
#
class Dictionary
  ##
  # Contents of the dictionary.
  #
  attr_reader :contents

  ##
  # Constructor.
  #
  def initialize
    @contents = {}
  end

  ##
  # Returns the minimum word length in the dictionary.
  #
  def min_word_length
    @contents.keys.min
  end

  ##
  # Returns the maximum word length in the dictionary.
  #
  def max_word_length
    @contents.keys.max
  end

  ##
  # Returns the words of the specified length.
  #
  # For <tt>length = 5</tt>:
  #
  #   +---------------------------+
  #   | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
  #   +-------------^-------------+
  #
  def words_of_length(length)
    bucket_for_length(length)
  end

  ##
  # Returns the words of the specified length +- offset.
  #
  # For <tt>length = 5</tt> and <tt>offset = 1</tt>:
  #
  #   +---------------------------+
  #   | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
  #   +---------^-------^---------+
  #
  def words_of_length_offset(length, offset)
    return words_of_length(length) if offset == 0

    result = Set.new
    result.merge(bucket_for_length(length - offset)) if (length - offset > 0)
    result.merge(bucket_for_length(length + offset)) if (length + offset > 0)
  end

  ##
  # Returns <tt>true</tt> if this dictionary contains the specified word.
  #
  def include?(word)
    bucket_for_word(word).include?(word)
  end

  ##
  # Ensures that this dictionary contains the specified word. Returns the
  # dictionary itself, so several calls may be chained together.
  #
  def add(word)
    new_bucket_for_word(word) << word
    self
  end

  alias :<< :add

  ##
  # Removes the specified word from this dictionary, if it is present. Returns
  # the deleted word, or <tt>nil</tt> if it was not found.
  #
  def delete(word)
    bucket_for_word(word).delete(word)
  end

  ##
  # Returns the bucket of words of the same length as the one specified. If
  # there is no bucket for that length, it returns an empty bucket.
  #
  def bucket_for_word(word)
    bucket_for_length(word == nil ? 0 : word.strip.length)
  end

  ##
  # Returns the bucket of words of the specified length. If there is no bucket
  # for that length, it returns an empty bucket.
  #
  def bucket_for_length(length)
    raise if length <= 0

    @contents[length] || Set.new
  end

  ##
  # Returns the bucket of words of the same length as the one specified. If
  # there is no bucket for that length, it creates it.
  #
  def new_bucket_for_word(word)
    new_bucket_for_length(word == nil ? 0 : word.strip.length);
  end

  ##
  # Returns the bucket of words of the specified length. If there is no bucket
  # for that length, it creates it.
  #
  def new_bucket_for_length(length)
    raise if length <= 0

    @contents[length] || (@contents[length] = Set.new)
  end

  private :bucket_for_word
  private :bucket_for_length
  private :new_bucket_for_word
  private :new_bucket_for_length

  ##
  # Loads the contents of the specified file into a new dictionary.
  #
  def self.load(file)
    dict = Dictionary.new

    File.open(file, 'r') do |file|
      while (dictw = file.gets)
        dictw.strip!
        dict << dictw
      end
    end

    dict
  end

end

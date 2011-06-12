# Data structure to hold the words in the dictionary.
#
class Dictionary

  # Loads the contents of the specified file into a new dictionary.
  #
  def self.load(file)
    dict = []

    File.open(file, 'r') do |file|
      while (dictw = file.gets)
        dictw.strip!
        dict << dictw
      end
    end

    return dict
  end

end

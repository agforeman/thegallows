package csci3320.thegallows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Random;


/**
 * The WordBank class defines the "WordBank" object.
 * "WordBank" objects contain the getGameWord() method, which returns a word for Hangman gameplay.
 *
 * @author  Justin Shapiro
 * @version 1.0
 * @since   2016-04-10
 *
 */
public class WordBank {

    /**
     * Stores a buffer that provides line-per-line access to a library file.
     */
    public BufferedReader game_word_buff;

    /**
     * Stores the word used in Hangman gameplay in a String object.
     */
    public String game_word = "";

    /**
     * Stores the library type to import in a String object.
     * This object is a copy of the argument passed to the WordBank constructor.
     */
    public String _difficulty;

    /**
     * This is the main constructor for WordBank.
     * @param difficulty Only "EASY", "MEDIUM", "HARD" or "FREEPLAY" should be passed in.
     */
    public WordBank(String difficulty) { _difficulty = difficulty; }

    /**
     * Method that reads from game_word_buff and stores a random line from the corresponding file
     * in a String object.
     * @return The word used for Hangman gameplay
     */
    public String getGameWord() {
        // Link the BufferReader object "game_word_buff" to the selected library file
        initFileBuffer(getFile(_difficulty));

        // Create a random number generator for
        // random line selection from the selected library file
        Random location_randomizer = new Random();

        // Produce a random int in the range [0, getFileLength(getFile(_difficulty))]
        int game_word_location = location_randomizer.nextInt(
                                 getFileLength(getFile(_difficulty)));

        int counter = 0; // loop counter used to keep track of line number relative to random int
        try {
            while (counter != game_word_location + 1) {
                // Line number counter in the BufferReader object
                // increments with each loop iteration
                game_word_buff.readLine();

                // When the line number counter in BufferedReader object equals the loop counter,
                // store the current line in the primary gameplay String object and exit loop
                if (counter == game_word_location) {
                    game_word = game_word_buff.readLine();
                    break;
                }

                counter++;
            }

            // The buffer should be closed when it is done being used
            game_word_buff.close();

        } catch (IOException e) { e.printStackTrace(); }

        return game_word;
    }

    /**
     * Method that links the selected library file to a BufferedReader with an InputStreamReader
     * that is initialized with a FileInputStream of the file
     * @param file_path The location of the file to link to the buffer, stored in a String object
     */
    public void initFileBuffer(String file_path) {
        try {
            game_word_buff = new BufferedReader(new InputStreamReader(
                             new FileInputStream(file_path)));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    /**
     * Method that returns the path of the library file based on the constructor argument
     * @param file_type Indicator of the library file stored in a String object
     * @return The path of the library file to select a gameplay word from
     */
    public String getFile(String file_type) {
        switch (file_type) {
            case "EASY":     return "assets/easy.txt";
            case "MEDIUM":   return "assets/medium.txt";
            case "HARD":     return "assets/hard.txt";
            case "FREEPLAY": return "assets/freeplay.txt";

            // The below line shouldn't ever be accessed, but a default case is required
            default:         return "no_file";
        }
    }

    /**
     * Method that uses LineNumberReader to get the number of lines in a library file.
     * The number of lines in a library file indicates the number of words stored in it.
     * This number is required to produce the max range of random values to select from for
     * use in getGameWord().
     * @param file_path The location of the file to retrieve the number of lines from,
     *                  stored in a St  ring object
     * @return An int containing 1 less than the number of lines in a file
     */
    public int getFileLength(String file_path) {
        int file_length = 0;

        try {
            LineNumberReader ln_num = new LineNumberReader(new FileReader(file_path));

            try {
                // see http://stackoverflow.com/a/5342096/4736556
                ln_num.skip(Long.MAX_VALUE);
            } catch (IOException e) { e.printStackTrace(); }

            file_length = ln_num.getLineNumber();
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        return file_length;
    }
}
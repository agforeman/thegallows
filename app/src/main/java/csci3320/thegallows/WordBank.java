package csci3320.thegallows;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
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

    Context _appContext;

    /**
     * Stores a buffer that provides line-per-line access to a library file.
     */
    public BufferedReader game_word_buff;

    /**
     * Stores the word used in Hangman gameplay in a String object.
     */
    public String game_word = "";

    /**
     * Stores the hint associated with the game_word in a String object
     */
    public String game_hint = "";

    /**
     * Stores the library type to import in a String object.
     * This object is a copy of the argument passed to the WordBank constructor.
     */
    public int _level;

    /**
     * This is the main constructor for WordBank.
     * @param appContext Needed to access the files stored in the assets folder outside of an activity
     * @param level The current level of the game
     */
    public WordBank(Context appContext, int level) {
        _appContext = appContext;
        _level = level;
    }

    /**
     * Method that reads from game_word_buff and stores a random line from the corresponding file
     * in a String object.
     * @return The word used for Hangman gameplay
     */
    public String getGameWord() {
        // Link the BufferReader object "game_word_buff" to the selected library file
        initFileBuffer(getFile());

        // Create a random number generator for
        // random line selection from the selected library file
        Random location_randomizer = new Random();

        // Produce a random int in the range [0, getFileLength(getFile(_difficulty))]
        int game_word_location = location_randomizer.nextInt(
                                 getFileLength(getFile()));

        int counter = 0; // loop counter used to keep track of line number relative to random int
        try {
            while (counter != game_word_location + 1) {
                // Line number counter in the BufferReader object
                // increments with each loop iteration
                game_word_buff.mark(getFileLength(getFile()));
                game_word_buff.readLine();

                // When the line number counter in BufferedReader object equals the loop counter,
                // store the current line in the primary gameplay String object and exit loop
                if (counter == game_word_location) {
                    game_word_buff.reset();
                    game_word = game_word_buff.readLine();
                    game_hint = game_word_buff.readLine();
                    break;
                }

                // Increment the line position in the buffer again to skip hint
                game_word_buff.readLine();

                counter++;
            }

            // The buffer should be closed when it is done being used
            game_word_buff.close();

        } catch (IOException e) { e.printStackTrace(); }

        return game_word;
    }

    public String getGameHint() { return game_hint; }

    public String getLevelCategoryString(int current_level) {
        switch(current_level) {
            case 1: return "Animals";
            case 2: return "Days of the Week";
            case 3: return "Numbers";
            case 4: return "Months of the Year";
            case 5: return "U.S. States";
            case 6: return "U.S. State Capitals";
            case 7: return "Photonic Alphabet";
            case 8: return "U.S. Presidents";
            case 9: return "Celebrities";/*
            case 9: return "[level 9 category]";
            case 10: return "[level 10 category]";
            case 11: return "[level 11 category]";
            case 12: return "[level 12 category]";
            case 13: return "[level 13 category]";
            case 14: return "[level 14 category]";
            case 15: return "[level 15 category]";
            case 16: return "[level 16 category]";
            case 17: return "[level 17 category]";
            case 18: return "[level 18 category]";
            case 19: return "[level 19 category]";
            case 20: return "[level 20 category]";
            case 21: return "[level 21 category]";
            case 22: return "[level 22 category]";
            case 23: return "[level 23 category]";
            case 24: return "[level 24 category]";
            case 25: return "[level 25 category]";
            case 26: return "[level 26 category]";
            case 27: return "[level 27 category]";
            case 28: return "[level 28 category]";
            case 29: return "[level 29 category]";
            case 30: return "[level 30 category]";
*/
            // The below line shouldn't ever be accessed, but a default case is required
            default:         return "internal_error";
        }
    }

    public String getLevelString() { return "Level " + Integer.toString(_level); }

    /**
     * Method that links the selected library file to a BufferedReader with an InputStreamReader
     * that is initialized with a FileInputStream of the file
     * @param file_path The location of the file to link to the buffer, stored in a String object
     */
    public void initFileBuffer(String file_path) {
        try {
            game_word_buff = new BufferedReader(new InputStreamReader(
                             _appContext.getAssets().open(file_path)));
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Method that returns the path of the library file based on the constructor argument
     * @return The path of the library file to select a gameplay word from
     */
    public String getFile() {
        if (_level == 0) {
            Random level_randomizer = new Random();

            _level = level_randomizer.nextInt(9) + 1;
        }

        switch (_level) {
            case 1: return "libraries/animals.txt";
            case 2: return "libraries/days.txt";
            case 3: return "libraries/numbers.txt";
            case 4: return "libraries/months.txt";
            case 5: return "libraries/us_states.txt";
            case 6: return "libraries/us_state_capitals.txt";
            case 7: return "libraries/nato_photonetic_alphabet.txt";
            case 8: return "libraries/us_presidents.txt";
            case 9: return "libraries/celebrities.txt";/*
            case 9: return "level9.txt";
            case 10: return "level10.txt";
            case 11: return "level11.txt";
            case 12: return "level12.txt";
            case 13: return "level13.txt";
            case 14: return "level14.txt";
            case 15: return "level15.txt";
            case 16: return "level16.txt";
            case 17: return "level17.txt";
            case 18: return "level18.txt";
            case 19: return "level19.txt";
            case 20: return "level20.txt";
            case 21: return "level21.txt";
            case 22: return "level22.txt";
            case 23: return "level23.txt";
            case 24: return "level24.txt";
            case 25: return "level25.txt";
            case 26: return "level26.txt";
            case 27: return "level27.txt";
            case 28: return "level28.txt";
            case 29: return "level29.txt";
            case 30: return "level30.txt";
*/
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
            BufferedReader temp_buff = new BufferedReader(new InputStreamReader(
                                       _appContext.getAssets().open(file_path)));
            while(temp_buff.readLine() != null)
                file_length++;

            temp_buff.close();
        } catch (IOException e) { e.printStackTrace(); }

        return file_length / 2;
    }

    public int getLevel() { return _level; }
}
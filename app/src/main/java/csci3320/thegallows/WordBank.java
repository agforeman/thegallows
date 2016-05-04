package csci3320.thegallows;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;


/**
 * The WordBank class defines the "WordBank" object. "WordBank" objects contain the getGameWord() method,
 * which returns a specific word for Hangman gameplay based on a level number received or a random
 * word from the range of implemented libraries if the input is 0.
 *
 * Recursive Gameplay sequences constantly need access to the WordBank class in order to generate a
 * WORD for the current stage of the game within that sequence. When a recursive sequence requests
 * a word from WordBank by calling its getGameWord() method, the WordBank determines, based on the input,
 * a file to load into a BufferedReader. The nextLine method of the resulting BufferedReader object
 * iterates through the file. A random number generator is used to specify to which odd numbered line
 * WordBank should iterate to. The resulting odd numbered line of the BufferedReader object is saved
 * into a String and this String is returned by getGameWord as the WORD used in Hangman gameplay.
 *
 * A recursive sequence from the Gameplay activity will most likely request a HINT immediately following
 * the request for a WORD. Since the WordBank object created by the Gameplay sequence is fully instantiated
 * during the fulfillment of the request for a WORD, a HINT can be retrieved that corresponds to the WORD
 * just returned.
 *
 * It is very important to programmatically set conditions in the Gameplay class that does not allow
 * getGameHint to be called before getGameWord. This would most likely result in an application crash
 * since the lack of instantiation in getGameHint allows a garbage value to searched for in a library
 * file. Since users do not use this class on the surface, it is not important to allow a HINT to be
 * returned before a WORD, nor does it make sense given the current implementation. However, it is
 * important for the programmers to note this usage requirement.
 *
 * @author  Justin Shapiro
 * @version 3.0
 * @since   2016-04-10
 */
public class WordBank {
    /**
     * The application context needs to be defined explicitly since WordBank does not extend Activity.
     */
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
     * Stores the hint associated with the game_word in a String object.
     */
    public String game_hint = "";
    /**
     * Stores the library type to import in a String object.
     * This object is a copy of the argument passed to the WordBank constructor.
     */
    public int _level;

    /**
     * This is the main constructor for WordBank.
     * @param appContext Needed to access the files stored in the assets folder outside of an activity.
     * @param level The current level of the game.
     */
    public WordBank(Context appContext, int level) {
        _appContext = appContext;
        _level = level;
    }
    /**
     * Method that reads from game_word_buff and stores a random line from the corresponding file.
     * in a String object.
     * @return The word used for Hangman gameplay.
     */
    public String getGameWord() {
        // link the BufferReader object "game_word_buff" to the selected library file
        initFileBuffer(getFile());

        // create a random number generator for
        // random line selection from the selected library file
        Random location_randomizer = new Random();

        // produce a random int in the range [0, getFileLength(getFile(_difficulty))]
        int game_word_location = location_randomizer.nextInt(
                                 getNumWords(getFile()));

        int counter = 0; // loop counter used to keep track of line number relative to random int
        try {
            while (counter != game_word_location + 1) {
                // line number counter in the BufferReader object
                // increments with each loop iteration
                game_word_buff.mark(getNumWords(getFile()));
                game_word_buff.readLine();

                // when the line number counter in BufferedReader object equals the loop counter,
                // store the current line in the primary gameplay String object and exit loop
                if (counter == game_word_location) {
                    game_word_buff.reset();
                    game_word = game_word_buff.readLine();
                    game_hint = game_word_buff.readLine();
                    break;
                }

                // increment the line position in the buffer again to skip hint
                game_word_buff.readLine();

                counter++;
            }

            // the buffer should be closed when it is done being used
            game_word_buff.close();

        } catch (IOException e) { e.printStackTrace(); }

        return game_word;
    }
    /**
     * Method that returns the String that contains the hint for the currently selected word.
     * In order for this method to return the correct value, it MUST be called AFTER getGameWord().
     * @return The hint used for Hangman gameplay.
     */
    public String getGameHint() { return game_hint; }
    /**
     * Method that returns the topic of the current_level for use in Gameplay.
     * @param current_level The current level that the Gameplay activity is currently on.
     * @return A String containing the category associated with a level.
     */
    public String getLevelCategoryString(int current_level) {
        switch(current_level) {
            case 1: return "Days of the Week";
            case 2: return "Months of the Year";
            case 3: return "Holidays";
            case 4: return "Animals";
            case 5: return "Numbers";
            case 6: return "U.S. States";
            case 7: return "U.S. State Capitals";
            case 8: return "NATO Photonic Alphabet";
            case 9: return "U.S. Presidents";
            case 10: return "Computer Science Terms";
            case 11: return "Celebrities";
            case 12: return "Countries";
            case 13: return "Car Companies";
            case 14: return "World's Richest People";
            case 15: return "World's Tallest Buildings";
            case 16: return "Breeds of Dog (Official Names)";
            case 17: return "World's Tallest Mountains";

            // The below line shouldn't ever be accessed, but a default case is required
            default:         return "internal_error";
        }
    }
    /**
     * Method returns a String in the form of "Level #" for use in Gameplay.
     * @return A formatted string in the form of "Level #".
     */
    public String getLevelString() { return "Level " + Integer.toString(_level); }
    /**
     * Method that links the selected library file to a BufferedReader with an InputStreamReader.
     * that is initialized with a FileInputStream of the file.
     * @param file_path The location of the file to link to the buffer, stored in a String object.
     */
    public void initFileBuffer(String file_path) {
        try {
            // fill the buffer with the return value of the InputStreamReader
            game_word_buff = new BufferedReader(new InputStreamReader(
                             _appContext.getAssets().open(file_path)));
        } catch (IOException e) { e.printStackTrace(); }
    }
    /**
     * Method that returns the path of the library file based on the constructor argument.
     * @return The path of the library file to select a gameplay word from.
     */
    public String getFile() {
        // if _level == 0, this class is being used to return a WORD for a Freeplay sequence and
        // therefore a _level number will be returned randomly.
        if (_level == 0) {
            Random level_randomizer = new Random();

            _level = level_randomizer.nextInt(17) + 1;
        }

        switch (_level) {
            case 1: return "libraries/days.txt";
            case 2: return "libraries/months.txt";
            case 3: return "libraries/holidays.txt";
            case 4: return "libraries/animals.txt";
            case 5: return "libraries/numbers.txt";
            case 6: return "libraries/us_states.txt";
            case 7: return "libraries/us_state_capitals.txt";
            case 8: return "libraries/nato_photonetic_alphabet.txt";
            case 9: return "libraries/us_presidents.txt";
            case 10: return "libraries/computer_science_terms.txt";
            case 11: return "libraries/celebrities.txt";
            case 12: return "libraries/countries.txt";
            case 13: return "libraries/car_companies.txt";
            case 14: return "libraries/richest_people.txt";
            case 15: return "libraries/tallest_buildings.txt";
            case 16: return "libraries/full_dogbreeds.txt";
            case 17: return "libraries/mountains.txt";

            // The below line shouldn't ever be accessed, but a default case is required
            default:         return "no_file";
        }
    }
    /**
     * Method that counts the number of words in a library file by dividing the number of lines by 2
     * The number of lines divided by 2 in a library file indicates the number of words stored in it.
     * This number is required to produce the max range of random values to select from for
     * use in getGameWord().
     * @param file_path The location of the file to retrieve the number of lines from,
     *                  stored in a St  ring object.
     * @return An int containing 1 less than the number of lines in a file.
     */
    public int getNumWords(String file_path) {
        int file_length = 0;

        try {
            // create temporary buffer for the file we are counting the lines of
            BufferedReader temp_buff = new BufferedReader(new InputStreamReader(
                                       _appContext.getAssets().open(file_path)));

            // with each iteration of the loop, the line position in the buffer will be increment until null,
            // and with each iteration, the file_length will be incremented
            while(temp_buff.readLine() != null)
                file_length++;

            temp_buff.close();
        } catch (IOException e) { e.printStackTrace(); }

        // file_length / 2 = the number of words in file
        return file_length / 2;
    }
    /**
     * Method that returns the current level that WordBank used to generate the word
     * @return The current level.
     */
    public int getLevel() { return _level; }
}
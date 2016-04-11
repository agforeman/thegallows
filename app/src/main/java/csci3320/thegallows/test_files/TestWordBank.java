/*
 *                     Created by Justin Shapiro on 04/10/2016 
 *                  -------------------------------------------------
 *                  
 * This file is the tester file for the WordBank class
 * Using this file, all WordBank functionality was proven to return correct values
 * The Android program will NOT reference this file, so do not reference it in Gameplay
 *
 * JAVADOC will not be specified for this file due to its insignificance to the app
 */

package csci3320.thegallows.test_files;

import csci3320.thegallows.WordBank;


public class TestWordBank {
    
    public static String EASY     = "EASY",
                         MEDIUM   = "MEDIUM",
                         HARD     = "HARD",
                         FREEPLAY = "FREEPLAY";
    
    public static void main(String[] args) {
        // Create a new WordBank from the library stored in easy.txt
        WordBank wb = new WordBank(EASY);

        // Test #1: Correctly return file path
        System.out.println("Test #1: Correctly return file path\n");
        System.out.println("EASY: " + wb.getFile(EASY));
        System.out.println("MEDIUM: " + wb.getFile(MEDIUM));
        System.out.println("HARD: " + wb.getFile(HARD));
        System.out.println("FREEPLAY:" + wb.getFile(FREEPLAY));
        System.out.println("Constructor \"EASY\": " + wb.getFile(wb._difficulty));


        // ------------------------
        System.out.println("\n");
        // ------------------------


        // Test #2: Correctly return file length
        System.out.println("Test #2: Correctly return file length\n");
        System.out.println("EASY: " + wb.getFileLength(wb.getFile(EASY)));
        System.out.println("MEDIUM: " + wb.getFileLength(wb.getFile(MEDIUM)));
        System.out.println("HARD: " + wb.getFileLength(wb.getFile(HARD)));
        System.out.println("FREEPLAY: " + wb.getFileLength(wb.getFile(FREEPLAY)));
        System.out.println("Constructor \"EASY\": " + wb.getFileLength(wb.getFile(wb._difficulty)));


        // ------------------------
        System.out.println("\n");
        // ------------------------


        // Test #3: Correctly return 20 random lines from the four files
        System.out.println("Test #3: Correctly return random lines from the four files\n");

        // assets/easy.txt
        System.out.println("assets/easy.txt\n" + "----------------\n");

        // Create a new WordBank from the library stored in easy.txt
        WordBank wb_e = new WordBank(EASY);

        for (int i = 0; i < 20; i++)
            System.out.print(wb_e.getGameWord() + " ");


        // ------------------------
        System.out.println("\n");
        // ------------------------


        // assets/medium.txt
        System.out.println("assets/medium.txt\n" + "----------------\n");

        // Create a new WordBank from the library stored in medium.txt
        WordBank wb_m = new WordBank(MEDIUM);

        for (int i = 0; i < 20; i++)
            System.out.print(wb_m.getGameWord() + " ");


        // ------------------------
        System.out.println("\n");
        // ------------------------


        // assets/hard.txt
        System.out.println("assets/hard.txt\n" + "----------------\n");

        // Create a new WordBank from the library stored in hard.txt
        WordBank wb_h = new WordBank(HARD);

        for (int i = 0; i < 20; i++)
            System.out.print(wb_h.getGameWord() + " ");


        // ------------------------
        System.out.println("\n");
        // ------------------------


        // assets/freeplay.txt
        System.out.println("assets/freeplay.txt\n" + "----------------\n");

        // Create a new WordBank from the library stored in freeplay.txt
        WordBank wb_f = new WordBank(FREEPLAY);

        // return 60 words since freeplay.txt is 3 times the size of easy.txt/medium.txt/hard.txt
        for (int i = 0; i < 60; i++) 
            System.out.print(wb_f.getGameWord() + " ");
    }
}

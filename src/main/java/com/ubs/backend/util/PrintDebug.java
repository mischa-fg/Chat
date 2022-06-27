package com.ubs.backend.util;

public class PrintDebug {
    /**
     * Method to print debug info to the console
     *
     * @param title            the Title of the Debug Info
     * @param information      the Content of the information
     * @param lineBreaksBefore the amount of linebreaks before the output
     * @author Marc
     * @since 17.07.2021
     */
    public static void printDebug(String title, Object information, int lineBreaksBefore) {
        printDebug(title, information, lineBreaksBefore, 0);
    }

    /**
     * Method to print debug info to the console
     *
     * @param title            the Title of the Debug Info
     * @param information      the Content of the information
     * @param lineBreaksBefore the amount of linebreaks before the output
     * @param lineBreaksAfter  the amount of linebreaks after the output
     * @author Marc
     */
    public static void printDebug(String title, Object information, int lineBreaksBefore, int lineBreaksAfter) {
        if (Variables.debug) {
            StringBuilder breaksBefore = new StringBuilder();
            StringBuilder breaksAfter = new StringBuilder();
            for (int i = 0; i < lineBreaksBefore; i++) breaksBefore.append("\n");
            for (int i = 0; i < lineBreaksAfter; i++) breaksAfter.append("\n");
            StringBuilder titleBuilder = new StringBuilder(title);
            for (int i = titleBuilder.length(); i < 25; i++) titleBuilder.append(" ");
            title = titleBuilder.toString();
            System.out.println(breaksBefore + "Debug | " + title + " : " + information + breaksAfter);
        }
    }

    /**
     * Method to print debug info to the console without linebreaks
     *
     * @param title       the Title of the Debug Info
     * @param information the Content of the information
     * @author Marc
     * @since 17.07.2021
     */
    public static void printDebug(String title, Object information) {
        printDebug(title, information, 0);
    }
}

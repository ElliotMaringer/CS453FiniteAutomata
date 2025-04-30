/*
 * Elliot Maringer
 */

import java.util.*;

public class GenerateString {

    public static void generateAllStringsHelper(Set<String> alphabet, int targetLength, String current, List<String> results) {
        if (current.length() == targetLength) {
            results.add(current);
            return;
        }
        // Add each symbol to current string
        for (String symbol : alphabet) {
            generateAllStringsHelper(alphabet, targetLength, current + symbol, results);
        }
    }

    public static List<String> generateAllStrings(Set<String> alphabet, int lengthLimit) {
        List<String> results = new ArrayList<>();
        // Add the empty string.
        results.add("");
        // For each length from 1 to lengthLimit generate the possible strings
        for (int len = 1; len <= lengthLimit; len++) {
            generateAllStringsHelper(alphabet, len, "", results);
        }
        return results;
    }
}

/*
 * Elliot Maringer
 */

import java.io.*;
import java.util.*;

public class FileParser {
    public static Automaton parseFile(String filePath) throws IOException {
        System.out.println("Attempting to read file from: " + filePath);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        Set<String> states = new HashSet<>();
        Set<String> alphabet = new LinkedHashSet<>(); 
        String initialState = "";
        Set<String> acceptStates = new HashSet<>();
        Map<String, Map<String, Set<String>>> transitions = new HashMap<>();
        Map<String, Set<String>> epsilonTransitions = new HashMap<>();

        String line;
        boolean firstLine = true;
        boolean isEpsilonNFA = false;
        boolean isNFA = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Skip empty or commented lines
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // Parse the header line for the alphabet
            if (firstLine) {
                // Split on | then split the second part by whitespace
                String[] parts = line.split("\\|");
                if (parts.length < 2) {
                    // Not a valid header line so skip
                    continue;
                }
                // Split on white space to get each symbol
                String[] symbols = parts[1].trim().split("\\s+");
                for (String symbol : symbols) {
                    if (symbol.equals("epsilon")) {
                        isEpsilonNFA = true;
                    }
                    alphabet.add(symbol);
                }
                firstLine = false;
                continue;
            }

            //Parse state/transition lines
            String[] parts = line.split("\\|");
            if (parts.length < 1) {
                // Not a valid state/transition line
                continue;
            }

            // parts[0] has the state info on initial or accept
            String stateInfo = parts[0].trim(); 

            // Determine if it’s an initial or accept state
            boolean isInitial = stateInfo.contains("->");
            boolean isAccept  = stateInfo.contains("*");

            // Extract the raw state name by removing extras
            String rawStateName = stateInfo.replace("->", "").replace("*", "").trim();

            // Add that state to the set of all states
            states.add(rawStateName);

            // If initial add to initial states
            if (isInitial) {
                initialState = rawStateName;
            }
            // If accept add to acceptStates
            if (isAccept) {
                acceptStates.add(rawStateName);
            }

            // Parse the transition portion 
            String[] transitionArray = new String[0];
            if (parts.length > 1) {
                transitionArray = parts[1].trim().split("\\s+");
            }

            // Make sure we have a 
            Map<String, Set<String>> stateTransitions = transitions.get(rawStateName);
            if (stateTransitions == null) {
                stateTransitions = new HashMap<>();
                transitions.put(rawStateName, stateTransitions);
            }

            // For each symbol in the alphabet parse the corresponding transition

            int index = 0;
            for (String symbol : alphabet) {
                
                if (index >= transitionArray.length) {
                    // No transition info for this symbol
                    stateTransitions.put(symbol, Collections.emptySet());
                    index++;
                    continue;
                }

                String nextStateString = transitionArray[index].trim(); 
                index++;

                // Remove the braces
                String insideBraces = nextStateString.replace("{", "").replace("}", "").trim();
                if (insideBraces.isEmpty()) {
                    stateTransitions.put(symbol, Collections.emptySet());
                    continue;
                }

                // If there's a comma then there are multiple possible next states so it is a NFA
                String[] stateArray = insideBraces.split(",");
                if (stateArray.length > 1) {
                    isNFA = true;
                }

                Set<String> nextStates = new HashSet<>();
                for (String s : stateArray) {
                    String dest = s.trim();
                    if (!dest.isEmpty()) {
                        nextStates.add(dest);
                        states.add(dest);
                    }
                }

                // If symbol is epsilon we will store in epsilonTransitions
                // else store in stateTransitions
                if (symbol.equals("epsilon")) {
                    epsilonTransitions.put(rawStateName, nextStates);
                } else {
                    stateTransitions.put(symbol, nextStates);
                }
            }
        }
        reader.close();

        // Decide on automaton type
        String automatonType = "DFA";
        if (isEpsilonNFA) {
            automatonType = "EpsilonNFA";
        } else if (isNFA) {
            automatonType = "NFA";
        }

        // Build and return the Automaton
        return new Automaton(states,alphabet, initialState, acceptStates, transitions, automatonType, epsilonTransitions);
    }
}

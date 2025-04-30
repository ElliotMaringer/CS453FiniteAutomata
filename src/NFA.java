/*
 * Elliot Maringer
 */

 import java.util.*;

public class NFA extends Automaton{
    public NFA(Set<String> states, Set<String> alphabet, String initialState, Set<String> acceptStates, Map<String, Map<String, Set<String>>> transitions) {
        super(states, alphabet, initialState, acceptStates, transitions, "NFA", null);
    }

    public Set<String> getNextStates(String currentState, String input) {
        Set<String> nextStates = super.getNextStates(currentState, input);
        
        return nextStates;
    }

    public void printInfo() {
        System.out.println("States: " + getStates());
        System.out.println("Alphabet: " + getAlphabet());
        System.out.println("Transition Table:\n");
        
        // Print header row with the alphabet symbols.
        System.out.print("|");
        for (String symbol : getAlphabet()) {
            System.out.print(" " + symbol);
        }
        System.out.println();
        
        // Print each state's transitions.
        for (String state : getStates()) {
            StringBuilder line = new StringBuilder();
            
            // Mark the initial state with corresponding symbol
            if (state.equals(getInitialState())) {
                line.append("-> ");
            } else if (getAcceptStates().contains(state)) {
                line.append("* ");
            } else {
                line.append("   ");
            }
            
            line.append(state).append(" |");
            Map<String, Set<String>> stateTransitions = getTransitions().get(state);
            
            // For each symbol in the alphabet print out the transitions
            for (String symbol : getAlphabet()) {
                line.append(" ");
                if (stateTransitions != null && stateTransitions.get(symbol) != null) {
                    line.append(stateTransitions.get(symbol).toString());
                } else {
                    line.append("{}");
                }
            }
            System.out.println(line.toString());
        }
        System.out.println();
        System.out.println("Initial State: " + getInitialState());
        System.out.println("Accept States: " + getAcceptStates());
    }

    public boolean runNFA(String inputString) {
        // Begin with the initial state.
        Set<String> currentStates = new HashSet<>();
        currentStates.add(getInitialState());

        // Process each input symbol.
        for (int i = 0; i < inputString.length(); i++) {
            String symbol = String.valueOf(inputString.charAt(i));
            Set<String> nextStates = new HashSet<>();
            for (String state : currentStates) {
                Set<String> transitions = getNextStates(state, symbol);
                if (transitions != null) {
                    nextStates.addAll(transitions);
                }
            }
            currentStates = nextStates;
        }

        // Accept if any of the current states is an accept state.
        for (String state : currentStates) {
            if (getAcceptStates().contains(state)) {
                return true;
            }
        }
        return false;
    }

    public List<String> generateLanguage(int lengthLimit) {
        List<String> accepted = new ArrayList<>();
        // Create a set of input symbols 
        Set<String> inputSymbols = new HashSet<>(getAlphabet());
        inputSymbols.remove("epsilon");
        
        // Call generate all strings method then test which ones work
        List<String> allStrings = GenerateString.generateAllStrings(inputSymbols, lengthLimit);
        for (String s : allStrings) {
            if (this.runNFA(s)) {
                accepted.add(s);
            }
        }
        return accepted;
    }

}


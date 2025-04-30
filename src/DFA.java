/*
 * Elliot Maringer
 */
import java.util.*;

public class DFA extends Automaton {

    public DFA(Set<String> states, Set<String> alphabet, String initialState, Set<String> acceptStates, Map<String, Map<String, Set<String>>> transitions) {
        super(states, alphabet, initialState, acceptStates, transitions, "DFA", null);
    }

    // DFA Specific get next state for a given states
    public Set<String> getNextStates(String currentState, String input) {
        Set<String> nextStates = super.getNextStates(currentState, input);
        if (nextStates.size() > 1) {
            throw new IllegalStateException("DFA can only have one nextState for a given input");
        }
        
        return nextStates;
    }

    public void printDFA(DFA d){
        System.out.println("DFA Details");
        System.out.println("States: " + d.getInitialState());
        System.out.println("Alphabet: " + d.getAlphabet());
        System.out.println("Inital State: " + d.getInitialState());
        System.out.println("Accept States: " + d.getAcceptStates());
        System.out.println("Transitions: " + d.getTransitions());
    }

    public boolean runDFA(String inputString) {
        String currentState = getInitialState(); // Start from the initial state

        // Process each symbol of the input string
        for (int i = 0; i < inputString.length(); i++) {
            String input = String.valueOf(inputString.charAt(i)); // Get the current input symbol
            Set<String> nextStates = getNextStates(currentState, input); // Get the next state based on the input

            if (nextStates == null || nextStates.isEmpty()) {
                System.out.println("No transition defined for state: " + currentState + " with input: " + input);
                return false;
            }

            currentState = nextStates.iterator().next();
        }

        return getAcceptStates().contains(currentState);

    }

    public void printInfo() {
        System.out.println("States: " + getStates());
        System.out.println("Alphabet: " + getAlphabet());
        System.out.println("Transition Table:");
        
        // Print header row with alphabet symbols
        String header = "|";
        List<String> sortedAlphabet = new ArrayList<>(getAlphabet());
        Collections.sort(sortedAlphabet);
        for(String symbol : sortedAlphabet) {
            header += " " + symbol;
        }
        System.out.println(header);
        
        // Print each state's transitions
        for(String state : getStates()) {
            String row = "";
            if(state.equals(getInitialState())) {
                row += "-> ";
            } else {
                row += "   ";
            }
            if(getAcceptStates().contains(state)) {
                row += "* ";
            } else {
                row += "  ";
            }
            row += state + " |";
            Map<String, Set<String>> stateTransitions = getTransitions().get(state);
            for(String symbol : sortedAlphabet) {
                // Assuming DFA gives exactly one destination for each symbol.
                String dest = stateTransitions.get(symbol).iterator().next();
                row += " " + dest;
            }
            System.out.println(row);
        }
        
        System.out.println("Initial State: " + getInitialState());
        System.out.println("Accept States: " + getAcceptStates());
    }

    public DFA getComplement() {
        Set<String> newAcceptStates = new HashSet<>(getStates());
        newAcceptStates.removeAll(getAcceptStates());
        return new DFA(getStates(), getAlphabet(), getInitialState(), newAcceptStates, getTransitions());
    }

    public static DFA intersect(DFA dfa1, DFA dfa2) {
        // Ensure both DFAs have the same alphabet
        if (!dfa1.getAlphabet().equals(dfa2.getAlphabet())) {
            throw new IllegalArgumentException("Alphabets must be the same for intersection.");
        }
        
        Set<String> newStates = new HashSet<>();
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        Set<String> newAcceptStates = new HashSet<>();
        
        // Use a queue for breadth-first construction
        Queue<String> queue = new LinkedList<>();
        
        // Represent combined states as "state1:state2"
        String initialCombined = dfa1.getInitialState() + ":" + dfa2.getInitialState();
        queue.add(initialCombined);
        newStates.add(initialCombined);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(":");
            String s1 = parts[0];
            String s2 = parts[1];
            
            Map<String, Set<String>> currentTransitions = new HashMap<>();
            for (String symbol : dfa1.getAlphabet()) {
                // For DFAs each transition returns exactly one state
                String next1 = dfa1.getTransitions().get(s1).get(symbol).iterator().next();
                String next2 = dfa2.getTransitions().get(s2).get(symbol).iterator().next();
                String combinedNext = next1 + ":" + next2;
                
                // Add the combined state if not seen before.
                if (!newStates.contains(combinedNext)) {
                    newStates.add(combinedNext);
                    queue.add(combinedNext);
                }
                // Set transition for the current combined state
                currentTransitions.put(symbol, Set.of(combinedNext));
            }
            newTransitions.put(current, currentTransitions);
            
            // For intersection a state is accepting if both automata are in accept states
            if (dfa1.getAcceptStates().contains(s1) && dfa2.getAcceptStates().contains(s2)) {
                newAcceptStates.add(current);
            }
        }
        
        return new DFA(newStates, dfa1.getAlphabet(), initialCombined, newAcceptStates, newTransitions);
    }

    public static DFA union(DFA dfa1, DFA dfa2) {
        // Ensure both DFAs have the same alphabet.
        if (!dfa1.getAlphabet().equals(dfa2.getAlphabet())) {
            throw new IllegalArgumentException("Alphabets must be the same for union.");
        }
        
        Set<String> newStates = new HashSet<>();
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        Set<String> newAcceptStates = new HashSet<>();
        
        Queue<String> queue = new LinkedList<>();
        String initialCombined = dfa1.getInitialState() + ":" + dfa2.getInitialState();
        queue.add(initialCombined);
        newStates.add(initialCombined);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(":");
            String s1 = parts[0];
            String s2 = parts[1];
            
            Map<String, Set<String>> currentTrans = new HashMap<>();
            for (String symbol : dfa1.getAlphabet()) {
                String next1 = dfa1.getTransitions().get(s1).get(symbol).iterator().next();
                String next2 = dfa2.getTransitions().get(s2).get(symbol).iterator().next();
                String combinedNext = next1 + ":" + next2;
                if (!newStates.contains(combinedNext)) {
                    newStates.add(combinedNext);
                    queue.add(combinedNext);
                }
                currentTrans.put(symbol, Set.of(combinedNext));
            }
            newTransitions.put(current, currentTrans);
            
            // For union a state is accepting if at least one of the states are an accpet state
            if (dfa1.getAcceptStates().contains(s1) || dfa2.getAcceptStates().contains(s2)) {
                newAcceptStates.add(current);
            }
        }
        
        return new DFA(newStates, dfa1.getAlphabet(), initialCombined, newAcceptStates, newTransitions);
    }

    public List<String> generateLanguage(int lengthLimit) {
        List<String> accepted = new ArrayList<>();
        // Create a set of input symbols
        Set<String> inputSymbols = new HashSet<>(getAlphabet());
        
        List<String> allStrings = GenerateString.generateAllStrings(inputSymbols, lengthLimit);
        for (String s : allStrings) {
            if (this.runDFA(s)) {
                accepted.add(s);
            }
        }
        return accepted;
    }
    
    
    
}


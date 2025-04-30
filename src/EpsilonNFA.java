/*
 * Elliot Maringer
 */

 import java.util.*;

public class EpsilonNFA extends Automaton {
    public EpsilonNFA(Set<String> states, Set<String> alphabet, String initialState, Set<String> acceptStates, Map<String, Map<String, Set<String>>> transitions,Map<String, Set<String>> epsilonTransitions) {
        super(states, alphabet, initialState, acceptStates, transitions, "NFA", epsilonTransitions);
    }


    public Set<String> getNextStates(String currentState, String input) {
        return super.getNextStates(currentState, input);
    }

    public void printInfo() {
        System.out.println("EpsilonNFA Details:");
        System.out.println("States: " + getStates());
        System.out.println("Alphabet: " + getAlphabet());
        System.out.println("Transition Table:\n");

        Set<String> inputSymbols = new HashSet<>(getAlphabet());
        inputSymbols.remove("epsilon");
        List<String> sortedSymbols = new ArrayList<>(inputSymbols);
        Collections.sort(sortedSymbols);
        sortedSymbols.add("epsilon");


        // Print header row for the symbols plus
        System.out.print("|");
        for (String symbol : sortedSymbols) {
            System.out.print(" " + symbol);
        }
        
        System.out.println();

        // Print each state's transitions
        for (String state : getStates()) {
            StringBuilder line = new StringBuilder();
            // Mark initial state with their corresponing symbols
            if (state.equals(getInitialState())) {
                line.append("-> ");
            } else if (getAcceptStates().contains(state)) {
                line.append("* ");
            } else {
                line.append("   ");
            }
            line.append(state).append(" |");

            // Print transitions for each symbol.
            Map<String, Set<String>> stateTransitions = getTransitions().get(state);
            for (String symbol : sortedSymbols) {
                line.append(" ");
                if (symbol.equals("epsilon")) {
                    // For epsilon use the epsilonTransitions map
                    Set<String> epsTransitions = getEpsilonTransitions().get(state);
                    line.append(epsTransitions != null ? epsTransitions.toString() : "{}");
                } else {
                    // For other symbols use the regular transitions.
                    if (stateTransitions != null && stateTransitions.get(symbol) != null) {
                        line.append(stateTransitions.get(symbol).toString());
                    } else {
                        line.append("{}");
                    }
                }
            }
            System.out.println(line.toString());
        }
        System.out.println();
        System.out.println("Initial State: " + getInitialState());
        System.out.println("Accept States: " + getAcceptStates());
    }

    public boolean runENFA(String inputString) {
        // Start with the epsilon closure of the initial state.
        Set<String> currentStates = new HashSet<>();
        currentStates.add(getInitialState());
        currentStates = epsilonClosure(currentStates);

        // Process each symbol in the input string.
        for (int i = 0; i < inputString.length(); i++) {
            String symbol = String.valueOf(inputString.charAt(i));
            Set<String> nextStates = new HashSet<>();
            for (String state : currentStates) {
                Map<String, Set<String>> stateTransitions = getTransitions().get(state);
                if (stateTransitions != null && stateTransitions.get(symbol) != null) {
                    nextStates.addAll(stateTransitions.get(symbol));
                }
            }
            // After consuming a symbol, update current states to the epsilon closure of nextStates.
            currentStates = epsilonClosure(nextStates);
        }

        // Accept if any of the current states is an accepting state.
        for (String state : currentStates) {
            if (getAcceptStates().contains(state)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> epsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>(states);
        boolean added = true;
        while (added) {
            added = false;
            Set<String> newStates = new HashSet<>();
            for (String state : closure) {
                Set<String> epsTransitions = getEpsilonTransitions().get(state);
                if (epsTransitions != null) {
                    for (String next : epsTransitions) {
                        if (!closure.contains(next)) {
                            newStates.add(next);
                        }
                    }
                }
            }
            if (!newStates.isEmpty()) {
                closure.addAll(newStates);
                added = true;
            }
        }
        return closure;
    }

    public List<String> generateLanguage(int lengthLimit) {
        List<String> accepted = new ArrayList<>();
        // Create a set of input symbols excluding epsilon.
        Set<String> inputSymbols = new HashSet<>(getAlphabet());
        inputSymbols.remove("epsilon");
        
        List<String> allStrings = GenerateString.generateAllStrings(inputSymbols, lengthLimit);
        for (String s : allStrings) {
            if (this.runENFA(s)) {
                accepted.add(s);
            }
        }
        return accepted;
    }

    public static EpsilonNFA union(Automaton a1, Automaton a2) {
        //Rename states in a2 to avoid collisions
        Automaton a2Renamed = renameStates(a2, "2");

        //Combine the sets of states
        Set<String> newStates = new HashSet<>();
        newStates.addAll(a1.getStates());
        newStates.addAll(a2Renamed.getStates());

        //Combine the alphabets 
        Set<String> newAlphabet = new HashSet<>(a1.getAlphabet());
        newAlphabet.addAll(a2Renamed.getAlphabet());
        

        // Merge transitions using helper method to get the transition maps
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        //
        for (String state : a1.getStates()) {
            newTransitions.put(state, cloneTransitionMap(a1.getTransitions().get(state)));
        }
        //
        for (String state : a2Renamed.getStates()) {
            newTransitions.put(state, cloneTransitionMap(a2Renamed.getTransitions().get(state)));
        }

        //Merge epsilon transitions
        Map<String, Set<String>> newEps = new HashMap<>();
        
        if (a1.getEpsilonTransitions() != null) {
            for (String s : a1.getEpsilonTransitions().keySet()) {
                newEps.put(s, new HashSet<>(a1.getEpsilonTransitions().get(s)));
            }
        }
        
        if (a2Renamed.getEpsilonTransitions() != null) {
            for (String s : a2Renamed.getEpsilonTransitions().keySet()) {
                newEps.put(s, new HashSet<>(a2Renamed.getEpsilonTransitions().get(s)));
            }
        }

        //Create a new initial state that has epsilon transitions to both old initial states
        String newInitial = "Initial";
        newStates.add(newInitial);
        newEps.put(newInitial, new HashSet<>());
        newEps.get(newInitial).add(a1.getInitialState());
        newEps.get(newInitial).add(a2Renamed.getInitialState());

        //Accept states is the union of old accept states
        Set<String> newAcceptStates = new HashSet<>(a1.getAcceptStates());
        newAcceptStates.addAll(a2Renamed.getAcceptStates());

        //Build and return the new EpsilonNFA
        return new EpsilonNFA(
            newStates, newAlphabet, newInitial, newAcceptStates, newTransitions, newEps
        );
    }

    private static Automaton renameStates(Automaton automaton, String prefix) {
        //Map oldName -> newName
        Map<String, String> renameMap = new HashMap<>();
        for (String oldState : automaton.getStates()) {
            renameMap.put(oldState, prefix + oldState);
        }

        //Build new sets
        Set<String> newStates = new HashSet<>();
        for (String s : automaton.getStates()) {
            newStates.add(renameMap.get(s));
        }

        //Rename transitions
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        for (String oldState : automaton.getStates()) {
            String newState = renameMap.get(oldState);
            newTransitions.put(newState, new HashMap<>());

            Map<String, Set<String>> oldTransMap = automaton.getTransitions().get(oldState);
            if (oldTransMap == null) continue;

            for (String symbol : oldTransMap.keySet()) {
                Set<String> dests = oldTransMap.get(symbol);
                Set<String> renamedDests = new HashSet<>();
                for (String d : dests) {
                    renamedDests.add(renameMap.get(d));
                }
                newTransitions.get(newState).put(symbol, renamedDests);
            }
        }

        //Rename epsilon transitions
        Map<String, Set<String>> newEps = new HashMap<>();
        if (automaton.getEpsilonTransitions() != null) {
            for (String oldState : automaton.getEpsilonTransitions().keySet()) {
                String newState = renameMap.get(oldState);
                Set<String> oldEps = automaton.getEpsilonTransitions().get(oldState);
                Set<String> renamedEps = new HashSet<>();
                for (String d : oldEps) {
                    renamedEps.add(renameMap.get(d));
                }
                newEps.put(newState, renamedEps);
            }
        }

        //Rename initial state
        String newInitial = renameMap.get(automaton.getInitialState());

        //Rename accept states
        Set<String> newAccepts = new HashSet<>();
        for (String a : automaton.getAcceptStates()) {
            newAccepts.add(renameMap.get(a));
        }

        // Build new automaton to return
        return new Automaton(
            newStates, automaton.getAlphabet(), newInitial, newAccepts, newTransitions, automaton.getType(), newEps
        );
    }
    //Helper method to create a seperate copy of the transition map
    private static Map<String, Set<String>> cloneTransitionMap(Map<String, Set<String>> original) {
        if (original == null) {
            return new HashMap<>();
        }
        Map<String, Set<String>> copy = new HashMap<>();
        for (String symbol : original.keySet()) {
            copy.put(symbol, new HashSet<>(original.get(symbol)));
        }
        return copy;
    }

    public static EpsilonNFA concatenate(Automaton a1, Automaton a2) {
        //Rename states in a2 to avoid collisions.
        Automaton a2Renamed = renameStates(a2, "2");

        //Combine the sets of states.
        Set<String> newStates = new HashSet<>();
        newStates.addAll(a1.getStates());
        newStates.addAll(a2Renamed.getStates());

        //Combine the alphabets.
        Set<String> newAlphabet = new HashSet<>(a1.getAlphabet());
        newAlphabet.addAll(a2Renamed.getAlphabet());

        //Merge transitions from both automatons.
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        for (String state : a1.getStates()) {
            newTransitions.put(state, cloneTransitionMap(a1.getTransitions().get(state)));
        }
        for (String state : a2Renamed.getStates()) {
            newTransitions.put(state, cloneTransitionMap(a2Renamed.getTransitions().get(state)));
        }

        //Merge epsilon transitions from both automatons.
        Map<String, Set<String>> newEps = new HashMap<>();
        if (a1.getEpsilonTransitions() != null) {
            for (String s : a1.getEpsilonTransitions().keySet()) {
                newEps.put(s, new HashSet<>(a1.getEpsilonTransitions().get(s)));
            }
        }
        if (a2Renamed.getEpsilonTransitions() != null) {
            for (String s : a2Renamed.getEpsilonTransitions().keySet()) {
                newEps.put(s, new HashSet<>(a2Renamed.getEpsilonTransitions().get(s)));
            }
        }

        // For all accept states in the first add an epsilon transition to the initial of the second
        for (String acceptState : a1.getAcceptStates()) {
            // Check if newEps has an entry for acceptState if not create one.
            if (newEps.get(acceptState) == null) {
                newEps.put(acceptState, new HashSet<>());
            }
            // add the new epsilon transition.
            newEps.get(acceptState).add(a2Renamed.getInitialState());
        }

        //Initial state remains the same
        String newInitial = a1.getInitialState();

        //The new accept states are the ones in the second automaton
        Set<String> newAcceptStates = new HashSet<>(a2Renamed.getAcceptStates());

        //Build and return the new EpsilonNFA.
        return new EpsilonNFA(newStates, newAlphabet, newInitial, newAcceptStates, newTransitions, newEps);
    }

    public static EpsilonNFA closure(Automaton a) {
        //Create a new initial state for the closure, which will also be an accept state for the empty string
        String newInitial = "Initial";
    
        //Combine both sets of states
        Set<String> newStates = new HashSet<>(a.getStates());
        newStates.add(newInitial);
    
        //The alphabet remains the same.
        Set<String> newAlphabet = new HashSet<>(a.getAlphabet());
    
        //Clone the transitions from the original automaton.
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        for (String state : a.getStates()) {
            newTransitions.put(state, cloneTransitionMap(a.getTransitions().get(state)));
        }
    
        //Merge epsilon transitions.
        Map<String, Set<String>> newEps = new HashMap<>();
        // Copy original epsilon transitions, if any.
        if (a.getEpsilonTransitions() != null) {
            for (String state : a.getEpsilonTransitions().keySet()) {
                newEps.put(state, new HashSet<>(a.getEpsilonTransitions().get(state)));
            }
        }
        
        //From the new initial state, add an epsilon transition to the original initial state.
        newEps.put(newInitial, new HashSet<>());
        newEps.get(newInitial).add(a.getInitialState());
        
        //For every accept state of the original automaton, add an epsilon transition back to the new initial state.
        for (String acceptState : a.getAcceptStates()) {
            if (newEps.get(acceptState) == null) {
                newEps.put(acceptState, new HashSet<>());
            }
            newEps.get(acceptState).add(newInitial);
        }
        
        //The new accept states include the new initial state for the empty string
        Set<String> newAcceptStates = new HashSet<>();
        newAcceptStates.add(newInitial);
        
        //Return the new EpsilonNFA representing the closure.
        return new EpsilonNFA(newStates, newAlphabet, newInitial, newAcceptStates, newTransitions, newEps);
    }
    
    



}

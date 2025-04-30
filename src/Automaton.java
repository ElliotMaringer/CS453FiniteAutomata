/*
 * Elliot Maringer
 */

import java.util.*;

public class Automaton {
  
    private Set<String> states;            
    
    private Set<String> alphabet;       
    
    private String initialState;         

    private Set<String> acceptStates;     

    private Map<String, Map<String, Set<String>>> transitions;
    
    private String automatonType;

    private Map<String, Set<String>> epsilonTransitions;
    
    // Constructor
    public Automaton(Set<String> states, Set<String> alphabet, String initialState, 
                     Set<String> acceptStates, Map<String, Map<String, Set<String>>> transitions, 
                     String automatonType, Map<String, Set<String>> epsilonTransitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.acceptStates = acceptStates;
        this.transitions = transitions;
        this.automatonType = automatonType;
        this.epsilonTransitions = epsilonTransitions;
    }

    // Getter methods for attributes
    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getAcceptStates() {
        return acceptStates;
    }

    public Map<String, Map<String, Set<String>>> getTransitions() {
        return transitions;
    }

    public Map<String, Set<String>> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public String getType(){
        return automatonType;
    }

    public boolean isAcceptingState(String state) {
        return acceptStates.contains(state);
    }

    public Set<String> getNextStates(String currentState, String input) {
        if(transitions.containsKey(currentState)) {
            Map<String, Set<String>> stateTransitions = transitions.get(currentState);
            if(stateTransitions.containsKey(input)) {
                return stateTransitions.get(input);
            }
        }
        return Set.of();
    }

}


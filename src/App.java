/*
 * Elliot Maringer
 */

import java.io.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("Usage: java App <action> <arguments>");
            return;
        }
        //Get action command from user as first argument and handle accordingly
        String action = args[0];
        switch(action) {
            case "--info": {
                String filePath = args[1]; //Assuming one FA input
                try {
                    Automaton automaton = FileParser.parseFile(filePath);
                    String type = automaton.getType();
                    //cases for handling each type of automata
                    switch (type) {
                        case "DFA": {
                            DFA dfa = new DFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions());
                            dfa.printInfo();
                            break;
                        }
                        case "NFA": {
                            NFA nfa = new NFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions());
                            nfa.printInfo();
                            break;
                        }
                        case "EpsilonNFA": {
                            EpsilonNFA enfa = new EpsilonNFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions(), automaton.getEpsilonTransitions());
                            enfa.printInfo();
                            break;
                        }
                        default:
                            System.out.println("Unsupported automaton type.");
                    }
                } catch (IOException e) {
                    System.out.println("An issue occurred while reading the file");
                }
                break;
            }
            case "--run": {
                // Parse filepath for a single FA
                String filePath = args[1];
                // Obtain an input to run FA on
                String input = args[2];
                try {
                    Automaton automaton = FileParser.parseFile(filePath);
                    String type = automaton.getType();
                    boolean result = false;
                    switch (type) {
                        case "DFA": {
                            DFA dfa = new DFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions());
                            result = dfa.runDFA(input);
                            break;
                        }
                        case "NFA": {
                            NFA nfa = new NFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(),
                                    automaton.getAcceptStates(), automaton.getTransitions());
                            result = nfa.runNFA(input);
                            break;
                        }
                        case "EpsilonNFA": {
                            EpsilonNFA enfa = new EpsilonNFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(),
                                    automaton.getAcceptStates(), automaton.getTransitions(), automaton.getEpsilonTransitions());
                            result = enfa.runENFA(input);
                            break;
                        }
                        default:
                            System.out.println("Unsupported automaton type.");
                    }
                    // Use truthyness of result from running our automaton to print accept or reject
                    System.out.println("Running on input [" + input + "]: " + (result ? "accept" : "reject"));
                } catch (IOException e) {
                    System.out.println("An issue occurred while reading the file");
                }
                break;
            }
            case "--language": {
                // Parse
                String filePath = args[1];
                // Obtain what length to generate strings up to
                int lengthLimit = Integer.parseInt(args[2]);
                try {
                    Automaton automaton = FileParser.parseFile(filePath);
                    List<String> language;
                    String type = automaton.getType();
                    switch(type) {
                        case "DFA": {
                            DFA dfa = new DFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions());
                            language = dfa.generateLanguage(lengthLimit);
                            break;
                        }
                        case "NFA": {
                            NFA nfa = new NFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions());
                            language = nfa.generateLanguage(lengthLimit);
                            break;
                        }
                        case "EpsilonNFA": {
                            EpsilonNFA enfa = new EpsilonNFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(), automaton.getAcceptStates(), automaton.getTransitions(), automaton.getEpsilonTransitions());
                            language = enfa.generateLanguage(lengthLimit);
                            break;
                        }
                        default: {
                            System.out.println("Unsupported automaton type for language command.");
                            return;
                        }
                    }
                    System.out.println("L(M) = {");
                    for (String s : language) {
                        System.out.println("  " + s + ",");
                    }
                    System.out.println("... }");
                } catch (IOException e) {
                    System.out.println("An issue occurred while reading the file");
                }
                break;
            }
            case "--complement": {
                // Parse single DFA
                String filePath = args[1];
                try {
                    Automaton automaton = FileParser.parseFile(filePath);
                    if (!automaton.getType().equals("DFA")) {
                        System.out.println("This command only supports DFAs.");
                        return;
                    }
                    DFA dfa = new DFA(automaton.getStates(), automaton.getAlphabet(), automaton.getInitialState(),
                            automaton.getAcceptStates(), automaton.getTransitions());
                    DFA compDFA = dfa.getComplement();
                    compDFA.printInfo();
                } catch (IOException e) {
                    System.out.println("An issue occurred while reading the file");
                }
                break;
            }

            case "--intersect": {
                // Parse both DFA from arguments
                String filePath1 = args[1];
                String filePath2 = args[2];
                try {
                    Automaton auto1 = FileParser.parseFile(filePath1);
                    Automaton auto2 = FileParser.parseFile(filePath2);
                    if (!auto1.getType().equals("DFA") || !auto2.getType().equals("DFA")) {
                        System.out.println("This command only supports DFAs.");
                        return;
                    }
                    DFA dfa1 = new DFA(auto1.getStates(), auto1.getAlphabet(), auto1.getInitialState(), auto1.getAcceptStates(), auto1.getTransitions());
                    DFA dfa2 = new DFA(auto2.getStates(), auto2.getAlphabet(), auto2.getInitialState(), auto2.getAcceptStates(), auto2.getTransitions());
                    DFA intersectDFA = DFA.intersect(dfa1, dfa2);
                    intersectDFA.printInfo();
                } catch(IOException e) {
                    System.out.println("An issue occurred while reading the file(s)");
                }
                break;
            }
            case "--union": {
                String filePath1 = args[1];
                String filePath2 = args[2];
                try {
                    Automaton auto1 = FileParser.parseFile(filePath1);
                    Automaton auto2 = FileParser.parseFile(filePath2);
                    
                    // If both are DFAs we will use the cross-product construction
                    if(auto1.getType().equals("DFA") && auto2.getType().equals("DFA")) {
                        DFA dfa1 = new DFA(auto1.getStates(), auto1.getAlphabet(), auto1.getInitialState(), auto1.getAcceptStates(), auto1.getTransitions());
                        DFA dfa2 = new DFA(auto2.getStates(), auto2.getAlphabet(), auto2.getInitialState(), auto2.getAcceptStates(), auto2.getTransitions());
                        DFA unionDFA = DFA.union(dfa1, dfa2);
                        unionDFA.printInfo();
                    } else {
                        // If at least one is nondeterministic we will use the epsilon-NFA union construction
                        EpsilonNFA unionENFA = EpsilonNFA.union(auto1, auto2);
                        unionENFA.printInfo();
                    }
                } catch(IOException e) {
                    System.out.println("An issue occurred while reading the file(s)");
                }
                break;
            }
            case "--concatenate": {
                //Parse file path for both FA
                String filePath1 = args[1];
                String filePath2 = args[2];
                try {
                    Automaton auto1 = FileParser.parseFile(filePath1);
                    Automaton auto2 = FileParser.parseFile(filePath2);
                    // Build an epsilon-NFA for the concatenation of the two automata.
                    EpsilonNFA concatENFA = EpsilonNFA.concatenate(auto1, auto2);
                    concatENFA.printInfo();
                } catch(IOException e) {
                    System.out.println("An issue occurred while reading the file(s)");
                }
                break;
            }
            case "--closure": {
                String filePath = args[1];
                try {
                    Automaton auto = FileParser.parseFile(filePath);
                    // Build an epsilon-NFA representing the closure of the automaton.
                    EpsilonNFA closureENFA = EpsilonNFA.closure(auto);
                    closureENFA.printInfo();
                } catch(IOException e) {
                    System.out.println("An issue occurred while reading the file");
                }
                break;
            }
            
            case "--to-dfa-full":
                System.out.println("Did not implement :(");
                break;
            case "--to-dfa-lazy":
                System.out.println("Did not implement :(");
                break;
            default:
                System.out.println("Please enter a valid action");
                break;
        }
    }
}

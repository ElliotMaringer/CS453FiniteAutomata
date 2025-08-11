This README provides directions to use a finite automate simulator in java that supports both DFAs, NFAs and epsilon NFAs.

The project can 
    -Load automata descriptions from text files
    -Run automata on a given input string
    -Print information about the automata
    -Generate the langue of an automata to a certain length
    -Construct new automata using operations such as complement, intersection, union, concatenation and closure.

To compile the program, use the following command in the directory containing the source files

    javac *.java

The program is designed to be run from the command line with one of several commands. For commands that produce new finite automaton, 
their transition table will be printed to the console. Filename should be inputed as a complete filepath. Here is a list of the supported command-line options:

1. Display automaton information:
       java App --info <FILENAME>

2. Simulate the automaton on an input string:
       java App --run <FILENAME> <INPUT>

3. Print all strings (up to a given length) in the automaton's language:
       java App --language <FILENAME> <LENGTHLIMIT>

4. Construct the complement of a DFA:
       java App --complement <FILENAME>

5. Construct the intersection of two DFAs:
       java App --intersect <FILENAME1> <FILENAME2> 

6. Construct the union of two automatons:
       java App --union <FILENAME1> <FILENAME2> 

7. Construct the concatenation of two automatons:
       java App --concatenate <FILENAME1> <FILENAME2> 

8. Construct the closure of an automaton:
       java App --closure <FILENAME> 

Additional Notes: The program assumes that automaton files are in the format of a transition table. 
For DFA union and intersection operations, it is currently assumed the both DFAs share the same alphabet.

Author: Elliot Maringer
Date: 03/12/2025



// This is part of a solution to Programming Assignment 4 of Algorithms II 
// at Coursera, whose instructors are Robert Sedgewick and Kevin Wayne of 
// Princeton University
// Details can be found at:
// http://coursera.cs.princeton.edu/algs4/assignments/boggle.html

import java.util.ArrayList;

public class Tuple{
    public Node node;
    public ArrayList<Integer> trail;
    public char[] prefix;
    public char alphabet;
    
    //this constructor builds the tuple: Node, a list of integers showing the order or boxes used, prefix and suffix
    // the tuple is used in DFS to find all valid words given a dictionary and a board in BoggleSolver.java
    
    public Tuple(Node nd, ArrayList<Integer> list, char [] str, char letter){
        node = nd;
        trail = list;
        int charArrayLength = str.length;
        prefix = new char[charArrayLength + 1];
        System.arraycopy(str, 0, prefix, 0, charArrayLength);
        prefix[charArrayLength] = letter;
        alphabet = letter;
    }       
}

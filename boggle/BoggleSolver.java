// This is part of a solution to Programming Assignment 4 of Algorithms II 
// at Coursera, whose instructors are Robert Sedgewick and Kevin Wayne of 
// Princeton University
// Details can be found at:
// http://coursera.cs.princeton.edu/algs4/assignments/boggle.html

import edu.princeton.cs.algs4.In;  // library of algs4
import edu.princeton.cs.algs4.StdOut; //library of algs4
import edu.princeton.cs.algs4.Stack;  //library of algs4
import java.util.ArrayList;
import java.util.HashSet;

public class BoggleSolver{
    private Node root;
    private Paths fourByFourPaths;
    public BoggleSolver(String[] dictionary){

        //create a root for the structure
        root = new Node(new char[0], false);
        //read each word from the dictionary into the data structure
        for (int i = 0; i < dictionary.length; i++){
            if (dictionary[i].length() > 2){
                char [] charArray = dictionary[i].toCharArray();
                Node.entry(charArray, 0, root);
            }
        }            
    }

    public Iterable <String> getAllValidWords(BoggleBoard board){
        // create a map of boxId to alphabet of the board using char []
        int idCounter = 0;     
        int boardRows = board.rows();
        int boardCols = board.cols();
        char [] numAlpha = new char[boardRows * boardCols];
        for (int r = 0; r < boardRows; r++){
            for (int c = 0; c < boardCols; c++){
                numAlpha[idCounter] = board.getLetter(r, c);
                idCounter++;
            }
        }
        Pathfinder findPaths = new Pathfinder(boardRows, boardCols, numAlpha, root, fourByFourPaths);
        return (HashSet<String>) findPaths.pathfinder();

    }
    
    // a nested class that does the DFS
    private class Pathfinder{    
        private char[] numLetter; //mapping from number to alphabet
        private int dimR, dimC;
        private Paths paths;
        private Node rt; //root Node
                
        public Pathfinder(int r, int c, char[] numAlphabet, Node theRoot, Paths cachedPaths){
            //find all possibly legal directions of each box
            dimR = r;
            dimC = c;
            rt = theRoot;
            numLetter= numAlphabet;
            if (cachedPaths == null || dimR != 4 || dimC !=4){ //use cached paths for a 4x4 board
                paths = new Paths(r, c);                  
            }
            else{
                paths = cachedPaths;
            }
        }
        
        public Iterable<String> pathfinder(){        
            // create an empty set of valid words
            // no duplicate
            HashSet<String> validWordList = new HashSet<String>();
            // create an empty stack for each state
            Stack <Tuple> idStack = new Stack <Tuple>();
            // iterate each boxId as first character
            for (int j = 0; j < dimR * dimC; j++){
                //create one list for each boxId, with the boxId as its first element
                //elements in the trail cannot be reused
                Node thisNode = rt.returnNode(numLetter[j]);
                if (thisNode != null){ //check if the corresponding first letter is a valid prefix    
                    ArrayList<Integer> intTrail = new ArrayList<Integer>();
                    intTrail.add(j);
                    idStack.push(new Tuple(thisNode, intTrail, new char[0], numLetter[j]));
                }
            }
            // Depth First Search
            while (!idStack.isEmpty()){
                // pop the stack to get the first state
                Tuple thisTuple = idStack.pop();
                // get the last boxId on the trail
                Node currentNode = thisTuple.node;
                // caching variables
                int wordLength = thisTuple.prefix.length;
                int trailLength = thisTuple.trail.size();
                int currentId = thisTuple.trail.get(trailLength - 1);
                ArrayList<Integer> currentIdTrail = thisTuple.trail;
                char[] potentialValidWord = thisTuple.prefix;
                char[] potentialValidWordU = new char[wordLength + 1];
                // check if node is a valid terminator
                if(Node.isValidWord(currentNode)){
                    String confirmedValidWord = new String(potentialValidWord);
                    validWordList.add(confirmedValidWord);  
                }
                // not a valid word; check if a 'Q' is detected, if so add a 'U' and and recheck
                if (currentNode.isQ()){
                    currentNode = currentNode.returnNode('U');                    
                    System.arraycopy(potentialValidWord, 0, potentialValidWordU, 0, wordLength);
                    potentialValidWordU[wordLength] = 'U';
                    if(Node.isValidWord(currentNode)){
                        //means valid word
                        String confirmedValidWordU = new String(potentialValidWordU);
                        validWordList.add(confirmedValidWordU); 
                    }
                    potentialValidWord = potentialValidWordU;
                }       
                ArrayList<Integer> currentPaths = paths.returnPathSets().get(currentId);
                // iterate the legal directions to find the first unused one
                for (int id: currentPaths){
                    Node nextNode = currentNode.returnNode(numLetter[id]);
                    if (!currentIdTrail.contains(id) && nextNode != null){
                        ArrayList<Integer> temporaryIdTrail = new ArrayList<Integer> (currentIdTrail);                 
                        temporaryIdTrail.add(id);         
                        idStack.push(new Tuple(nextNode, temporaryIdTrail, potentialValidWord, numLetter[id]));
                    }
                }
            }            
            return  validWordList;
        } 
    }
   

    public int scoreOf(String word){ //validity of a word needs to be checked regardless of the board
        int score = 0;
        int index = 0; 
        int wordLength = word.length();
        char [] charArray = word.toCharArray();
        Node currentNode = root;
        // keep going down from root until all characters are counted and see if
        // the node at that point can be the last character
        while (index < wordLength){ 
            char prefix = charArray[index];
            Node nextNode = currentNode.returnNode(prefix);
            if (nextNode != null){
                if (index == wordLength - 1){
                    if (nextNode.isTerminated()){ //valid 
                        break;
                    }
                    else{ // no more character and the word is not valid
                        return 0;}
                    }
                else{
                    currentNode = nextNode;
                }
            }
            else{// next character doesn't match
                return 0;
            }
            index++;
        }
        // count word's score
        if (word.length() > 2 && wordLength < 5){score = 1;}
        else if (word.length() == 5){score = 2;}
        else if (word.length() == 6){score = 3;}
        else if (word.length() == 7){score = 5;}
        else if (word.length() >= 8){score = 11;}

        return score;
    }
    
    public static void main (String[] args){
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board =new BoggleBoard(args[1]);
        
        int score = 0;

        for (String word:solver.getAllValidWords(board)){
          StdOut.println(word);
          score += solver.scoreOf(word);
        }        
        StdOut.println("Score = " + score);
    }
        

}
// This is part of a solution to Programming Assignment 4 of Algorithms II 
// at Coursera, whose instructors are Robert Sedgewick and Kevin Wayne of 
// Princeton University
// Details can be found at:
// http://coursera.cs.princeton.edu/algs4/assignments/boggle.html


//This class creates a node with link to the next nodes and additional information
import java.util.HashMap;

public class Node{
    private HashMap<Character, Node>symbolTable = new HashMap<Character, Node>(); // store the set of possible next characters
    private boolean isTerminated = false; //if this node can be the end of a word
    private boolean isQ = false; //is this a 'Q'?
    
    //this constructor builds the leaf with an indicator for proper ending
    public Node(char[] str, boolean isLetterQ){
        isTerminated = true;
        isQ = isLetterQ; //unusual to have a Q ending and the word will not be searchable
    }
    // this constructor builds the links of nodes of characters 
    // parameter shows whether the letter is 'Q'
    public Node(boolean is_Q){
        isQ = is_Q;
    }
    
    // this method builds new branches for a new word, one character at a time 
    public static void entry(char[] newString, int index, Node currentNode){
        Node nextNode = null;
        int strLength = newString.length;// cached
        char prefix = '\0'; //null
        boolean isLetterQ = false; //default
        while (index < strLength -1){
            prefix =  newString[index];      
            if (prefix == 'Q'){ //a 'Q' without a following 'U' is invalid so no need to put in structure
                if (newString[index + 1] != 'U'){
                    return;
                }
                else{
                    isLetterQ = true;
                }
            }
            //if the next character in a suffix already exists in the current node
            nextNode = currentNode.returnNode(prefix);
            if (nextNode == null){
                // if the next character does not exist in this node's symbol table
                // build a new node with the status of whether the letter is 'Q'
                nextNode = new Node(isLetterQ);   
                currentNode.returnSymbolTable().put(prefix, nextNode);
            }
            currentNode = nextNode; // move to next character
            isLetterQ = false; //reset default            
            index++;
        }
        // this character is the last, terminate iteration
        prefix =  newString[index]; 
        //a 'Q' without a following 'U' is invalid so no need to put in structure
        if (prefix == 'Q'){return; }
        currentNode.returnSymbolTable().put(prefix, new Node(newString, isLetterQ));
    }
    
    // return the status of a node
    public static boolean isValidWord(Node currentNode){
        if (currentNode.isTerminated()){
            return true; //true means valid word
        }
        return false;
    }
    // return the next node from the current node if one for a particular 
    // letter exists; null otherwise
    public Node returnNode(char letter){
        if (symbolTable.containsKey(letter)){
            return symbolTable.get(letter);
        }
        else {return null;}
    }
    // return true if the node is 'Q'
    public boolean isQ(){
        return isQ;
    }
    
    // return true if the node can be a proper ending
    public boolean isTerminated(){
        return isTerminated;
    }
    
    
    // return if node's symbolTable is empty
    public boolean isHashMapEmpty(){
        return symbolTable.isEmpty();
    }
    
    // return node's symbolTable
    public HashMap<Character, Node> returnSymbolTable() {
        return symbolTable;
    }  
}
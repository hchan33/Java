// This is part of a solution to Programming Assignment 4 of Algorithms II 
// at Coursera, whose instructors are Robert Sedgewick and Kevin Wayne of 
// Princeton University
// Details can be found at:
// http://coursera.cs.princeton.edu/algs4/assignments/boggle.html

// this class creates an object with all legal directions from all boxIds.
import java.util.ArrayList;

public class Paths{     
    private ArrayList<ArrayList<Integer>> pathSets;
    //constructor
    public Paths(int m, int n){
        pathSets =  allLegalDirections(m, n);        
    }
    
    // this method returns what directions the next move can take for the boxId
    // boxId is the id between 0 and dimR x dimC - 1  (e.g. 0 to 15 in a 4x4 box)
    // return an integer arrayList
    private ArrayList<Integer> legalDirection(int boxId, int dimR, int dimC){
        ArrayList<Integer> results = new ArrayList<Integer>();
        int row = boxId / dimC;
        int column = boxId % dimC;
        int newIndex = 0;
        int newR = 0;
        int newC = 0;
        // check if surrounding boxes are potentially legal directions
        for (int r = -1; r < 2; r++){
            for (int c = -1; c < 2; c++){
                newR = row + r ;
                newC = column + c;
                if (newR >= 0 && newR < dimR && newC >= 0 && newC < dimC){
                    newIndex = newR * dimC + newC;
                    //direction is legal
                    if (newIndex != boxId) {results.add(newIndex);}
                }
            }
        }  
        return results;
    }
    
    // create an ArrayList of ArrayList of the sets of legal directions  
    private ArrayList<ArrayList<Integer>>  allLegalDirections(int dimR, int dimC){
        ArrayList<ArrayList<Integer>> directionSets = new ArrayList<ArrayList<Integer>>();  
        for (int i = 0; i < dimR * dimC; i++){
            directionSets.add(legalDirection(i, dimR, dimC));
        }  
        return directionSets;
    }
    
    // return an ArrayList of ArrayList of the sets of legal directions  
    public ArrayList<ArrayList<Integer>> returnPathSets(){
        return pathSets;
    }   
   
    public static void main (String [] args){
        int row = Integer.parseInt(args[0]);
        int column = Integer.parseInt(args[1]);
        Paths allPaths = new Paths(row, column);
        for (int i = 0; i < row * column; i++){            
            System.out.println(i + ": " + allPaths.pathSets.get(i));        
        }
    }
}
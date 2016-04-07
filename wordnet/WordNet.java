//Homework 1 WordNet of Algorithms II (Coursera) by Robert Sedgewick and Kevin Wayne (Princeton University)
//see http://coursera.cs.princeton.edu/algs4/assignments/wordnet.html

//the following methods are provided by the course in algs4.jar
// http://algs4.cs.princeton.edu/code/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet{

    // constructor takes the name of the two input files
    //private BinarySearchST<String, String> st = new BinarySearchST<String, String>();  // create a symbol table for word/integer pair    MUCH SLOWER TO LOAD BUT MAY NEED IT FOR LOG PERFRMANCE FOR SEARCH
    private ST<String, String> st = new ST<String, String>();  // create a symbol table for word/integer pair
    private ST<Integer, String> st_intnoun = new ST<Integer, String>(); //create a symbol table for integer/word pair    
    private Digraph G; 
    private Boolean marked[];
    private int edgeTo[];
    private int prev;   
    private SAP sap; 
    public WordNet(String synsets, String hypernyms)
    {
        In in1 = new In(synsets); // create In objects, one for synsets and one for hypernyms
        In in2 = new In(hypernyms);         
        String[] syns = (in1.readAll()).split("\n"); //read synsets as one big string, then split elements by new line 
        String[] hypers = (in2.readAll()).split("\n"); //read hypernyms as one big string, then split elements by new line
        int syns_size = syns.length;  // number of synsets
        G = new Digraph(syns_size); // create a digraph with syns_size vertices
        marked = new Boolean[syns_size];
        edgeTo = new int[syns_size];
            
        for (String s: syns) { //iterate syns and collect data for symbol table st; each s is a line
            String[] elements= s.split(",");
            String id = elements[0]; // first element is id
            marked[Integer.parseInt(id)] = false;
            edgeTo[Integer.parseInt(id)] = -10;
            st_intnoun.put(Integer.parseInt(id), elements[1]); // create an entry for each line in st_intnoun
            String[] syn_sets = elements[1].split(" "); //second element is a synset separated by space
            for (String ss: syn_sets){ // iterate each noun in a synset
                if (!st.contains(ss)) // if there is not already a key in st
                {
                    st.put(ss, id);
                }
                else
                {
                    String curr_id = st.get(ss); // get the value in st with the key ss
                    curr_id = curr_id + " " + id; // concatenate id and add a space in between
                    st.put(ss, curr_id); // update value (id) in st
                }
            }
        }

        for (String h: hypers) { //iterate hypers and collect data for digraph G
            int i = 0; //counter for iterating elements of each hypernyms entry
            String[] elems= h.split(","); // each element is separated by comma
            int tail = -1; 
            while (i < elems.length){
              if (i == 0)
              {
                tail = Integer.parseInt(elems[i]); // save the vertex for tail of edge
              }
              else
              {
                G.addEdge(tail,Integer.parseInt(elems[i])); // for each head create an edge from the tail
              }
              i++;
            }
        }
        DirectedCycle finder = new DirectedCycle(G);  //create a DirectedCycle object to check for cycle
        if (finder.hasCycle()) {
            throw new IllegalArgumentException("Cycle detected");
        }  
        prev= -1;
        for (int k=0; k < syns_size; k++)
        {
            dfs(k);            
        }        
        sap = new SAP(G); //create SAP object with G
    }
        
    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns()
    {
        return st.keys(); // guarantee no duplicate because ST has no duplicate keys    
    }
        
    // is the word a WordNet noun?
    public boolean isNoun(String word)
    {
        return st.contains(word); //need Binary search to achieve logN
    }
        
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB)
    {
        if (!(isNoun(nounA) && isNoun(nounB)))
        {
            throw new IllegalArgumentException("word not found"); 
        }
               
        String idA = st.get(nounA); //the value of st which is a string of concatentated ids
        String[] id_A = idA.split(" "); // break up idA into a string array
        Bag<Integer> id_a = new Bag<Integer>(); //create an integer bag
        for (int i=0; i< id_A.length; i++)
        {
            id_a.add(Integer.parseInt(id_A[i]));  // read the string and move it into the integer array
        }
        String idB = st.get(nounB); // repeat the above process for noun B
        String[] id_B = idB.split(" ");
        Bag<Integer> id_b = new Bag<Integer>();
        for (int i=0; i< id_B.length; i++)
        {
            id_b.add(Integer.parseInt(id_B[i]));
        }       
        
        int dist =  sap.length(id_a, id_b); //distance as defined in assignment              
        return dist;
    }
        
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB)
    {
        if (!(isNoun(nounA) && isNoun(nounB)))
        {
            throw new IllegalArgumentException("word not found"); 
        }
        String idA = st.get(nounA); //the value of st which is a string of concatentated ids
        String[] id_A = idA.split(" "); // break up idA into a string array
        Bag<Integer> id_a = new Bag<Integer>(); //create an integer bag
        for (int i=0; i< id_A.length; i++)
        {
            id_a.add(Integer.parseInt(id_A[i]));  // read the string and move it into the integer array
        }
        String idB = st.get(nounB); // repeat the above process for noun B
        String[] id_B = idB.split(" ");
        Bag<Integer> id_b = new Bag<Integer>();
        for (int i=0; i< id_B.length; i++)
        {
            id_b.add(Integer.parseInt(id_B[i]));
        }       
        
        int ances=  sap.ancestor(id_a, id_b); //distance as defined in assignment      
        return st_intnoun.get(ances);

    }
    private void dfs(int v) { // DFS for root checking 
        marked[v] = true;     // node marked visited
        Boolean branches = false; //flag for leaf(true)
        for (int w : G.adj(v)) //if there is leaf
        {
            branches = true;
            if (!marked[w]) 
            {
                edgeTo[w] = v;
                dfs(w); //recursive dfs
            }
        }
        if (!branches) //the end of trail
        {
            if (prev == -1)
            {
                prev = v; // first time 
            }
            
            else if (prev != v) //every time the trail should end at same node
            {
                throw new IllegalArgumentException("not single rooted");
            }
        }
    }
        
    // for unit testing of this class
    public static void main(String[] args)
    {    
        WordNet wn = new WordNet(args[0], args[1]); //create WordNet object with synsets.txt and hypernyms.txt as arguments        
 
        StdOut.println("Black_Plague,black_marlin");
        int a= wn.distance("Black_Plague","black_marlin");
        StdOut.println( "water,bed");
        int b= wn.distance("water","bed");
        StdOut.println("Black_Plague,black_marlin");
        int c= wn.distance("Black_Plague","black_marlin");
        StdOut.println("bed,water");
        int d= wn.distance("bed","water");
        StdOut.println("bed,water");
        int e= wn.distance("bed","water");
        StdOut.println("soda,soda");
        int f= wn.distance("soda","soda");
        StdOut.println("table,zebra");
        int g= wn.distance("table","zebra");
        StdOut.println("water,soda");
        int h= wn.distance("water","soda");
        StdOut.println("zebra, table");
        int i= wn.distance("zebra","table");
        StdOut.println("table, zebra");
        int j= wn.distance("table","zebra");
        StdOut.println(a);
        StdOut.println(b);
        StdOut.println(c);
        StdOut.println(d);
        StdOut.println(e);
        StdOut.println(f);
        StdOut.println(g);        
        StdOut.println(h);
        StdOut.println(i);
        StdOut.println(i);       
    }
}
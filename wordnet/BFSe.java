/*************************************************************************
 *  Compilation:  javac BreadthFirstDirectedPaths.java
 *  Execution:    java BreadthFirstDirectedPaths V E
 *  Dependencies: Digraph.java Queue.java Stack.java
 *
 *  Run breadth first search on a digraph.
 *  Runs in O(E + V) time.
 *
 *  % java BreadthFirstDirectedPaths tinyDG.txt 3
 *  3 to 0 (2):  3->2->0
 *  3 to 1 (3):  3->2->0->1
 *  3 to 2 (1):  3->2
 *  3 to 3 (0):  3
 *  3 to 4 (2):  3->5->4
 *  3 to 5 (1):  3->5
 *  3 to 6 (-):  not connected
 *  3 to 7 (-):  not connected
 *  3 to 8 (-):  not connected
 *  3 to 9 (-):  not connected
 *  3 to 10 (-):  not connected
 *  3 to 11 (-):  not connected
 *  3 to 12 (-):  not connected
 *
 *************************************************************************/

/**
 *  The <tt>BreadthDirectedFirstPaths</tt> class represents a data type for finding
 *  shortest paths (number of edges) from a source vertex <em>s</em>
 *  (or set of source vertices) to every other vertex in the digraph.
 *  <p>
 *  This implementation uses breadth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space (not including the digraph) proportional to <em>V</em>.
 *  <p>
 *  For additional documentation, see <a href="/algs4/41graph">Section 4.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
//This version is modified from the original

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;


public class BFSe {
    private static final int INFINITY = Integer.MAX_VALUE;
    private boolean[] marked;  // marked[v] = is there an s->v path?
    private int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
    private int[] distTo;      // distTo[v] = length of shortest s->v path
    private boolean[] marked2;  // marked[v] = is there an s->v path?
    private int[] edgeTo2;      // edgeTo[v] = last edge on shortest s->v path
    private int[] distTo2;      // distTo[v] = length of shortest s->v path
    private int min_sap = Integer.MAX_VALUE;
    private int ancestor = -1;
    private Boolean initialize = false;
    private Queue<Integer>moved;
    private Queue<Integer>moved2;
    private Digraph g;
  
    public BFSe(Digraph G) {
        g = G;
        marked = new boolean[G.V()];
        distTo = new int[G.V()];
        edgeTo = new int[G.V()];
        moved = new Queue<Integer>();
        for (int v = 0; v < G.V(); v++) {distTo[v] = INFINITY;}
        marked2 = new boolean[G.V()];
        distTo2 = new int[G.V()];
        edgeTo2 = new int[G.V()];
        moved2 = new Queue<Integer>();
        for (int v = 0; v < G.V(); v++) {distTo2[v] = INFINITY;}
    }
    
    private void reinitialize(){
          for(int s:moved)
          {
              marked[s] = false;
              distTo[s] = INFINITY;
              edgeTo[s] = -10; //0
          }
          for(int s:moved2)
          {
              marked2[s] = false;
              distTo2[s] = INFINITY;
              edgeTo2[s] = -10; //0
          }
          ancestor = -1;
          min_sap = INFINITY;
          moved = new Queue<Integer>(); 
          moved2 = new Queue<Integer>();               
    }
     

    // BFS from multiple sources (modified for two vertex concurrent BFS for SAP search)
    public void bfs(int s1, int s2) {
        if(initialize)
        {
           reinitialize();
        }
        Queue<Integer> q = new Queue<Integer>();  // queue for BFS
        
        marked[s1] = true; //s2 initialization
        distTo[s1] = 0;
        moved.enqueue(s1);        
        q.enqueue(s1);
        
        while (!q.isEmpty()) {
            int v = q.dequeue(); //take first of line off queue
            moved.enqueue(v); // put v into moved queue
            for (int w : g.adj(v)) { // for each outdegree
                if (!marked[w])    //if it hasn't been reached 
                {   
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1; // add one to the path to v
                    marked[w] = true; //reached
                    moved.enqueue(w);
                    q.enqueue(w);
                }
            }
        }

        marked2[s2] = true;  //s1 initialization
        distTo2[s2] = 0;
        moved2.enqueue(s2);
        q.enqueue(s2);
        
        
        while (!q.isEmpty()) {
            int v = q.dequeue(); //take first of line off queue
            moved2.enqueue(v); // put v into moved queue
            if (distTo2[v] > min_sap) //no need to continue if distance to the vertice is longer than current min_sap
            { 
              initialize = true;
              break;
            }
            if (marked[v]){
                int sap = distTo[v]+distTo2[v];
                if (sap < min_sap){
                    ancestor = v;
                    min_sap=sap;
                }
            }
            for (int w : g.adj(v)) { // for each outdegree
                if (!marked2[w])    //if it hasn't been reached 
                {   
                    edgeTo2[w] = v;
                    distTo2[w] = distTo2[v] + 1; // add one to the path to v
                    marked2[w] = true; //reached
                    moved2.enqueue(w);
                    q.enqueue(w);
                }
                if (marked[w])
                {
                    int sap = distTo[w]+distTo2[v]+1;
                    if (sap < min_sap){
                        ancestor = w;
                        min_sap=sap;
                    }
                }
                
            }
        }
        if (s1==s2){min_sap = 0; ancestor = s1;}
        if (min_sap== INFINITY){min_sap = -1;}
        initialize = true;
    }

    /**
     * Is there a directed path from the source <tt>s</tt> (or sources) to vertex <tt>v</tt>?
     * @param v the vertex
     * @return <tt>true</tt> if there is a directed path, <tt>false</tt> otherwise
     */
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    /**
     * Returns the number of edges in a shortest path from the source <tt>s</tt>
     * (or sources) to vertex <tt>v</tt>?
     * @param v the vertex
     * @return the number of edges in a shortest path
     */
    public int distTo(int v) {
        return distTo[v];
    }

    public int min_sap() { //return min_sap
        return min_sap;
    }
    
     public int ancestor() { //return ancestor
        return ancestor;
    }
    
    /**
     * Returns a shortest path from <tt>s</tt> (or sources) to <tt>v</tt>, or
     * <tt>null</tt> if no such path.
     * @param v the vertex
     * @return the sequence of vertices on a shortest path, as an Iterable
     */
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Integer> path = new Stack<Integer>();
        int x;
        for (x = v; distTo[x] != 0; x = edgeTo[x])
            path.push(x);
        path.push(x);
        return path;
    }

    /**
     * Unit tests the <tt>BreadthFirstDirectedPaths</tt> data type.
     */
  
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        StdOut.println(G);
        BFSe bfs = new BFSe(G);
        bfs.bfs(6,9);

        StdOut.println(bfs.ancestor());
        StdOut.println(bfs.min_sap());
        bfs.bfs(9,6);

        StdOut.println(bfs.ancestor());
        StdOut.println(bfs.min_sap());
        }
    }
  





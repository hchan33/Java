import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
public class SAP{
    
    private Digraph g;
    private int ancestor = -1; // for ancestor method; calculated from distance 
    private int length = -1; //for ancestor method; calculated from distance
    private int ancestor_after_length = -1; // for length method
    private BFSe bfse;// modified BFS constructor
    private static final int INFINITY = Integer.MAX_VALUE;
    
    private void setAncestor(int d)
    {
        ancestor = d;
    }
    private int getAncestor()
    {
        return ancestor;
    }
     private void setLength(int d)
    {
        length = d;
    }
    private int getLength()
    {
        return length;
    }
    private void setAncestor_after_length(int d)
    {
        ancestor_after_length = d;
    }
    private int getAncestor_after_length()
    {
        return ancestor_after_length;
    }
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G)
    {
        g = new Digraph(G);
        bfse = new BFSe(g);
    }
    
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w)
    {
        if (v < 0 || w < 0 || v > g.V() - 1 || w > g.V() - 1)
        {
            throw new IndexOutOfBoundsException("v or w not valid!");
        }

        setAncestor_after_length(-1); //reset variable 
        bfse.bfs(v,w);
        setAncestor_after_length(bfse.ancestor());
        return bfse.min_sap();
      
    }
    
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w)
    {
        if (v < 0 || w < 0 || v > g.V() - 1 || w > g.V() - 1)
        {
            throw new IndexOutOfBoundsException("v or w not valid!");
        }
      
        setAncestor_after_length(-1); //reset variable 
        length(v,w);
        return getAncestor_after_length();
    }
    
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        if (w == null || v == null){throw new NullPointerException("Null Argument!");}
        setLength(INFINITY); // reset length and ancestor
        setAncestor(-1);
        
        for(int vv: v) 
        {
            for(int ww:w)
            {
                if (vv <0 || ww<0 || vv>g.V()-1 || ww>g.V()-1)
                { 
                    throw new IndexOutOfBoundsException("v or w not valid!");
                }
                int len = length(vv,ww);    //run len(int v, int w) on each pair of v and w
                if (len != -1){
                    if (len < getLength())
                    {
                        setLength(len);
                        setAncestor(getAncestor_after_length());
                    }
                }
            }    
        }
        if (getLength() == INFINITY)
        {
            return -1;
        }
        else {return getLength();}
 
    }
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        setLength(INFINITY); // reset length and ancestor to default
        setAncestor(-1);
        if (length(v,w) == -1) //ancestor and length will be set after this
        {
            return -1;
        }
        else
        {
            return getAncestor();
        }
    
    }
    
    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
          int v = StdIn.readInt();
          int w = StdIn.readInt();
          int length   = sap.length(v, w);
          int ancestor = sap.ancestor(v, w);
          StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);        
          }
    }
}
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.DirectedCycle;

public class Outcast{
    // constructor takes a WordNet object
    private WordNet wn;
    public Outcast(WordNet wordnet)
    {
        wn = wordnet;
    }
        
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns)
    {
        int max = 0;
        String answer = "";    
        for (String s: nouns)
        {
            int dist = 0;
            for (String t:nouns)
            {
                int dis = wn.distance(s,t);
                dist = dist + dis;
            }

            if (dist > max)
            {
                 max = dist;
                 answer = s;
            } 
        }
        return answer;
    }
        
    // for unit testing of this class (such as the one below)
    public static void main(String[] args)
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

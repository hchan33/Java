//Homework 3 Baseball Elimiation of Algorithms II (Coursera) by Robert Sedgewick and Kevin Wayne (Princeton University)
//see http://www.cs.princeton.edu/courses/archive/spring04/cos226/assignments/baseball.html

//the following methods are provided by the course in algs4.jar
// http://algs4.cs.princeton.edu/code/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;

public class BaseballElimination                   // create a baseball division from given filename in format specified below
{
    
    private int team_num ; // number of teams
    private int pair_num; // number of pair nodes
    private int[][] g;// game between i and j
    private int[] w ; //number of wins
    private int[] l; // number of losses
    private int[] r; // number of remaining games;
    private int s = 0; //vertice number for s
    private int t; // vertice number for t 
    private Queue<String> teams= new Queue<String>(); //names of teams
    private Queue<String> trivials= new Queue<String>(); //names of teams trivially eliminated
    private ST<String, Integer> map = new ST<String, Integer>();
    private ST<Integer, String> rmap = new ST<Integer, String>();
    private ST<Integer, Integer> teammap = new ST<Integer,Integer>(); // use in FlowNetwork
    private int v; // number of vertices
    private int G = 0; //initial flow: total number of remaining games
    
    
    // vertice numbers:
    // 0 -> s 
    // 1 ... (nC2) --> pair nodes
    // (nC2)+1 ... (nC2)+team_num-1 --> team nodes
    // v-1 --> t
    
    public BaseballElimination(String filename)
    {
        In in = new In(filename); // create In object to read file
        String[] lines = (in.readAll()).split("\n"); //read file as one big string, then split elements by new line 
        int line_num = lines.length;  // number of lines in the file
        for (int i = 0 ; i < line_num; i++) //iterate the lines
         {
             String trimmed = lines[i].trim();
             if (i == 0)  //first line is number of teams
             {
                 team_num = Integer.parseInt(trimmed);
                 w = new int[team_num]; //initialization
                 l = new int[team_num];
                 r = new int[team_num];
                 g = new int[team_num][team_num];
                 pair_num = (team_num - 1)*(team_num - 2)/2;    
                 v = 1 + 1 + pair_num + (team_num - 1);  // s + t + number of games between any two teams (excluding team queried) + number of teams excluding team --> number of vertices
                 t = v - 1; // code for the t vertice (s is 0)
                     
             }
             else //after first line, each line is one team record
             {
                 String delims = "[ ]+"; // each item separated by one or more space
                 String[] each_line = trimmed.split(delims); // split elements in a line by space 
                 for (int j = 0; j < each_line.length; j++)
                 {
                     if (j == 0) // first element is name
                     {
                         teams.enqueue(each_line[j].trim()); //put team name in Queue teams 
                         map.put(each_line[j].trim(),i - 1); // create a name, code symbol table; team number starts from 0
                         rmap.put(i - 1, each_line[j]); // create a code, name ST symbol table
                     }
                     else if (j == 1) 
                     {
                         w[i-1] = Integer.parseInt(each_line[j].trim()); // game won for a team
                     }
                     else if (j == 2)
                     {
                         l[i-1] = Integer.parseInt(each_line[j].trim()); //game lost for a team
                     }
                     else if (j == 3)
                     {
                         r[i-1] = Integer.parseInt(each_line[j].trim()); // number of remaining games for a team
                     }
                     else 
                     {
                         g[i-1][j-4] = Integer.parseInt(each_line[j].trim());   //number of games left between these two teams
                         G += g[i-1][j-4];
                     }
                 }
             }
         }
       
        G = G/2; //adjusted for double counting
    }
   
          
    public  int numberOfTeams()     // number of teams
    {
        return team_num;
    }         
    
    public Iterable<String> teams()                             // all teams
    {
        return teams;
    }
    
    public int wins(String team)                      // number of wins for given team
    {
       if (!map.contains(team)){throw new java.lang.IllegalArgumentException("invalid argument");}
       return w[map.get(team)];
    }

    public int losses(String team)                   // number of losses for given team
    {
        if (!map.contains(team)){throw new java.lang.IllegalArgumentException("invalid argument");}
        return l[map.get(team)];
    }
    
    public  int remaining(String team)                // number of remaining games for given team
    {
         if (!map.contains(team)){throw new java.lang.IllegalArgumentException("invalid argument");}
         return r[map.get(team)];
    }
    
    public int against(String team1, String team2)    // number of remaining games between team1 and team2
    {
        if (!map.contains(team1) || !map.contains(team2)){throw new java.lang.IllegalArgumentException("invalid argument");}
        return g[map.get(team1)][map.get(team2)];
    }

    public  boolean isEliminated(String team)             // is given team eliminated?        
    {
        if (!map.contains(team)){throw new java.lang.IllegalArgumentException("invalid argument");}
        if (trivial_eliminated(team)){return true;}
        
        int target = map.get(team);        
        FlowNetwork fn = buildFN(team);
        FordFulkerson fk = new FordFulkerson(fn,s,t);
        Queue<Integer> queue = new Queue<Integer>();        
                
        for (int i = 1 + pair_num; i <= pair_num + team_num; i++)
        {
            if (i != target)
            {                  
                if (fk.inCut(i))
                {
                    queue.enqueue(i);
                }
            }
        }
        if (queue.size() == 0){return false;}
        else {return true;}
    }
    
    private boolean trivial_eliminated(String team)
    {
        boolean eliminated = false;
        trivials = new Queue<String>(); 
        for (int i = 0; i < team_num; i++)
        {
            if (w[map.get(team)] + r[map.get(team)] < w[i])
            {
                eliminated = true;
                trivials.enqueue(rmap.get(i));                
            }
        }
        if (eliminated){StdOut.println("trivial: "+" "+team);}
        return eliminated;
    }
    
    private FlowNetwork buildFN(String team)
    {   //first create FlowNetwork fn
        FlowNetwork fn = new FlowNetwork(v); // create empty FlowNetwork with v vertices 
        int target = map.get(team);
        int k = 1 ; // counter for pair vertices
        int counter = 1;
        int tc = pair_num + 1;
        int[] teams = new int[team_num];
        for (int z = 0; z < team_num; z++){
            if (z != target){
                teams[z] = tc;
                tc++;
            }
        }
            
        for (int i = 0; i < team_num; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (i!= target && j!= target) // target not in team or pair vertices
                {
                        int tail = 0;
                        int head = k;
                        double capacity = g[j][i];
                        int flow = 0;
                        FlowEdge edge = new FlowEdge(tail, head, capacity, flow); 
                        fn.addEdge(edge);//add edge from s to pair node
                                               
                        tail = head;
                        int h = teams[j] ; // team node number 
                        capacity = Double.POSITIVE_INFINITY;
                        edge = new FlowEdge(tail, h, capacity, flow);
                        fn.addEdge(edge);  // add edge from pair node to one team node
                        
                        tail = head;
                        int r = teams[i]; //team node number 
                        
                        edge = new FlowEdge(tail, r, capacity, flow);
                        fn.addEdge(edge);  // add edge from pair node to the other team node
                                               
                        k++;                                                                      
                }
            }
            if (i != target)
            {
                int tail = counter + pair_num;                
                counter++;
                if (t != tail){
                    int capacity = w[target] + r[target] - w[i];                    
                    int flow = 0;
                    FlowEdge edge = new FlowEdge(tail, t, capacity, flow);
                    teammap.put(tail, i);
                    fn.addEdge(edge);  // add edge from pair node to the other team node     
                }
            }            
        }
        return fn;
    }
        
    public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated
    {
        if (!map.contains(team)){throw new java.lang.IllegalArgumentException("invalid argument");}
        int target = map.get(team);
        if (trivial_eliminated(team))
        {
            return trivials;
        }
        Queue <String> queue = new Queue<String>();
        FlowNetwork fn = buildFN(team);
        FordFulkerson fk = new FordFulkerson(fn,s,t);     
        for (int i = 1 + pair_num; i < pair_num+team_num; i++)
        {
            if (teammap.get(i) != target)
            {                  
                if (fk.inCut(i))
                    queue.enqueue(rmap.get(teammap.get(i)));
                }
            }
        
        if (queue.size() ==0){return null;}
        else{return queue;}
    }
        
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String ttt : division.certificateOfElimination(team))
                    StdOut.print(ttt + " ");
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
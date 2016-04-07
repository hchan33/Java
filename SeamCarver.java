//Homework 2 Seam Carving of Algorithms II (Coursera) by Robert Sedgewick and Kevin Wayne (Princeton University)
//see http://coursera.cs.princeton.edu/algs4/assignments/seamCarving.html

//the following methods are provided by the course in algs4.jar
// http://algs4.cs.princeton.edu/code/


import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
  
   private int h; //current height (non-transposed)
   private int w; //current weight (non_transposed)
   private int[][] grid;
   private boolean transposed = false; // whether energy 2d array has been transposed
   private boolean horizontal = false; // whether horizontal or vertical seam is sought
   private boolean call = false;
 
   public SeamCarver(Picture picture){
       h = picture.height();
       w = picture.width();
       grid = new int[h][w]; // color grid initialization
       for (int c = 0; c < w; c++){
           for (int r = 0; r < h; r++){
               grid[r][c]  = picture.get(c,r).getRGB(); // fill matrix with colors
           }
       }
   }
     
   public Picture picture(){                       // current picture     
       Picture new_p = new Picture(w,h);
       if (transposed){         
         transpose();}
    
       for (int c = 0; c < w; c++){         
           for (int r = 0; r < h; r++){
               Color ccc = new Color(grid[r][c]);
               new_p.set(c,r, ccc);
           }
       }  
       return new_p;           
   }
  
   public int width(){                         // width of current picture
       return w;
   }
   
   public int height(){                        // height of current picture   
       return h;
   }
   
   public double energy(int x, int y){            // energy of pixel at column x and row y
       int i = 0; int j = 0; // i/j are conventions here for column/row counters for CURRENT orientation
       if (call){
           if (transposed){i = h; j = w;}
           else {i= w; j = h;}
       }
       else{
           i = w; j = h;
           if(transposed){
               transpose();
           }
       }
       if (x < 0 || y < 0 || x > i - 1 || y > j - 1){throw new java.lang.IndexOutOfBoundsException("parameter out of bound");}
       int c = x; // current orientation
       int r = y;
     
       if (r == 0 || r == j - 1 || c == 0 || c == i - 1){return 1000;} // at border same energy
       else{

           Color l  = new Color(grid[r][c-1]); 
           Color ri  = new Color(grid[r][c+1]); 
           Color t  = new Color(grid[r-1][c]); 
           Color b  = new Color(grid[r+1][c]);       
 
           double x_grad = Math.pow((l.getRed() - ri.getRed()),2) + Math.pow((l.getGreen() - ri.getGreen()),2) + Math.pow((l.getBlue() - ri.getBlue()),2);
           double y_grad = Math.pow((t.getRed() - b.getRed()),2) + Math.pow((t.getGreen() - b.getGreen()),2) + Math.pow((t.getBlue() - b.getBlue()),2);
           return Math.sqrt(x_grad + y_grad);                                  
       }     
   }
   
   public int[] findHorizontalSeam(){            // sequence of indices for horizontal seam 
       horizontal = true;  //indicate horizontal operation
       if (!transposed){transpose();} // transpose to use findVerticalSeam
       return findVerticalSeam();
   }
   
   public int[] findVerticalSeam(){              // sequence of indices for vertical seam  
              
       class Innerclass{       
           private coord[][] edgeTo; // for dfs
           private double[][] distTo; // for dfs       
       
           public int[] infv(){           
               double min_energy = Double.MAX_VALUE; // total energy of the minimum path
               if (!horizontal && transposed){transpose();} // need original orientation to use findVerticalSeam
               int i = 0 ; int j = 0;
               if (transposed){i = h; j = w;}
               else {i = w; j = h;}
               int[] a = new int[j];  // return array
               edgeTo = new coord[j][i]; // arrays for last edge to row j column i
               distTo = new double[j][i]; // arrays for distance from source to row j column i  
               
               for (int y = 0 ; y < j ; y++){  //check each column for shortest path
                 for (int x = 0; x < i; x++){
                   if (y == 0){distTo[y][x] = energy(x,y);}
                   else{distTo[y][x] = Double.POSITIVE_INFINITY;}
                 }
               }
               
               for (int y = 0 ; y < j ; y++){  //check each column for shortest path
                 for (int x = 0; x < i; x++){
                   if (y != j - 1){   // if this row is not bottom
                     relax(x, y, x, y + 1);  // three possible dfs positions
                     if (x - 1 >= 0){ // if x-1 not beyond border 
                       relax(x, y, x - 1, y + 1);}
                     if(x + 1 <= i - 1){ //if x+1 not beyond border
                       relax(x, y, x + 1, y + 1);}
                   }
                 }
               }
               int k = 0; //indicator of the end of which seam has the least energy
               for (int x2 = 0; x2 < i; x2++){ //check each bottom  
                 if (min_energy > distTo[j-1][x2]){
                   min_energy = distTo[j-1][x2];
                   
                   k = x2; // store the x of end of min_energy seam at k
                   a[j-1] = k; // store bottom of min_energy seam at a[j-1]
                   int l = j - 1; // counter for retracing seam from end
                   
                   while (l > 0){
                     k = edgeTo[l][k].x;  // k now becomes the x of the seam one step back
                     a[--l] = k; // store k at a array at location l-1
                   } 
                 }
               }
               return a;
           }


           private void relax(int x1, int y1, int x2, int y2){ //(x1,y1) is tail, (x2,y2) is head
               double a = energy(x2,y2);               
               if (distTo[y2][x2] > distTo[y1][x1] + a){
                   distTo[y2][x2] = distTo[y1][x1] + a;
                   edgeTo[y2][x2] = new coord(x1,y1);
               }
           } 
       
       }
       call = true;
       Innerclass il = new Innerclass();
       int[] result = il.infv();
       call = false;
       horizontal = false;
       return result;
   }
 
    private class coord{ // private class to store coordinate
        public int x;
        public int y;
        public coord(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
      
   public void removeHorizontalSeam(int[] a)   // remove horizontal seam from picture
   {
       horizontal = true;
       if (!transposed){transpose();}
       if (width() != a.length) {StdOut.println(width() + " " + a + " " + " " + "horizontal: " + horizontal);throw new java.lang.IllegalArgumentException("a's length not equal to width"); }
       removeVerticalSeam(a);
   }
  
   public void removeVerticalSeam(int[] a)     // remove vertical seam from picture
   {    
       if (height() <= 1 &&  horizontal) {throw new java.lang.IllegalArgumentException("height/width less than or equal to 1"); }
       if (width() <= 1  && !horizontal) {throw new java.lang.IllegalArgumentException("height/width less than or equal to 1"); }
       if (!horizontal && transposed){transpose();} //transpose if wrong orientation is used
       if (!horizontal && height() != a.length) {throw new java.lang.IllegalArgumentException("a's length not equal to height"); }
       int i = 0; int j = 0; //column/row counter of CURRENT orientation
       if (transposed){i = h; j = w;}
       else {i = w; j = h;} 
       for (int y = 0; y < j; y++)
       {
           int x = a[y]; //tells which x in each line y should be eliminated
           if (y>0)
           {
               int prev = a[y-1];
               if (Math.abs(prev - a[y]) > 1)
               {
                     throw new java.lang.IllegalArgumentException("seam is off");
               } 
           }
           
           if (x < 0|| x > i - 1 ){throw new java.lang.IllegalArgumentException("x out of bound");}
           int [] backend = new int[i-x-1]; //e.g. 100 columns, column 50 to be removed; size of backend will be 100-50-1 = 49
           System.arraycopy(grid[y], x + 1, backend, 0, i - x - 1); // basically, move the backend unaffected to backend (temp array)
           System.arraycopy(backend, 0 , grid[y] , x, i - x - 1); // move backend array to the spot where seam is removed
       }

       if (horizontal){h--;} // so now height will be reduced by 1
       else {w--;}
       horizontal = false; //reset default
              
   }
   
   private void transpose()
   {
       int i = 0; int j = 0; // i/j are column/row counters from CURRENT orientation
       if (transposed){i = h; j = w; transposed = false;} // transposed is set to status after transpose
       else           {i = w; j = h; transposed = true; }   
       int[][] o_grid = new int[i][j];
       for (int c = 0; c < i; c++) 
         {
             for (int r = 0; r < j; r++)
             {
                 o_grid[c][r] = grid[r][c];
             }
         }
     grid = o_grid;     
  }
   
    public static void main(String[] args) {
        Picture pp = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(pp);

        StdOut.println("1:");
        int[] a = sc.findHorizontalSeam();
        StdOut.println(sc.width());
        StdOut.println(sc.height());
        
        StdOut.println("2:");
        sc.removeHorizontalSeam(a);        
        StdOut.println(sc.width());
        StdOut.println(sc.height());
      
    }
}
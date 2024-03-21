import java.util.*;

public class Sudoku {
    /**
     * Returns the filled in sudoku grid.
     *
     * @param grid the partially filled in grid. unfilled positions are -1.
     * @return the fully filled sudoku grid.
     */
    public static int[][] solve(int[][] grid) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        int n = grid.length;
        int sqr = (int)Math.sqrt(n);
        Set<Integer> domain = new HashSet<>();
        for(int i=1; i<=n; i++){
            domain.add(i);
        }
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                
                List<Integer> d = new ArrayList<>();
                Set<Integer> affects = new HashSet<>();
                if(grid[i][j] != -1){
                    d.add(grid[i][j]);
                } else {
                    Set<Integer> tmpd = new HashSet<>();
                    tmpd.addAll(domain);
                    for(int k=0; k<n; k++){
                        if(grid[i][k] != -1){
                            tmpd.remove(grid[i][k]);
                        } else {
                            if(k!=j)
                                affects.add(i*n + k);
                        }
                        if(grid[k][j] != -1){
                            tmpd.remove(grid[k][j]);
                        } else {
                            if(k!=i)
                                affects.add(k*n + j);
                        }
                    }
                    int si = i/sqr;
                    si*=sqr;
                    int sj = j/sqr;
                    sj*=sqr;
                    // System.out.println(i/sqr);
                    // System.out.println(j/sqr);
                    for(int k = si; k<si+sqr; k++){
                        for(int l = sj; l<sj+sqr; l++){
                            if(grid[k][l] != -1){
                                tmpd.remove(grid[k][l]);
                            } else { // if grid kl == -1 then affects
                                if(k!=i || l!=j)
                                    affects.add(k*n + l);
                            }
                        }
                    }
                    d.addAll(tmpd);
                    // System.out.println(d.toString());
                }
                List<Integer> aff = new ArrayList<>(affects);
                Solver.Variable newVar = new Solver.Variable(d,-1, i*n+j, aff);
                newVar.variablesLength = sqr;
                variables.add(newVar);
                constraints.add(new Solver.SudokuConstraint(newVar));
            }
        }


        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        int[] result = solver.findOneSolution();

        
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                grid[i][j] = result[i*n+j];
            }
        }
        return grid;
    }
}

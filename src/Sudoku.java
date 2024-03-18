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
        List<Integer> domain = new ArrayList<>();
        for(int i=1; i<=n; i++){
            domain.add(i);
        }
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                List<Integer> d = new ArrayList<>();
                if(grid[i][j] == -1){
                    d.addAll(domain);
                } else {
                    d.add(grid[i][j]);
                }
                
                Solver.Variable newVar = new Solver.Variable(d,-1, i*n+j);
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

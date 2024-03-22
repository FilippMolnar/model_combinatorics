import java.util.*;

public class NQueens {
    /**
     * Returns the number of N-Queen solutions
     */
    public static int getNQueenSolutions(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        // For each row ...
        for(int i=0; i<n; i++){
            List<Integer> domain = new ArrayList<>();
            // Add all columns to domain
            for(int j=0; j<n; j++){
                // SYMMETRY BREAKING CONSTRAINT: Only look at half of the Queen positions in the first row, then add the symmetric solutions at the end.
                if((i != 0) || (j <= (n-1)/2)){
                    domain.add(j);
                }
            }
            List<Integer> affects = new ArrayList<>();
            for(int j=0; j<n; j++){
                if(i==j) continue;
                affects.add(j);
            }
            Solver.Variable newVar = new Solver.Variable(domain,-1, i, affects);
            variables.add(newVar);
            constraints.add(new Solver.NQueensConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        // SYMMETRY BREAKING CONSTRAINT: Adding symmetric solutions.
        List<int[]> newResult = new LinkedList<>();
        for(int[] sol : result){
            newResult.add(sol);
            if(sol[0] != n/2){
                int[] mirror = new int[n];
                for (int i = 0; i < n; i++) {
                    mirror[i] = n - 1 - sol[i];
                }
                newResult.add(mirror);
            }
        }

        return newResult.size();
    }
}

import java.util.*;

public class NQueens {
    /**
     * Returns the number of N-Queen solutions
     */
    public static int getNQueenSolutions(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        for(int i=0; i<n; i++){
            List<Integer> domain = new ArrayList<>();
            for(int j=0; j<=n; j++){
                domain.add(j);
            }
            Solver.Variable newVar = new Solver.Variable(domain,-1, i);
            variables.add(newVar);
            // ADD COLUMN AND DIAGONAL CONSTRAINTS
            // ADD SYMMETRY BREAKING CONSTRAINT - ALWAYS GO FROM TOP TO BOTTOM ROW
            // constraints.add(new Solver.SubsetsConstraint(newVar));
        }
        // TODO: add your constraints

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        // TODO: use result to construct answer
        return -1;
    }
}

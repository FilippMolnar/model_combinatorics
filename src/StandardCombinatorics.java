import java.util.*;

public class StandardCombinatorics {
    /**
     * Returns a list of all binary strings of length n
     */
    public static List<String> getBinaryStrings(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        for(int i=0; i<n; i++){
            Solver.Variable newVar = new Solver.Variable(Arrays.asList(0,1),-1, -1);
            variables.add(newVar);
            constraints.add(new Solver.NoConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();
        
        List<String> ret = new ArrayList<>();
        for(int[] sol : result){
            String s = "";
            for(int i=0; i<sol.length; i++){
                s += sol[i] == 0 ? "0" : "1";
            }
            ret.add(s);
        }
        return ret;
    }

    /**
     * Returns a list of all combinations of k elements from the set {1,...,n} without repetitions
     */
    public static List<int[]> getCombinationsWithoutRepetition(int n, int k) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        // Add variables and constraints
        for(int i=0; i<k; i++){
            List<Integer> v = new ArrayList<>();
            for(int j=0; j<n; j++){
                v.add(j+1);
            }
            Solver.Variable newVar = new Solver.Variable(v,-1, -1);
            variables.add(newVar);
            constraints.add(new Solver.CombinationsWithoutRepetitionConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        return result;
    }

    /**
     * Returns a list of all combinations of k elements from the set {1,...,n} with repetitions
     */
    public static List<int[]> getCombinationsWithRepetition(int n, int k) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        for(int i=0; i<k; i++){
            List<Integer> v = new ArrayList<>();
            for(int j=0; j<n; j++){
                v.add(j+1);
            }
            Solver.Variable newVar = new Solver.Variable(v,-1, -1);
            variables.add(newVar);
            constraints.add(new Solver.CombinationsWithRepetitionConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        return result;
    }

    /**
     * Returns a list of all subsets in the set {1,...,n}
     */
    public static List<int[]> getSubsets(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        for(int i=0; i<n; i++){
            Solver.Variable newVar = new Solver.Variable(Arrays.asList(0,1),-1, -1);
            variables.add(newVar);
            constraints.add(new Solver.NoConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        List<int[]> ret = new ArrayList<>();
        for(int[] sol : result){
            List<Integer> l = new ArrayList<>();
            for(int i=0; i<sol.length; i++){
                if(sol[i] == 1){
                    l.add(i+1);
                }
            }
            int[] arr = new int[l.size()];
            for (int i = 0; i < l.size(); i++) {
                arr[i] = l.get(i);
            }
            ret.add(arr);
        }
        return ret;



        // // Initialize lists for variables and constraints
        // List<Solver.Variable> variables = new ArrayList<>();
        // List<Solver.Constraint> constraints = new ArrayList<>();

        // for(int i=0; i<n; i++){
        //     List<Integer> v = new ArrayList<>();
        //     for(int j=0; j<=n; j++){
        //         v.add(j);
        //     }
        //     Solver.Variable newVar = new Solver.Variable(v,-1, -1);
        //     variables.add(newVar);
        //     constraints.add(new Solver.SubsetsConstraint(newVar));
        // }

        // // Convert to arrays
        // Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        // variablesArray = variables.toArray(variablesArray);
        // Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        // constraintsArray = constraints.toArray(constraintsArray);

        // // Use solver
        // Solver solver = new Solver(variablesArray, constraintsArray);
        // List<int[]> result = solver.findAllSolutions();

        // List<int[]> ret = new ArrayList<>();
        // for(int[] sol : result){
        //     int k = 0;
        //     for(int i=0; i<sol.length; i++){
        //         if(sol[i] != 0) k++;
        //     }
        //     int[] subset = new int[k];
        //     k=0;
        //     for(int i=0; i<sol.length; i++){
        //         if(sol[i] != 0){
        //             subset[k++] = sol[i];
        //         }
        //     }
        //     ret.add(subset);
        // }
        // return ret;
    }

    /**
     * Returns a list of all permutations in the set {1,...,n}
     */
    public static List<int[]> getSetPermutations(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        for(int i=0; i<n; i++){
            List<Integer> v = new ArrayList<>();
            for(int j=0; j<n; j++){
                v.add(j+1);
            }
            Solver.Variable newVar = new Solver.Variable(v,-1, -1);
            variables.add(newVar);
            constraints.add(new Solver.PermutationsConstraint(newVar));
        }

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        return result;
    }
}

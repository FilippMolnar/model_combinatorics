import java.util.*;

class Solver {

    // Example implementation of the Constraint interface.
    // It enforces that for given variable X, it holds that 5 < X < 10.
    //
    // This particular constraint will most likely not be very useful to you...
    // Remove it and design a few constraints that *can* help you!
    static abstract class BetweenFiveAndTenConstraint {
        Variable var;

        public BetweenFiveAndTenConstraint(Variable var) {
            this.var = var;
        }

        void infer() {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (5 < x && x < 10)
                    newDomain.add(x);
            }

            this.var.domain = newDomain;
        }
    }

    static class CombinationsWithoutRepetitionConstraint extends Constraint{
        Variable var;

        public CombinationsWithoutRepetitionConstraint(Variable var) {
            this.var = var;
        }

        void infer(Variable previousVariable) {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (x > previousVariable.value)
                    newDomain.add(x);
            }

            this.var.domain = newDomain;
        }
    }

    static class NoConstraint extends Constraint{
        Variable var;

        public NoConstraint(Variable var) {
            this.var = var;
        }

        void infer(Variable previousVariable) {
            
        }
    }

    static class Variable {
        List<Integer> domain;
        int index;
        int value;

        public Variable(List<Integer> domain, int value, int index) {
            this.domain = domain;
            this.index = index;
            this.value = value;
        }
    }

    static abstract class Constraint {
        /**
         * Tries to reduce the domain of the variables associated to this constraint, using inference
         */
        abstract void infer(Variable variable);
    }


    Variable[] variables;
    Variable[] propagatedVariables;
    Constraint[] constraints;
    List<int[]> solutions;
    // you can add more attributes

    /**
     * Constructs a solver.
     * @param variables The variables in the problem
     * @param constraints The constraints applied to the variables
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        this.variables = variables;
        this.propagatedVariables = variables.clone();

        this.constraints = constraints;

        solutions = new LinkedList<>();
    }

    void propagate(Variable variable, int index){
        for(int i=index; i<variables.length; i++){
            constraints[i].infer(variable);
        }
    }

    int[] convertToArray(Deque<Integer> tmp_sol){
        int n = tmp_sol.size();
        int[] ret = new int[n];
        int i=0;
        for(int el:tmp_sol){
            ret[i++] = el;
        }
        return ret;
    }

    /**
     * Solves the problem using search and inference.
     */
    void search(boolean findAllSolutions /* you can add more params */) {
        Deque<Integer> tmp_sol = new LinkedList<>();
        Deque<Variable> assStack = new LinkedList<>();
        
        for(int i=0; i<variables[0].domain.size(); i++){
            assStack.addLast(new Variable(variables[0].domain, variables[0].domain.get(i), 0));
        }
        while(!assStack.isEmpty()){
            Variable var = assStack.removeLast();
            while(tmp_sol.size() > var.index) tmp_sol.removeLast();
            
            tmp_sol.addLast(var.value);
            System.out.println(tmp_sol);
            int size = tmp_sol.size();
            if(size == variables.length){
                solutions.add(convertToArray(tmp_sol));
                tmp_sol.removeLast();
                continue;
            }
            propagate(var, tmp_sol.size()-1);
            for(int i=0; i<variables[size].domain.size(); i++){
                assStack.addLast(new Variable(variables[size].domain, variables[size].domain.get(i), size));
            }

        }
        

    }


    /**
     * Searches for one solution that satisfies the constraints.
     * @return The solution if it exists, else null
     */
    int[] findOneSolution() {
        solve(false);

        return !solutions.isEmpty() ? solutions.get(0) : null;
    }

    /**
     * Searches for all solutions that satisfy the constraints.
     * @return The solution if it exists, else null
     */
    List<int[]> findAllSolutions() {
        solve(true);

        return solutions;
    }

    /**
     * Main method for solving the problem.
     * @param findAllSolutions Whether the solver should return just one solution, or all solutions
     */
    void solve(boolean findAllSolutions) {
        // here you can do any preprocessing you might want to do before diving into the search

        search(findAllSolutions /* you can add more params */);
    }
}
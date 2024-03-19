import java.util.*;

class Solver {

    static class SudokuConstraint extends Constraint{
        Variable var;

        public SudokuConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();
            
            for(int x : this.var.domain){
                boolean flag = false;
                for (VariableAssigned v : tmp_sol) {
                    int n = this.var.variablesLength;
                    int size = n*n;
                    // Check if variable value in the domain
                    if(x == v.value){
                        // Check if variables in the same row
                        if(this.var.index / size == v.place / size){
                            flag = true;
                            continue;
                        }
                        // Check if variables in the same column
                        if(this.var.index % size == v.place % size){
                            flag = true;
                            continue;
                        }
                        // Check if variables in the same nine
                        if(((this.var.index / n) % n == (v.place / n) % n) && ((this.var.index / (size * n)) % n == (v.place / (size * n)) % n)){
                            flag = true;
                            continue;
                        }
                    }
                }
                if(flag == false){
                    newDomain.add(x);
                }
            }

            // this.var.domain = newDomain;
            this.var.domains.addLast(newDomain);
        }
    }


    // ABOUT SYMMETRY BREAKING CONSTRAINT:
    // Our Solver uses DFS that starts with the first variable, tries every value from the domain,
    // propagates the constraints for the next (second) variable, and tries all the remaining 
    // values from the domain. Since we encoded our rows as variables in the same order, 
    // our algorithm will always place queens on rows in the same order, thus breaking symmetry.
    static class NQueensConstraint extends Constraint{
        Variable var;

        public NQueensConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();
            
            for(int x : this.var.domain){
                boolean flag = false;
                for (VariableAssigned v : tmp_sol) {
                    // no column                
                    if(x == v.value){
                        flag = true;
                        continue;
                    }
                    //no diagonal
                    if((v.value + v.place == x + this.var.index) || (v.value - v.place == x - this.var.index)){
                        flag = true;
                        continue;
                    }
                }
                if(flag == false){
                    newDomain.add(x);
                }
            }

            // this.var.domain = newDomain;
            this.var.domains.addLast(newDomain);
        }
    }

    static class CombinationsWithoutRepetitionConstraint extends Constraint{
        Variable var;

        public CombinationsWithoutRepetitionConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (x > tmp_sol.getLast().value)
                    newDomain.add(x);
            }

            this.var.domains.addLast(newDomain);
            // System.out.println(newDomain.toString());
            // this.var.domain = newDomain;
        }
    }


    static class CombinationsWithRepetitionConstraint extends Constraint{
        Variable var;

        public CombinationsWithRepetitionConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (x >= tmp_sol.getLast().value)
                    newDomain.add(x);
            }

            // this.var.domain = newDomain;
            this.var.domains.addLast(newDomain);

        }
    }

    static class SubsetsConstraint extends Constraint{
        Variable var;

        public SubsetsConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();

            for (Integer x : this.var.domain) {
                if (tmp_sol.getLast().value == 0 || x > tmp_sol.getLast().value){
                    newDomain.add(x);
                }
            }

            this.var.domains.addLast(newDomain);
            // this.var.domain = newDomain;
        }
    }

    static class PermutationsConstraint extends Constraint{
        Variable var;

        public PermutationsConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();
            
            for(int x : this.var.domain){
                boolean flag = false;
                for (VariableAssigned v : tmp_sol) {                
                    if(x == v.value){
                        flag = true;
                        continue;
                    }
                }
                if(flag == false){
                    newDomain.add(x);
                }
            }

            // this.var.domain = newDomain;
            this.var.domains.addLast(newDomain);
        }
    }

    static class NoConstraint extends Constraint{
        Variable var;

        public NoConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            
        }
    }

    static class Variable {
        List<Integer> domain;
        Deque<List<Integer>> domains;
        int index;
        int variablesLength;
        int value;

        public Variable(List<Integer> domain, int value, int index) {
            this.domain = domain;
            this.domains = new LinkedList<>();
            this.domains.addLast(domain);
            this.index = index;
            this.value = value;
        }

    }

    static abstract class Constraint {
        abstract void infer(Deque<VariableAssigned> tmp_sol);
    }


    Variable[] variables;
    Constraint[] constraints;
    List<int[]> solutions;

    /**
     * Constructs a solver.
     * @param variables The variables in the problem
     * @param constraints The constraints applied to the variables
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        this.variables = variables;

        this.constraints = constraints;

        solutions = new LinkedList<>();
    }

    int[] convertToArray(Deque<VariableAssigned> tmp_sol){
        int n = tmp_sol.size();
        int[] ret = new int[n];
        int i=0;
        for(VariableAssigned el:tmp_sol){
            ret[i++] = el.value;
        }
        return ret;
    }

    void printSol(Deque<VariableAssigned> tmp_sol){
        System.out.print("tmp_sol");
        for(VariableAssigned el:tmp_sol){
            System.out.print(el.value);
        }
        System.out.println();
    }

    static class VariableAssigned {
        int place;
        int index;
        int value;

        public VariableAssigned(int place, int value, int index) {
            this.place = place;
            this.index = index;
            this.value = value;
        }

    }

    /**
     * Solves the problem using search and inference.
     */
    void search(boolean findAllSolutions /* you can add more params */) {
        Deque<VariableAssigned> tmp_sol = new LinkedList<>();
        Deque<VariableAssigned> assStack = new LinkedList<>();
        
        for(int i=0; i<variables[0].domain.size(); i++){
            assStack.addLast(new VariableAssigned(0, variables[0].domain.get(i), i));
        }
        while(!assStack.isEmpty()){
            VariableAssigned var = assStack.removeLast();
            while(tmp_sol.size() > var.place){
                if(variables[tmp_sol.size()].domains.size() > 1){
                    variables[tmp_sol.size()].domains.removeLast();
                }
                tmp_sol.removeLast();
            }
            tmp_sol.addLast(var);
            int size = tmp_sol.size();

            //found solution
            if(size == variables.length){
                solutions.add(convertToArray(tmp_sol));
                if(findAllSolutions == false){
                    return;
                }
                tmp_sol.removeLast();
                continue;
            }
            constraints[tmp_sol.size()].infer(tmp_sol);
            for(int i=0; i<variables[size].domains.getLast().size(); i++){
                assStack.addLast(new VariableAssigned(size, variables[size].domains.getLast().get(i), i));
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
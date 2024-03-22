import java.util.*;

class Solver {

    static class SudokuConstraint extends Constraint{
        Variable var;

        public SudokuConstraint(Variable var) {
            this.var = var;
        }
        void infer(Deque<VariableAssigned> tmp_sol) {
            Set<Integer> newDomain = new HashSet<>(this.var.domains.getLast());
            
            VariableAssigned v = tmp_sol.getLast();
            if(this.var.affects.contains(v.index)){
                newDomain.remove(v.value);
            }

            this.var.domains.addLast(new ArrayList<>(newDomain));
        }
    }


    // SYMMETRY BREAKING CONSTRAINT: Our symmetry breaking constraint is implemented in the preprocessing and postprocessing steps in NQueens.java
    static class NQueensConstraint extends Constraint{
        Variable var;

        public NQueensConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();
            
            for(int x : this.var.domains.getLast()){
                boolean flag = false;
                for (VariableAssigned v : tmp_sol) {
                    // no column                
                    if(x == v.value){
                        flag = true;
                        continue;
                    }
                    //no diagonal
                    if((v.value + v.index == x + this.var.index) || (v.value - v.index == x - this.var.index)){
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
            int max = 0;
            for(VariableAssigned v : tmp_sol){
                if(v.index < var.index){
                    max = Math.max(max, v.value);
                }
            }
            for (Integer x : this.var.domains.getLast()) {
                if (x > max)
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
            VariableAssigned last = tmp_sol.getLast();
            
            for (Integer x : this.var.domains.getLast()) {
                if( last.index < var.index){
                    if(last.value <= x){
                        newDomain.add(x);
                    }
                } else if(last.index > var.index){
                    if(last.value >= x){
                        newDomain.add(x);
                    }
                }
            }

            this.var.domains.addLast(newDomain);

        }
    }

    static class PermutationsConstraint extends Constraint{
        Variable var;

        public PermutationsConstraint(Variable var) {
            this.var = var;
        }

        void infer(Deque<VariableAssigned> tmp_sol) {
            List<Integer> newDomain = new LinkedList<>();
            VariableAssigned last = tmp_sol.getLast();
            for(int x : this.var.domains.getLast()){                
                if(last.value != x){
                    newDomain.add(x);
                }
            }

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

    static class Variable implements Comparable<Variable> {
        List<Integer> domain;
        List<Integer> affects;
        Deque<List<Integer>> domains;
        int index;
        int variablesLength;
        int value;

        public Variable(List<Integer> domain, int value, int index, List<Integer> affects) {
            this.domain = domain;
            this.affects = affects;
            this.domains = new LinkedList<>();
            this.domains.addLast(domain);
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(Variable c) {
            if (this.domains.getLast().size() > c.domains.getLast().size()){
                return 1;
            } else {
                if( this.domains.getLast().size() < c.domains.getLast().size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
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
        int n = variables.length;
        int[] ret = new int[n];
        for(VariableAssigned el:tmp_sol){
            ret[el.index] = el.value;
        }
        return ret;
    }

    void printSol(Deque<VariableAssigned> tmp_sol){
        System.out.print("tmp_sol: ");
        int[] arr = convertToArray(tmp_sol);
        for(int i:arr){
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    static class VariableAssigned {
        int index;
        int value;

        public VariableAssigned(int place, int value) {
            this.index = place;
            this.value = value;
        }

    }

    
    static class VariablePQ implements Comparable<VariablePQ> {
        int index;
        int domainSize;

        public VariablePQ(int index, int s) {
            this.domainSize= s;
            this.index = index;
        }

        @Override
        public int compareTo(VariablePQ c) {
            if (this.domainSize > c.domainSize){
                return 1;
            } else {
                if(this.domainSize < c.domainSize){
                    return -1;
                } else {
                    return 0;
                }
            }
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VariablePQ c = (VariablePQ) o;
            return this.domainSize == c.domainSize && this.index == c.index;
        }
    }

    int findSmallestVariable(Set<Integer> notUsed){
        int idx = 0;
        int size = 1000000000;
        for(int i : notUsed){
            if(variables[i].domains.getLast().size()<size) {
                idx = i;
                size = variables[i].domains.getLast().size();
            }
        }
        return idx;
    }

    void search(boolean findAllSolutions /* you can add more params */) {
        Deque<VariableAssigned> tmp_sol = new LinkedList<>();
        Deque<VariableAssigned> assStack = new LinkedList<>();
        Set<Integer> notUsed = new HashSet<>();
        boolean[] usedPlace = new boolean[variables.length];
        
        //init unusedplace
        for(int i=0; i<variables.length; i++){
            notUsed.add(i);
        }

        int smallestVariable = findSmallestVariable(notUsed);
        for(int i=0; i<variables[smallestVariable].domain.size(); i++){
            assStack.addLast(new VariableAssigned(smallestVariable, variables[smallestVariable].domain.get(i)));
        }
        // notUsed.remove(smallestVariable);

        while(!assStack.isEmpty()){
            VariableAssigned var = assStack.removeLast();
            if(!notUsed.contains(var.index)){
                while(tmp_sol.size() >= 1 && tmp_sol.getLast().index != var.index){
                    repropagate(tmp_sol, notUsed);
                    notUsed.add(tmp_sol.getLast().index);
                    tmp_sol.removeLast();
                }
                if(tmp_sol.size() >= 1 && tmp_sol.getLast().index == var.index){
                    repropagate(tmp_sol, notUsed);
                    notUsed.add(tmp_sol.getLast().index);

                    tmp_sol.removeLast();
                }
            }
            // repropagate(tmp_sol, usedPlace);
            tmp_sol.addLast(var);
            notUsed.remove(var.index);
            int size = tmp_sol.size();

            //found solution
            if(size == variables.length){
                solutions.add(convertToArray(tmp_sol));
                if(findAllSolutions == false){
                    return;
                }
                notUsed.add(var.index);
                tmp_sol.removeLast();
                continue;
            }
            // constraints[tmp_sol.size()].infer(tmp_sol);
            if(!propagate(tmp_sol, notUsed)){
                continue;
            }
            int smallestVar = findSmallestVariable(notUsed);
            List<Integer> dom = variables[smallestVar].domains.getLast();
            for(int i=0; i<dom.size(); i++){
                assStack.addLast(new VariableAssigned(smallestVar, dom.get(i)));
            }
        }
    }
    void repropagate(Deque<VariableAssigned> sol, Set<Integer> notUsed){
        List<Integer> affected = variables[sol.getLast().index].affects;
        for(int a : affected){
            if(notUsed.contains(a)){
                Deque<List<Integer>> domains = variables[a].domains;
                if(domains.size() > 1){
                    domains.removeLast();
                }
            }
        }
    }

    boolean propagate(Deque<VariableAssigned> sol, Set<Integer> notUsed){
        List<Integer> affected = variables[sol.getLast().index].affects;
        boolean flag = false;
        for(int a : affected){
            if(notUsed.contains(a)){
                constraints[a].infer(sol);
            }
            if(variables[a].domains.getLast().size() == 0){
                flag = true;
            }

        }
        if(flag){
            return false;
        }
        return true;
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
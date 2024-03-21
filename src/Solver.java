import java.util.*;

class Solver {

    static class SudokuConstraint extends Constraint{
        Variable var;

        public SudokuConstraint(Variable var) {
            this.var = var;
        }
        void infer(Deque<VariableAssigned> tmp_sol) {
            Set<Integer> newDomain = new HashSet<>(this.var.domains.getLast());
            
            for (VariableAssigned v : tmp_sol) {
                if(this.var.affects.contains(v.index)){
                    newDomain.remove(v.value);
                }
            }

            // this.var.domain = newDomain;
            this.var.domains.addLast(new ArrayList<>(newDomain));
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

    void search(boolean findAllSolutions) {
        Deque<VariableAssigned> tmp_sol = new LinkedList<>();
        Deque<VariableAssigned> assStack = new LinkedList<>();
        PriorityQueue<VariablePQ> pq = new PriorityQueue<>();
        boolean[] usedPlace = new boolean[variables.length];
        
        //init pq
        for(int i=0; i<variables.length; i++){
            pq.add(new VariablePQ(variables[i].index, variables[i].domains.getLast().size()));
            usedPlace[i] = false;
        }
        //init assStack
        VariablePQ firstPQ = pq.remove();

        for(int i=0; i<variables[firstPQ.index].domains.getLast().size(); i++){
            assStack.addLast(new VariableAssigned(firstPQ.index, variables[firstPQ.index].domains.getLast().get(i)));
        }

        while(!assStack.isEmpty()){
            VariableAssigned var = assStack.removeLast();
            if(usedPlace[var.index]){
                // printSol(tmp_sol);
                VariableAssigned last = tmp_sol.getLast();
                if(tmp_sol.size() == variables.length && tmp_sol.getLast().index == var.index){
                    usedPlace[last.index] = false;
                    tmp_sol.removeLast();
                } else {
                    if(tmp_sol.size() == variables.length){
                        pq.add(new VariablePQ(last.index, variables[last.index].domains.getLast().size()));    
                        usedPlace[last.index] = false;
                        tmp_sol.removeLast();
                    }
                    while(tmp_sol.getLast().index != var.index){
                        usedPlace[tmp_sol.getLast().index] = false;
                        repropagate2(tmp_sol.getLast(), pq, usedPlace);
                        pq.add(new VariablePQ(tmp_sol.getLast().index, variables[tmp_sol.getLast().index].domains.getLast().size()));
                        tmp_sol.removeLast();
                    }
                    // printSol(tmp_sol);
    
                    if(tmp_sol.getLast().index == var.index){
                        usedPlace[tmp_sol.getLast().index] = false;
                        repropagate2(tmp_sol.getLast(), pq, usedPlace);
                        
                        tmp_sol.removeLast();
                    }   
                }
            }
            if(var.index == 61){
                int[] sol = convertToArray(tmp_sol);
                int[] s = new int[]{sol[57],sol[58],sol[59],sol[60]};

                int a = 5;
            }

            tmp_sol.addLast(var);
            usedPlace[var.index] = true;
            int size = tmp_sol.size();

            //found solution
            if(size == variables.length){
                // printSol(tmp_sol);
                solutions.add(convertToArray(tmp_sol));
                if(findAllSolutions == false)
                    return;
                continue;
            }
            if(!propagate2(tmp_sol, pq, usedPlace)){
                continue;
            }
            if(pq.size() == 0) continue;
            VariablePQ smallestVariable = pq.remove();
            if(usedPlace[smallestVariable.index]) continue;
            
            List<Integer> dom = variables[smallestVariable.index].domains.getLast();
            for(int i=0; i<dom.size(); i++){
                assStack.addLast(new VariableAssigned(smallestVariable.index, dom.get(i)));
            }
        }
    }
    boolean propagate2(Deque<VariableAssigned> sol, PriorityQueue<VariablePQ> pq, boolean[] usedPlace){
        List<Integer> affected = variables[sol.getLast().index].affects;
        for(int a : affected){
            VariablePQ newVar = new VariablePQ(variables[a].index, variables[a].domains.getLast().size());
            if(pq.remove(newVar)){
                if(a == 61 && variables[a].domains.size() == 1){
                    int[] solut = convertToArray(sol);
                    int[] s = new int[]{solut[57],solut[58],solut[59],solut[60]};
                    
                    int b = 5;
                }
                constraints[a].infer(sol);
                newVar.domainSize = variables[a].domains.getLast().size();
                if(!usedPlace[a]) pq.add(newVar);
                if(newVar.domainSize == 0){
                    return false;
                }

            }
        }
        return true;
    }

    void repropagate2(VariableAssigned last, PriorityQueue<VariablePQ> pq, boolean[] usedPlace){
        List<Integer> affected = variables[last.index].affects;
        for(int a : affected){
            Deque<List<Integer>> domains = variables[a].domains;
            if(domains.size() > 1){
                VariablePQ newVar = new VariablePQ(a, domains.getLast().size());
                if(!usedPlace[a]){ 
                    domains.removeLast();
                    if(pq.remove(newVar)){
                        newVar.domainSize = domains.getLast().size();
                        pq.add(newVar);
                    }
                }
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
import java.util.*;

class SolverAdam {
    static int fixedPoint;
    static Variable[] variables;
    static Constraint[] constraints;
    static LinkedList<int[]> solutions;

    static class VariableAssignment {
        public int fixedPointId;
        public int value;

        public VariableAssignment(int value, int fixedPointId) {
            this.value = value;
            this.fixedPointId = fixedPointId;
        }

        @Override
        public String toString() {
            return "<fixedPoint: " + this.fixedPointId + ", val: " + this.value + ">";
        }
    }

    public static class Variable {
        int id;
        VariableAssignment assignment;
        // Stack of propagations. Always one domain propagation per fixed point (may be same as previous domain).
        Deque<List<Integer>> propagations;
        // Constraints including this variable
        int[] constraintIds;

        final List<Integer> defaultDomain;

        /**
         * Constructs a new variable.
         * @param domain A list of values that the variable can take
         */
        public Variable(List<Integer> domain) {
            this.defaultDomain = new ArrayList<>(domain);
        }

        public Variable(int value) {
            this.defaultDomain = new ArrayList<>();
            this.assignment = new VariableAssignment(value, -1);
        }


        @Override
        public String toString() {
            if (assignment != null) {
                return "[" + this.id + "]: " + assignment.toString();
            }
            return "[" + this.id + "]: " + propagations.peek().toString();
        }
    }

    // TODO: sort constraints by the number of unassigned variables (less = better)
    public abstract static class Constraint {
        int id;
        Integer[] varIds;

        /**
         * Tries to reduce the domain of the variables associated to this constraint, using inference
         * Also recursively infers other constraints if at least one reduction found
         * 
         * When inferring, current variable values are "assigned value" or if that is null, then the "propagations" domain on top of the stack
         * 
         * @return false if a reduction results in empty domain 😊 no solution possible)
         */
        abstract boolean infer(int varId);
    }

    // Not equal with offset
    public static class NeqOffsetConstraint extends Constraint {
        int offset;

        public NeqOffsetConstraint(int id1, int id2, int offset) {
            this.varIds = new Integer[] { id1, id2 };
            this.offset = offset;
        }

        @Override
        public boolean infer(int varId) {
            int otherId;
            if (this.varIds[1] == varId) {
                otherId = this.varIds[0];
            }else if (this.varIds[0] == varId) {
                otherId = this.varIds[1];
            }else {
                throw new Error("[NeqConstraint] varId=" + varId + " not found in constraint varIds=" + Arrays.toString(this.varIds));
            }
            Variable currVar = variables[varId];
            Variable otherVar = variables[otherId];

            Integer valToRemove = currVar.assignment.value + offset;

            // If other variable assigned to the value that is being removed, no solution possible
            if (otherVar.assignment != null) return otherVar.assignment.value != valToRemove;

            List<Integer> domain = otherVar.propagations.peek();
            
            if (domain.contains(valToRemove)) {
                domain.remove(valToRemove);

                // Domain empty, which means no solution possible
                if (domain.isEmpty()) return false;
                
                if (domain.size() != 1) return true;

                // Domain restricted to size 1, which means the variable is now assigned to a value
                otherVar.assignment = new VariableAssignment(domain.get(0), Solver.fixedPoint);

                // Update new propagations based on assigned value
                for (Integer cId : variables[otherId].constraintIds) {
                    // If solution not possible as a result of propagation, return false
                    if (!constraints[cId].infer(otherId)) return false;
                }
            }

            return true;
        }
    }

    // Specialized case of NeqOffset with offset 0
    public static class NeqConstraint extends NeqOffsetConstraint {
        public NeqConstraint(int id1, int id2) {
            super(id1, id2, 0);
        }
    }

    public static class GrEqConstraint extends Constraint {
        // id1 is greater or equal than id2
        public GrEqConstraint(int id1, int id2) {
            this.varIds = new Integer[] { id1, id2 };
        }

        @Override
        public boolean infer(int varId) {
            int otherId;
            boolean otherIsLhs;
            if (this.varIds[1] == varId) {
                otherId = this.varIds[0];
                otherIsLhs = true;
            }else if (this.varIds[0] == varId) {
                otherId = this.varIds[1];
                otherIsLhs = false;
            }else {
                throw new Error("[GrEqConstraint] varId=" + varId + " not found in constraint varIds=" + Arrays.toString(this.varIds));
            }

            Variable currVar = variables[varId];
            Variable otherVar = variables[otherId];

            Integer currValue = currVar.assignment.value;

            // Other variable assigned to the value that is being removed, no solution possible
            if (otherVar.assignment != null) {
                if (otherIsLhs) return otherVar.assignment.value >= currValue;
                return currValue >= otherVar.assignment.value;
            }

            List<Integer> domain = otherVar.propagations.peek();

            // Filter values to be removed
            List<Integer> valsToRemove = new ArrayList<>();
            for (Integer otherDomainVal : domain) {
                if (otherIsLhs && otherDomainVal < currValue) valsToRemove.add(otherDomainVal);
                if (!otherIsLhs && otherDomainVal > currValue) valsToRemove.add(otherDomainVal);
            }

            // No vals to remove, return
            if (valsToRemove.isEmpty()) return true;

            for (Integer toRemove : valsToRemove) {
                domain.remove(toRemove);
            }

            // Domain empty, no solution possible
            if (domain.size() == 0) return false;

            if (domain.size() != 1) return true;

            // Domain restricted to only one value, assign it
            otherVar.assignment = new VariableAssignment(domain.get(0), fixedPoint);
            
            // Update new propagations
            for (Integer cId : variables[otherId].constraintIds) {
                // If solution not possible as a result of propagation, return false
                if (!constraints[cId].infer(otherId)) return false;
            }

            return true;
        }
    }

    public static class GrConstraint extends Constraint {
        // id1 is greater or equal than id2
        public GrConstraint(int id1, int id2) {
            this.varIds = new Integer[] { id1, id2 };
        }

        @Override
        public boolean infer(int varId) {
            int otherId;
            boolean otherIsLhs;
            if (this.varIds[1] == varId) {
                otherId = this.varIds[0];
                otherIsLhs = true;
            }else if (this.varIds[0] == varId) {
                otherId = this.varIds[1];
                otherIsLhs = false;
            }else {
                throw new Error("[GrConstraint] varId=" + varId + " not found in constraint varIds=" + Arrays.toString(this.varIds));
            }

            Variable currVar = variables[varId];
            Variable otherVar = variables[otherId];

            Integer currValue = currVar.assignment.value;

            // Other variable assigned to the value that is being removed, no solution possible
            if (otherVar.assignment != null) {
                if (otherIsLhs) return otherVar.assignment.value > currValue;
                return currValue > otherVar.assignment.value;
            }

            List<Integer> domain = otherVar.propagations.peek();

            // Filter values to be removed
            List<Integer> valsToRemove = new ArrayList<>();
            for (Integer otherDomainVal : domain) {
                if (otherIsLhs && otherDomainVal <= currValue) valsToRemove.add(otherDomainVal);
                if (!otherIsLhs && otherDomainVal >= currValue) valsToRemove.add(otherDomainVal);
            }

            // No vals to remove, return
            if (valsToRemove.isEmpty()) return true;

            for (Integer toRemove : valsToRemove) {
                domain.remove(toRemove);
            }

            // Domain empty, no solution possible
            if (domain.size() == 0) return false;

            if (domain.size() != 1) return true;

            // Domain restricted to only one value, assign it
            otherVar.assignment = new VariableAssignment(domain.get(0), fixedPoint);
            
            // Update new propagations
            for (Integer cId : variables[otherId].constraintIds) {
                // If solution not possible as a result of propagation, return false
                if (!constraints[cId].infer(otherId)) return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return "<GrConstraint:  x" + this.varIds[0] + " > x" + this.varIds[1] + "  >";
        }
    }


    /**
     * Constructs a solver.
     * @param variables The variables in the problem
     * @param constraints The constraints applied to the variables
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        Solver.fixedPoint = 0;
        Solver.variables = new Variable[variables.length];
        for (int i = 0; i < variables.length; i++) {
            Solver.variables[i] = variables[i];
            Solver.variables[i].id = i;
//            Solver.variables[i].assignment = null;
            Solver.variables[i].propagations = new ArrayDeque<>();
            Solver.variables[i].propagations.push(new ArrayList<>(Solver.variables[i].defaultDomain));
        }

        Solver.constraints = new Constraint[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            Solver.constraints[i] = constraints[i];
            Solver.constraints[i].id = i;
        }

        for (Variable var : Solver.variables) {
            if (var.constraintIds != null || var.assignment != null) continue;
            List<Constraint> varConstraints = new ArrayList<>();
            for (Constraint constr : Solver.constraints) {
                // Add constraint to variable's list if the constraint affects the variable
                for (Integer varId : constr.varIds) {
                    if (varId == var.id) {
                        varConstraints.add(constr);
                        break;
                    }
                }
            }
            var.constraintIds = new int[varConstraints.size()];
            for (int i = 0; i < varConstraints.size(); i++) {
                var.constraintIds[i] = varConstraints.get(i).id;
            }
        }

        Solver.solutions = new LinkedList<>();
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

    /**
     * Solves the problem using search and inference.
     */
    void search(boolean findAllSolutions /* you can add more params */) {
        
        // if not findAllSolutions and solutions not empty, return
        if (!findAllSolutions && !Solver.solutions.isEmpty()) return;

        // Find unassigned variable
        Variable unassignedVar = null;
        for (Variable var : Solver.variables) {
            if (var.assignment != null) continue;
            if (unassignedVar == null) {
                unassignedVar = var;
            } else {
                unassignedVar = unassignedVar.propagations.peek().size() < var.propagations.peek().size()? unassignedVar : var;
            }
        }

        // If all variables assigned, solution found
        if (unassignedVar == null) {
            int[] solution = new int[Solver.variables.length];
            for (int i = 0; i < Solver.variables.length; i++) {
                solution[i] = Solver.variables[i].assignment.value;
            }   
            Solver.solutions.add(solution);
            return;
        }

        // Try all possible assignments
        for (Integer val : unassignedVar.propagations.peek()) {
            Solver.fixedPoint++;

            int currFixedPoint = Solver.fixedPoint;
            boolean isSolutionStillPossible = true;

            // Add new propagation domains (copy last)
            for (Variable variable : Solver.variables) {
                if (variable.assignment==null) variable.propagations.push(new ArrayList<>(variable.propagations.peek()));
            }

            // Perform an assignment and propagation pass 
                // if propagation restricts domain to empty list, no solution possible
                // if after propagation domain has only one number, create assignment
            unassignedVar.assignment = new VariableAssignment(val, currFixedPoint);
            for (Integer cId : unassignedVar.constraintIds) {
                if (Solver.constraints[cId].infer(unassignedVar.id)) continue;
                isSolutionStillPossible = false;
                break;
            }
            
            // if solution is possible
                // Call this method recursively with updated assignment and propagations
            
            if (isSolutionStillPossible) search(findAllSolutions);

            // Pop assignment and propagation
            for (Variable popVariable : Solver.variables) {
                if (popVariable.assignment != null && popVariable.assignment.fixedPointId == currFixedPoint) {
                    popVariable.assignment = null;
                }

                if (popVariable.assignment == null || (popVariable.assignment != null && popVariable.assignment.fixedPointId == currFixedPoint)) {
                    popVariable.propagations.pop();
                }

            }

        }

    }
}
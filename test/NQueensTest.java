import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NQueensTest {
    @Test
    public void testNQueenSolutions() {
        int[] solutions = {
                0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200
        };

        for (int n = 1; n <= 12; n++) {
            System.out.print("Running getNQueenSolutions (n = " + n + ")...");

            long startMillis = System.currentTimeMillis();
            int result = NQueens.getNQueenSolutions(n);
            long time = System.currentTimeMillis() - startMillis;

            assertEquals(solutions[n], result, "Expected " + solutions[n] + " but got " + result + " (n = " + n + ")");

            System.out.println(" passed in " + time + "ms");
        }
    }
}
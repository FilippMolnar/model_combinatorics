import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SudokuTest {
    @Test
    public void testSolve() {
        String directoryPath = "./src/sudoku_instances/";
        File folder = new File(directoryPath);
        File[] files = folder.listFiles();

        assertNotNull(files, "Couldn't find Sudoku files");

        for (File file : files) {
            String filePath = directoryPath + file.getName();

            int[][] grid;
            try {
                grid = parse(filePath);
            } catch (FileNotFoundException e) {
                System.err.println("Couldn't open file " + filePath);
                continue;
            }

            System.out.print("Running solve on " + file.getName() + "...");

            long startMillis = System.currentTimeMillis();
            int[][] solvedGrid = Sudoku.solve(grid);
            long time = System.currentTimeMillis() - startMillis;

            int size = solvedGrid.length == 9 ? 3 : solvedGrid.length == 16 ? 4 : 5;
            int sizeSquared = solvedGrid.length;

            // Row check
            for (int iy = 0; iy < sizeSquared; iy++) {
                Set<Integer> seen = new HashSet<>();

                for (int ix = 0; ix < sizeSquared; ix++) {
                    if (seen.contains(solvedGrid[iy][ix]))
                        throw new AssertionFailedError(filePath + ": Solution contained at least two " + solvedGrid[iy][ix] + "'s in row " + (iy + 1));
                    if (solvedGrid[iy][ix] == -1)
                        throw new AssertionFailedError(filePath + ": Solution contained unfilled spot at (" + (ix + 1) + ", " + (iy + 1) + ")");
                    seen.add(solvedGrid[iy][ix]);
                }
            }

            // Column check
            for (int ix = 0; ix < sizeSquared; ix++) {
                Set<Integer> seen = new HashSet<>();

                for (int iy = 0; iy < sizeSquared; iy++) {
                    if (seen.contains(solvedGrid[iy][ix]))
                        throw new AssertionFailedError(filePath + ": Solution contained at least two " + solvedGrid[iy][ix] + "'s in column " + (ix + 1));
                    seen.add(solvedGrid[iy][ix]);
                }
            }

            // Block check
            for (int by = 0; by < sizeSquared; by += size) {
                for (int bx = 0; bx < sizeSquared; bx += size) {
                    Set<Integer> seen = new HashSet<>();

                    for (int iy = by; iy < by + size; iy++) {
                        for (int ix = bx; ix < bx + size; ix++) {
                            if (seen.contains(solvedGrid[iy][ix]))
                                throw new AssertionFailedError(filePath + ": Solution contained at least two " + solvedGrid[iy][ix] + "'s in block (" + (bx / 3) + ", " + (by / 3) + ")");
                            seen.add(solvedGrid[iy][ix]);
                        }
                    }
                }
            }

            System.out.println(" passed in " + time + "ms");
        }
    }

    private int[][] parse(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner sc = new Scanner(file);

        int size = sc.nextInt();
        int sizeSquared = size * size;
        sc.nextInt();

        int[][] grid = new int[sizeSquared][sizeSquared];
        for (int iy = 0; iy < sizeSquared; iy++) {
            for (int ix = 0; ix < sizeSquared; ix++) {
                grid[iy][ix] = sc.nextInt();
            }
        }

        return grid;
    }
}
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StandardCombinatoricsTest {
    @org.junit.Test
    public void testBinaryStrings() {
        int[] ns = {3, 10};

        for (int t = 0; t < ns.length; t++) {
            int n = ns[t];

            System.out.print("Running getBinaryStrings (n = " + n + ")...");

            long startMillis = System.currentTimeMillis();
            List<String> result = StandardCombinatorics.getBinaryStrings(n);
            long time = System.currentTimeMillis() - startMillis;

            assertNotNull(result, "Result was null");
            assertEquals(1 << n, result.size(), "Expected " + (1 << n) + " strings, but received " + result.size());

            Collections.sort(result);
            String prev = null;
            for (String curr : result) {
                if (prev != null) {
                    assertNotEquals(prev, curr, "Result contained \"" + curr + "\" twice");
                }

                assertEquals(n, curr.length(), "Result contained \"" + curr + "\", which is not of size " + n);
                for (char c : curr.toCharArray()) {
                    if (c != '0' && c != '1') {
                        throw new AssertionFailedError("Result contained \"" + curr + "\", with non-binary characters");
                    }
                }

                prev = curr;
            }

            System.out.println(" passed in " + time + "ms");
        }
    }

    @org.junit.Test
    public void testCombinationsWithoutRepetition() {
        int[] ns = {3, 100, 1000, 20};
        int[] ks = {2, 3, 2, 8};

        for (int t = 0; t < ns.length; t++) {
            int n = ns[t];
            int k = ks[t];

            System.out.print("Running getCombinationsWithoutRepetition (n = " + n + ", k = " + k + ")...");

            long startMillis = System.currentTimeMillis();
            List<int[]> result = StandardCombinatorics.getCombinationsWithoutRepetition(n, k);
            long time = System.currentTimeMillis() - startMillis;

            assertNotNull(result, "Result was null");

            int comb = 1;
            for (int i = 0; i < k; i++) {
                comb *= n - i;
                comb /= i + 1;
            }

            assertEquals(comb, result.size(), "Expected " + comb + " combinations, but received " + result.size());
            result.sort((a, b) -> {
                if (a.length < b.length)
                    return -1;
                if (a.length > b.length)
                    return 1;
                for (int i = 0; i < a.length; i++) {
                    if (a[i] < b[i])
                        return -1;
                    if (a[i] > b[i])
                        return 1;
                }
                return 0;
            });

            int[] prev = null;
            for (int[] curr : result) {
                if (prev != null) {
                    assertNotEquals(prev, curr, "Result contained an instance twice");
                }

                assertEquals(k, curr.length, "Result contained an instance which is not of size " + k);
                for (int x : curr) {
                    if (x < 1 || x > n) {
                        throw new AssertionFailedError("Result contained an instance with out-of-range numbers");
                    }
                }

                int[] sorted = Arrays.stream(curr).sorted().toArray();
                for (int i = 1; i < sorted.length; i++) {
                    assertNotEquals(sorted[i - 1], sorted[i], "Result contained an instance with duplicate numbers");
                }

                prev = curr;
            }

            System.out.println(" passed in " + time + "ms");
        }
    }

    @org.junit.Test
    public void testCombinationsWithRepetition() {
        int[] ns = {3, 100, 12};
        int[] ks = {2, 3, 8};

        for (int t = 0; t < ns.length; t++) {
            int n = ns[t];
            int k = ks[t];

            System.out.print("Running getCombinationsWithRepetition (n = " + n + ", k = " + k + ")...");

            long startMillis = System.currentTimeMillis();
            List<int[]> result = StandardCombinatorics.getCombinationsWithRepetition(n, k);
            long time = System.currentTimeMillis() - startMillis;

            assertNotNull(result, "Result was null");

            int comb = 1;
            for (int i = 0; i < k; i++) {
                comb *= n - 1 + k - i;
                comb /= i + 1;
            }

            assertEquals(comb, result.size(), "Expected " + comb + " combinations, but received " + result.size());
            result.sort((a, b) -> {
                if (a.length < b.length)
                    return -1;
                if (a.length > b.length)
                    return 1;
                for (int i = 0; i < a.length; i++) {
                    if (a[i] < b[i])
                        return -1;
                    if (a[i] > b[i])
                        return 1;
                }
                return 0;
            });

            int[] prev = null;
            for (int[] curr : result) {
                if (prev != null) {
                    assertNotEquals(prev, curr, "Result contained an instance twice");
                }

                assertEquals(k, curr.length, "Result contained an instance which is not of size " + k);
                for (int x : curr) {
                    if (x < 1 || x > n) {
                        throw new AssertionFailedError("Result contained an instance with out-of-range numbers");
                    }
                }

                prev = curr;
            }

            System.out.println(" passed in " + time + "ms");
        }
    }

    @org.junit.Test
    public void testSubsets() {
        int[] ns = {3, 10, 20};

        for (int t = 0; t < ns.length; t++) {
            int n = ns[t];

            System.out.print("Running getSubsets (n = " + n + ")...");

            long startMillis = System.currentTimeMillis();
            List<int[]> result = StandardCombinatorics.getSubsets(n);
            long time = System.currentTimeMillis() - startMillis;

            assertNotNull(result, "Result was null");

            assertEquals(1 << n, result.size(), "Expected " + (1 << n) + " subsets, but received " + result.size());
            result.sort((a, b) -> {
                if (a.length < b.length)
                    return -1;
                if (a.length > b.length)
                    return 1;
                for (int i = 0; i < a.length; i++) {
                    if (a[i] < b[i])
                        return -1;
                    if (a[i] > b[i])
                        return 1;
                }
                return 0;
            });

            int[] prev = null;
            for (int[] curr : result) {
                if (prev != null) {
                    assertNotEquals(prev, curr, "Result contained an instance twice");
                }

                for (int x : curr) {
                    if (x < 1 || x > n) {
                        throw new AssertionFailedError("Result contained an instance with out-of-range numbers");
                    }
                }

                int[] sorted = Arrays.stream(curr).sorted().toArray();
                for (int i = 1; i < sorted.length; i++) {
                    assertNotEquals(sorted[i - 1], sorted[i], "Result contained an instance with duplicate numbers");
                }

                prev = curr;
            }

            System.out.println(" passed in " + time + "ms");
        }
    }

    @org.junit.Test
    public void testSetPermutations() {
        int[] ns = {3, 8, 10};

        for (int t = 0; t < ns.length; t++) {
            int n = ns[t];

            System.out.print("Running getSetPermutations (n = " + n + ")...");

            long startMillis = System.currentTimeMillis();
            List<int[]> result = StandardCombinatorics.getSetPermutations(n);
            long time = System.currentTimeMillis() - startMillis;

            assertNotNull(result, "Result was null");

            int fac = 1;
            for (int i = 2; i <= n; i++) {
                fac *= i;
            }

            assertEquals(fac, result.size(), "Expected " + fac + " permutations, but received " + result.size());
            result.sort((a, b) -> {
                if (a.length < b.length)
                    return -1;
                if (a.length > b.length)
                    return 1;
                for (int i = 0; i < a.length; i++) {
                    if (a[i] < b[i])
                        return -1;
                    if (a[i] > b[i])
                        return 1;
                }
                return 0;
            });

            int[] prev = null;
            for (int[] curr : result) {
                if (prev != null) {
                    assertNotEquals(prev, curr, "Result contained an instance twice");
                }

                assertEquals(n, curr.length, "Result contained an instance which is not of size " + n);
                for (int x : curr) {
                    if (x < 1 || x > n) {
                        throw new AssertionFailedError("Result contained an instance with out-of-range numbers");
                    }
                }

                int[] sorted = Arrays.stream(curr).sorted().toArray();
                for (int i = 1; i < sorted.length; i++) {
                    assertNotEquals(sorted[i - 1], sorted[i], "Result contained an instance with duplicate numbers");
                }

                prev = curr;
            }

            System.out.println(" passed in " + time + "ms");
        }
    }
}
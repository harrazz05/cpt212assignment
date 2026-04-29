import java.math.BigInteger;
import java.util.Random;

// Class name updated to match your filename Karatsuba.java
public class Karatsuba {

    // Counter class to track primitive operations (assignment, addition, etc.) [cite: 34, 43]
    static class OpCounter {
        long ops = 0;
        void add(int n) { ops += n; }
        void reset() { ops = 0; }
    }

    static OpCounter simpleCounter = new OpCounter();
    static OpCounter karatsubaCounter = new OpCounter();

    // --- PART 1: Simple Multiplication Algorithm [cite: 9, 33] ---
    public static BigInteger simpleMultiply(BigInteger x, BigInteger y, boolean printSteps) {
        String s1 = x.toString();
        String s2 = y.toString();
        int n = s1.length();
        BigInteger totalSum = BigInteger.ZERO;
        simpleCounter.add(1); // Assignment [cite: 34]

        // Step 1: Multiply each digit and keep track of carriers [cite: 11]
        for (int i = n - 1; i >= 0; i--) {
            int digit2 = s2.charAt(i) - '0';
            StringBuilder partialStr = new StringBuilder();
            StringBuilder carrierStr = new StringBuilder();
            int carrier = 0;

            for (int j = n - 1; j >= 0; j--) {
                int digit1 = s1.charAt(j) - '0';
                int prod = digit1 * digit2 + carrier;
                simpleCounter.add(2); // Multiplication + Addition [cite: 34]

                partialStr.insert(0, prod % 10);
                carrier = prod / 10;
                carrierStr.insert(0, carrier);
                simpleCounter.add(2); // Modulo + Division [cite: 34]
            }

            // Step 2: Add up shifted partial products [cite: 13]
            BigInteger partialRow = new BigInteger(partialStr.toString());
            int shift = (n - 1 - i);
            BigInteger shiftedRow = partialRow.multiply(BigInteger.TEN.pow(shift));
            totalSum = totalSum.add(shiftedRow);
            simpleCounter.add(3); // Shift + Addition assignments [cite: 34]

            // Print partials and carriers for small numbers as required [cite: 12, 50]
            if (printSteps && n <= 10) {
                System.out.println("Multiplier Digit " + digit2 + " | Partials: " + partialStr + " | Carriers: " + carrierStr);
            }
        }
        return totalSum;
    }

    // --- PART 2: Karatsuba Algorithm [cite: 42, 43] ---
    public static BigInteger mult(BigInteger x, BigInteger y) {
        karatsubaCounter.add(1); // Method call/overhead [cite: 43]

        // Base case: small numbers use simple multiplication
        if (x.compareTo(BigInteger.TEN) < 0 || y.compareTo(BigInteger.TEN) < 0) {
            karatsubaCounter.add(1);
            return x.multiply(y);
        }

        int n = Math.max(x.toString().length(), y.toString().length());
        int m = (n / 2) + (n % 2);
        karatsubaCounter.add(2);

        // Splitting logic using Base 10 as specified [cite: 10]
        BigInteger powerOf10 = BigInteger.TEN.pow(m);
        BigInteger a = x.divide(powerOf10);
        BigInteger b = x.remainder(powerOf10);
        BigInteger c = y.divide(powerOf10);
        BigInteger d = y.remainder(powerOf10);
        karatsubaCounter.add(5);

        // 3 Recursive calls
        BigInteger z0 = mult(a, c);
        BigInteger z2 = mult(b, d);
        BigInteger z1 = mult(a.add(b), c.add(d));
        karatsubaCounter.add(3);

        // Gauss trick to find middle term
        BigInteger middle = z1.subtract(z0).subtract(z2);
        karatsubaCounter.add(2);

        // Combining the result
        BigInteger result = z0.multiply(BigInteger.TEN.pow(2 * m))
                .add(middle.multiply(BigInteger.TEN.pow(m)))
                .add(z2);
        karatsubaCounter.add(4);

        return result;
    }

    // Helper to randomly generate n digits 
    public static BigInteger generateRandom(int n, Random r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(i == 0 ? r.nextInt(9) + 1 : r.nextInt(10));
        }
        return new BigInteger(sb.toString());
    }

    public static void main(String[] args) {
        Random rand = new Random();

        // Test Part 1 requirements: Print steps for small numbers [cite: 12, 50]
        System.out.println("--- Part 1: Simple Multiplication (Step-by-Step) ---");
        BigInteger n1 = new BigInteger("52301");
        BigInteger n2 = new BigInteger("38042");
        simpleMultiply(n1, n2, true);

        // Test Part 2 requirements: Large numbers and operation counting [cite: 43, 50]
        System.out.println("\n--- Part 2: Experiment Data for Graphing ---");
        System.out.println("n\tSimple_Ops\tKaratsuba_Ops");

        int[] testSizes = {10, 50, 100, 200, 500};
        for (int n : testSizes) {
            BigInteger a = generateRandom(n, rand);
            BigInteger b = generateRandom(n, rand);

            simpleCounter.reset();
            simpleMultiply(a, b, false);
            long simpleTotal = simpleCounter.ops;

            karatsubaCounter.reset();
            mult(a, b);
            long karaTotal = karatsubaCounter.ops;

            System.out.println(n + "\t" + simpleTotal + "\t\t" + karaTotal);
        }
    }
}
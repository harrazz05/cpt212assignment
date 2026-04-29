import java.math.BigInteger;
import java.util.Random;

public class Karatsuba {

    // Counter class to track primitive operations (assignment, addition, etc.) [cite: 34, 43]
    static class OpCounter {
        long ops = 0;
        void add(int n) { ops += n; }
        void reset() { ops = 0; }
    }

    static OpCounter simpleCounter = new OpCounter();
    static OpCounter karatsubaCounter = new OpCounter();

    // --- PART 1: Simple Multiplication Algorithm (Modified with provided logic) ---
    public static BigInteger simpleMultiply(BigInteger x, BigInteger y, boolean printSteps) {
        String s1 = x.toString();
        String s2 = y.toString();
        int n1_len = s1.length(); // Length of first number
        int n2_len = s2.length(); // Length of second number
        BigInteger totalSum = BigInteger.ZERO; simpleCounter.add(1); // Assignment

        // Outer loop for multiplier (s2) - Step 1
        for (int i = n2_len - 1; i >= 0; i--) { // Iterate over digits of s2
            simpleCounter.add(1); // Loop overhead
            int digit2 = s2.charAt(i) - '0'; simpleCounter.add(1);

            StringBuilder currentPartial = new StringBuilder();
            StringBuilder currentCarriersForPrint = new StringBuilder(); // Store carriers for printing
            int carrier = 0; simpleCounter.add(1);

            // Inner loop for multiplicand (s1)
            for (int j = n1_len - 1; j >= 0; j--) { // Iterate over digits of s1
                simpleCounter.add(1); // Loop overhead
                int digit1 = s1.charAt(j) - '0'; simpleCounter.add(1);

                int product = (digit1 * digit2) + carrier; simpleCounter.add(2); // Multi + Add
                int partial = product % 10; simpleCounter.add(1); // Modulo
                carrier = product / 10; simpleCounter.add(1);    // Division

                currentPartial.insert(0, partial);
                currentCarriersForPrint.insert(0, carrier); // Store carriers for printing
            }

            // Print for small numbers
            if (printSteps && n1_len <= 10) {
                System.out.println("multiplier digit: " + digit2);
                System.out.println("partial products: " + currentPartial);
                System.out.println("carriers:         " + currentCarriersForPrint);
            }

            // Step 2: Shifting and Adding
            BigInteger rowValue = calculateRowValueForSimpleMultiply(currentPartial.toString(), carrier, n2_len - 1 - i);
            simpleCounter.add(1); // Method call overhead
            totalSum = totalSum.add(rowValue); simpleCounter.add(1);
        }
        return totalSum;
    }

    // Helper method for simpleMultiply to calculate row value
    private static BigInteger calculateRowValueForSimpleMultiply(String partials, int finalCarrier, int shift) {
        simpleCounter.add(1); // Comparison
        String res = (finalCarrier > 0 ? finalCarrier : "") + partials;
        simpleCounter.add(1); // String concatenation / assignment
        BigInteger val = new BigInteger(res); simpleCounter.add(1);
        BigInteger shiftedVal = val.multiply(BigInteger.TEN.pow(shift)); simpleCounter.add(2); // Pow + Multiply
        return shiftedVal;
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

        // Splitting logic using Base 10
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


        BigInteger middle = z1.subtract(z0).subtract(z2);
        karatsubaCounter.add(2);

        // combining the result
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
        Random rand = new Random(42);

        // Test Part 1 requirements: Print steps for small numbers [cite: 12, 50]
        System.out.println("--- Part 1: Simple Multiplication (Step-by-Step) ---");
        BigInteger n1 = new BigInteger("52301");
        BigInteger n2 = new BigInteger("38042");
        simpleMultiply(n1, n2, true);

        // Test Part 2 requirements: Large numbers and operation counting [cite: 43, 50]
        System.out.println("\n--- Part 2: Experiment Data for Graphing ---");
        System.out.println("n\tSimple_Ops\tKaratsuba_Ops");

        int[] testSizes = {10, 50, 100, 200, 500, 1000, 2000};
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
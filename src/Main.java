import java.math.BigInteger;
import java.util.Random;

class SimpleMultiplication {
    // Global counter for primitive operations (assignments, additions, etc.)
    static long opCount = 0;

    public static String multiply(String num1, String num2) {
        int n = num1.length();
        int m = num2.length();
        BigInteger totalSum = BigInteger.ZERO; opCount++; // Assignment

        // Outer loop for multiplier (num2) - Step 1
        for (int i = m - 1; i >= 0; i--) {
            opCount++; // Loop overhead
            int digit2 = num2.charAt(i) - '0'; opCount++;

            StringBuilder currentPartial = new StringBuilder();
            StringBuilder currentCarriers = new StringBuilder();
            int carrier = 0; opCount++;

            // Inner loop for multiplicand (num1)
            for (int j = n - 1; j >= 0; j--) {
                opCount++; // Loop overhead
                int digit1 = num1.charAt(j) - '0'; opCount++;

                int product = (digit1 * digit2) + carrier; opCount += 2; // Multi + Add
                int partial = product % 10; opCount++; // Modulo
                carrier = product / 10; opCount++;    // Division

                currentPartial.insert(0, partial);
                currentCarriers.insert(0, carrier);
            }

            // Print for small numbers
            if (n <= 10) {
                System.out.println("multiplier digit: " + digit2);
                System.out.println("partial products: " + currentPartial);
                System.out.println("carriers:         " + currentCarriers);
            }

            // Step 2: Shifting and Adding
            BigInteger rowValue = calculateRowValue(currentPartial.toString(), carrier, m - 1 - i);
            opCount++; // Method call overhead
            totalSum = totalSum.add(rowValue); opCount++;
        }
        return totalSum.toString();
    }

    private static BigInteger calculateRowValue(String partials, int finalCarrier, int shift) {
        opCount++; // Comparison
        String res = (finalCarrier > 0 ? finalCarrier : "") + partials;
        opCount++;
        BigInteger val = new BigInteger(res); opCount++;
        BigInteger shiftedVal = val.multiply(BigInteger.TEN.pow(shift)); opCount += 2; // Pow + Multiply
        return shiftedVal;
    }

    static void main() {
        //PART A: Verification
        System.out.println("=== Manual Verification ===");
        String n1 = "52301";
        String n2 = "00380"; // Padding to ensure same length as per instructions
        opCount = 0; // Reset counter
        String result = multiply(n1, n2);
        System.out.println("Result: " + result);


        // --- PART B: Random Data Generation (For Graphing) ---
        System.out.println("\n=== Experimental Data Collection ===");
        System.out.println("n\tOperations\tTime (ns)");

        // Test for different values of n (e.g., 10, 100, 1000, up to 10000)
        int[] testSizes = {10, 50, 100,200, 500, 1000, 2000, 5000};

        for (int n : testSizes) {
            String random1 = generateRandomNumber(n);
            String random2 = generateRandomNumber(n);

            opCount = 0; // Reset for each size

            // Start timing
            long startTime = System.nanoTime();

            multiply(random1, random2);

            // End timing
            long endTime = System.nanoTime();
            long duration = endTime - startTime; // duration in nanoseconds

            // Print n, opCount, and time
            System.out.println(n + "\t" + opCount);// + "\t" + duration);
        }
    }

    // Helper method to generate a random number with n digits
    public static String generateRandomNumber(int n) {
        Random rand = new Random(42);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            // First digit shouldn't be 0 if you want exactly n digits
            if (i == 0) sb.append(rand.nextInt(9) + 1);
            else sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}
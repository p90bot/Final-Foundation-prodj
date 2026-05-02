import java.util.*;

public class Main {

    // List to store coefficients as strings
    // Each index represents the exponent (index 0 = constant, 1 = x, 2 = x^2, etc.)
    private List<String> data;

    // Constructor: initializes the list of coefficients
    public Main(List<String> data) {
        this.data = data;
    }

    // Converts the polynomial into a readable string
    // Starts recursion from index 0 (constant term)
    public String toString() {
        return rec_string(0);
    }

    // Recursive method to build the polynomial string
    private String rec_string(int index) {

        // Base case: if we've gone past the last coefficient, return empty string
        if (index >= data.size()) return "";

        // Convert current coefficient from string to double
        double c = Double.parseDouble(data.get(index));

        // Recursively process the rest of the list
        String ret = rec_string(index + 1);

        // If coefficient is 0, skip this term completely
        if (c == 0) return ret;

        String term;

        // Build the term depending on the exponent (index)
        if (index == 0) {
            // Constant term (no x)
            term = formatNumber(c);
        } else if (index == 1) {
            // x term
            term = formatCoeff(c) + "x";
        } else {
            // Higher powers like x^2, x^3, etc.
            term = formatCoeff(c) + "x^" + index;
        }

        // If nothing comes after this term, just return it
        if (ret.equals("")) return term;

        // Handle positive and negative signs properly
        if (c > 0) {
            return term + " + " + ret;
        } else {
            // Remove extra negative sign from ret to avoid "--"
            return term + " - " + ret.replaceFirst("-", "");
        }
    }

    // Formats coefficient for terms with x
    // Handles special cases like:
    // 1x -> x
    // -1x -> -x
    private String formatCoeff(double c) {
        if (c == 1) return "";
        if (c == -1) return "-";
        return formatNumber(c);
    }

    // Formats numbers so integers don’t show as decimals
    // Example: 5.0 -> "5", but 5.5 stays "5.5"
    private String formatNumber(double c) {
        if (c == (int) c) return "" + (int) c;
        return "" + c;
    }

    // Main method: runs the program
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);

        // Ask user for input
        System.out.println("Enter coefficients separated by spaces:");
        String input = kb.nextLine();

        // Split input into a list of strings
        List<String> data = Arrays.asList(input.split(" "));

        // Create polynomial object
        Main poly = new Main(data);

        // Print the polynomial
        System.out.println("Polynomial:");
        System.out.println(poly);

        kb.close();
    }
}
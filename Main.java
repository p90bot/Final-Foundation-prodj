import java.util.*;

public class Main {

    private List<String> data;

    // Constructor
    public Main(List<String> data) {
        this.data = data;
    }

    // Convert polynomial to string
    public String toString() {
        return rec_string(0);
    }

    // Recursive method
    private String rec_string(int index) {
        if (index >= data.size()) return "";

        double c = Double.parseDouble(data.get(index));
        String ret = rec_string(index + 1);

        if (c == 0) return ret;

        String term;

        // Build term based on exponent
        if (index == 0) {
            term = formatNumber(c);
        } else if (index == 1) {
            term = formatCoeff(c) + "x";
        } else {
            term = formatCoeff(c) + "x^" + index;
        }

        if (ret.equals("")) return term;

        if (c > 0) return term + " + " + ret;
        else return term + " - " + ret.replaceFirst("-", "");
    }

    // Format coefficient (handles 1x, -1x)
    private String formatCoeff(double c) {
        if (c == 1) return "";
        if (c == -1) return "-";
        return formatNumber(c);
    }

    // Remove .0 for integers
    private String formatNumber(double c) {
        if (c == (int) c) return "" + (int) c;
        return "" + c;
    }

    // Main method to test
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);

        System.out.println("Enter coefficients separated by spaces:");
        String input = kb.nextLine();

        List<String> data = Arrays.asList(input.split(" "));
        Main poly = new Main(data);

        System.out.println("Polynomial:");
        System.out.println(poly);

        kb.close();
    }
}
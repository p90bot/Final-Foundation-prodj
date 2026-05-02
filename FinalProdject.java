import java.util.*;

public class FinalProdject {

    public static void main(String[] args) {
        // Use Scanner to read user input
        try (Scanner kb = new Scanner(System.in)) {

            System.out.print("Enter expression: ");
            String input = kb.nextLine();

            // Check for empty input
            if (input.trim().isEmpty()) {
                System.out.println("Error: Empty expression");
                return;
            }

            // ================= TOKENIZE =================
            // Convert raw string into tokens (numbers, operators, parentheses)
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            // Print tokens
            System.out.println("Tokens:");
            for (Token t : tokens) {
                System.out.print(t + " ");
            }
            System.out.println();

            // ================= PARSE =================
            // Build expression tree from tokens
            Parser parser = new Parser(tokens);
            Node root = parser.expression();

            // Ensure no extra tokens are left (invalid input if there are)
            if (parser.hasRemaining()) {
                throw new RuntimeException("Unexpected tokens after expression");
            }

            System.out.println("Parse successful!");

            // ================= PRINT TREE =================
            // Display tree structure in a clean visual format
            System.out.println("\nExpression Tree:");
            printTree(root, "", true);

            // ================= EVALUATE =================
            // Compute the result of the expression
            double result = evaluate(root);
            System.out.println("\nEvaluation Result: " + result);
        } catch (Exception e) {
            // Catch and display any errors
            System.err.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // TREE PRINTER - Displays the AST (Abstract Syntax Tree)
    // Uses box-drawing characters for clean formatting
    // =========================================================
    
    public static void printTree(Node node, String prefix, boolean isLast) {
        if (node == null) return;
        
        // Print current node
        System.out.println(prefix + (isLast ? "└── " : "├── ") + node.value);
        
        // Collect children (left and right)
        List<Node> children = new ArrayList<>();
        if (node.left != null) children.add(node.left);
        if (node.right != null) children.add(node.right);
        
        // Recursively print children
        for (int i = 0; i < children.size(); i++) {
            boolean lastChild = (i == children.size() - 1);

            // Adjust prefix to maintain tree structure
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            printTree(children.get(i), newPrefix, lastChild);
        }
    }

    // =========================================================
    // EVALUATION - Recursively computes expression value
    // =========================================================

    public static double evaluate(Node node) {

        // Base case: leaf node (number)
        if (node.left == null && node.right == null) {
            return Double.parseDouble(node.value);
        }

        // Recursively evaluate left and right subtrees
        double left = node.left != null ? evaluate(node.left) : 0;
        double right = node.right != null ? evaluate(node.right) : 0;

        // Apply operation based on node value
        switch (node.value) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": 
                if (right == 0) throw new ArithmeticException("Division by zero");
                return left / right;

            // Unary minus (negation)
            case "u-": return -right;

            default:
                throw new RuntimeException("Invalid operator: " + node.value);
        }
    }

    // =============================================================
    // TOKEN CLASS - Represents a single token
    // Example: NUMBER(5), OP(+), LPAREN(()
    // =============================================================

    static class Token {
        String type;   // Type: NUMBER, OP, LPAREN, RPAREN
        String value;  // Actual value

        public Token(String type, String value) {
            this.type = type;
            this.value = value;
        }

        // Print token in readable format
        @Override
        public String toString() {
            return "[" + type + ":" + value + "]";
        }
    }

    // =============================================================
    // TOKENIZER - Breaks input string into tokens
    // =============================================================

    static class Tokenizer {
        private String input;
        private int pos = 0;  // Current position in string

        public Tokenizer(String input) {
            // Remove spaces for easier parsing
            this.input = input.replaceAll(" ", "");
        }

        public List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();

            // Loop through input string
            while (pos < input.length()) {
                char c = input.charAt(pos);

                // If digit → build full number
                if (Character.isDigit(c)) {
                    StringBuilder num = new StringBuilder();

                    while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                        num.append(input.charAt(pos++));
                    }

                    tokens.add(new Token("NUMBER", num.toString()));
                }

                // Operators
                else if ("+-*/".indexOf(c) != -1) {
                    tokens.add(new Token("OP", String.valueOf(c)));
                    pos++;
                }

                // Left parenthesis
                else if (c == '(') {
                    tokens.add(new Token("LPAREN", "("));
                    pos++;
                }

                // Right parenthesis
                else if (c == ')') {
                    tokens.add(new Token("RPAREN", ")"));
                    pos++;
                }

                // Invalid character
                else {
                    throw new RuntimeException("Unknown character: " + c);
                }
            }

            return tokens;
        }
    }

    // =============================================================
    // NODE - Represents a node in the expression tree
    // Each node can have left and right children
    // =============================================================

    static class Node {
        String value; // operator or number
        Node left, right;

        public Node(String value) {
            this.value = value;
        }
    }

    // =============================================================
    // PARSER - Builds the expression tree using recursive descent
    // Grammar:
    // expression → term ((+ | -) term)*
    // term → factor ((* | /) factor)*
    // factor → NUMBER | '(' expression ')' | unary minus
    // =============================================================

    static class Parser {
        private List<Token> tokens;
        private int pos = 0; // Current position in token list

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        // Look at current token without consuming it
        private Token peek() {
            if (pos < tokens.size()) return tokens.get(pos);
            return null;
        }

        // Consume and return current token
        private Token consume() {
            return tokens.get(pos++);
        }

        // Check if there are leftover tokens
        public boolean hasRemaining() {
            return pos < tokens.size();
        }

        // Handles + and - (lowest precedence)
        public Node expression() {
            Node node = term();

            while (peek() != null && (peek().value.equals("+") || peek().value.equals("-"))) {
                Token op = consume();

                Node newNode = new Node(op.value);
                newNode.left = node;
                newNode.right = term();

                node = newNode;
            }

            return node;
        }

        // Handles * and / (higher precedence)
        private Node term() {
            Node node = factor();

            while (peek() != null && (peek().value.equals("*") || peek().value.equals("/"))) {
                Token op = consume();

                Node newNode = new Node(op.value);
                newNode.left = node;
                newNode.right = factor();

                node = newNode;
            }

            return node;
        }

        // Handles numbers, parentheses, and unary minus
        private Node factor() {
            Token token = peek();

            if (token == null) {
                throw new RuntimeException("Unexpected end of expression");
            }

            // Unary minus (e.g., -5 or -(3+2))
            if (token.type.equals("OP") && token.value.equals("-")) {
                consume();

                Node newNode = new Node("u-");
                newNode.right = factor();

                return newNode;
            }

            // Number
            if (token.type.equals("NUMBER")) {
                consume();
                return new Node(token.value);
            }

            // Parentheses
            if (token.type.equals("LPAREN")) {
                consume();

                Node node = expression();

                if (peek() == null || !peek().type.equals("RPAREN")) {
                    throw new RuntimeException("Missing ')'");
                }

                consume();
                return node;
            }

            throw new RuntimeException("Invalid expression at: " + token.value);
        }
    }
}
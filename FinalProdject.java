import java.util.*;

public class FinalProdject {

    public static void main(String[] args) {
        try (Scanner kb = new Scanner(System.in)) {
            System.out.print("Enter expression: ");
            String input = kb.nextLine();

            if (input.trim().isEmpty()) {
                System.out.println("Error: Empty expression");
                return;
            }

            // ================= TOKENIZE =================
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            System.out.println("Tokens:");
            for (Token t : tokens) {
                System.out.print(t + " ");
            }
            System.out.println();

            // ================= PARSE =================
            Parser parser = new Parser(tokens);
            Node root = parser.expression();

            if (parser.hasRemaining()) {
                throw new RuntimeException("Unexpected tokens after expression");
            }

            System.out.println("Parse successful!");

            // ================= PRINT TREE =================
            System.out.println("\nExpression Tree:");
            printTree(root, "", true);

            // ================= EVALUATE =================
            double result = evaluate(root);
            System.out.println("\nEvaluation Result: " + result);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ================= TREE PRINTER =================
    public static void printTree(Node node, String prefix, boolean isLast) {
        if (node == null) return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + node.value);

        List<Node> children = new ArrayList<>();
        if (node.left != null) children.add(node.left);
        if (node.right != null) children.add(node.right);

        for (int i = 0; i < children.size(); i++) {
            boolean lastChild = (i == children.size() - 1);
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            printTree(children.get(i), newPrefix, lastChild);
        }
    }

    // ================= EVALUATION =================
    public static double evaluate(Node node) {
        if (node.left == null && node.right == null) {
            return Double.parseDouble(node.value);
        }

        double left = node.left != null ? evaluate(node.left) : 0;
        double right = node.right != null ? evaluate(node.right) : 0;

        switch (node.value) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/":
                if (right == 0) throw new ArithmeticException("Division by zero");
                return left / right;
            case "u-": return -right;
            default:
                throw new RuntimeException("Invalid operator: " + node.value);
        }
    }

    // ================= TOKEN =================
    static class Token {
        String type;
        String value;

        public Token(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return "[" + type + ":" + value + "]";
        }
    }

    // ================= TOKENIZER =================
    static class Tokenizer {
        private String input;
        private int pos = 0;

        public Tokenizer(String input) {
            this.input = input.replaceAll(" ", "");
        }

        public List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();

            while (pos < input.length()) {
                char c = input.charAt(pos);

                if (Character.isDigit(c)) {
                    StringBuilder num = new StringBuilder();
                    while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                        num.append(input.charAt(pos++));
                    }
                    tokens.add(new Token("NUMBER", num.toString()));
                }
                else if ("+-*/".indexOf(c) != -1) {
                    tokens.add(new Token("OP", String.valueOf(c)));
                    pos++;
                }
                else if (c == '(') {
                    tokens.add(new Token("LPAREN", "("));
                    pos++;
                }
                else if (c == ')') {
                    tokens.add(new Token("RPAREN", ")"));
                    pos++;
                }
                else {
                    throw new RuntimeException("Unknown character: " + c);
                }
            }

            return tokens;
        }
    }

    // ================= NODE =================
    static class Node {
        String value;
        Node left, right;

        public Node(String value) {
            this.value = value;
        }
    }

    // ================= PARSER =================
    static class Parser {
        private List<Token> tokens;
        private int pos = 0;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        private Token peek() {
            if (pos < tokens.size()) return tokens.get(pos);
            return null;
        }

        private Token consume() {
            return tokens.get(pos++);
        }

        public boolean hasRemaining() {
            return pos < tokens.size();
        }

        // 🔥 UPDATED METHOD HERE
       public Node expression() {
    Node node = term();

    while (peek() != null && (peek().value.equals("+") || peek().value.equals("-"))) {
        Token op = consume();

        Node newNode = new Node(op.value);
        newNode.left = node;

        // 🔥 Handle ++ and --
        if (peek() != null && peek().type.equals("OP")) {
            if (peek().value.equals("+")) {
                consume(); // second '+'
                newNode.right = new Node("1"); // +1
            } 
            else if (peek().value.equals("-")) {
                consume(); // second '-'
                newNode.right = new Node("1"); // subtract 1
            } 
            else {
                newNode.right = term();
            }
        } 
        else {
            newNode.right = term();
        }

        node = newNode;
    }

    return node;
}

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

        private Node factor() {
            Token token = peek();

            if (token == null) {
                throw new RuntimeException("Unexpected end of expression");
            }

            // Unary minus
            if (token.type.equals("OP") && token.value.equals("-")) {
                consume();
                Node newNode = new Node("u-");
                newNode.right = factor();
                return newNode;
            }

            if (token.type.equals("NUMBER")) {
                consume();
                return new Node(token.value);
            }

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
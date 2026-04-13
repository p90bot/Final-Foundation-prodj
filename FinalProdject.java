import java.util.*;

public class FinalProdject {
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);

        System.out.print("Enter expression: ");
        String input = kb.nextLine();

        // Step 1: Tokenize
        Tokenizer tokenizer = new Tokenizer(input);
        List<Token> tokens = tokenizer.tokenize();

        System.out.println("Tokens:");
        for (Token t : tokens) {
            System.out.print(t.value + " ");
        }
        System.out.println();

        // Step 2: Parse (build tree)
        Parser parser = new Parser(tokens);
        Node root = parser.expression();

        System.out.println("Parse successful!");
        // Step 2: Parse (build tree)


       // Step 3: Print Tree
      System.out.println("\nPretty Expression Tree:");
      printPrettyTree(root);

      
      

      // Step 4: Evaluate
      int result = evaluate(root);
      System.out.println("\nEvaluation Result: " + result);

        kb.close();
    }

    public static int evaluate(Node node) {
        // Leaf node (number)
        if (node.left == null && node.right == null) {
            return Integer.parseInt(node.value);
        }

        int left = evaluate(node.left);
        int right = evaluate(node.right);

        switch (node.value) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": return left / right;
        }

        throw new RuntimeException("Invalid operator");
    }

  public static void printPrettyTree(Node root) {
    int height = getHeight(root);
    int width = (int) Math.pow(2, height) * 2;

    List<StringBuilder> lines = new ArrayList<>();
    for (int i = 0; i < height * 2; i++) {
        StringBuilder line = new StringBuilder();
        for (int j = 0; j < width; j++) {
            line.append(" ");
        }
        lines.add(line);
    }

    fillTree(lines, root, 0, width / 2, width / 4);

    for (StringBuilder line : lines) {
        System.out.println(line.toString());
    }
}

  public static void printBranches(Node node) {
    if (node.left != null) System.out.print(" / ");
    else System.out.print("  ");
    if (node.right != null) System.out.print(" \\ ");
    else System.out.print("  ");

    System.out.println();
  }
  public static void fillTree(List<StringBuilder> lines, Node node, int row, int col, int gap) {
    if (node == null) return;

    String val = node.value;
    for (int i = 0; i < val.length(); i++) {
        lines.get(row).setCharAt(col + i, val.charAt(i));
    }

    if (node.left != null) {
        lines.get(row + 1).setCharAt(col - gap, '/');
        fillTree(lines, node.left, row + 2, col - gap * 2, gap / 2);
    }

    if (node.right != null) {
        lines.get(row + 1).setCharAt(col + gap, '\\');
        fillTree(lines, node.right, row + 2, col + gap * 2, gap / 2);
    }
}
    public static int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }


}

class Token {
    public String type;
    public String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

class Tokenizer {
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
                String num = "";
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                    num += input.charAt(pos++);
                }
                tokens.add(new Token("NUMBER", num));
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
        }
        return tokens;
    }
}

class Node {
    String value;
    Node left, right;

    public Node(String value) {
        this.value = value;
    }
}

class Parser {
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

        if (token.type.equals("NUMBER")) {
            consume();
            return new Node(token.value);
        }

        if (token.type.equals("LPAREN")) {
            consume();
            Node node = expression();
            consume(); // RPAREN
            return node;
        }

        throw new RuntimeException("Invalid expression");
    }
}

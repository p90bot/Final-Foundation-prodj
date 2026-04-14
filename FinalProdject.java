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
    public static int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    public static void printPrettyTree(Node root) {
    int height = getHeight(root);
    int width = (int) Math.pow(2, height ) * 3;

      List<StringBuilder> lines = new ArrayList<>();
    for (int i = 0; i < height * 2 - 1; i++) {
        // For Java 8 compatibility (no String.repeat())
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < width; j++) sb.append(' ');
        lines.add(sb);
    }
     fillTree(lines, root, 0, width / 2, width / 5);
    
    for (StringBuilder line : lines) {
        // Trim trailing spaces only
        String str = line.toString();
        int end = str.length();
        while (end > 0 && str.charAt(end - 1) == ' ') end--;
        System.out.println(str.substring(0, end));
    }
}
public static void fillTree(List<StringBuilder> lines, Node node, int row, int col, int gap) {
    if (node == null || row >= lines.size()) return;

    String val = node.value;

    // Center the value at col
    int startPos = col - val.length() / 2;
    for (int i = 0; i < val.length(); i++) {
        int pos = startPos + i;
        if (pos >= 0 && pos < lines.get(row).length()) {
            lines.get(row).setCharAt(pos, val.charAt(i));
        }
    }

    // Draw branches and recurse
    if (row + 1 < lines.size()) {

        int newGap = Math.max(1, gap / 2);
        // Left branch
        if (node.left != null) {
            int leftCol = col - gap;
            if (leftCol >= 0 && leftCol < lines.get(row + 1).length()) {
                lines.get(row + 1).setCharAt(leftCol, '/');
            }
            // FIXED: Use leftCol (where the branch is), not col - gap * 2
            fillTree(lines, node.left, row + 2, leftCol, newGap);
        }

        // Right branch
        if (node.right != null) {
            int rightCol = col + gap;
            if (rightCol >= 0 && rightCol < lines.get(row + 1).length()) {
                lines.get(row + 1).setCharAt(rightCol, '\\');
            }
            // FIXED: Use rightCol (where the branch is), not col + gap * 2
            fillTree(lines, node.right, row + 2, rightCol, newGap);
        }
    }
}

 
    
   
public static void printLevel(List<Node> nodes, int level, int maxLevel, int width) {
    if (nodes.isEmpty() || isAllNull(nodes)) return;

    int floor = maxLevel - level;
    int edgeLines = (int) Math.pow(2, Math.max(floor - 1, 0));
    int firstSpaces = (int) Math.pow(2, floor) ;
    int betweenSpaces = (int) Math.pow(2, floor + 1) * 1;

    printSpaces(firstSpaces);

    List<Node> newNodes = new ArrayList<>();
    for (Node node : nodes) {
        if (node != null) {
            System.out.print(node.value);
            newNodes.add(node.left);
            newNodes.add(node.right);
        } else {
            System.out.print(" ");
            newNodes.add(null);
            newNodes.add(null);
        }
        printSpaces(betweenSpaces);
    }
    System.out.println();

    // print branches
    for (int i = 1; i <= edgeLines; i++) {
        for (int j = 0; j < nodes.size(); j++) {
            printSpaces(firstSpaces - i);

            if (nodes.get(j) == null) {
                printSpaces(edgeLines * 2 + i + 1);
                continue;
            }

            if (nodes.get(j).left != null)
                System.out.print("/");
            else
                printSpaces(1);

            printSpaces(i * 2 - 1);

            if (nodes.get(j).right != null)
                System.out.print("\\");
            else
                printSpaces(1);

            printSpaces(edgeLines * 2 - i);
        }
        System.out.println();
    }

    printLevel(newNodes, level + 1, maxLevel, width);
}
public static void printSpaces(int count) {
    for (int i = 0; i < count; i++) System.out.print(" ");
}

public static boolean isAllNull(List<Node> list) {
    for (Node node : list) {
        if (node != null) return false;
    }
    return true;
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
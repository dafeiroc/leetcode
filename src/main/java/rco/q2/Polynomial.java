package rco.q2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Polynomial {

  private String expression;
  private List<HashMap<Character, Integer>> vals;

  public Polynomial(String expression, List<HashMap<Character, Integer>> vals) {
    this.expression = expression;
    this.vals = vals;
  }

  public static void main(String[] args) {
    Polynomial polynomial = getInputFromFile("src/main/resources/rco/q2/input0.txt");
    outputToFile(polynomial, "src/main/resources/rco/q2/out0.txt");
  }

  public static boolean isOp(String s) {
    return "+".equals(s) || "-".equals(s) || "*".equals(s);
  }
  public static boolean isOp(char c) {
    return c == '+' || c == '-' || c == '*';
  }
  public static boolean isInt(String token) {
    return token.matches("^-?\\d+$");
  }

  /**
   * if op1 has higher precedence than op2, return 1, else -1, if precedence is same, return 0.
   * @param op1
   * @param op2
   * @return 0, 1, -1
   */
  public static int precedence(char op1, char op2) {
    if (!isOp(op1) || !isOp(op2)) {
      throw new IllegalArgumentException(String.format("operator %s or %s is invalid", op1, op2));
    } else  {
      Map<Character, Integer> map = new HashMap<>();
      map.put('*', 1);
      map.put('+', 0);
      map.put('-', 0);
      return map.get(op1) - map.get(op2);
    }

  }

  /**
   * Use one loop to read polynomial then convert it to reverse polish notation, if any integer is present, substitute the integer for variable.
   * input a+4-b+10+c, and a = 5 output: 5 4 + b - 10 + c +
   * @param expr arithmetic expression
   * @param map the hashmap held the variable and integer
   * @return reverse polish notation
   */
  public static String[] rpn(String expr, Map<Character, Integer> map) {
    List<String> out = new ArrayList<>();
    Stack<String> stack = new Stack<>();

    char tokens[] = expr.toCharArray();
    for (int i = 0; i < tokens.length; i++) {
      if (Character.isDigit(tokens[i])) {
        // if an integer
        StringBuilder sb = new StringBuilder();
        while (i < tokens.length && Character.isDigit(tokens[i])) {
          sb.append(tokens[i]);
          i++;
        }
        out.add(sb.toString());
        i--;

      } else if (Character.isLowerCase(tokens[i])) {
        // if [a-z]
        if (map.containsKey(tokens[i])) {
          out.add(String.valueOf(map.get(tokens[i])));
        } else {
          out.add(String.valueOf(tokens[i]));
        }

      } else if (tokens[i] == '(') {
        stack.push("(");
      } else if (tokens[i] == ')') {
        while (!stack.empty() && !stack.peek().equals("(")) {
          out.add(stack.pop());
        }
        stack.pop();
      } else if (isOp(tokens[i])) {
        while (!stack.empty() && isOp(stack.peek())) {
          if (precedence(tokens[i], stack.peek().charAt(0)) <= 0) {
            out.add(stack.pop());
            continue;
          }
          break;
        }
        stack.push(String.valueOf(tokens[i]));
      }

    }

    while (!stack.empty()) {
      out.add(stack.pop());
    }
    String s[] = new String[out.size()];
    return out.toArray(s);
  }


  public static String evaluate(String tokens[]) {
    Stack<String> stack = new Stack<>();
    for (String token : tokens) {
      if (!isOp(token)) {
        stack.push(token);
      } else {
        String t2 = stack.pop();
        String t1 = stack.pop();

        String result = evaluate(t1, t2, token);
        stack.push(result);
      }
    }
    return stack.pop();
  }

  public static String evaluate(String token1, String token2, String op) {

    StringBuilder sb = new StringBuilder();
    if (isInt(token1) && isInt(token2)) {
      sb.append(evaluate(Integer.parseInt(token1), Integer.parseInt(token2), op.charAt(0)));
    } else if (isInt(token1) && !isInt(token2)) {
      // if 3 - x, then 3-x or if 3 * x, then 3x
      sb.append(evaluate(Integer.parseInt(token1), token2, op, true));
    } else if(!isInt(token1) && isInt(token2)) {
      // if x - 3, then x-3 or if x * 3, then 3x
       sb.append(evaluate(Integer.parseInt(token2), token1, op, false));
    } else {
      sb.append(token1);
      if ("+".equals(op) || "-".equals(op)) {
        sb.append(op);
      }
      sb.append(token2);
    }
    return sb.toString();

  }


  public static String evaluate(int t1, String t2, String op, boolean flag) {
    if (t1 == 0) {
      if("+".equals(op) || "-".equals(op)) {
        return t2;
      } else if("*".equals(op)) {
        return "0";
      } else {
        throw new UnsupportedOperationException(String.format("operator %s is not supported!", op));
      }
    } else {
      if("+".equals(op) || "-".equals(op)) {
        if(flag) {
          return new StringBuilder().append(t1).append(op).append(t2).toString();
        } else {
          return new StringBuilder().append(t2).append(op).append(t1).toString();
        }
      } else if("*".equals(op)) {
        if(t1 == 1) {
          return t2;
        } else if(t1 == -1) {
          return "-" + t2;
        } else {
          return new StringBuilder().append(t1).append(t2).toString();
        }
      } else {
        throw new UnsupportedOperationException(String.format("operator %s is not supported!", op));
      }
    }
  }

  public static int evaluate(int t1, int t2, char op) {
    switch (op) {
      case '+':
        return t1 + t2;
      case '-':
        return t1 - t2;
      case '*':
        return t1 * t2;
    }
    throw new UnsupportedOperationException(String.format("operator %c is not supported!", op));
  }


  public static Polynomial getInputFromFile(String inputFilePath) {
    String expr = null;
    List<HashMap<Character, Integer>> vals = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
      String line;
      for (int i = 0; (line = br.readLine()) != null; i++) {
        if (i == 0) {
          expr = line;
        } else if (i > 1) {
          String equations[] = line.split(",");
          HashMap<Character, Integer> kv = new HashMap<>();
          for (String equation : equations) {
            String v[] = equation.split("=");
            if (v.length == 2) {
              kv.put(v[0].charAt(0), Integer.parseInt(v[1]));
            }
          }
          vals.add(kv);
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return new Polynomial(expr, vals);

  }

  public static void outputToFile(Polynomial polynomial, String outputFilePath) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
      for (HashMap<Character, Integer> val : polynomial.vals) {
        String result = evaluate(rpn(polynomial.expression, val));
        bw.write(result);
        bw.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



}

package rco.q1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourColorGraph {

  private List<Character> vertices;
  private int[][] adjacencyMatrix;
  private List<String> source;

  public FourColorGraph(List<Character> vertices, int[][] adjacencyMatrix, List<String> source) {
    this.vertices = vertices;
    this.adjacencyMatrix = adjacencyMatrix;
    this.source = source;
  }

  /**
   * Get one solution and save it in result array.
   *
   * @param adjacencyMatrix the adjacency matrix of the graph
   * @param result          solution
   */
  public static void paint(int[][] adjacencyMatrix, int result[]) {
    int color = 1; //start from the first color
    int area = 1; //start from the 2nd area
    int k;
    result[0] = 1; // suppose 1st area's color is 1
    while (area < result.length) {
      while (color <= 4) {
        if (area >= result.length)
          break;
        k = 0;
        while ((k < area) && (result[k] * adjacencyMatrix[area][k] != color))
          k++;
        if (k < area)
          color++;
        else {
          result[area] = color;
          area++;
          color = 1;
        }
      }
      if (color > 4) {
        area = area - 1;
        color = result[area] + 1;
      }
    }
  }

  /**
   * Generate the Graph object(vertex array and adjacency matrix).
   *
   * @param inputFilePath the input file path
   * @return the Graph object.
   * @throws IOException input file not found or IO exception.
   */
  public static FourColorGraph getInputFromFile(String inputFilePath) {
    // vertex list for graph
    List<Character> vertices = new ArrayList<>();
    // original matrix
    List<String> source = new ArrayList<>();
    String s;
    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
      while ((s = br.readLine()) != null) {
        source.add(s);
        for (int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if (!vertices.contains(c)) {
            vertices.add(c);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // adjacency matrix for graph
    int[][] adjacencyMatrix = new int[vertices.size()][vertices.size()];

    for (int i = 0; i < source.size(); i++) {
      String line = source.get(i);
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        if (j < line.length() - 1) {
          char cRight = line.charAt(j + 1);
          if (c != cRight) {
            adjacencyMatrix[vertices.indexOf(c)][vertices.indexOf(cRight)] = 1;
            adjacencyMatrix[vertices.indexOf(cRight)][vertices.indexOf(c)] = 1;
          }
        }
        if (i < source.size() - 1) {
          char cDown = source.get(i + 1).charAt(j);
          if (c != cDown) {
            adjacencyMatrix[vertices.indexOf(c)][vertices.indexOf(cDown)] = 1;
            adjacencyMatrix[vertices.indexOf(cDown)][vertices.indexOf(c)] = 1;
          }
        }
      }
    }
    return new FourColorGraph(vertices, adjacencyMatrix, source);
  }

  /**
   * Output one solution to a file.
   *
   * @param graph graph object
   * @param outputFilePath output file path
   * @throws IOException
   */
  public static void outputToFile(FourColorGraph graph, String outputFilePath) {
    int[] result = new int[graph.vertices.size()];
    paint(graph.adjacencyMatrix, result);
    final Map<Integer, Character> map = new HashMap<>();
    map.put(1, '+');
    map.put(2, '-');
    map.put(3, '*');
    map.put(4, '/');

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
      for (String line : graph.source) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
          char c = line.charAt(i);
          int index = graph.vertices.indexOf(c);
          sb.append(map.get(result[index]));
        }
        bw.write(sb.toString());
        bw.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public static void main(String[] args) throws IOException {
    FourColorGraph graph = getInputFromFile("src/main/resources/rco/q1/input0.txt");
    outputToFile(graph, "src/main/resources/rco/q1/output0.txt");
  }
}
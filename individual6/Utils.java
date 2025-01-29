package aed.individual6;

import es.upm.aedlib.positionlist.*;
import es.upm.aedlib.set.*;

import java.util.LinkedList;

import es.upm.aedlib.graph.*;
import es.upm.aedlib.map.HashTableMap;

public class Utils {

  /**
   * Helper method to find all vertices reachable from a starting vertex.
   * This method uses Breadth-First Search (BFS) to traverse the graph and 
   * collect all reachable vertices, following only edges marked as `true`.
   */
  private static <V> Set<Vertex<V>> reachableFrom(DirectedGraph<V, Boolean> graph, Vertex<V> start) {
      // Initialize a set to track visited vertices
      Set<Vertex<V>> visited = new HashTableMapSet<>();
      // Initialize a queue for BFS traversal
      LinkedList<Vertex<V>> queue = new LinkedList<>();
      queue.add(start); // Start from the given vertex
      visited.add(start); // Mark the starting vertex as visited

      // Perform BFS
      while (!queue.isEmpty()) {
          Vertex<V> current = queue.poll(); // Dequeue the current vertex
          // Iterate through all outgoing edges from the current vertex
          for (Edge<Boolean> edge : graph.outgoingEdges(current)) {
              if (edge.element()) { // Consider only edges marked as `true`
                  Vertex<V> neighbor = graph.endVertex(edge); // Get the edge's destination vertex
                  if (!visited.contains(neighbor)) { // Check if the neighbor is unvisited
                      visited.add(neighbor); // Mark the neighbor as visited
                      queue.add(neighbor); // Add the neighbor to the queue
                  }
              }
          }
      }
      return visited; // Return the set of reachable vertices
  }

  /**
   * Finds the set of vertices that are reachable from BOTH vertices `v1` and `v2`.
   * It calculates the intersection of vertices reachable from each vertex individually.
   */
  public static <V> Set<Vertex<V>> reachableFromBoth(DirectedGraph<V, Boolean> g, Vertex<V> v1, Vertex<V> v2) {
      // Get the set of vertices reachable from v1
      Set<Vertex<V>> reachableFromV1 = reachableFrom(g, v1);
      // Get the set of vertices reachable from v2
      Set<Vertex<V>> reachableFromV2 = reachableFrom(g, v2);

      // Create a result set to store the intersection
      Set<Vertex<V>> result = new HashTableMapSet<>();
      for (Vertex<V> vertex : reachableFromV1) {
          if (reachableFromV2.contains(vertex)) { // Check if the vertex is in both sets
              result.add(vertex); // Add the vertex to the result
          }
      }

      return result; // Return the intersection of reachable vertices
  }

  /**
   * Finds a path from `from` to `to` in an undirected graph such that the sum of edge weights
   * along the path is less than the given limit. If no such path exists, returns null.
   */
  public static <V> PositionList<Edge<Integer>> existsPathLess(
      UndirectedGraph<V, Integer> g, Vertex<V> from, Vertex<V> to, int limit) {

      // Helper class to store a path's state during traversal
      class PathNode {
          Vertex<V> vertex; // Current vertex
          PositionList<Edge<Integer>> path; // Path taken so far
          int weightSum; // Total weight of the path

          PathNode(Vertex<V> vertex, PositionList<Edge<Integer>> path, int weightSum) {
              this.vertex = vertex;
              this.path = path;
              this.weightSum = weightSum;
          }
      }

      // Initialize a set to track visited vertices
      Set<Vertex<V>> visited = new HashTableMapSet<>();
      // Initialize a queue for BFS-like traversal
      LinkedList<PathNode> queue = new LinkedList<>();
      // Start the traversal from the `from` vertex
      queue.add(new PathNode(from, new NodePositionList<>(), 0));

      while (!queue.isEmpty()) {
          PathNode current = queue.poll(); // Dequeue the current path state

          // Skip processing if the vertex has already been visited
          if (visited.contains(current.vertex)) continue;
          visited.add(current.vertex); // Mark the vertex as visited

          // Check if the target vertex is reached
          if (current.vertex.equals(to)) {
              return current.path; // Return the path if the destination is reached
          }

          // Explore all neighbors
          for (Edge<Integer> edge : g.edges(current.vertex)) { // Iterate through all edges connected to the current vertex
              Vertex<V> neighbor = g.opposite(current.vertex, edge); // Get the opposite vertex
              int newWeightSum = current.weightSum + edge.element(); // Calculate the new weight sum

              // Check if the neighbor can be visited without exceeding the limit
              if (!visited.contains(neighbor) && newWeightSum < limit) {
                  // Create a new path by copying the current path and adding the new edge
                  PositionList<Edge<Integer>> newPath = new NodePositionList<>();
                  for (Edge<Integer> e : current.path) {
                      newPath.addLast(e);
                  }
                  newPath.addLast(edge); // Add the current edge to the path
                  // Add the new state to the queue
                  queue.add(new PathNode(neighbor, newPath, newWeightSum));
              }
          }
      }

      return null; // No valid path found
  }
}

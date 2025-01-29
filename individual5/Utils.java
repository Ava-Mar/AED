package aed.individual5;

import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.*;
import es.upm.aedlib.tree.*;


public class Utils {

  public static <E> PositionList<Position<E>> longestPath(Tree<E> t) {
	  // Return an empty list if the tree is empty
      PositionList<Position<E>> result = new NodePositionList<>();
      if (t.isEmpty()) {
          return result;
      }

      // Start the recursive helper function from the root of the tree
      return findLongestPath(t, t.root());
  }

  private static <E> PositionList<Position<E>> findLongestPath(Tree<E> tree, Position<E> node) {
      // If the node is a leaf, return a list containing only this node
      if (!tree.children(node).iterator().hasNext()) {
          PositionList<Position<E>> path = new NodePositionList<>();
          path.addLast(node);
          return path;
      }

      // Find the longest path among the children
      PositionList<Position<E>> longestSubpath = null;

      for (Position<E> child : tree.children(node)) {
          PositionList<Position<E>> childPath = findLongestPath(tree, child);

          // Compare the lengths of paths to find the longest one
          if (longestSubpath == null || childPath.size() > longestSubpath.size()) {
              longestSubpath = childPath;
          }
      }

      // Add the current node to the front of the longest path
      if (longestSubpath != null) {
          longestSubpath.addFirst(node);
      }

      return longestSubpath;
  }
}

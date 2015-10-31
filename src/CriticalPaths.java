import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

/**
 * 
 */

/**
 * Program to find Critical paths in PERT charts
 * 
 * @author Akshay
 *
 */
public class CriticalPaths {

	private Graph graph;
	private ArrayList<Graph.Vertex> criticalVertexList;
	int criticalPathLength;
	public ArrayList<ArrayDeque<Graph.Vertex>> allCriticalPaths;

	static private int phase = 0;
	static private long startTime, endTime, elapsedTime;

	/* Constructor */
	public CriticalPaths(Graph g) {
		this.graph = g;
		criticalVertexList = new ArrayList<Graph.Vertex>();
		criticalPathLength = Integer.MIN_VALUE;
		allCriticalPaths = new ArrayList<>();
	}

	public static void main(String args[]) throws FileNotFoundException {
		Scanner s;
		if (args.length > 0) {
			s = new Scanner(new File(args[0]));
		} else {
			s = new Scanner(System.in);
		}
		/**
		 * Read the graph from the file
		 */
		Graph g = readInput(s);
		CriticalPaths criticalPath = new CriticalPaths(g);

		timer();
		Graph.Vertex[] topologicalOrder = criticalPath.DfSTop();
		criticalPath.calculateEC(topologicalOrder);
		criticalPath.calculateLC(topologicalOrder);
		criticalPath.calculateSlack();

		criticalPath.getCriticalPaths(topologicalOrder);
		timer();

		criticalPath.display();
	}

	/**
	 * Method to display EC,LC and slack of each node
	 */
	private void display() {
		System.out.println(criticalPathLength + " "
				+ (criticalVertexList.size() - 2) + " "
				+ allCriticalPaths.size());
		displayAllCriticalPaths();
		System.out.println("Task \tEC \tLC \tSlack");
		for (int i = 1; i < graph.V.length - 1; i++) {
			Graph.Vertex v = graph.V[i];
			System.out.println(v.name + "\t" + v.EC + "\t" + v.LC + "\t"
					+ v.slack);
		}
	}

	/**
	 * Method to display all the critical paths from source node to sink node.
	 */
	private void displayAllCriticalPaths() {
		int count = 1;
		for (ArrayDeque<Graph.Vertex> p : allCriticalPaths) {
			System.out.print("\n" + (count++) + ": ");
			Iterator deqIterator = p.descendingIterator();
			while (deqIterator.hasNext()) {
				Graph.Vertex v = (Graph.Vertex) deqIterator.next();
				System.out.print(v.name + " ");
			}
		}
		System.out.println("\n");
	}

	/**
	 * Method for calculating the critical paths in graph
	 * 
	 * @param topologicalOrder
	 *            : Array containing topological order
	 */
	private void getCriticalPaths(Graph.Vertex[] topologicalOrder) {
		graph.setSeenFalse();
		Graph.Vertex source = topologicalOrder[0];
		Graph.Vertex sink = topologicalOrder[topologicalOrder.length - 1];
		getPaths(source, sink, new ArrayDeque<Graph.Vertex>());

		for (ArrayDeque<Graph.Vertex> path : allCriticalPaths) {
			path.removeLast();
		}
	}

	/**
	 * Method to get Critical path in graphs
	 * 
	 * @param vertex
	 *            :current vertex visited
	 * @param sink
	 *            :sink vertex of the graph
	 * @param pathDeque
	 *            :A Deque of vertices
	 */
	private void getPaths(Graph.Vertex vertex, Graph.Vertex sink,
			ArrayDeque<Graph.Vertex> pathDeque) {
		if (vertex == sink) {
			ArrayDeque<Graph.Vertex> path = new ArrayDeque<>();
			path.addAll(pathDeque);
			allCriticalPaths.add(path);
			return;
		}

		vertex.visited = true;
		pathDeque.push(vertex);
		for (Graph.Edge edge : vertex.Adj) {
			Graph.Vertex v = edge.otherEnd(vertex);

			if (!v.visited && v.slack == 0 && v.EC == vertex.EC + v.duration) {
				getPaths(v, sink, pathDeque);
			}
		}
		vertex.visited = false;
		pathDeque.pop();

	}

	/**
	 * Method to calculate slack of each node
	 */
	private void calculateSlack() {

		for (Graph.Vertex u : graph) {
			u.slack = u.LC - u.EC;
			if (u.slack == 0) {
				criticalVertexList.add(u);
			}
		}
	}

	/**
	 * Method to calculate the LC of each node in graph
	 * 
	 * @param topologicalOrder
	 *            : A array having topological order
	 */
	private void calculateLC(Graph.Vertex[] topologicalOrder) {

		Graph.Vertex sink = topologicalOrder[topologicalOrder.length - 1];
		sink.LC = sink.EC;
		for (int i = topologicalOrder.length - 1; i >= 0; i--) {
			Graph.Vertex u = topologicalOrder[i];
			for (Graph.Edge edge : u.Adj) {
				Graph.Vertex v = edge.otherEnd(u);
				if ((v.LC - v.duration) < u.LC) {
					u.LC = v.LC - v.duration;
				}
			}
		}

		criticalPathLength = sink.LC;
	}

	/**
	 * Method to calculate EC of each node in the graph
	 * 
	 * @param topologicalOrder
	 *            :A array having topological order
	 */
	private void calculateEC(Graph.Vertex[] topologicalOrder) {

		graph.V[0].EC = 0;
		for (Graph.Vertex u : topologicalOrder) {
			for (Graph.Edge edge : u.revAdj) {
				Graph.Vertex v = edge.otherEnd(u);
				if ((v.EC + u.duration) > u.EC) {
					u.EC = v.EC + u.duration;
				}
			}
		}
	}

	/**
	 * Timer function to calculate time required for operations
	 */
	public static void timer() {
		if (phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			memory();
			phase = 0;
		}
	}

	/**
	 * Method to monitor the memory usage of the program
	 */
	public static void memory() {
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed / 1000000 + " MB / "
				+ memAvailable / 1000000 + " MB.");
	}

	
	/**
	 * Method for reading thegraph
	 * @param s :Scanner element
	 * @return Returns a graph object
	 */
	private static Graph readInput(Scanner s) {
		Graph g = Graph.readGraph(s);
		return g;
	}	

	
	/**
	 * Method to calculate the topological order of a given graph
	 * @return returns the vertex array
	 */
	private Graph.Vertex[] DfSTop() {

		Stack<Graph.Vertex> topologicalStack = new Stack<>();
		for (Graph.Vertex vertex : graph) {
			if (!vertex.visited) {
				DFSVisit(vertex, topologicalStack);
			}
		}

		/**
		 * Create a Vertex array from the Topological order stack <br />
		 * The stack is in the reverse topological order
		 */
		Graph.Vertex[] topologicalOrder = new Graph.Vertex[graph.V.length];
		int index = 0;
		while (!topologicalStack.isEmpty()) {
			topologicalOrder[index] = topologicalStack.pop();
			index++;
		}
		return topologicalOrder;
	}

	/**
	 * Method for Depth First Traversal of the graph
	 *
	 * @param vertex
	 *            The current vertex being visited
	 * @param stack
	 *            The stack to maintain topological order
	 */
	public static void DFSVisit(Graph.Vertex vertex, Stack<Graph.Vertex> stack) {
		vertex.visited = true;
		vertex.active = true;

		for (Graph.Edge edge : vertex.Adj) {
			Graph.Vertex otherVertex = edge.otherEnd(vertex);
			if (!otherVertex.visited) {
				DFSVisit(otherVertex, stack);
			} else if (otherVertex.active) {
				System.out.println("Not a Dag");
			}
		}
		stack.push(vertex);
		vertex.active = false;
	}

}

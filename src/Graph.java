import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Class to represent a graph
 * 
 * @author Akshay
 */
public class Graph implements Iterable<Graph.Vertex> {

	static final int INFINITY = Integer.MAX_VALUE;
	static final int NEGATIVE_INFINITY = Integer.MIN_VALUE;

	public Vertex[] V;
	public int N;

	/* Constructor */
	Graph(int size) {
		N = size;
		V = new Vertex[size + 2];
		for (int i = 0; i < size + 2; i++) {
			V[i] = new Vertex(i);
		}
	}

	/**
	 * Class that represents an arc in a Graph
	 */
	public class Edge {
		public Vertex From;
		public Vertex To;

		/* Constructor */
		Edge(Vertex u, Vertex v) {
			From = u;
			To = v;
		}

		/**
		 * Method to find the other end end of the arc given a vertex reference
		 * 
		 * @param u
		 *            : Vertex
		 * @return
		 */
		public Vertex otherEnd(Vertex u) {
			/**
			 * If the vertex u is the head of the arc, then return the tail,
			 * else return the head
			 */
			if (From == u) {
				return To;
			} else {
				return From;
			}
		}

		/**
		 * Method to represent the edge in the form (x,y) where x is the head of
		 * the arc and y is the tail of the arc
		 *
		 * @return String representation of the edge
		 */
		@Override
		public String toString() {
			return "(" + From + "," + To + ")";
		}
	}

	/**
	 * Class to represent a vertex of a graph
	 */
	public class Vertex {
		public int name;
		public boolean visited; // flag to check if the vertex has already been
		public boolean active;

		public int duration;
		public int EC;
		public int LC;
		public int slack;

		/**
		 * The Adjacency list and Reverse Adjacency list of the vertex
		 */
		public LinkedList<Edge> Adj; // adjacency list
		public LinkedList<Edge> revAdj;

		/**
		 * Constructor for the vertex
		 * 
		 * @param n
		 *            The name of the vertex
		 */
		Vertex(int n) {
			name = n;
			visited = false;
			active = false;
			Adj = new LinkedList<>();
			revAdj = new LinkedList<>();
			EC = NEGATIVE_INFINITY;
			LC = INFINITY;
			slack = NEGATIVE_INFINITY;
		}

		/**
		 * Method to represent a vertex by its name
		 */
		@Override
		public String toString() {
			return Integer.toString(name);
		}
	}

	/**
	 * Method to add an edge as well as a reverse edge to the Adjacency and
	 * Reverse Adjacency list respectively
	 *
	 * @param a
	 *            The head vertex from where the edge occurs
	 * @param b
	 *            The tail to where the edge meets
	 */
	void addEdge(int a, int b) {
		Edge e = new Edge(V[a], V[b]);
		Edge revEdge = new Edge(V[b], V[a]);
		V[a].Adj.add(e);
		V[b].revAdj.add(revEdge);
	}

	/**
	 * Method to set the seen property of all vertices in the graph to false
	 */
	void setSeenFalse() {
		for (Vertex vertex : V) {
			vertex.visited = false;
		}
	}

	/**
	 * Method to create an instance of VertexIterator
	 */
	@Override
	public Iterator<Vertex> iterator() {
		return new VertexIterator<>(V, N + 1);
	}

	/**
	 * A Custom Iterator Class for iterating through the vertices in a graph
	 *
	 * @param <Vertex>
	 */
	private class VertexIterator<Vertex> implements Iterator<Vertex> {

		private int nodeIndex = 0;
		private final Vertex[] iterV;// array of vertices to iterate through
		private final int iterN; // size of the array

		/**
		 * Constructor for VertexIterator
		 *
		 * @param v
		 *            : Array of vertices
		 * @param n
		 *            : Size of the graph
		 */
		private VertexIterator(Vertex[] v, int n) {
			nodeIndex = 0;
			iterV = v;
			iterN = n + 1;
		}

		/**
		 * Method to check if there is any vertex left in the iteration
		 * Overrides the default hasNext() method of Iterator Class
		 */
		@Override
		public boolean hasNext() {
			return nodeIndex != iterN;
		}

		/**
		 * Method to return the next Vertex object in the iteration Overrides
		 * the default next() method of Iterator Class
		 */
		@Override
		public Vertex next() {
			return (Vertex) iterV[nodeIndex++];
		}

		/**
		 * Throws an error if a vertex is attempted to be removed
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Method to read and populate the graph from the standard input
	 *
	 * @param in
	 *            The standard input stream
	 * @return The constructed graph from the input stream
	 */
	static Graph readGraph(Scanner in) {
		int n;

		String str = in.next();
		if (str.trim().charAt(0) == '#') {
			in.nextLine();
			n = in.nextInt();
		} else {
			n = Integer.parseInt(str);
		}
		int m = in.nextInt();

		Graph g = new Graph(n);

		/* Populate the duration for each vertex from the standard input */

		for (int i = 1; i < n + 1; i++) {
			g.V[i].duration = in.nextInt();
		}

		
		 /* Add an edge to the graph by reading the integers from the standard input*/
		 
		 
		for (int i = 0; i < m; i++) {
			int u = in.nextInt();
			int v = in.nextInt();
			g.addEdge(u, v);
		}

		
		 /* Add a dummy source node and sink node */
		 
		for (int i = 1; i < g.V.length - 1; i++) {
			g.addEdge(0, i);
			g.addEdge(i, g.V.length - 1);
		}

		g.addEdge(0, g.V.length - 1);

		in.close();
		return g;
	}

	/**
	 * Method to print the graph
	 */
	void printGraph() {
		for (Vertex u : this) {
			System.out.print(u + ": ");
			for (Edge e : u.Adj) {
				System.out.print(e);
			}
			System.out.println();
		}
	}
}

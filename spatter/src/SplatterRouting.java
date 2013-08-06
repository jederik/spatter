import java.util.ArrayList;
import java.util.List;

	
public class SplatterRouting {

	public static final int NODE_COUNT = 10;
	public static final double ADVERTISING_ROUNDS = 5D;

	private static final int VECTOR_LENGTH = 10000;
	
	private static SCVector[] addresses, routes;
	private static boolean[][] neighbors; 
	
	public static void main(String[] args) {
		
		initNodes();
		initEdges(3);
		System.out.println("Original Graph:");
		displayGraph();
		System.out.println();
		
		advertise(ADVERTISING_ROUNDS);
		System.out.println("Learned Graph:");
		displayRoutes();
	}

	public static final double THRESHOLD = .52D;
	
	private static void displayRoutes() {
		for (int i=0; i<NODE_COUNT; i++) {
			for (int j=0; j<NODE_COUNT; j++) {
				boolean match = SCVector.match(routes[i], addresses[j]) > THRESHOLD;
				System.out.print(match ? " 1" : " 0");
				
			}
			System.out.println();
		}
	}

	private static void displayGraph() {
		for (int i=0; i<NODE_COUNT; i++) {
			for (int j=0; j<NODE_COUNT; j++) {
				System.out.print(neighbors[i][j] ? " 1" : " 0");
			}
			System.out.println();
		}
	}

	private static void initEdges(double averageDegree) {
		neighbors = new boolean[NODE_COUNT][NODE_COUNT];
		for (int i=0; i<NODE_COUNT; i++) {
			for (int j=i+1; j<NODE_COUNT; j++) {
				if (Math.random() < averageDegree / NODE_COUNT) {
					neighbors[i][j] = neighbors[j][i] = true;
				}
			}
		}
	}

	private static void advertise(double rounds) {
		SCVector[] dummy = new SCVector[0];
		for (int i=0; i<NODE_COUNT*rounds; i++) {
			int node = (int) (Math.random() * NODE_COUNT);
			List<SCVector> neighborRoutingVectors = new ArrayList<SCVector>();
			for (int j=0; j<NODE_COUNT; j++) {
				if (neighbors[node][j]) {
//					SCVector route = SCVector.sum(routes[j], addresses[j]);
					neighborRoutingVectors.add(routes[j]);
				}
			}
			if (neighborRoutingVectors.size() == 0) continue;
			SCVector neighborsum = SCVector.sum((SCVector[]) neighborRoutingVectors.toArray(dummy));
			routes[node] = SCVector.sum(routes[node], neighborsum);
/**/		routes[node] = SCVector.sum(routes[node], addresses[node]);
		}
	}

	private static void initNodes() {
		addresses = new SCVector[VECTOR_LENGTH];
		routes = new SCVector[VECTOR_LENGTH];
		for (int i = 0; i < addresses.length; i++) {
			addresses[i] = new SCVector(VECTOR_LENGTH);
			addresses[i].randomize();
			routes[i] = new SCVector(VECTOR_LENGTH);
			routes[i] = SCVector.xor(routes[i], addresses[i]);
		}
	}
}

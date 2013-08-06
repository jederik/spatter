import java.util.ArrayList;
import java.util.List;
	
public class SplatterRouting {

	public static final int NODE_COUNT = 20;
	public static final double ADVERTISING_ROUNDS = 5D;
	public static final int ROUTING_ROUNDS = 100;

	private static final int VECTOR_LENGTH = 10000;
	
	private static SCVector[] addresses, routes;
	private static boolean[][] neighbors;
	
	public static void main(String[] args) {
		
		// init network
		initNodes();
		initEdges(3);
		System.out.println("Original Graph:");
		displayGraph();
		System.out.println();

		// advertise addresses
		advertise(ADVERTISING_ROUNDS);
		System.out.println("Learned Graph:");
		displayRoutes();
		System.out.println();
		
		// random routing
		System.out.print("Running random routing tests...");
		double avgHC = testRouting(randomRouter);
		System.out.println("Average hop count (random routing):" + avgHC);
		
		// splatter routing
		System.out.print("Running splatter routing tests...");
		avgHC = testRouting(splatterRouter);
		System.out.println("Average hop count (splatter routing):" + avgHC);
		
	}

	private static Router randomRouter = new Router() {
		
		@Override
		public int getNextHop(int currentHop, int target) {
			
			// node isolated?
			boolean isolated = true;
			for (int j=0; j<NODE_COUNT; j++) {
				if (neighbors[currentHop][j]) {
					isolated = false;
				}
			}
			if (isolated) {
				return -1;
			}

			while (true) {
				int j = (int) (Math.random() * NODE_COUNT);
				if (neighbors[currentHop][j]) return j;
			}
		}
	};
	
	private static Router splatterRouter = new Router() {
		
		@Override
		public int getNextHop(int currentHop, int target) {
			int nextHop = -1;
			double bestMatch = 0D;
			for (int j=0; j<NODE_COUNT; j++) {
				if (neighbors[currentHop][j]) {
					double match = SCVector.match(routes[j], addresses[target]);
					if (match > bestMatch) {
						nextHop = j;
						bestMatch = match;
					}
				}
			}
			return nextHop;
		}
	};
	
	private static final int MAX_HOPS = NODE_COUNT * NODE_COUNT;

	private static interface Router {
		int getNextHop(int currentHop, int target);
	}
	
	private static double testRouting(Router router) {

		int fails = 0;
		int totalHops = 0;
		
		for (int r=0; r<ROUTING_ROUNDS; r++) {
			
			int source = (int) (Math.random() * NODE_COUNT);
			int target = (int) (Math.random() * NODE_COUNT);
			
			int hopCounter = 0;
			
			int i = source;
			while (i != target && hopCounter <= MAX_HOPS) {
				
				// determine optimal next hop
				int nextHop = router.getNextHop(i, target);
				
				// isolated node?
				if (nextHop==-1) {
					hopCounter = 0;
					fails++;
					break;
				}
				
				hopCounter++;
				i = nextHop;
				
			}
			

			if (hopCounter > MAX_HOPS) {
				fails++;
			}
			
			else {
				totalHops += hopCounter;
			}
			
		}
		
		System.out.println(fails + " fails");
		return ((double) totalHops) / (ROUTING_ROUNDS-fails);
		
	}

	public static final double THRESHOLD = .6D;
	
	private static void displayRoutes() {
		for (int i=0; i<NODE_COUNT; i++) {
			for (int j=0; j<NODE_COUNT; j++) {
				double match = SCVector.match(routes[i], addresses[j]);
//				System.out.print(match + "\t");
				System.out.print(match > THRESHOLD ? " 1" : " 0");
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
/**/					SCVector route = SCVector.sum(routes[j], addresses[j]);
					neighborRoutingVectors.add(route);
				}
			}
			if (neighborRoutingVectors.size() == 0) continue;
			SCVector neighborsum = SCVector.sum((SCVector[]) neighborRoutingVectors.toArray(dummy));
			routes[node] = SCVector.sum(routes[node], neighborsum);
//		routes[node] = SCVector.sum(routes[node], addresses[node]);
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


public class SplatterRouting {

	public static final int NODE_COUNT = 100;
	public static final double ADVERTISING_ROUNDS = 5D;

	private static final int VECTOR_LENGTH = 10000;
	
	private static SCVector[] addresses, routes;
	private static boolean[][] edges; 
	
	public static void main(String[] args) {
		initNodes();
		initEdges(6);
		advertise(ADVERTISING_ROUNDS);
	}

	private static void initEdges(double averageDegree) {
		edges = new boolean[NODE_COUNT][NODE_COUNT];
		for (int i=0; i<NODE_COUNT; i++) {
			for (int j=0; j<NODE_COUNT; j++) {
				if (Math.random() < averageDegree / NODE_COUNT) {
					edges[i][j] = true;
				}
			}
		}
	}

	private static void advertise(double rounds) {
		for (int i=0; i<NODE_COUNT*rounds; i++) {
			int node = (int) (Math.random() * NODE_COUNT);
			
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

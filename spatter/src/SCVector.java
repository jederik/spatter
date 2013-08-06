
public class SCVector {
	
	
	
	static final public byte[] MASKS = {1, 2, 4, 8, 16, 32, 64, -128};
	
	static public SCVector xor(SCVector... operands) {
		
		int length = operands[0].getLength();
		SCVector result = new SCVector(length);
		
		for (int i=0; i<result.data.length; i++) {
			for (int j = 0; j < operands.length; j++) {
				result.data[i] ^= operands[j].data[i];
			}
		}
		
		return result;
	}
	
	static public SCVector sum(SCVector... operands) {
		
		int length = operands[0].getLength();
		SCVector result = new SCVector(length);
		
		for (int i=0; i<length; i++) {
			int bias = 0;
			for (int j = 0; j < operands.length; j++) {
				if (operands[j].get(i)) {
					bias++;
				} else {
					bias--;
				}
			}
			if (bias>0) {
				result.put(i, true);
			}
			else if (bias<0) {
				result.put(i, false);
			}
			else {
				result.put(i, Math.random()<.5);
			}
		}
		return result;
	}
	
	static public double match(SCVector op1, SCVector op2) {
		SCVector xor = xor(op1, op2);
		int matchCount = 0;
		for (int i=0; i<xor.length; i++) {
			if (!xor.get(i)) matchCount++;
		}
		return ((double) matchCount) / xor.length;
	}

	private byte[] data;
	private int length;
	
	public SCVector(int length) {
		this.length = length;
		data = new byte[length / 8];
	}
	
	public void randomize() {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (Math.random() * 256);
		}
	}
	
	public boolean get(int i) {
		return (data[i/8] & MASKS[i%8]) != 0;
	}
	
	public void put(int i, boolean value) {
		if (value) data[i/8] |= MASKS[i%8];
		else data[i/8] &= ~MASKS[i%8];
	}
	
	public int getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(data.length * 8);
		for (int i=0; i<length; i++) {
			sb.append(get(i) ? '1' : '0');
		}
		return sb.toString();
	}
	
	static final private int DEFAULT_LENGTH = 8000;
	
	public static void main(String[] args) {
//		SCVector v1 = new SCVector(DEFAULT_LENGTH);
//		SCVector v2 = new SCVector(DEFAULT_LENGTH);
//		v1.randomize();
//		v2.randomize();
//		SCVector sum = sum(v1, v2);
//		System.out.println(v1);
//		System.out.println(v2);
//		System.out.println(sum);
//		System.out.println(match(v1, sum));
//		System.out.println(match(v2, sum));
//		SCVector comp = new SCVector(DEFAULT_LENGTH);
//		comp.randomize();
//		System.out.println(match(comp, sum));
		
		SCVector set = new SCVector(DEFAULT_LENGTH);
		SCVector[] v = new SCVector[100];
		for (int t=0; t<100; t++) {
			v[t] = new SCVector(DEFAULT_LENGTH);
			v[t].randomize();
//			set = sum(set, v[t]);
		}
		set = sum(v);
		for (int t=0; t<100; t++) {
			System.out.println(match(set, v[t]));
		}
	}
}













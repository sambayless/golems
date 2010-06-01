package test;

public class TestPowerOfTwo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for (int i = -1000; i < 1000; i++)
		{
			int power = Integer.highestOneBit(i-1);
			System.out.println(i + "\t" +  2 * power); 
		}

	}

}

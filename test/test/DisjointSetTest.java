package test;

import com.golemgame.util.DisjointSet;

public class DisjointSetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DisjointSet set = new DisjointSet(10);
		
		set.union(0, 1);
		
		set.union(3, 4);
		
		set.union(1, 4);
		
		System.out.println(set.find(0));
		System.out.println(set.find(1));
		System.out.println(set.find(3));
		
	}

}

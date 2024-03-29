package no.ntnu.idi.dm.arm.apriori;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BruteForceApriori<V> extends BaseApriori<V> {

	public BruteForceApriori(List<ItemSet<V>> transactions, Double minSup,
			Double minConf) {
		super(transactions, minSup, minConf);
	}
	
	private List<ItemSet<V>> getSingles(List<ItemSet<V>> trans) {
		Set<ItemSet<V>> singles = new HashSet<ItemSet<V>>(); 
		for (ItemSet<V> set : trans) {
			for (V v : set.getItems()) {
				ItemSet<V> single = new ItemSet<V>(v);
				singles.add(single);
			}
		}
		return new LinkedList<ItemSet<V>>(singles);
	}
	
	private List<ItemSet<V>> genCombos(List<ItemSet<V>> setMinusK) {
		Set<ItemSet<V>> combos = new HashSet<ItemSet<V>>();
		for (int i = 0; i < setMinusK.size(); i++) {
			ItemSet<V> set1 = setMinusK.get(i);
			for (int j = 0; j < setMinusK.size(); j++) {
				ItemSet<V> set2 = setMinusK.get(j);
				ItemSet<V> diff = set2.difference(set1);
				
				for (V v : diff.getItems()) {
					ItemSet<V> combo = set1.union(v);
					if (combo.size() > set1.size()) {
						combos.add(combo);
					}
				}
			}
		}
		return new LinkedList<ItemSet<V>>(combos);
	}
	
	private List<ItemSet<V>> removeInfrequent(List<ItemSet<V>> sets) {
		List<ItemSet<V>> frequent = new LinkedList<ItemSet<V>>();
		for (ItemSet<V> set : sets) {
			double support = getAndCacheSupportForItemset(set);
			if (support >= minSup) {
				frequent.add(set);
			}
		}
		return frequent;
	}

	@Override
	public void apriori() {
		List<ItemSet<V>> singles = getSingles(this.transactions);
		
		List<ItemSet<V>> currentSets = singles;
		for (int i = 1; currentSets.size() > 0; i++) {
			List<ItemSet<V>> frequentSets = removeInfrequent(currentSets);
			frequentItemSets.put(i, frequentSets);
			
			System.out.println("Level " + i);
			System.out.println("\tGenerated " + currentSets.size()
					+ " candidates.");
			System.out.println("\t\t" + currentSets);
			System.out.println("\tKept " + frequentSets.size()
					+ " frequent itemsets");
			System.out.println("\t\t" + frequentSets);
			
			currentSets = genCombos(currentSets);
		}
	}

}

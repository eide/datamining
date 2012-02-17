package no.ntnu.idi.dm.arm.apriori;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FkMinus1FKMinus1<V> extends BaseApriori<V> {

	public FkMinus1FKMinus1(List<ItemSet<V>> transactions, Double minSup,
			Double minConf) {
		super(transactions, minSup, minConf);
	}
	
	private double getSupport(ItemSet<V> set) {
		int occurrence = 0;
		for (ItemSet<V> tranSet : this.transactions) {
			if (tranSet.intersection(set).size() == set.size()) {
				occurrence++;
			}
		}
		return (double) occurrence / this.transactions.size();
	}

	@Override
	public List<ItemSet<V>> aprioriGen(
			List<ItemSet<V>> frequentCandidatesKMinus1) {

		Collections.sort(frequentCandidatesKMinus1);
		int allGeneratedCandidatesCounter = 0;
		Set<ItemSet<V>> frequentCandidateSet = new HashSet<ItemSet<V>>();

		for (int i = 0; i < frequentCandidatesKMinus1.size(); i++) {
			ItemSet<V> set1 = frequentCandidatesKMinus1.get(i);
			for (int j = 0; j < frequentCandidatesKMinus1.size(); j++) {
				ItemSet<V> set2 = frequentCandidatesKMinus1.get(j);
				ItemSet<V> diff = set2.difference(set1);
				
				for (V v : diff.getItems()) {
					ItemSet<V> combo = set1.union(v);
					if (combo.size() != set1.size() + 1) {
						continue;
					}
					
					frequentCandidateSet.add(combo);
					getAndCacheSupportForItemset(combo);
					allGeneratedCandidatesCounter++;
				}
			}
		}

		return new LinkedList<ItemSet<V>>(frequentCandidateSet);
	}

}

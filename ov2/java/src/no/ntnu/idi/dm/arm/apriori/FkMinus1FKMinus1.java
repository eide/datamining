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

	@Override
	public List<ItemSet<V>> aprioriGen(
			List<ItemSet<V>> frequentCandidatesKMinus1) {

		Collections.sort(frequentCandidatesKMinus1);
		int allGeneratedCandidatesCounter = 0;
		Set<ItemSet<V>> frequentCandidateSet = new HashSet<ItemSet<V>>();

		// TODO

		return new LinkedList<ItemSet<V>>(frequentCandidateSet);
	}

}

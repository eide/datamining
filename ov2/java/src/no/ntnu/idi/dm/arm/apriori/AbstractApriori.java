package no.ntnu.idi.dm.arm.apriori;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AbstractApriori<V> {

	// we store the frequent itemsets in here (whatever's left after pruning)
	protected HashMap<Integer, List<ItemSet<V>>> frequentItemSets;

	// we cache the relative support count of itemsets
	private Map<ItemSet<V>, Double> supportCache = new HashMap<ItemSet<V>, Double>();

	// the transactions of the data set
	protected List<ItemSet<V>> transactions;

	// we keep the final rules that were generated
	private List<AssociationRule<V>> rules;

	// we store minSup and minConf
	protected Double minSup;
	protected Double minConf;

	/**
	 * initialise with a list of transactions
	 * 
	 * @param transactions
	 * @param minConf
	 * @param minSupport
	 */
	public AbstractApriori(List<ItemSet<V>> transactions, Double minSup,
			Double minConf) {
		this.transactions = transactions;
		this.minSup = minSup;
		this.minConf = minConf;
		frequentItemSets = new HashMap<Integer, List<ItemSet<V>>>();
		rules = new LinkedList<AssociationRule<V>>();
	}

	/**
	 * iterate over the transactions and return all 1-itemsets
	 * 
	 * @return
	 */
	public List<ItemSet<V>> getAllItemsetsOfSizeOne() {
		Set<ItemSet<V>> sizeOneItemSets = new HashSet<ItemSet<V>>();
		// iterate transactions
		for (ItemSet<V> nextItemSet : transactions) {
			// iterate items in each transaction
			for (V v : nextItemSet.getItems()) {
				// create a new ItemSet and add it to our set
				ItemSet<V> itemSet = new ItemSet<V>();
				itemSet.addItem(v);
				sizeOneItemSets.add(itemSet);
				// update support counts for these itemsets on the fly
				getAndCacheSupportForItemset(itemSet);
			}
		}
		// convert to list and return
		return new LinkedList<ItemSet<V>>(sizeOneItemSets);
	}

	/**
	 * get or calculate support for the given itemset it is either fetched from
	 * the cache or computed from the transaction set
	 * 
	 * @param itemset
	 * @return support count
	 */
	public double getAndCacheSupportForItemset(ItemSet<V> itemset) {
		// check cache first and return if it is found there
		Double double1 = supportCache.get(itemset);
		if (double1 != null) {
			return double1;
		}

		// else iterate the transactions and count support
		int occurrenceCount = 0;
		Iterator<ItemSet<V>> transactionIterator = transactions.iterator();

		while (transactionIterator.hasNext()) {
			ItemSet<V> transaction = transactionIterator.next();
			// System.out.println("comparing to: " + transaction);
			if (transaction.intersection(itemset).size() == itemset.size()) {
				// System.out.println("found in: " + transaction);
				occurrenceCount++;
			}
		}

		// store relative support
		double d = ((double) occurrenceCount) / transactions.size();
		supportCache.put(itemset, d);
		return d;
	}

	/**
	 * 
	 * 
	 * @param minSupport
	 * @param candidates
	 * @return
	 */
	protected List<ItemSet<V>> pruneInfrequentCandidates(Double minSupport,
			List<ItemSet<V>> candidates) {

		List<ItemSet<V>> frequentCandidates = new LinkedList<ItemSet<V>>();
		Iterator<ItemSet<V>> candidateIterator = candidates.iterator();
		while (candidateIterator.hasNext()) {
			ItemSet<V> next = candidateIterator.next();
			Double support = supportCache.get(next);
			if (support >= minSupport) {
				frequentCandidates.add(next);
			}
		}
		return frequentCandidates;
	}

	/**
	 * see apriori-gen method in literature, to be implemented by subclasses
	 * 
	 * @param frequentCandidatesKMinus1
	 * @return
	 */
	public abstract List<ItemSet<V>> aprioriGen(
			List<ItemSet<V>> frequentCandidatesKMinus1);

	abstract public void apriori();

	// we deal with actual association rules from here on
	
	private List<ItemSet<V>> combinator(List<ItemSet<V>> setMinusK) {
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

	public void apGenRules(ItemSet<V> frequentItemSet,
			List<ItemSet<V>> H_m, double minConf) {
		if (H_m.size() > 0 && frequentItemSet.size() > H_m.get(0).size()) {
			List<ItemSet<V>> nextSets = new LinkedList<ItemSet<V>>();
			List<ItemSet<V>> H_m1 = combinator(H_m);
			for (ItemSet<V> h_m1 : H_m1) {
				AssociationRule<V> rule = createAssociationRule(
						h_m1, frequentItemSet);
				if (rule.getItemSetA().size() > 0 && rule.getConfidence() >= minConf && rule.getSupport() >= minSup) {
					rules.add(rule);
					nextSets.add(h_m1);
					System.out.println(rule);
				}
			}
			apGenRules(frequentItemSet, nextSets, minConf);
		}
	}

	/**
	 * start generating rules by iterating al frequent 2+ itemsets
	 * 
	 * @param minConf
	 */
	public void generateAllRules() {

		// for all levels (1-itemsets, 2-itemsets, ...)
		for (Entry<Integer, List<ItemSet<V>>> entry : frequentItemSets
				.entrySet()) {
			// we only do this for itemsets larger 1, it'd be kind of a boring
			// rule otherwise now, wouldn't it
			if (entry.getKey() > 1) {
				System.out.println("Generating rules for level: "
						+ entry.getKey());

				// for each itemset this size
				for (ItemSet<V> itemSet : entry.getValue()) {

					System.out.println("Processing itemset: " + itemSet);

					// first we iterate all possible 1-item-consequents and add
					// the possible rules if their support and confidence is ok

					// create an itemset of all single items in this set
					List<ItemSet<V>> list1 = new LinkedList<ItemSet<V>>();
					for (V oneConsequent : itemSet.getItems()) {
						ItemSet<V> sizeOneItemSet = new ItemSet<V>();

						// output every 1-item consequent rule satisfying
						// minconf and minsupp thresholds
						ItemSet<V> antecedent = itemSet
								.difference(oneConsequent);
						ItemSet<V> consequent = new ItemSet<V>(oneConsequent);
						AssociationRule<V> oneConsequentRule = createAssociationRule(
								antecedent, consequent);

						if (oneConsequentRule.getSupport() >= minSup
								&& oneConsequentRule.getConfidence() >= minConf) {
							rules.add(oneConsequentRule);
							System.out.println(oneConsequentRule);
						}

						sizeOneItemSet.addItem(oneConsequent);
						list1.add(sizeOneItemSet);
					}

					// we start the recursion for this itemset and all size one
					// elements in it
					apGenRules(itemSet, list1, minConf);
				}
			}
		}
	}

	/**
	 * create an association rule from the given itemsets
	 * 
	 * @param antecedent
	 * @param consequent
	 * @return rule object
	 */
	private AssociationRule<V> createAssociationRule(ItemSet<V> antecedent,
			ItemSet<V> consequent) {

		// create rule
		AssociationRule<V> rule = new AssociationRule<V>(
				antecedent.difference(consequent), consequent);

		// compute confidence and support
		Double support = supportCache.get(antecedent.union(consequent));
		Double leftSupport = supportCache.get(antecedent);
		if (support != null) {
			rule.setSupport(support);
			rule.setConfidence(support / leftSupport);
		}
		return rule;
	}

	public List<AssociationRule<V>> getRules() {
		return rules;
	}

}

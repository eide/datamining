package no.ntnu.idi.dm.arm.runnables;

import java.util.LinkedList;
import java.util.List;

import no.ntnu.idi.dm.arm.apriori.AbstractApriori;
import no.ntnu.idi.dm.arm.apriori.BruteForceApriori;
import no.ntnu.idi.dm.arm.apriori.FKMinus1F1Apriori;
import no.ntnu.idi.dm.arm.apriori.FkMinus1FKMinus1;
import no.ntnu.idi.dm.arm.apriori.ItemSet;

public class AprioriItemSetGenerator {

	private static List<ItemSet<String>> createDataSet() {
		ItemSet<String> t1 = new ItemSet<String>();
		t1.addItem("bread");
		t1.addItem("milk");

		ItemSet<String> t2 = new ItemSet<String>();
		t2.addItem("bread");
		t2.addItem("diapers");
		t2.addItem("beer");
		t2.addItem("eggs");

		ItemSet<String> t3 = new ItemSet<String>();
		t3.addItem("milk");
		t3.addItem("diapers");
		t3.addItem("beer");
		t3.addItem("cola");

		ItemSet<String> t4 = new ItemSet<String>();
		t4.addItem("bread");
		t4.addItem("milk");
		t4.addItem("diapers");
		t4.addItem("beer");

		ItemSet<String> t5 = new ItemSet<String>();
		t5.addItem("bread");
		t5.addItem("milk");
		t5.addItem("diapers");
		t5.addItem("cola");

		ItemSet<String> t6 = new ItemSet<String>();
		t6.addItem("bread");
		t6.addItem("diapers");
		t6.addItem("milk");

		List<ItemSet<String>> transactions = new LinkedList<ItemSet<String>>();
		transactions.add(t1);
		transactions.add(t2);
		transactions.add(t3);
		transactions.add(t4);
		transactions.add(t5);
		// transactions.add(t6);
		return transactions;
	}

	public static void main(String[] args) {

		// get the data set
		List<ItemSet<String>> transactions = createDataSet();

		// print transactions ... just in case
		System.out.println(transactions);

		// threshold
		Double minSup = .4d;
		Double minConf = .8;

		System.out.println("We set the relative minsup to " + minSup);

		AbstractApriori<String> apriori;

//		apriori = new BruteForceApriori<String>(transactions, minSup, minConf);
		apriori = new FKMinus1F1Apriori<String>(transactions, minSup, minConf);
//		apriori = new FkMinus1FKMinus1<String>(transactions, minSup, minConf);

		apriori.apriori();
		apriori.generateAllRules();

		System.out
				.println("Generated " + apriori.getRules().size() + " rules.");

	}

}

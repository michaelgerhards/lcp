package algorithm.pbws;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import algorithm.StaticSchedulingAlgorithm;

public class BreakDownCostPrinter {

	
	public static PrintWriter pw = new PrintWriter(new OutputStream() {

		@Override
		public void write(int b) throws IOException {
			// nothing

		}
	});
	private final StaticSchedulingAlgorithm algorithm;;

	public BreakDownCostPrinter(StaticSchedulingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void println(String s) {
		pw.println(s);
	}

	public PrintWriter getPw() {
		return pw;
	}

	public void printTotalCostsAndResources() {
		printTotalCosts();
		printTotalResources();
	}

	public void printTotalCosts() {
		double totalCosts = algorithm.getWorkflow().getTotalCost();
		pw.println(totalCosts);
	}

	public void printTotalResources() {
		int totalResources = getTotalResources();
		pw.println(totalResources);
	}

	public int getTotalResources() {
		int totalResources = algorithm.getWorkflow().getLanes().size();
		return totalResources;
	}
}

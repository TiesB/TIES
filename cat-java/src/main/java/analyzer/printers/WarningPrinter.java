package analyzer.printers;

import analyzer.collectors.Collector;

public class WarningPrinter extends AbstractPrinter {

    public WarningPrinter(Collector collector) {
        super(collector);
    }

    /**
     * Print out warnings to the console
     */
    @Override
    public void print() {
        for (String warning : collector.getWarnings()) {
            System.out.println("[WARNING] " + warning);
        }
    }
}

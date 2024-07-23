package analyzer.printers;

import analyzer.collectors.Collector;
import analyzer.data.FunctionData;

public class FunctionPrinter extends AbstractPrinter {
    public FunctionPrinter(Collector collector) {
        super(collector);
    }

    @Override
    public void print() {
        for (FunctionData function : collector.getFunctions()) {
            System.out.println(function);
        }
    }
}

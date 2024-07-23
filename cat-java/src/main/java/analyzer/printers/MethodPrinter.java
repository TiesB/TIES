package analyzer.printers;

import analyzer.collectors.Collector;
import analyzer.data.MethodData;

public class MethodPrinter extends AbstractPrinter {
    public MethodPrinter(Collector collector) {
        super(collector);
    }

    @Override
    public void print() {
        for (MethodData method : collector.getMethods().values()) {
            System.out.println(method);
        }
    }
}

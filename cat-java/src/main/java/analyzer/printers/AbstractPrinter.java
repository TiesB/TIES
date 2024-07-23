package analyzer.printers;

import analyzer.collectors.Collector;

public abstract class AbstractPrinter implements Printable {

    protected Collector collector;

    public AbstractPrinter(Collector collector) {

        this.collector = collector;
    }

}

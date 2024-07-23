package analyzer.data;

import java.util.List;

public class Data {
    public List<MethodData> methods;
    public List<FunctionData> functions;

    public Data(List<MethodData> methods, List<FunctionData> functions) {
        this.methods = methods;
        this.functions = functions;
    }
}

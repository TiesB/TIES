package analyzer.util;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import analyzer.collectors.Collector;

public final class JsonWriter {
    public static void outputJson(final Collector collector) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        outputMainJson(gson, collector);
        outputUnresolvedMethodsJson(gson, collector);
    }

    private static void outputMainJson(Gson gson, Collector collector) throws IOException {
        JsonObject obj = new JsonObject();
        obj.add("methods",
                gson.toJsonTree(collector.getMethods().values()));
        obj.add("functions", gson.toJsonTree(collector.getFunctions()));

        try (PrintWriter out = new PrintWriter("main.json")) {
            out.println(obj);
        }
    }

    private static void outputUnresolvedMethodsJson(Gson gson, Collector collector) throws IOException {
        try (PrintWriter out = new PrintWriter("unresolved_expressions.json")) {
            out.println(gson.toJson(collector.getUnresolvedExpressions().toArray()));
        }
    }
}

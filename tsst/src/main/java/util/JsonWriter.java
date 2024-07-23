package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import models.Data;
import models.Test;

public final class JsonWriter {
    public static void outputJson(Data data, List<Test> tests) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        JsonObject obj = new JsonObject();

        obj.add("tests", gson.toJsonTree(tests));
        obj.add("functions", gson.toJsonTree(data.getFunctionsForJson()));
        obj.add("methods", gson.toJsonTree(data.getMethodsForJson()));

        try (PrintWriter out = new PrintWriter("tests_" + Config.NAME + ".json")) {
            out.println(obj);
        }
    }
}

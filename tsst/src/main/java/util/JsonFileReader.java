package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import models.Data;

public final class JsonFileReader {
    public static Data readJson(File file) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        JsonReader reader = new JsonReader(new FileReader(file));

        Data data = gson.fromJson(reader, Data.class);

        return data;
    }
}

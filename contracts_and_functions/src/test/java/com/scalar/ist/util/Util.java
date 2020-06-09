package com.scalar.ist.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Util {
  public static String readJsonSchemaFromResources(String fileName) {
    return readJsonSchemaFromResources(Paths.get("arguments", "schema", fileName));
  }

  public static String readJsonSchemaFromResources(Path relativePath) {
    return new JSONObject(
            new JSONTokener(
                Objects.requireNonNull(
                    Util.class.getClassLoader().getResourceAsStream(relativePath.toString()))))
        .toString();
  }
}

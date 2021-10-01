package com.scalar.ist.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;

public class Util {

  public static String readJsonSchemaFromResources(String fileName) {
    return readJson(Paths.get("arguments", "schema", fileName));
  }

  public static JsonObject readJsonObject(Path relativePath) {
    return Json.createReader(
            Objects.requireNonNull(
                Util.class.getClassLoader().getResourceAsStream(relativePath.toString())))
        .readObject();
  }

  public static String readJson(Path relativePath) {
    return readJsonObject(relativePath).toString();
  }

  public static JsonObject readAssetSchemaFromResources(String filename) {
    return readJsonObject(Paths.get("asset", "schema", filename));
  }

  public static JsonObject readTableSchemaFromResources(String filename) {
    return readJsonObject(Paths.get("table", "schema", filename));
  }

  public static JsonObject readArgumentsSampleFromResources(String filename) {
    return readJsonObject(Paths.get("argument", filename));
  }

  public static JsonObject readAssetSampleFromResources(String filename) {
    return readJsonObject(Paths.get("asset", filename));
  }
}

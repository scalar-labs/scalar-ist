package com.scalar.ist.tools;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class Util {

  public static JsonObject readJsonFromFile(String fileName) {
    try {
      return Json.createReader(new BufferedInputStream(new FileInputStream(fileName))).readObject();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String randomAlphanumeric(int length) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();
    String generatedString =
        random
            .ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    return generatedString;
  }

  public static String randomLowerAlphabet(int length) {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    Random random = new Random();
    String generatedString =
        random
            .ints(leftLimit, rightLimit + 1)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    return generatedString;
  }
}

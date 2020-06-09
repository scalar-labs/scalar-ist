package com.scalar.ist.util;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import org.hashids.Hashids;

public class Hasher {
  public static String hash(String value) {
    return hash("", value);
  }

  public static String hash(String salt, String value) {
    Hashids hashids = new Hashids(salt);
    String hexValue = HashCode.fromBytes(value.getBytes(Charsets.UTF_8)).toString();
    return hashids.encodeHex(hexValue);
  }
}

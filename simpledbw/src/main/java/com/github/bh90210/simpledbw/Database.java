package com.github.bh90210.simpledbw;

import androidx.collection.ArrayMap;

import dbwrapper.Dbwrapper;
import dbwrapper.SimpleDBW;

public class Database {
  public static ArrayMap<byte[], byte[]> DumpAll() {
    // call the class
    SimpleDBW db = new SimpleDBW();
    // get total numbers of keys in the database
    long total = db.keyOnlyIterator();
    ArrayMap<byte[], byte[]> map = new ArrayMap((int)total);
    // collect keys and values to a []byte<array>
    for (int i = 0; i < total; i++) {
      // get the next key
      byte[] key = Dbwrapper.dump(i);
      // get the value of that key
      byte[] value = db.view(key);
      //construct ArrayMap
      map.put(key, value);
    }
    // return ArrayMap
    return map;
  }

  public static ArrayMap<byte[], byte[]> ViewPrefix(byte[] prefix) {
    // call the class
    SimpleDBW db = new SimpleDBW();
    // get total numbers of keys in the database
    long total = db.keyOnlyIteratorPrefix(prefix);
    ArrayMap<byte[], byte[]> map = new ArrayMap<>();
    // collect keys and values to a []byte<array>
    for (int i = 0; i < total; i++) {
      // get the next key
      byte[] key = Dbwrapper.dump(i);
      // get the value of that key
      byte[] value = db.view(key);
      //construct ArrayMap
      map.put(key, value);
    }
    // return ArrayMap
    return map;
  }

  public static void DropDB() {
    SimpleDBW db = new SimpleDBW();
    db.dropAll();
  }

  public static void PrefixDrop(byte[] prefix) {
    SimpleDBW db = new SimpleDBW();
    db.dropPrefix(prefix);
  }

  public static byte[] View(byte[] key) {
    SimpleDBW db = new SimpleDBW();
    byte[] value = db.view(key);
    return value;
  }

  public static void Delete(byte[] key) {
    SimpleDBW db = new SimpleDBW();
    db.delete(key);
  }

  public static long Mii(byte[] key) {
    SimpleDBW db = new SimpleDBW();
    long returnedInt = db.incInt(key);
    return returnedInt;
  }

  public static void Update(byte[] key, byte[] value) {
    SimpleDBW db = new SimpleDBW();
    db.update(key, value);
  }

  public void PrePopulate(byte[] key, byte[] value) {
    SimpleDBW db = new SimpleDBW();
    db.prePopulate(key, value);
  }
}

class Test {
  companion object {
    var prop: Int = 0;
  }
}

// TESTED_OBJECT_KIND: property
// TESTED_OBJECTS: Test, prop
// FLAGS: ACC_STATIC, ACC_PUBLIC, ACC_DEPRECATED

// TESTED_OBJECT_KIND: property
// TESTED_OBJECTS: Test$Companion, prop
// ABSENT: TRUE

package org.goldstandard.assurance.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SheetLocationsTest {
  private static final String TEST_STRING = "MyString";
  private SheetLocations TEST_MAP;

  @BeforeEach
  void setUp() {
    TEST_MAP = new SheetLocations();
    var offset = Offsets.builder().start(10).end(20).build();
    TEST_MAP.put(offset, TEST_STRING);
  }

  @Test
  void shouldReturnStringForGivenOffsetsWhenGivenExactOffsets() {
    assertThat(TEST_MAP.getLocation(10, 20)).isEqualTo(TEST_STRING);
  }

  @Test
  void shouldReturnStringForGivenOffsetsWhenGivenOverlappingOffsets() {
    assertThat(TEST_MAP.getLocation(12, 18)).isEqualTo(TEST_STRING);
  }

  @Test
  void shouldNotReturnStringForGivenOffsetsWhenGivenNonOverlappingStart() {
    assertThat(TEST_MAP.getLocation(8, 18)).isNull();
  }

  @Test
  void shouldNotReturnStringForGivenOffsetsWhenGivenNonOverlappingEnd() {
    assertThat(TEST_MAP.getLocation(12, 22)).isNull();
  }

  @Test
  void shouldReturnNullForEmptyMap() {
    assertThat(new SheetLocations().getLocation(1, 2)).isNull();
  }
}

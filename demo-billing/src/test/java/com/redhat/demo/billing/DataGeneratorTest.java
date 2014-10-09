package com.redhat.demo.billing;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class DataGeneratorTest {
  @Test
  public void testRandomPhoneNoGenerator() {
    String phoneNo = new DataGenerator().getRandomPhoneNo();
    assertThat(phoneNo, notNullValue());
    String[] values = phoneNo.split("-");
    assertThat(values.length, is(3));
    assertThat(values[0].length(), is(3));
    assertThat(values[1].length(), is(3));
    assertThat(values[2].length(), is(4));
  }
}
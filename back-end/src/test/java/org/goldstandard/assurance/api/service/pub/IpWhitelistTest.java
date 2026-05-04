package org.goldstandard.assurance.api.service.pub;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class IpWhitelistTest {

  @Test
  void shouldWhitelistSpecificV4Ip() {
    var whitelist = new IpWhitelist(List.of("46.149.111.41/32"));

    assertThat(whitelist.isV4Whitelisted("46.149.111.41")).isTrue();

    assertThat(whitelist.isV4Whitelisted("46.149.111.42")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.149.111.40")).isFalse();

    assertThat(whitelist.isV4Whitelisted("46.149.112.41")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.149.110.41")).isFalse();

    assertThat(whitelist.isV4Whitelisted("46.150.111.41")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.148.111.41")).isFalse();

    assertThat(whitelist.isV4Whitelisted("47.149.111.41")).isFalse();
    assertThat(whitelist.isV4Whitelisted("45.149.111.41")).isFalse();
  }

  @Test
  void shouldWhitelistV4IpRange() {
    var whitelist = new IpWhitelist(List.of("46.149.111.0/24"));

    assertThat(whitelist.isV4Whitelisted("46.149.111.255")).isTrue();
    assertThat(whitelist.isV4Whitelisted("46.149.111.127")).isTrue();
    assertThat(whitelist.isV4Whitelisted("46.149.111.0")).isTrue();

    assertThat(whitelist.isV4Whitelisted("46.149.112.0")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.149.110.255")).isFalse();

    assertThat(whitelist.isV4Whitelisted("46.150.111.0")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.148.111.0")).isFalse();

    assertThat(whitelist.isV4Whitelisted("47.150.111.0")).isFalse();
    assertThat(whitelist.isV4Whitelisted("45.148.111.0")).isFalse();
  }

  @Test
  void shouldReturnFalseForV6IpAddress() {
    var whitelist = new IpWhitelist(List.of());

    assertThat(whitelist.isV4Whitelisted("0:0:0:0:0:0:0:1")).isFalse();
  }

  @Test
  void shouldReturnFalseIfInputIpIsInvalid() {
    var whitelist = new IpWhitelist(List.of());

    assertThat(whitelist.isV4Whitelisted("invalid")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.149.110.256")).isFalse();
    assertThat(whitelist.isV4Whitelisted("46.149.111.-1")).isFalse();
  }
}

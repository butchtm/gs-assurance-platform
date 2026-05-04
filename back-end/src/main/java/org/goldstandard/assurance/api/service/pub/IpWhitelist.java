package org.goldstandard.assurance.api.service.pub;

import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressTrie;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@RequiredArgsConstructor
@Slf4j
public class IpWhitelist {

  private final IPv4AddressTrie v4Addresses;

  public IpWhitelist(Collection<String> allowedV4CIDRs) {
    this(toIPv4AddressTrie(allowedV4CIDRs));
  }

  public boolean isV4Whitelisted(String ip) {
    var address = new IPAddressString(ip).getAddress();
    if (Objects.isNull(address)) {
      log.warn("null address created from ip {} returning allowed false", ip);
      return false;
    }
    var v4Address = address.toIPv4();
    if (Objects.isNull(v4Address)) {
      log.warn("null v4 address created from ip {} returning allowed false", ip);
      return false;
    }
    var allowed = v4Addresses.elementContains(v4Address);
    log.debug("{} allowed {}", ip, allowed);
    return allowed;
  }

  private static IPv4AddressTrie toIPv4AddressTrie(Collection<String> allowedV4Cidrs) {
    var addresses = new IPv4AddressTrie();
    log.info("building ip whitelist with allowed v4 CIDRs {}", allowedV4Cidrs);
    if (CollectionUtils.isEmpty(allowedV4Cidrs)) {
      return addresses;
    }
    allowedV4Cidrs.stream().map(IpWhitelist::toIPv4PrefixBlock).forEach(addresses::add);
    return addresses;
  }

  private static IPv4Address toIPv4PrefixBlock(String cidr) {
    var parts = cidr.split("/");
    var addressString = new IPAddressString(parts[0]);
    int prefixBlockLength = Integer.parseInt(parts[1]);
    return addressString.getAddress().toIPv4().toPrefixBlock(prefixBlockLength);
  }
}

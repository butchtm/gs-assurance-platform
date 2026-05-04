package org.goldstandard.assurance.api.service.pub;

import static org.goldstandard.assurance.api.configuration.ratelimiting.PublicRateLimitProperties.*;

import io.github.bucket4j.BandwidthBuilder.BandwidthBuilderBuildStage;
import io.github.bucket4j.BandwidthBuilder.BandwidthBuilderCapacityStage;
import io.github.bucket4j.Bucket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PublicRateLimitService {

  private final RateLimitEndpointProperties properties;
  private final IpWhitelist whitelist;
  private final Map<String, Bucket> cache;

  @Builder
  public PublicRateLimitService(RateLimitEndpointProperties properties, IpWhitelist whitelist) {
    this(properties, whitelist, new ConcurrentHashMap<>());
  }

  public List<String> getPathPatterns() {
    return properties.getPathPatterns();
  }

  public boolean isWhitelisted(String ip) {
    return whitelist.isV4Whitelisted(ip);
  }

  public Bucket resolveBucket(String remoteAddress) {
    return cache.computeIfAbsent(remoteAddress, key -> newBucket());
  }

  public void reset() {
    cache.clear();
  }

  private Bucket newBucket() {
    return Bucket.builder().addLimit(buildLimit()).build();
  }

  private Function<BandwidthBuilderCapacityStage, BandwidthBuilderBuildStage> buildLimit() {
    var requestsPerInterval = properties.getRequestsPerInterval();
    var refillDuration = properties.getRefillInterval();
    return limit ->
        limit.capacity(requestsPerInterval).refillIntervally(requestsPerInterval, refillDuration);
  }
}

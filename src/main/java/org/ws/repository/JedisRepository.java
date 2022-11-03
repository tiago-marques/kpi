package org.ws.repository;

import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.CreateParams;
import com.redislabs.redistimeseries.DuplicatePolicy;
import com.redislabs.redistimeseries.MultiRangeParams;
import com.redislabs.redistimeseries.Range;
import com.redislabs.redistimeseries.RedisTimeSeries;
import com.redislabs.redistimeseries.Value;
import com.redislabs.redistimeseries.information.Info;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.ws.enums.GroupBy;
import org.ws.enums.Reducer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

@ApplicationScoped
public class JedisRepository {

    private final RedisTimeSeries rts;
    private final JedisPool jedisPool;

    @Inject
    public JedisRepository(
        @ConfigProperty(name = "quarkus.redis.hosts", defaultValue = "redis://localhost:60135")
            String host,
        RedisRepository redisRepository
    ) {
        jedisPool = new JedisPool(host);
        rts = new RedisTimeSeries(jedisPool);
    }

    public Value[] fetch(String key, Long from, Long to, Aggregation aggregation, ChronoUnit unit, int value) {
        if (from > to) {
            throw new IllegalArgumentException("'from' must be lower or equal than 'to'");
        }

        long timeBucket = to - from;
        if (value > 0) {
            timeBucket = Instant.EPOCH.plus(value, unit).toEpochMilli();
        }

        try {
            return rts.range(key, from, to, aggregation, timeBucket);
        } catch (JedisDataException e) {
            return new Value[0];
        }
    }

    public Range[] fetchAll(GroupBy group, Long from, Long to, Aggregation aggregation, Reducer reducer,
        ChronoUnit unit, int value) {
        if (from > to) {
            throw new IllegalArgumentException("'from' must be lower or equal than 'to'");
        }

        long timeBucket = to - from;
        if (value > 0) {
            timeBucket = Instant.EPOCH.plus(value, unit).toEpochMilli();
        }

        String rec = reducer.name().toLowerCase();
        String grpBy = group.name().toLowerCase();

        try {
            MultiRangeParams mr = new MultiRangeParams();
            mr.aggregation(aggregation, timeBucket);
            mr.withLabels(true);
            mr.groupByReduce(grpBy, rec);
            return rts.mrange(from, to, mr, "type=".concat("currency"));
        } catch (JedisDataException e) {
            return new Range[0];
        }
    }

    public void create(String key, double amount, long timestamp, long retention) {
        create(key, amount, timestamp, retention, null);
    }

    public void create(String key, double amount, long timestamp, long retention,
        Map<String, String> labels) {

        CreateParams createParams = new CreateParams();
        createParams.retentionTime(retention);
        createParams.duplicatePolicy(DuplicatePolicy.SUM);
        createParams.labels(labels);
        rts.add(key, timestamp, amount, createParams);
    }

    public Info getMeta(String key) {
        return rts.info(key);
    }
}
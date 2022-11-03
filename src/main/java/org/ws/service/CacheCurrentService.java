package org.ws.service;

import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.Range;
import com.redislabs.redistimeseries.Value;
import com.redislabs.redistimeseries.information.Info;
import io.smallrye.mutiny.Uni;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.ws.enums.GroupBy;
import org.ws.enums.Reducer;
import org.ws.repository.JedisRepository;
import org.ws.repository.RedisRepository;

@ApplicationScoped
public class CacheCurrentService {

    public static final String KEY = "currency";
    public static final String KEY_CURRENCY_CONCAT = "currency:";

    @Inject
    JedisRepository jedisRepository;

    @Inject
    RedisRepository redisRepository;



    public Value[] fetch(String iso, Long from, Long to, Aggregation aggregation, ChronoUnit unit, int value) {
        String key = KEY_CURRENCY_CONCAT.concat(iso);
        return jedisRepository.fetch(key, from, to, aggregation, unit, value);
    }

    public Range[] fetchAll(GroupBy group, Long from, Long to, Aggregation aggregation, Reducer reducer,
        ChronoUnit unit, int value) {
        return jedisRepository.fetchAll(group, from, to, aggregation, reducer, unit, value);
    }

    public void create(String iso, double amount, Instant time, ChronoUnit unit, int value) {
        Map<String, String> labels = new HashMap<>() {
            {
                put("type", KEY);
                put("iso", iso);
            }
        };
        String key = KEY_CURRENCY_CONCAT.concat(iso);

        if (value == 0) {
            value = 2;
            unit = ChronoUnit.DAYS;
        }
        long timestamp = time.toEpochMilli();
        long retention = Instant.EPOCH.plus(value, unit).toEpochMilli();

        jedisRepository.create(key, amount, timestamp, retention, labels);
    }

    public Info getMeta(String iso) {
        String key = KEY_CURRENCY_CONCAT.concat(iso);
        return jedisRepository.getMeta(key);
    }

    public Uni<List<String>> keys() {
        return redisRepository.keys();
    }

    public Uni<Void> delete(String iso) {
        String key = KEY_CURRENCY_CONCAT.concat(iso);
        return redisRepository.del(key);
    }
}
package org.ws.service;


import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.Range;
import com.redislabs.redistimeseries.Value;
import com.redislabs.redistimeseries.information.Info;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Cacheable;
import javax.transaction.Transactional;
import org.ws.enums.GroupBy;
import org.ws.enums.Reducer;
import org.ws.model.Volume;
import org.ws.repository.VolumeRepository;
import org.ws.to.SeriesMetaTO;
import org.ws.to.VolumeTO;

@ApplicationScoped
public class VolumeService {

    @Inject
    CacheCurrentService cacheCurrentService;

    @Inject
    VolumeRepository volumeRepository;

    @Transactional
    public VolumeTO create(VolumeTO volume, ChronoUnit unit, int value) {
        Volume v = new Volume(volume.getIso(), volume.getAmount(), volume.getTimestamp());
        volumeRepository.persistAndFlush(v);
        Double amount = v.getAmount().setScale(2, RoundingMode.HALF_UP).doubleValue();
        Instant time = v.getTimestamp();
        cacheCurrentService.create(v.getIso(), amount, time, unit, value);
        return new VolumeTO(v.getIso(), v.getAmount(), v.getTimestamp());
    }

    @Transactional
    public List<Volume> findAll() {
        return volumeRepository.findAll().list();
    }

    @Transactional
    public Volume findByIso(String iso) {
        return volumeRepository.findByIso(iso);
    }

    public Uni<Void> del(String iso) {
        return cacheCurrentService.delete(iso);
    }

    public Uni<List<String>> keys() {
        return cacheCurrentService.keys();
    }

    @CacheInvalidate(cacheName = "findVolume")
    public List<Range> findVolumes(GroupBy group, Instant from, Instant to, Aggregation agg, Reducer rec) {
        return findVolumes(group, from, to, agg, rec, null, 0);
    }

    public List<Range> findVolumes(GroupBy group, Instant from, Instant to, Aggregation agg, Reducer rec,
        ChronoUnit unit, int value) {
        Range[] values = cacheCurrentService.fetchAll(group, from.toEpochMilli(), to.toEpochMilli(), agg, rec, unit,
            value);
        return Arrays.stream(values).toList();
    }

    @CacheResult(cacheName = "find-volume-iso")
    public List<Value> findVolume(@CacheKey String iso, @CacheKey Instant from, @CacheKey Instant to, @CacheKey Aggregation agg) {
        return findVolume(iso, from, to, agg, null, 0);
    }

    public List<Value> findVolume( String iso, Instant from,  Instant to,  Aggregation agg, ChronoUnit unit, int value) {
        Value[] values = cacheCurrentService.fetch(iso, from.toEpochMilli(), to.toEpochMilli(), agg, unit, value);
        return Arrays.stream(values).toList();
    }

    public SeriesMetaTO info(String iso) {
        Info i = cacheCurrentService.getMeta(iso);
        return new SeriesMetaTO(i.getLabel("type"), i.getLabel("iso"), i.getProperty("firstTimestamp"),
            i.getProperty("lastTimestamp"));
    }
}
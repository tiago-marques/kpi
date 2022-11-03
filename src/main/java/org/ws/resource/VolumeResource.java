package org.ws.resource;

import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.Range;
import com.redislabs.redistimeseries.Value;
import io.smallrye.mutiny.Uni;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.ws.service.VolumeService;
import org.ws.enums.GroupBy;
import org.ws.enums.Reducer;
import org.ws.to.SeriesMetaTO;
import org.ws.to.VolumeTO;

@Path("/volumes")
public class VolumeResource {

    public static final String SUM = "SUM";
    public static final String MINUTES = "MINUTES";
    public static final String TYPE = "TYPE";

    @Inject
    VolumeService service;

    @GET
    public Uni<List<String>> ccy() {
        return service.keys();
    }

    @POST
    public VolumeTO create(VolumeTO volume,
        @DefaultValue(MINUTES) @QueryParam(value = "unit") ChronoUnit unit,
        @DefaultValue("0") @QueryParam(value = "value") int value
        ) {
        return service.create(volume, unit, value);
    }

    @DELETE
    @Path("/currency/{iso}")
    public Uni<Void> delete(String iso) {
        return service.del(iso);
    }

    @GET
    @Path("/currency/{iso}/info")
    public SeriesMetaTO info(String iso) {
        return service.info(iso);
    }


    @GET
    @Path("/currency/{iso}")
    public List<Value> get(String iso,
        @DefaultValue(MINUTES) @QueryParam(value = "unit") ChronoUnit unit,
        @DefaultValue("0") @QueryParam(value = "value") int value,
        @DefaultValue(SUM) @QueryParam(value = "aggregation") Aggregation agg,
        @QueryParam(value = "timestamp_from") Instant from,
        @QueryParam(value = "timestamp_to") Instant to) {
        return service.findVolume(iso, from, to, agg, unit, value);
    }

    @GET
    @Path("/all")
    public List<Range> getAll(
        @DefaultValue(TYPE) @QueryParam(value = "groupBy") GroupBy group,
        @DefaultValue(MINUTES) @QueryParam(value = "unit") ChronoUnit unit,
        @DefaultValue("0") @QueryParam(value = "value") int value,
        @DefaultValue(SUM) @QueryParam(value = "aggregation") Aggregation agg,
        @DefaultValue(SUM) @QueryParam(value = "reducer") Reducer rec,
        @QueryParam(value = "timestamp_from") Instant from,
        @QueryParam(value = "timestamp_to") Instant to) {
        return service.findVolumes(group, from, to, agg, rec, unit, value);
    }


}
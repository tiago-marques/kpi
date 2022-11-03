package org.ws.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import javax.enterprise.context.ApplicationScoped;
import org.ws.model.Volume;

@ApplicationScoped
public class VolumeRepository implements PanacheRepository<Volume> {

    public Volume findByIso(String iso) {
        return find("from Volume where iso = ?1", Sort.descending("timestamp"), iso).firstResult();
    }
}
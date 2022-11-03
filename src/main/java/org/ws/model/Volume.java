package org.ws.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "volumes")
@RegisterForReflection
public class Volume {

    private Long id;
    private String iso;
    private BigDecimal amount;
    private Instant timestamp;

    public Volume(){}

    public Volume(String iso, BigDecimal amount, Instant timestamp) {
        this.iso = iso;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Id
    @SequenceGenerator(name = "volumeSeq", sequenceName = "volume_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "volumeSeq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
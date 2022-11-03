package org.ws.to;


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

@RegisterForReflection
@JsonNaming(SnakeCaseStrategy.class)
@Schema(example = """
{
  "iso": "BRL",
  "amount": 1,
  "timestamp": "2022-03-10T00:00:00Z"
}
    """)
public class VolumeTO {

    private String iso;
    private BigDecimal amount;
    private Instant timestamp;

    public VolumeTO(String iso, BigDecimal amount, Instant timestamp) {
        this.iso = iso;
        this.amount = amount;
        this.timestamp = timestamp;
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
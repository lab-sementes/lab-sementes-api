package br.com.setrem.computacao.pie.labsementes.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class MeasurementID implements Serializable {

    @Column(name = "sensor_id", nullable = false)
    public Integer sensorId;

    @Column(name = "ts", nullable = false)
    public Instant ts;

    public MeasurementID() {}

    public MeasurementID(Integer sensorId, Instant ts) {
        this.sensorId = sensorId;
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MeasurementID that = (MeasurementID) o;
        return Objects.equals(sensorId, that.sensorId) && Objects.equals(ts, that.ts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, ts);
    }
}

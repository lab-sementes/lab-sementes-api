package br.com.setrem.computacao.pie.labsementes.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "measurement")
public class Measurement extends PanacheEntityBase {

    // Primary Key Composta
    @EmbeddedId
    public MeasurementID id;

    // Os valores medidos
    public double temperature;
    public double humidity;

    // Relacionamentos com o Sensor
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    public Sensor sensor;

    // Contrutor para facilitar a inserção
    public Measurement() {}

    public Measurement(Sensor sensor, Instant ts, Double temperature, Double humidity) {
        this.id = new MeasurementID(sensor.id, ts);
        this.sensor = sensor;
        this.temperature = temperature;
        this.humidity = humidity;
    }
}

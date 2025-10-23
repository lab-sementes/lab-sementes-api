package org.setrem.computacao.pie.labsementes.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.setrem.computacao.pie.labsementes.SensorType;

@Entity
@Table(name = "sensors")
public class Sensor extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    public Long sensorId;

    @Column(name = "sensor_name", unique = true, nullable = false)
    public String sensorName;

    @Column(name = "sensor_type", nullable = false)
    public SensorType sensorType;

    public String location;

    @Column(name = "unique_address")
    public String uniqueAddress;
}
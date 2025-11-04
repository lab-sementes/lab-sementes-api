package br.com.setrem.computacao.pie.labsementes.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "measurements")
public class Measurement extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    public Long measurementId;

    @Column(name = "\"time\"", nullable = false)
    public Instant time;

    @Column(nullable = false)
    public Double temperature;

    public Double humidity;

    /*
    * Cria um mapeamento do relacionamento da tabela de Measurements com a de Sensores
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    public Sensor sensor;
}

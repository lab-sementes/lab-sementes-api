package br.com.setrem.computacao.pie.labsementes.model;

import br.com.setrem.computacao.pie.labsementes.SensorStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import br.com.setrem.computacao.pie.labsementes.SensorType;
import jakarta.transaction.Transactional;

import java.time.Instant;

@Entity
@Table(name = "sensor")
public class Sensor extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    public Integer id;

    @Column(name = "sensor_name", length = 255, unique = true, nullable = false)
    public String sensorName;

    @Column(name = "sensor_type", length = 100, nullable = false)
    public SensorType sensorType;

    @Column(name = "unique_address", length = 100, unique = true)
    public String uniqueAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public SensorStatus status;

    @Column(name = "data_criacao", nullable = false, updatable = false) // updatable=false é boa prática
    public Instant dataCriacao;

    @Column(name = "data_ultima_mudanca_status")
    public Instant dataUltimaMudancaStatus;

    public static Sensor findByAddress(String address) {
        return find("uniqueAddress", address).firstResult();
    }

    @Transactional
    public void desativar(Instant data) {
        this.status = SensorStatus.Desativado;
        this.dataUltimaMudancaStatus = data;
        this.persistAndFlush();
    }
}
package org.setrem.computacao.pie.labsementes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.setrem.computacao.pie.labsementes.dto.MeasurementRequest;
import org.setrem.computacao.pie.labsementes.model.Measurement;
import org.setrem.computacao.pie.labsementes.model.Sensor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MeasurementService {

    @Transactional
    public Measurement create(MeasurementRequest dto) {

        // Encontra o sensor correspondente ao ID
        Optional<Sensor> optional = Sensor.findByIdOptional(dto.sensorId);
        Sensor sensor = optional.orElseThrow(() -> new NotFoundException("Sensor com ID " + dto.sensorId + " não encontrado"));

        // Cria uma nova medida
        Measurement measurement = new Measurement();
        measurement.sensor = sensor;
        measurement.temperature = dto.temperature;
        measurement.humidity = dto.humidity;

        // Se não receber um timestamp, pega o tempo atual
        measurement.time = (dto.time != null) ? dto.time : Instant.now();

        // Salva e retorna
        measurement.persist();
        return measurement;
    }

    public Optional<Measurement> getLatestForSensor(Long sensorId) {
        return Measurement
                .find("sensor.sensorId = ?1 ORDER BY time DESC", sensorId)
                .firstResultOptional();
    }

    public List<Measurement> getForSensor(Long sensorId, int pageIndex, int pageSize) {
        return Measurement
                .find("sensor.sensorId = ?1 ORDER BY time DESC", sensorId)
                .page(pageIndex, pageSize)
                .list();
    }

    public List<Measurement> getForSensorInTimeRange(Long sensorId, Instant start, Instant end) {
        return Measurement.list(
                "sensor.sensorId = ?1 AND time >= ?2 AND time <= ?3 ORDER BY time DESC",
                sensorId, start, end
        );
    }
}

package br.com.setrem.computacao.pie.labsementes.service;

import br.com.setrem.computacao.pie.labsementes.dto.SensorDataAggregate;
import br.com.setrem.computacao.pie.labsementes.model.MeasurementID;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import br.com.setrem.computacao.pie.labsementes.dto.MeasurementDTO;
import br.com.setrem.computacao.pie.labsementes.model.Measurement;
import br.com.setrem.computacao.pie.labsementes.model.Sensor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MeasurementService {

    // Injeta um datasource para lidarmos com queries nativas do Timescale
    @Inject
    DataSource dataSource;

    // Injetando o Serviço de email
    @Inject
    Mailer mailer;

    // Injetando o email destino
    @ConfigProperty(name = "lab.email.responsavel")
    String emailResponsavel;

    // --- Métodos do Panache (CRUD Simples)

    @Transactional
    public Measurement create(MeasurementDTO dto) {
        Sensor sensor = Sensor.findById(dto.sensorId());

        if (sensor == null) {
            throw new NotFoundException("Sensor com ID " + dto.sensorId() + " não encontrado.");
        }

        Measurement measurement = new Measurement(
                sensor,
                dto.ts() != null ? dto.ts() : Instant.now(),
                dto.temperature(),
                dto.humidity()
        );

        measurement.persist();

        verificarEEnviarAlertas(sensor, measurement);

        return measurement;
    }

    public Optional<Measurement> findById(Integer sensorId, Instant ts) {
        MeasurementID id = new MeasurementID(sensorId, ts);
        return Measurement.findByIdOptional(id);
    }

    // --- Métodos com DataSource (Consultas nativas do Timescale)

    public List<SensorDataAggregate> getAggregates(Integer sensorId, String bucketDuration) {
        List<SensorDataAggregate> resultados = new ArrayList<>();

        String sql = String.format("""
            SELECT
                time_bucket('%s', ts) AS hora,
                avg(temperature) AS media
            FROM
                measurement
            WHERE
                sensor_id = ?
            GROUP BY
                hora
            ORDER BY
                hora DESC
            LIMIT 100
        """, bucketDuration);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sensorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeamento manual para o DTO
                    OffsetDateTime odt = rs.getObject("hora", OffsetDateTime.class);
                    Instant horaAgregada = odt.toInstant();
                    Double mediaValor = rs.getDouble("media");

                    resultados.add(new SensorDataAggregate(horaAgregada, mediaValor));
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Erro SQL Nativo: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
        return resultados;
    }

    public Optional<Measurement> findLatestMeasurement(Integer sensorId) {
        // Selecionamos todos os campos para reconstruir o objeto Measurement
        String sql = """
            SELECT ts, sensor_id, temperature, humidity 
            FROM measurement 
            WHERE sensor_id = ? 
            ORDER BY ts DESC 
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sensorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeamento manual do ResultSet para a Entidade Measurement

                    // 1. Mapear o TIMESTAMP (usando OffsetDateTime, como corrigimos antes)
                    OffsetDateTime odt = rs.getObject("ts", OffsetDateTime.class);

                    // 2. Criar a chave composta (ID)
                    MeasurementID id = new MeasurementID(sensorId, odt.toInstant());

                    // 3. Reconstruir a entidade Measurement (sem o relacionamento 'sensor')
                    Measurement latest = new Measurement();
                    latest.id = id;
                    latest.temperature = rs.getDouble("temperature");
                    latest.humidity = rs.getDouble("humidity");

                    // O campo 'sensor' estará null, mas para esta leitura rápida é aceitável,
                    // pois o ID já o referencia. Se o objeto 'Sensor' for essencial,
                    // você teria que buscá-lo separadamente ou fazer um JOIN.

                    return Optional.of(latest);
                }
            }

        } catch (SQLException e) {
            String errorMessage = "Erro SQL Nativo ao buscar última leitura: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }

        return Optional.empty();
    }
}

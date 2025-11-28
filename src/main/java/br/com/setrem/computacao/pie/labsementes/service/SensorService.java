package br.com.setrem.computacao.pie.labsementes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import br.com.setrem.computacao.pie.labsementes.model.Sensor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SensorService {

    public List<Sensor> listarTodos() {
        return Sensor.listAll();
    }

    public Optional<Sensor> buscarPorId(Integer id) {
        return Sensor.findByIdOptional(id);
    }

    public Optional<Sensor> buscarPorAddress(String address) {
        return Optional.ofNullable(Sensor.findByAddress(address));
    }

    @Transactional
    public Sensor create(Sensor sensor) {

        if (sensor.dataCriacao == null) {
            sensor.dataCriacao = Instant.now();
        }

        // Garantir que a data da última mudança de status também seja setada na criação
        if (sensor.dataUltimaMudancaStatus == null) {
            sensor.dataUltimaMudancaStatus = sensor.dataCriacao;
        }

        sensor.persist();
        return sensor;
    }

    @Transactional
    public Optional<Sensor> atualizar(Integer id, Sensor sensorAtualizado) {

        Optional<Sensor> sensorOptional = Sensor.findByIdOptional(id);

        if (sensorOptional.isEmpty()) {
            return Optional.empty();
        }

        Sensor sensorExistente = sensorOptional.get();

        sensorExistente.sensorName = sensorAtualizado.sensorName;
        sensorExistente.sensorType = sensorAtualizado.sensorType;
        sensorExistente.uniqueAddress = sensorAtualizado.uniqueAddress;

        sensorExistente.sala = sensorAtualizado.sala;
        sensorExistente.tempMin = sensorAtualizado.tempMin;
        sensorExistente.tempMax = sensorAtualizado.tempMax;
        sensorExistente.umidMin = sensorAtualizado.umidMin;
        sensorExistente.umidMax = sensorAtualizado.umidMax;

        if (sensorExistente.status != sensorAtualizado.status) {
            sensorExistente.status = sensorAtualizado.status;
            sensorExistente.dataUltimaMudancaStatus = Instant.now();
        }

        return Optional.of(sensorExistente);
    }

    @Transactional
    public boolean deletar(Integer id) {
        return Sensor.deleteById(id);
    }

    @Transactional
    public Optional<Sensor> desativarSensor(Integer id) {
        Optional<Sensor> sensorOptional = Sensor.findByIdOptional(id);

        sensorOptional.ifPresent(sensor -> sensor.desativar(Instant.now()));

        return sensorOptional;
    }
}

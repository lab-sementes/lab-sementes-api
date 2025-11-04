package br.com.setrem.computacao.pie.labsementes.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import br.com.setrem.computacao.pie.labsementes.model.Sensor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SensorService {
    public List<Sensor> listarTodos() {
        return Sensor.listAll();
    }

    public Optional<Sensor> buscarPorId(Long id) {
        return Sensor.findByIdOptional(id);
    }

    @Transactional
    public Sensor create(Sensor sensor) {
        sensor.persist();
        return sensor;
    }

    @Transactional
    public Optional<Sensor> atualizar(Long id, Sensor sensorAtualizado) {
        Optional<Sensor> sensorOptional = Sensor.findByIdOptional(id);

        if (sensorOptional.isEmpty()) {
            return Optional.empty();
        }

        Sensor sensorExistente = sensorOptional.get();
        sensorExistente.sensorName = sensorAtualizado.sensorName;
        sensorExistente.sensorType = sensorAtualizado.sensorType;
        sensorExistente.location = sensorAtualizado.location;
        sensorExistente.uniqueAddress = sensorAtualizado.uniqueAddress;

        return Optional.of(sensorExistente);
    }

    @Transactional
    public boolean deletar(Long id) {
        return Sensor.deleteById(id);
    }
}

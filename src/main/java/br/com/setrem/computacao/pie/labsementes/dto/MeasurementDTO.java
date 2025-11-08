package br.com.setrem.computacao.pie.labsementes.dto;

import java.time.Instant;

// Um record é uma classe apenas de dados, como uma @dataclass do Python.
// O mesmo se aplica a um DTO ≥ Objeto para transferir dados.
public record MeasurementDTO (
        Integer sensorId,
        Instant ts,
        Double temperature,
        Double humidity
){}

package br.com.setrem.computacao.pie.labsementes.dto;

import java.time.Instant;

public record SensorDataAggregate(
        Instant hora,
        Double media
) {}

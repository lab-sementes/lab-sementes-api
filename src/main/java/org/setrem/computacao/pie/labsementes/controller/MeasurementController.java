package org.setrem.computacao.pie.labsementes.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.jboss.resteasy.reactive.RestResponse;
import org.setrem.computacao.pie.labsementes.dto.MeasurementRequest;
import org.setrem.computacao.pie.labsementes.model.Measurement;
import org.setrem.computacao.pie.labsementes.service.MeasurementService;

import java.time.Instant;
import java.util.List;

@Path("/measurements")
public class MeasurementController {
    @Inject
    MeasurementService measurementService;


    @POST
    public RestResponse<Measurement> create(MeasurementRequest dto) {
        try {
            Measurement newMeasurement = measurementService.create(dto);
            return RestResponse.status(RestResponse.Status.CREATED, newMeasurement);
        } catch (NotFoundException e) {
            return RestResponse.status(RestResponse.Status.NOT_FOUND, null);
        }
    }

    @GET
    @Path("/sensor/{sensorId}")
    public RestResponse<List<Measurement>> getBySensor(
            @PathParam("sensorId") Long sensorId,
            @QueryParam("latest") boolean latest,
            @QueryParam("start") Instant start,
            @QueryParam("end") Instant end,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {

        if (latest) {
            // Se ?latest=true, retorna só a última (ou 404)
            return measurementService.getLatestForSensor(sensorId)
                    .map(List::of) // Converte o Optional<Measurement> para List<Measurement>
                    .map(RestResponse::ok)
                    .orElse(RestResponse.status(RestResponse.Status.NOT_FOUND));
        }

        if (start != null && end != null) {
            // Se ?start=...&end=..., busca por intervalo
            List<Measurement> measurements = measurementService.getForSensorInTimeRange(sensorId, start, end);
            return RestResponse.ok(measurements);
        }

        // Se não, faz a busca paginada padrão
        List<Measurement> measurements = measurementService.getForSensor(sensorId, page, size);
        return RestResponse.ok(measurements);
    }
}

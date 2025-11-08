package br.com.setrem.computacao.pie.labsementes.controller;

import br.com.setrem.computacao.pie.labsementes.dto.SensorDataAggregate;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;
import br.com.setrem.computacao.pie.labsementes.dto.MeasurementDTO;
import br.com.setrem.computacao.pie.labsementes.model.Measurement;
import br.com.setrem.computacao.pie.labsementes.service.MeasurementService;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@Path("/measurements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MeasurementController {

    @Inject
    MeasurementService measurementService;

    // --- Endpoints Panache
    @POST
    public RestResponse<Measurement> criar(MeasurementDTO dto) {
        try {
            Measurement newMeasurement = measurementService.create(dto);
            return RestResponse.ResponseBuilder.ok(newMeasurement).status(RestResponse.Status.CREATED).build();
        } catch (NotFoundException e) {
            return RestResponse.notFound();
        } catch (Exception e) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{sensorId}/{ts}")
    public RestResponse<Measurement> buscarPorId(
            @PathParam("sensorId") Integer sensorId,
            @PathParam("ts") Instant ts) {
        return measurementService.findById(sensorId, ts)
                .map(measurement -> RestResponse.ResponseBuilder.ok(measurement).build())
                .orElse(RestResponse.status(RestResponse.Status.NOT_FOUND));
    }

    // --- Endpoints DataSource

    @GET
    @Path("/{sensorId}/aggregates")
    public RestResponse<List<SensorDataAggregate>> buscarAgregacao(
            @PathParam("sensorId") Integer sensorId,
            @QueryParam("bucket") @DefaultValue("1 hour") String bucketDuration) {

        List<SensorDataAggregate> agregados = measurementService.getAggregates(sensorId, bucketDuration);

        if (agregados.isEmpty()) {
            return RestResponse.noContent();
        }

        return RestResponse.ResponseBuilder.ok(agregados).build();
    }

    @GET
    @Path("/{sensorId}/latest")
    public RestResponse<Measurement> getLatestMeasurement(@PathParam("sensorId") Integer sensorId) {

        return measurementService.findLatestMeasurement(sensorId)
                .map(measurement -> RestResponse.ResponseBuilder.ok(measurement).build())
                .orElse(RestResponse.status(RestResponse.Status.NOT_FOUND));
    }
}

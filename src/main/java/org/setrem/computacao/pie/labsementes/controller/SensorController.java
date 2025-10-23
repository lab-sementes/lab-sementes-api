package org.setrem.computacao.pie.labsementes.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;
import org.setrem.computacao.pie.labsementes.model.Sensor;
import org.setrem.computacao.pie.labsementes.service.SensorService;

import java.util.List;

@Path("/sensores")
@Consumes(MediaType.APPLICATION_JSON)
public class SensorController {
    @Inject
    private SensorService sensorService;

    @GET
    public RestResponse<List<Sensor>> listarTodos() {
        return RestResponse.ResponseBuilder.ok(sensorService.listarTodos()).build();
    }

    @POST
    public RestResponse<Sensor> criar(Sensor sensor) {
        Sensor newSensor = sensorService.create(sensor);
        return RestResponse.ResponseBuilder.ok(newSensor).status(RestResponse.Status.CREATED).build();
    }

    @GET
    @Path("/{id}")
    public RestResponse<Sensor> buscarPorId(@PathParam("id") Long id) {

        return sensorService.buscarPorId(id)
                .map(sensor -> RestResponse.ResponseBuilder.ok(sensor).build())
                .orElse(RestResponse.status(RestResponse.Status.NOT_FOUND));
    }

    @PUT
    @Path("/{id}")
    public RestResponse<Sensor> atualizar(@PathParam("id") Long id, Sensor sensor) {
        return sensorService.atualizar(id, sensor)
                .map(sensorAtualizado -> RestResponse.ResponseBuilder.ok(sensorAtualizado).build())
                .orElse(RestResponse.status(RestResponse.Status.NOT_FOUND));
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<?> deletar(@PathParam("id") Long id) {
        if (sensorService.deletar(id)) {
            return RestResponse.ResponseBuilder.noContent().build();
        }
        return RestResponse.ResponseBuilder.notFound().build();
    }
}

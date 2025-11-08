package br.com.setrem.computacao.pie.labsementes.exceptions;

import br.com.setrem.computacao.pie.labsementes.dto.ErrorResponse;
import org.hibernate.PropertyValueException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.Instant;

public class ExceptionMapper {

    // Mapper para valores obrigatórios não preenchidos
    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapPropertyValueException(PropertyValueException e) {
        String fieldName = e.getPropertyName() != null ? e.getPropertyName() : "um campo obrigatório";

        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                RestResponse.Status.BAD_REQUEST.getStatusCode(),
                "Erro de Validação de Persistência",
                String.format("O campo obrigatório '%s' não foi fornecido.", fieldName)
        );

        return RestResponse.status(RestResponse.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(Exception e) {

        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                RestResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Erro Interno do Servidor",
                String.format("Ocorreu um erro inesperado: '%s'", e.getMessage())
        );

        return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, error);
    }
}

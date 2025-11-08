package br.com.setrem.computacao.pie.labsementes.dto;

// Usamos um record para padronizar mensagens de erro.
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message
) {}

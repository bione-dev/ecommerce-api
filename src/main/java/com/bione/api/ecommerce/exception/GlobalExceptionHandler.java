package com.bione.api.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 Tratamento para ClienteNotFoundException
    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> handleClienteNotFoundException(ClienteNotFoundException ex) {
        log.error("Erro: Cliente não encontrado - ID {}", ex.getDetails(), ex);

        String detalhes = "O cliente com ID (" + ex.getDetails() + ") não foi encontrado.";
        ErroPadraoDTO erroPadraoDTO = ErroPadraoDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .mensagem("Cliente não encontrado no banco de dados")
                .detalhes(detalhes)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroPadraoDTO);
    }

    // 🟡 Tratamento para ClienteInvalidException (Erro de validação)
    @ExceptionHandler(ClienteInvalidException.class)
    public ResponseEntity<ErroPadraoDTO> handleClienteInvalidException(ClienteInvalidException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());

        ErroPadraoDTO erroPadraoDTO = ErroPadraoDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem("Dados do cliente inválidos")
                .detalhes(ex.getMessage()) // Exibe detalhes da validação
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroPadraoDTO);
    }

    // 🟠 Tratamento para erros de validação (exemplo: campos obrigatórios não preenchidos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação nos campos de entrada");

        // Obtém todos os erros de validação e os concatena em uma string
        String detalhes = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroPadraoDTO erroPadraoDTO = ErroPadraoDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem("Erro de validação nos campos")
                .detalhes(detalhes)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroPadraoDTO);
    }

    // 🔵 Tratamento de exceção genérica (Erros inesperados)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDTO> handleGenericException(Exception ex) {
        log.error("Erro interno no servidor: {}", ex.getMessage(), ex);

        ErroPadraoDTO erroPadraoDTO = ErroPadraoDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensagem("Erro interno no servidor")
                .detalhes("Ocorreu um erro inesperado. Contate o suporte.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroPadraoDTO);
    }
}

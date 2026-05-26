package com.ezworks.controller;

import com.ezworks.dto.payment.MetodoPagoRequest;
import com.ezworks.dto.payment.MetodoPagoResponse;
import com.ezworks.dto.payment.MiDeudaResponse;
import com.ezworks.dto.payment.TransaccionResponse;
import com.ezworks.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/mi-deuda")
    public MiDeudaResponse miDeuda() {
        return pagoService.obtenerMiDeuda();
    }

    @GetMapping("/metodos")
    public List<MetodoPagoResponse> metodos() {
        return pagoService.listarMetodosPago();
    }

    @PostMapping("/metodos")
    @ResponseStatus(HttpStatus.CREATED)
    public MetodoPagoResponse registrarMetodo(@Valid @RequestBody MetodoPagoRequest request) {
        return pagoService.registrarMetodoPago(request);
    }

    @PostMapping("/deudas/{deudaId}/pagar")
    public TransaccionResponse pagarDeuda(
            @PathVariable Long deudaId,
            @RequestBody Map<String, Long> body) {
        Long metodoPagoId = body.get("metodoPagoId");
        if (metodoPagoId == null) {
            throw new com.ezworks.exception.ApiException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "metodoPagoId es requerido");
        }
        return pagoService.pagarDeuda(deudaId, metodoPagoId);
    }
}

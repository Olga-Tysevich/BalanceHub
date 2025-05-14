package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.services.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v1/api/transfers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@Validated
public class TransferController {
    private final TransferService transferService;

    @Operation(
            summary = "Initiate a money transfer",
            description = "Creates a new money transfer between two accounts based on the provided transfer details."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Transfer initiated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))
    )
    @PostMapping("/add")
    public ResponseEntity<?> addTransfer(@RequestBody @Valid MoneyTransferReq req) {
        Long transferId = transferService.createTransfer(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("transferId", transferId));
    }

}

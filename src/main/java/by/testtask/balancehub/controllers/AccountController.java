package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.services.AccountService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @Operation(
            summary = "Create a new account",
            description = "Creates a new account for the user with the given details. The account is created with an initial balance."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Account successfully created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountDTO.class))
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/add")
    public ResponseEntity<?> addAccount(@RequestBody @Valid AccountDTO req) {
        Long accountId = accountService.createAccount(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accountId", accountId));
    }

}

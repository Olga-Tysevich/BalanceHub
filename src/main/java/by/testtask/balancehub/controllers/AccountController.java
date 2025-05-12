package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/add")
    public ResponseEntity<?> addAccount(@RequestBody @Valid AccountDTO req) {
        Long accountId = accountService.createAccount(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accountId", accountId));
    }

}

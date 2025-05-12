package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v1/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final AccountService accountService;

    @PostMapping("/add")
    public ResponseEntity<?> addTransfer(@RequestBody @Valid MoneyTransferReq req) {
        Long transferId = accountService.createTransfer(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("transferId", transferId));
    }

}

package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import jakarta.validation.constraints.NotNull;

public interface TransferService {

    void makeTransfer(@NotNull TransferDTO transferDTO);

    Long createTransfer(@NotNull MoneyTransferReq moneyTransferReq);

    void cancelPendingTransfers();

}

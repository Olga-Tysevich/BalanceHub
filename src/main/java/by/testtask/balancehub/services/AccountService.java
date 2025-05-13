package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import jakarta.validation.constraints.NotNull;

public interface AccountService {

    Long createAccount(@NotNull AccountDTO accountDTO);

    Long createTransfer(@NotNull MoneyTransferReq moneyTransferReq);

    void makeTransfer(@NotNull TransferDTO transferDTO);

    void safelyIncreaseBalance();

}

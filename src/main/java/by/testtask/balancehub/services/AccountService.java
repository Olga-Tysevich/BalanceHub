package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.redis.TransferDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import jakarta.validation.constraints.NotNull;

public interface AccountService {

    /*
    * Этот метод мог бы быть вызван если бы юзеры создавались через сервис,
    * но они создаются миграциями по заданию
    */
    Long createAccount(@NotNull AccountDTO accountDTO);

    void createTransfer(@NotNull MoneyTransferReq moneyTransferReq);

    void makeTransfer(@NotNull TransferDTO transferDTO);

}

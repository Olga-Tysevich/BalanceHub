package by.testtask.balancehub.services;

import by.testtask.balancehub.domain.Transfer;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;

public interface AccountService {

    /*
    * Этот метод мог бы быть вызван если бы юзеры создавались через сервис,
    * но они создаются миграциями по заданию
    */
    Long createAccount(AccountDTO accountDTO);

    void createTransfer(MoneyTransferReq moneyTransferReq);

    void makeTransfer(Transfer transfer);

}

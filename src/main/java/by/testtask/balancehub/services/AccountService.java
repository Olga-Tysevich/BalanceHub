package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;

public interface AccountService {

    Long createAccount(AccountDTO accountDTO);

    void transfer(MoneyTransferReq moneyTransferReq);

}

package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.dto.req.MoneyTransferReq;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.mappers.AccountMapper;
import by.testtask.balancehub.repos.AccountRepo;
import by.testtask.balancehub.services.AccountService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private  final AccountMapper accountMapper;

    @Override
    public Long createAccount(AccountDTO accountDTO) {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        Long currentUserId = currentUser.getId();

        if (!currentUserId.equals(accountDTO.getUserId()))
            throw new AccessDeniedException("The current user is not allowed to modify this account. Current user id: " + currentUserId
                    + " account owner id: " + accountDTO.getUserId());

        if (accountRepo.existsById(accountDTO.getId())) throw new RuntimeException("Account already exists");

        Account account = accountMapper.toEntity(accountDTO);
        account.setUser(currentUser);

        accountRepo.save(account);

        return account.getId();
    }

    @Override
    public void transfer(MoneyTransferReq moneyTransferReq) {

    }



}

package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.dto.common.AccountDTO;
import by.testtask.balancehub.repos.AccountRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_EMAIL_LIST;
import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_PASSWORD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AccountServiceImplTest extends BaseTest {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    public void testCreateAccount_AccountAlreadyExists_ThrowsRuntimeException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        Account existing = accountRepo.findById(1L).get();

        AccountDTO dto = new AccountDTO();
        dto.setId(existing.getId());
        dto.setUserId(existing.getUser().getId());

        assertThatThrownBy(() -> accountService.createAccount(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account already exists");
    }

    @Test
    public void testCreateAccount_DifferentUser_ThrowsAccessDeniedException() {
        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);

        AccountDTO dto = new AccountDTO();
        dto.setId(9999L);
        dto.setUserId(999L);

        assertThatThrownBy(() -> accountService.createAccount(dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not allowed to modify this account");
    }

}

package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.BaseTest;
import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.events.Events;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.AccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BalanceSchedulerTest extends BaseTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private BalanceScheduler balanceScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        balanceScheduler = new BalanceScheduler(eventPublisher, userMapper, accountRepo, cacheManager);
    }

    @Test
    void testIncreaseBalanceUpToLimit() {

        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("100.00"));
        account.setInitialBalance(new BigDecimal("100.00"));

        when(accountRepo.findAll()).thenReturn(List.of(account));
        when(userMapper.toUserIndex(any())).thenReturn(new UserIndexDTO());
        when(cacheManager.getCache("users")).thenReturn(cache);

        balanceScheduler.increaseBalances();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(accountCaptor.capture());

        Account updated = accountCaptor.getValue();
        assertEquals(new BigDecimal("110.00"), updated.getBalance());
        verify(eventPublisher).publishEvent(any(Events.UserChangedEvent.class));
    }

    @Test
    void testBalanceDoesNotExceedMaxLimit() {
        User user = new User();
        user.setId(2L);

        Account account = accountRepo.findById(1L).get();
        account.setBalance(new BigDecimal("300.00"));
        account.setInitialBalance(new BigDecimal("100.00"));

        when(accountRepo.findAll()).thenReturn(List.of(account));

        balanceScheduler.increaseBalances();

        verify(accountRepo, never()).save(any());
    }

    @Test
    void testInitialBalanceIsZeroAndUpdated() {
        User user = new User();
        user.setId(3L);

        Account account = accountRepo.findById(3L).get();
        account.setBalance(new BigDecimal("100.00"));
        account.setInitialBalance(BigDecimal.ZERO);

        when(accountRepo.findAll()).thenReturn(List.of(account));
        when(userMapper.toUserIndex(any())).thenReturn(new UserIndexDTO());
        when(cacheManager.getCache("users")).thenReturn(cache);

        balanceScheduler.increaseBalances();

        verify(accountRepo).save(argThat(acc -> acc.getInitialBalance().equals(new BigDecimal("50.00"))));
    }
}
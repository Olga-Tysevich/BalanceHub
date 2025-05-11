//package by.testtask.balancehub.services.impl;
//
//import by.testtask.balancehub.BaseTest;
//import by.testtask.balancehub.domain.Account;
//import by.testtask.balancehub.domain.Transfer;
//import by.testtask.balancehub.dto.req.MoneyTransferReq;
//import by.testtask.balancehub.events.Events;
//import by.testtask.balancehub.events.listeners.TransferEventListener;
//import by.testtask.balancehub.repos.AccountRepo;
//import by.testtask.balancehub.repos.TransferRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.context.annotation.Bean;
//
//import java.math.BigDecimal;
//
//import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_EMAIL_LIST;
//import static by.testtask.balancehub.utils.TestConstants.USERNAME_1_PASSWORD;
//import static org.mockito.Mockito.*;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith({SpringExtension.class, MockitoExtension.class})
//class AccountServiceIntegrationTest extends BaseTest {
//
//    @Mock
//    private AccountRepo accountRepo;
//
//    @Mock
//    private TransferRepo transferRepo;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @Mock
//    private RedisTemplate<String, Transfer> redisTemplate;  // Мокаем RedisTemplate
//
//    @Mock
//    private RedisConnectionFactory redisConnectionFactory;  // Мокаем RedisConnectionFactory
//
//    @InjectMocks
//    private AccountServiceImpl accountService;
//
//    @InjectMocks
//    private TransferEventListener transferEventListener;
//
//    @TestConfiguration
//    static class TestConfig {
//
//        @Bean
//        public RedisTemplate<String, Transfer> redisTemplate() {
//            RedisTemplate<String, Transfer> template = mock(RedisTemplate.class);  // Мокаем RedisTemplate
//            return template;
//        }
//
//        @Bean
//        public RedisConnectionFactory redisConnectionFactory() {
//            return mock(RedisConnectionFactory.class);  // Мокаем RedisConnectionFactory
//        }
//
//        @Bean
//        public TransferEventListener transferEventListener(RedisTemplate<String, Transfer> redisTemplate) {
//            return new TransferEventListener(redisTemplate);  // Передаем замоканный RedisTemplate
//        }
//    }
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testTransferMoney() throws InterruptedException {
//        Account fromAccount = accountRepo.findById(1L).get();
//
//        super.setAuthentication(USERNAME_1_EMAIL_LIST.getFirst(), USERNAME_1_PASSWORD);
//
//        fromAccount.setBalance(new BigDecimal("200.00"));
//        fromAccount.setHold(BigDecimal.ZERO);
//        when(accountRepo.save(fromAccount)).thenReturn(fromAccount);
//
//        Account toAccount = accountRepo.findById(2L).get();
//        toAccount.setBalance(BigDecimal.ZERO);
//        when(accountRepo.save(toAccount)).thenReturn(toAccount);
//
//        MoneyTransferReq transfer = new MoneyTransferReq();
//        transfer.setFromAccountId(fromAccount.getId());
//        transfer.setToAccountId(toAccount.getId());
//        transfer.setAmount(new BigDecimal("50.00"));
//
//        accountService.createTransfer(transfer);
//
//        Transfer processedTransfer = new Transfer();
//        processedTransfer.setFromAccount(fromAccount);
//        processedTransfer.setToAccount(toAccount);
//        processedTransfer.setAmount(new BigDecimal("50.00"));
//        transferRepo.save(processedTransfer);
//
//        accountService.makeTransfer(processedTransfer);
//
//        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("50.00"));
//        assertThat(fromAccount.getHold()).isEqualTo(new BigDecimal("50.00"));
//
//        verify(eventPublisher, times(1)).publishEvent(any(Events.TransferEvent.class));
//        verify(eventPublisher, times(1)).publishEvent(any(Events.TransferConfirmed.class));
//
//        // Проверка, что метод RedisTemplate вызвался
//        verify(redisTemplate, times(1)).opsForList().leftPush(eq("TRANSFER_QUEUE_NAME"), any(Transfer.class));
//        verify(redisTemplate, times(1)).opsForList().leftPush(eq("CONFIRMED_TRANSFER_QUEUE_NAME"), any(Transfer.class));
//    }
//}

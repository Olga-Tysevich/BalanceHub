package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.Account;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.id = :accountId AND (a.balance - :amount) >= 0")
    Optional<Account> findByIdAndSufficientBalance(@NotNull Long accountId, @NotNull BigDecimal amount);

    @Query("SELECT a.user.id FROM Account a WHERE a.id = :accountId")
    Optional<Long> findUserIdByAccountId(@NotNull Long accountId);

    @Query("SELECT a.id FROM Account a WHERE a.bonusBalance <= a.initialBalance * :maxAllowedInterestRate")
    Page<Long> findAccountIdsWithBalanceUpToPercent(@Param("maxAllowedInterestRate") BigDecimal maxAllowedInterestRate, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

}

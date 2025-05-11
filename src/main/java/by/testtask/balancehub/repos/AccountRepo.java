package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    boolean existsByUser(@NotNull User user);

    @Query("SELECT a FROM Account a WHERE a.id = :accountId AND (a.balance - :amount) >= 0")
    Optional<Account> findByIdAndSufficientBalance(@NotNull Long accountId, @NotNull BigDecimal amount);

    @Query("SELECT a.user.id FROM Account a WHERE a.id = :accountId")
    Optional<Long> findUserIdByAccountId(@NotNull Long accountId);

}

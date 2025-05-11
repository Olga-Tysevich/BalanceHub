package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    boolean existsByUser(@NotNull User user);

}

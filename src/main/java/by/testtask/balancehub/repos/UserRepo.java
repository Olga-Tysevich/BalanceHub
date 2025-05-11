package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("""
                SELECT u FROM User u
                LEFT JOIN u.emails e
                LEFT JOIN u.phones p
                WHERE e.email = :value OR p.phoneNumber = :value
            """)
    Optional<User> findByEmailOrPhone(@NotBlank @Param("value") String value);

    Optional<User> findByName(@NotBlank String name);

}

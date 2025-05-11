package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.EmailData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepo extends JpaRepository<EmailData, Long> {

    @Query("""
                SELECT COUNT(e) > 0 FROM EmailData e
                WHERE e.email = :email AND e.user.id <> :userId
            """)
    boolean existsByEmailAndUserIdNot(@NotBlank @Param("email") String email, @NotNull @Param("userId") Long userId);

    @Query("""
                SELECT COUNT(e) > 0 FROM EmailData e
                WHERE e.id = :emailId AND e.user.id = :userId
            """)
    boolean existsByIdAndUserId(@NotNull @Param("emailId") Long emailId, @NotNull @Param("userId") Long userId);


}

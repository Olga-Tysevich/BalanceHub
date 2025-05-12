package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.PhoneData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepo extends JpaRepository<PhoneData, Long> {

    @Query("""
                SELECT COUNT(p) > 0 FROM PhoneData p
                WHERE p.phoneNumber = :phone AND p.user.id <> :userId
            """)
    boolean existsByPhoneNumberAndUserIdNot(@NotBlank @Param("phone") String phone, @NotNull @Param("userId") Long userId);

    @Query("""
                SELECT COUNT(p) > 0 FROM PhoneData p
                WHERE p.id = :phoneId AND p.user.id = :userId
            """)
    boolean existsByIdAndUserId(@NotNull @Param("phoneId") Long phoneId, @NotNull @Param("userId") Long userId);

}

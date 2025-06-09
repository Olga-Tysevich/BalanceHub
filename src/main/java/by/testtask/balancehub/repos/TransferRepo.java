package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.Transfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferRepo extends JpaRepository<Transfer, Long> {

    @Query("SELECT t.id FROM Transfer t WHERE t.status = 'PENDING' AND t.createdAt < CURRENT_DATE")
    Page<Long> findTransferIdsWithStatusPendingAndCreatedAToToday(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transfer t WHERE t.id = :id")
    Optional<Transfer> findByIdForUpdate(@Param("id") Long id);

}

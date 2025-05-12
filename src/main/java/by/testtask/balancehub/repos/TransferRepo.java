package by.testtask.balancehub.repos;

import by.testtask.balancehub.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepo extends JpaRepository<Transfer, Long> {

}

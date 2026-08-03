package com.srm.credit.repository;

import com.srm.credit.domain.ReceivableType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivableTypeRepository extends JpaRepository<ReceivableType, Long> {

    Optional<ReceivableType> findByName(String name);
}

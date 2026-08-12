package com.srm.credit.repository;

import com.srm.credit.domain.ReceivableType;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ReceivableTypeRepository extends JpaRepository<ReceivableType, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReceivableType> findByName(String name);
}

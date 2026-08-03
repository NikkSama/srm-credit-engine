package com.srm.credit.repository;

import com.srm.credit.domain.ExchangeRate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("""
            SELECT er FROM ExchangeRate er
            WHERE er.baseCurrency.code = :base
              AND er.quoteCurrency.code = :quote
            ORDER BY er.validAt DESC
            LIMIT 1
            """)
    Optional<ExchangeRate> findLatest(@Param("base") String base, @Param("quote") String quote);
}

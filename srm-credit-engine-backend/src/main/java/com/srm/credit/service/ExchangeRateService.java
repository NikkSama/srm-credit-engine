package com.srm.credit.service;

import com.srm.credit.domain.Currency;
import com.srm.credit.domain.ExchangeRate;
import com.srm.credit.dto.ExchangeRateRequest;
import com.srm.credit.exception.ResourceNotFoundException;
import com.srm.credit.repository.CurrencyRepository;
import com.srm.credit.repository.ExchangeRateRepository;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ExchangeRateService {

    private final ExchangeRateRepository rateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(ExchangeRateRepository rateRepository,
                               CurrencyRepository currencyRepository) {
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public ExchangeRate upsert(ExchangeRateRequest request) {
        log.info("Attempting to upsert exchange rate for pair: {} -> {}",
                request.baseCurrency(), request.quoteCurrency());

        Currency base = resolveCurrency(request.baseCurrency());
        Currency quote = resolveCurrency(request.quoteCurrency());

        ExchangeRate newRate = new ExchangeRate();
        newRate.setBaseCurrency(base);
        newRate.setQuoteCurrency(quote);
        newRate.setRate(request.rate());
        newRate.setValidAt(Instant.now());

        ExchangeRate savedRate = rateRepository.save(newRate);
        log.info("Exchange rate saved successfully. ID: {}, Pair: {} -> {}, Rate: {}",
                savedRate.getId(), request.baseCurrency(), request.quoteCurrency(), savedRate.getRate());

        return savedRate;
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> listAll() {
        log.debug("Listing all exchange rates");
        return rateRepository.findAll();
    }

    //currency validator
    private Currency resolveCurrency(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Currency validation failed. Code not found: {}", code);
                    return new ResourceNotFoundException("Currency not found: " + code);
                });
    }
}
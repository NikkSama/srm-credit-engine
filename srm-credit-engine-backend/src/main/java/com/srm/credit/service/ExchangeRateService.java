package com.srm.credit.service;

import com.srm.credit.domain.Currency;
import com.srm.credit.domain.ExchangeRate;
import com.srm.credit.dto.ExchangeRateRequest;
import com.srm.credit.exception.ResourceNotFoundException;
import com.srm.credit.repository.CurrencyRepository;
import com.srm.credit.repository.ExchangeRateRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Currency base = resolveCurrency(request.baseCurrency());
        Currency quote = resolveCurrency(request.quoteCurrency());

        //TODO: usar mapstruct
        ExchangeRate newRate = new ExchangeRate();
        newRate.setBaseCurrency(base);
        newRate.setQuoteCurrency(quote);
        newRate.setRate(request.rate());
        newRate.setValidAt(Instant.now());
        return rateRepository.save(newRate);
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> listAll() {
        return rateRepository.findAll();
    }

    //valida moeda
    private Currency resolveCurrency(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));
    }
}

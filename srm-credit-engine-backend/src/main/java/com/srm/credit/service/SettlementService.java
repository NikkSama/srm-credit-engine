package com.srm.credit.service;

import com.srm.credit.domain.Currency;
import com.srm.credit.domain.ExchangeRate;
import com.srm.credit.domain.ReceivableType;
import com.srm.credit.domain.Settlement;
import com.srm.credit.dto.SettlementRequest;
import com.srm.credit.dto.SettlementResponse;
import com.srm.credit.exception.BusinessException;
import com.srm.credit.exception.ResourceNotFoundException;
import com.srm.credit.mapper.SettlementMapper;
import com.srm.credit.repository.CurrencyRepository;
import com.srm.credit.repository.ExchangeRateRepository;
import com.srm.credit.repository.ReceivableTypeRepository;
import com.srm.credit.repository.SettlementRepository;
import com.srm.credit.service.pricing.PricingCalculator;
import com.srm.credit.service.pricing.PricingResult;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final PricingCalculator calculator;
    private final CurrencyRepository currencyRepository;
    private final ReceivableTypeRepository receivableTypeRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;

    public SettlementService(PricingCalculator calculator,
                             CurrencyRepository currencyRepository,
                             ReceivableTypeRepository receivableTypeRepository,
                             ExchangeRateRepository exchangeRateRepository,
                             SettlementRepository settlementRepository,
                             SettlementMapper settlementMapper) {
        this.calculator = calculator;
        this.currencyRepository = currencyRepository;
        this.receivableTypeRepository = receivableTypeRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.settlementRepository = settlementRepository;
        this.settlementMapper = settlementMapper;
    }

    @Transactional(readOnly = true)
    public SettlementResponse simulate(SettlementRequest req) {
        return settlementMapper.toResponse(buildSettlement(req));
    }

    @Transactional
    public SettlementResponse create(SettlementRequest req) {
        Settlement saved = settlementRepository.save(buildSettlement(req));
        return settlementMapper.toResponse(saved);
    }

    /** Loads references, prices and assembles a (transient) settlement entity. */
    private Settlement buildSettlement(SettlementRequest request) {
        Context ctx = load(request);
        PricingResult result = calculator.price(
                request.faceValue(), request.termMonths(), request.baseRate(),
                ctx.receivableType().getMonthlySpread(), ctx.rateValue());

        Settlement entity = new Settlement();
        entity.setAssignor(request.assignor());
        entity.setReceivableType(ctx.receivableType());
        entity.setFaceValue(request.faceValue());
        entity.setTermMonths(request.termMonths());
        entity.setBaseRate(request.baseRate());
        entity.setAppliedSpread(result.appliedSpread());
        entity.setOriginalCurrency(ctx.original());
        entity.setPaymentCurrency(ctx.payment());
        entity.setExchangeRate(ctx.rate());
        entity.setPresentValue(result.presentValue());
        entity.setNetValuePaid(result.netValuePaid());
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    //valida a entrada
    private Context load(SettlementRequest request) {
        ReceivableType type = receivableTypeRepository.findByName(request.receivableType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receivable type not found: " + request.receivableType()));

        Currency original = currencyRepository.findByCode(request.originalCurrency())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Currency not found: " + request.originalCurrency()));
        Currency payment = currencyRepository.findByCode(request.paymentCurrency())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Currency not found: " + request.paymentCurrency()));

        ExchangeRate rate = null;
        boolean crossCurrency = !request.originalCurrency().equalsIgnoreCase(request.paymentCurrency());
        if (crossCurrency) {
            rate = exchangeRateRepository.findLatest(request.originalCurrency(), request.paymentCurrency())
                    .orElseThrow(() -> new BusinessException(
                            "No exchange rate available for %s -> %s"
                                    .formatted(request.originalCurrency(), request.paymentCurrency())));
        }
        return new Context(type, original, payment, rate, crossCurrency);
    }

    private record Context(ReceivableType receivableType, Currency original, Currency payment,
                           ExchangeRate rate, boolean crossCurrency) {
        java.math.BigDecimal rateValue() {
            return rate == null ? null : rate.getRate();
        }
    }
}

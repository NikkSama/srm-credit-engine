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
import com.srm.credit.service.pricing.strategy.PricingContext;
import com.srm.credit.service.pricing.strategy.PricingStrategyResolver;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business layer for settlement pricing and persistence.
 * Creation runs in a single ACID transaction so nothing is left half-settled.
 */
@Slf4j
@Service
public class SettlementService {

    private final PricingCalculator calculator;
    private final CurrencyRepository currencyRepository;
    private final ReceivableTypeRepository receivableTypeRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    private final PricingStrategyResolver strategyResolver;

    public SettlementService(PricingCalculator calculator,
                             CurrencyRepository currencyRepository,
                             ReceivableTypeRepository receivableTypeRepository,
                             ExchangeRateRepository exchangeRateRepository,
                             SettlementRepository settlementRepository,
                             SettlementMapper settlementMapper,
                             PricingStrategyResolver strategyResolver) {
        this.calculator = calculator;
        this.currencyRepository = currencyRepository;
        this.receivableTypeRepository = receivableTypeRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.settlementRepository = settlementRepository;
        this.settlementMapper = settlementMapper;
        this.strategyResolver = strategyResolver;
    }

    /** Stateless calculation used by the operator panel (no persistence). */
    @Transactional(readOnly = true)
    public SettlementResponse simulate(SettlementRequest req) {
        log.info("Simulating settlement for receivable type: {}", req.receivableType());
        Settlement transientSettlement = buildSettlement(req);

        log.debug("Simulation completed. Calculated Present Value (PV): {}", transientSettlement.getPresentValue());
        return settlementMapper.toResponse(transientSettlement);
    }

    /** Prices and persists a settlement atomically. */
    @Transactional
    public SettlementResponse create(SettlementRequest req) {
        log.info("Starting settlement creation for assignor: {}, type: {}", req.assignor(), req.receivableType());

        Settlement saved = settlementRepository.save(buildSettlement(req));

        log.info("Settlement created successfully. ID: {}, Net Paid Value: {}", saved.getId(), saved.getNetValuePaid());
        return settlementMapper.toResponse(saved);
    }

    /** Loads references, prices and assembles a (transient) settlement entity. */
    private Settlement buildSettlement(SettlementRequest req) {
        Context ctx = load(req);

        String typeName = ctx.receivableType().getName();
        PricingContext pricingCtx = new PricingContext(
                typeName, req.faceValue(), req.termMonths(),
                req.baseRate(), ctx.receivableType().getMonthlySpread());

        BigDecimal spread = strategyResolver.resolve(typeName).resolveSpread(pricingCtx);
        log.debug("Pricing strategy resolved for type: {}. Applied spread: {}", typeName, spread);

        PricingResult result = calculator.price(
                req.faceValue(), req.termMonths(), req.baseRate(),
                spread, ctx.rateValue());

        log.debug("Pricing calculation result - PV: {}, Net Paid: {}", result.presentValue(), result.netValuePaid());

        Settlement entity = new Settlement();
        entity.setAssignor(req.assignor());
        entity.setReceivableType(ctx.receivableType());
        entity.setFaceValue(req.faceValue());
        entity.setTermMonths(req.termMonths());
        entity.setBaseRate(req.baseRate());
        entity.setAppliedSpread(result.appliedSpread());
        entity.setOriginalCurrency(ctx.original());
        entity.setPaymentCurrency(ctx.payment());
        entity.setExchangeRate(ctx.rate());
        entity.setPresentValue(result.presentValue());
        entity.setNetValuePaid(result.netValuePaid());
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    private Context load(SettlementRequest req) {
        ReceivableType type = receivableTypeRepository.findByName(req.receivableType())
                .orElseThrow(() -> {
                    log.warn("Receivable type not found: {}", req.receivableType());
                    return new ResourceNotFoundException("Receivable type not found: " + req.receivableType());
                });

        Currency original = currencyRepository.findByCode(req.originalCurrency())
                .orElseThrow(() -> {
                    log.warn("Original currency not found: {}", req.originalCurrency());
                    return new ResourceNotFoundException("Currency not found: " + req.originalCurrency());
                });

        Currency payment = currencyRepository.findByCode(req.paymentCurrency())
                .orElseThrow(() -> {
                    log.warn("Payment currency not found: {}", req.paymentCurrency());
                    return new ResourceNotFoundException("Currency not found: " + req.paymentCurrency());
                });

        ExchangeRate rate = null;
        boolean crossCurrency = !req.originalCurrency().equalsIgnoreCase(req.paymentCurrency());
        if (crossCurrency) {
            log.debug("Cross-currency operation detected: {} -> {}", req.originalCurrency(), req.paymentCurrency());
            rate = exchangeRateRepository.findLatest(req.originalCurrency(), req.paymentCurrency())
                    .orElseThrow(() -> {
                        log.error("Missing exchange rate for critical conversion: {} -> {}", req.originalCurrency(), req.paymentCurrency());
                        return new BusinessException("No exchange rate available for %s -> %s".formatted(req.originalCurrency(), req.paymentCurrency()));
                    });
        }
        return new Context(type, original, payment, rate, crossCurrency);
    }

    private record Context(ReceivableType receivableType, Currency original, Currency payment,
                           ExchangeRate rate, boolean crossCurrency) {
        BigDecimal rateValue() {
            return rate == null ? null : rate.getRate();
        }
    }
}
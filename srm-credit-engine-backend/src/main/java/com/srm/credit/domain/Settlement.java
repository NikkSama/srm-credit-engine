package com.srm.credit.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settlement", uniqueConstraints = {
        @UniqueConstraint(name = "uk_settlement_prevent_race", columnNames = {"assignor", "faceValue", "createdAt"})
})
@Getter
@Setter
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String assignor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receivable_type_id")
    private ReceivableType receivableType;

    @Column(name = "face_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal faceValue;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "base_rate", nullable = false, precision = 9, scale = 6)
    private BigDecimal baseRate;

    @Column(name = "applied_spread", nullable = false, precision = 9, scale = 6)
    private BigDecimal appliedSpread;

    @ManyToOne(optional = false)
    @JoinColumn(name = "original_currency_id")
    private Currency originalCurrency;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_currency_id")
    private Currency paymentCurrency;

    @ManyToOne
    @JoinColumn(name = "exchange_rate_id")
    private ExchangeRate exchangeRate;

    @Column(name = "present_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal presentValue;

    @Column(name = "net_value_paid", nullable = false, precision = 18, scale = 2)
    private BigDecimal netValuePaid;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}

package com.srm.credit.report;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SettlementStatementRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public SettlementStatementRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public PageResult<SettlementStatementRow> search(String assignor, String paymentCurrency,
                                                     Instant from, Instant to,
                                                     int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (assignor != null && !assignor.isBlank()) {
            where.append(" AND s.assignor ILIKE :assignor ");
            params.addValue("assignor", "%" + assignor + "%");
        }
        if (paymentCurrency != null && !paymentCurrency.isBlank()) {
            where.append(" AND pc.code = :paymentCurrency ");
            params.addValue("paymentCurrency", paymentCurrency);
        }
        if (from != null) {
            where.append(" AND s.created_at >= :from ");
            params.addValue("from", OffsetDateTime.ofInstant(from, java.time.ZoneOffset.UTC));
        }
        if (to != null) {
            where.append(" AND s.created_at <= :to ");
            params.addValue("to", OffsetDateTime.ofInstant(to, java.time.ZoneOffset.UTC));
        }

        String baseFrom = """
                 FROM settlement s
                 JOIN receivable_type rt ON rt.id = s.receivable_type_id
                 JOIN currency oc ON oc.id = s.original_currency_id
                 JOIN currency pc ON pc.id = s.payment_currency_id
                """;

        Long total = jdbc.queryForObject(
                "SELECT count(*) " + baseFrom + where, params, Long.class);
        long totalElements = total == null ? 0L : total;

        params.addValue("limit", size);
        params.addValue("offset", (long) page * size);

        String dataSql = """
                SELECT s.id, s.assignor, rt.name AS receivable_type,
                       s.face_value, s.present_value, s.net_value_paid,
                       oc.code AS original_currency, pc.code AS payment_currency,
                       s.created_at
                """ + baseFrom + where
                + " ORDER BY s.created_at DESC LIMIT :limit OFFSET :offset ";

        List<SettlementStatementRow> rows = new ArrayList<>(jdbc.query(dataSql, params, (rs, i) ->
                new SettlementStatementRow(
                        rs.getLong("id"),
                        rs.getString("assignor"),
                        rs.getString("receivable_type"),
                        rs.getBigDecimal("face_value"),
                        rs.getBigDecimal("present_value"),
                        rs.getBigDecimal("net_value_paid"),
                        rs.getString("original_currency"),
                        rs.getString("payment_currency"),
                        rs.getTimestamp("created_at").toInstant())));

        return PageResult.of(rows, page, size, totalElements);
    }
}

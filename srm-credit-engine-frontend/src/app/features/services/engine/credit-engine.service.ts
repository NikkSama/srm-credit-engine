import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ReceivableTypeRequest,
  ReceivableTypeResponse,
  ExchangeRateRequest,
  ExchangeRateResponse,
  SettlementRequest,
  SettlementResponse,
  StatementFilterRequest,
  SettlementStatementRow,
  PageResult,
} from '../../models/credit-engine.model';

@Injectable({
  providedIn: 'root',
})
export class CreditEngineService {
  private readonly baseUrl = 'http://localhost:8080/api/v1';
  private readonly http = inject(HttpClient);

  listReceivableTypes(): Observable<ReceivableTypeResponse[]> {
    return this.http.get<ReceivableTypeResponse[]>(`${this.baseUrl}/receivable-types`);
  }

  createReceivableType(request: ReceivableTypeRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/receivable-types`, request);
  }

  listExchangeRates(): Observable<ExchangeRateResponse[]> {
    return this.http.get<ExchangeRateResponse[]>(`${this.baseUrl}/exchange-rates`);
  }

  upsertExchangeRate(request: ExchangeRateRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/exchange-rates`, request);
  }

  simulateSettlement(request: SettlementRequest): Observable<SettlementResponse> {
    return this.http.post<SettlementResponse>(`${this.baseUrl}/settlements/simulate`, request);
  }

  createSettlement(request: SettlementRequest): Observable<SettlementResponse> {
    return this.http.post<SettlementResponse>(`${this.baseUrl}/settlements`, request);
  }

  listSettlementStatements(
    filters: StatementFilterRequest,
    page: number = 0,
    size: number = 10,
  ): Observable<PageResult<SettlementStatementRow>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());

    if (filters.assignor) {
      params = params.set('assignor', filters.assignor);
    }

    if (filters.paymentCurrency) {
      params = params.set('paymentCurrency', filters.paymentCurrency);
    }

    if (filters.from) {
      params = params.set('from', new Date(filters.from).toISOString());
    }

    if (filters.to) {
      params = params.set('to', new Date(filters.to).toISOString());
    }

    return this.http.get<PageResult<SettlementStatementRow>>(
      `${this.baseUrl}/reports/settlement-statement`,
      { params },
    );
  }
}

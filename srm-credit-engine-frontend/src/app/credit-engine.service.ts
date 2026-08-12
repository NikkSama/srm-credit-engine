import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  ReceivableTypeRequest, ReceivableTypeResponse, 
  ExchangeRateRequest, ExchangeRateResponse, 
  SettlementRequest, SettlementResponse, 
  StatementFilterRequest,
  SettlementStatementRow,
  PageResult
} from './credit-engine.model';

@Injectable({
  providedIn: 'root'
})
export class CreditEngineService {
  private baseUrl = 'http://localhost:8080/api/v1'; 

  constructor(private http: HttpClient) {}

  // --- Endpoint de receivable types ---
  listReceivableTypes(): Observable<ReceivableTypeResponse[]> {
    return this.http.get<ReceivableTypeResponse[]>(`${this.baseUrl}/receivable-types`);
  }
  createReceivableType(request: ReceivableTypeRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/receivable-types`, request);
  }

  // --- Endpoints de exchange rate ---
  listExchangeRates(): Observable<ExchangeRateResponse[]> {
    return this.http.get<ExchangeRateResponse[]>(`${this.baseUrl}/exchange-rates`);
  }
  upsertExchangeRate(request: ExchangeRateRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/exchange-rates`, request);
  }

  // --- Endpoints de settlement ---
  simulateSettlement(request: SettlementRequest): Observable<SettlementResponse> {
    return this.http.post<SettlementResponse>(`${this.baseUrl}/settlements/simulate`, request);
  }
  createSettlement(request: SettlementRequest): Observable<SettlementResponse> {
    return this.http.post<SettlementResponse>(`${this.baseUrl}/settlements`, request);
  }
  listSettlementStatements(filters: StatementFilterRequest, page: number = 0, size: number = 10): Observable<PageResult<SettlementStatementRow>> {
  let params: any = { page: page.toString(), size: size.toString() };
  if (filters.assignor) params.assignor = filters.assignor;
  if (filters.paymentCurrency) params.paymentCurrency = filters.paymentCurrency;
  if (filters.from) params.from = new Date(filters.from).toISOString();
  if (filters.to) params.to = new Date(filters.to).toISOString();

  return this.http.get<PageResult<SettlementStatementRow>>(`${this.baseUrl}/reports/settlement-statement`, { params });
}
}
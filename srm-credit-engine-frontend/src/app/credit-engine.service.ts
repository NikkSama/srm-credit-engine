import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  ReceivableTypeRequest, ReceivableTypeResponse, 
  ExchangeRateRequest, ExchangeRateResponse, 
  SettlementRequest, SettlementResponse 
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
}
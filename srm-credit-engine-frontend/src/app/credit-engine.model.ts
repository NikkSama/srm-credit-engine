// --- RECEIVABLE TYPES ---
export interface ReceivableTypeRequest {
  name: string;
}
export interface ReceivableTypeResponse {
  id: number;
  name: string;
  monthlySpread: number;
}

// --- EXCHANGE RATES ---
export interface ExchangeRateRequest {
  baseCurrency: string;
  quoteCurrency: string;
  rate: number;
}
export interface ExchangeRateResponse {
  id: number;
  baseCurrency: { id: number; code: string; name: string };
  quoteCurrency: { id: number; code: string; name: string };
  rate: number;
  validAt: string;
}

// --- SETTLEMENTS ---
export interface SettlementRequest {
  assignor: string;
  receivableType: string;
  faceValue: number;
  termMonths: number;
  baseRate: number;
  originalCurrency: string;
  paymentCurrency: string;
}
export interface SettlementResponse {
  id?: number;
  assignor: string;
  presentValue: number;
  netValuePaid: number;
  appliedSpread: number;
  createdAt: string;
}
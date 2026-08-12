import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditEngineService } from './credit-engine.service';
import { HttpErrorResponse } from '@angular/common/http';
import { 
  ReceivableTypeResponse, ExchangeRateResponse, 
  SettlementRequest, SettlementResponse, 
  SettlementStatementRow,
  StatementFilterRequest
} from './credit-engine.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  activeTab: 'settlement' | 'config' = 'settlement';
  
  successMessage: string | null = null;
  errorMessage: string | null = null;

  receivableTypes: ReceivableTypeResponse[] = [];
  exchangeRates: ExchangeRateResponse[] = [];

  settlementForm: SettlementRequest = {
    assignor: 'SRM Holding', receivableType: 'CHEQUE_PRE_DATADO',
    faceValue: 50000.00, termMonths: 5, baseRate: 0.015,
    originalCurrency: 'BRL', paymentCurrency: 'BRL'
  };
  settlementResult: SettlementResponse | null = null;

  newTypeForm = { monthlySpread: 0, name: '' };
  newRateForm = { baseCurrency: 'USD', quoteCurrency: 'BRL', rate: 5.25 };

  constructor(private engineService: CreditEngineService) {}

  ngOnInit(): void {
    this.reloadConfigurations();
  }

  reloadConfigurations(): void {
    this.engineService.listReceivableTypes().subscribe(res => this.receivableTypes = res);
    this.engineService.listExchangeRates().subscribe(res => this.exchangeRates = res);
  }

  // --- Settlement ---
  onSimulate(): void {
    this.clearMessages();
    this.engineService.simulateSettlement(this.settlementForm).subscribe({
      next: (res) => this.settlementResult = res,
      error: (err) => this.handleHttpError(err)
    });
  }

  onCreateSettlement(): void {
    this.clearMessages();
    this.engineService.createSettlement(this.settlementForm).subscribe({
      next: (res) => {
        this.settlementResult = res;
        this.successMessage = `Settlement processing authorized successfully! ID: ${res.id}`;
      },
      error: (err) => this.handleHttpError(err)
    });
  }

  // --- Config ---
  onCreateType(): void {
    this.clearMessages();
    this.engineService.createReceivableType(this.newTypeForm).subscribe({
      next: () => {
        this.successMessage = `Receivable type '${this.newTypeForm}' registered successfully.`;
        this.newTypeForm.name = '';
        this.newTypeForm.monthlySpread = 0;
        this.reloadConfigurations();
      },
      error: (err) => this.handleHttpError(err)
    });
  }

  onUpsertRate(): void {
    this.clearMessages();
    this.engineService.upsertExchangeRate(this.newRateForm).subscribe({
      next: () => {
        this.successMessage = `Exchange rate for ${this.newRateForm.baseCurrency}/${this.newRateForm.quoteCurrency} updated.`;
        this.reloadConfigurations();
      },
      error: (err) => this.handleHttpError(err)
    });
  }

  reportFilters: StatementFilterRequest = {
    assignor: '',
    paymentCurrency: '',
    from: '',
    to: ''
  };

  reportRows: SettlementStatementRow[] = [];

  onReport(): void {
    this.clearMessages();
    
    this.engineService.listSettlementStatements(this.reportFilters).subscribe({
      next: (res) => {
        this.reportRows = res.content;
        this.successMessage = `Settlement statements retrieved successfully. Count: ${res.content.length}`;
      },
      error: (err) => this.handleHttpError(err)
    });
  }

  private clearMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
    this.settlementResult = null;
  }

  private handleHttpError(err: any): void {
    console.error('API Error Intercepted:', err);
    if (err instanceof HttpErrorResponse) {
      if (err.error) {
        if (err.error.detail) {
          this.errorMessage = err.error.detail;
          return;
        }
        
        if (typeof err.error === 'string') {
          this.errorMessage = err.error;
          return;
        }
      }
      
      if (err.status === 0) {
        this.errorMessage = 'Backend server is unreachable. Please ensure Spring Boot is running on port 8080 and CORS is enabled.';
        return;
      }
    }
    this.errorMessage = err.message || 'An unexpected communication error occurred.';
  }
}
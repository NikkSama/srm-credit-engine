import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditEngineService } from './credit-engine.service';
import { 
  ReceivableTypeResponse, ExchangeRateResponse, 
  SettlementRequest, SettlementResponse 
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

  newTypeForm = { name: '' };
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
        this.successMessage = `Receivable type '${this.newTypeForm.name}' registered successfully.`;
        this.newTypeForm.name = '';
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

  private clearMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
    this.settlementResult = null;
  }

  private handleHttpError(err: any): void {
    if (err.error && err.error.detail) {
      this.errorMessage = err.error.detail; 
    } else {
      this.errorMessage = 'Connection failure or unexpected internal error.';
    }
  }
}
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ExchangeRateResponse, ReceivableTypeResponse, SettlementRequest, SettlementResponse, SettlementStatementRow, StatementFilterRequest } from '../models/credit-engine.model';
import { CreditEngineService } from '../services/engine/credit-engine.service';
import { Settlement } from "./components/settlement/settlement";
import { Report } from "./components/report/report";
import { Result } from "./components/result/result";
import { Config } from "./components/config/config";
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    Settlement,
    Report,
    Result,
    Config
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard implements OnInit {
  private readonly engineService = inject(CreditEngineService);
  private readonly cdr = inject(ChangeDetectorRef);

  public activeTab: 'settlement' | 'config' = 'settlement';

  public successMessage: string | null = null;
  public errorMessage: string | null = null;

  public receivableTypes: ReceivableTypeResponse[] = [];
  public exchangeRates: ExchangeRateResponse[] = [];

  public settlementForm: SettlementRequest = {
    assignor: 'SRM Holding',
    receivableType: 'CHEQUE_PRE_DATADO',
    faceValue: 50000.0,
    termMonths: 5,
    baseRate: 0.015,
    originalCurrency: 'BRL',
    paymentCurrency: 'BRL',
  };
  public settlementResult: SettlementResponse | null = null;

  public newTypeForm = { monthlySpread: 1.5, name: '' };
  public newRateForm = { baseCurrency: 'USD', quoteCurrency: 'BRL', rate: 5.25 };

  public reportFilters: StatementFilterRequest = {
    assignor: '',
    paymentCurrency: '',
    from: '',
    to: '',
  };

  public reportRows: SettlementStatementRow[] = [];
  public readonly reportColumns = ['id', 'assignor', 'faceValue', 'netValuePaid', 'paymentCurrency', 'createdAt'];

  ngOnInit(): void {
    this.reloadConfigurations();
  }

  public reloadConfigurations(): void {
    this.engineService.listReceivableTypes().subscribe({
      next: (res) => {
        this.receivableTypes = res;
        this.cdr.markForCheck();
      },
    });

    this.engineService.listExchangeRates().subscribe({
      next: (res) => {
        this.exchangeRates = res;
        this.cdr.markForCheck();
      },
    });
  }

  public onSimulate(): void {
    this.clearMessages();
    this.engineService.simulateSettlement(this.settlementForm).subscribe({
      next: (res) => {
        this.settlementResult = res;
        this.cdr.markForCheck();
      },
      error: (err) => this.handleHttpError(err),
    });
  }

  public onCreateSettlement(): void {
    this.clearMessages();
    this.engineService.createSettlement(this.settlementForm).subscribe({
      next: (res) => {
        this.settlementResult = res;
        this.successMessage = `Settlement processing authorized successfully! ID: ${res.id}`;
        this.cdr.markForCheck();
      },
      error: (err) => this.handleHttpError(err),
    });
  }

  public onCreateType(): void {
    this.clearMessages();
    this.engineService.createReceivableType(this.newTypeForm).subscribe({
      next: () => {
        this.successMessage = `Receivable type '${this.newTypeForm.name}' registered successfully.`;
        this.newTypeForm.name = '';
        this.newTypeForm.monthlySpread = 0;
        this.reloadConfigurations();
        this.cdr.markForCheck();
      },
      error: (err) => this.handleHttpError(err),
    });
  }

  public onUpsertRate(): void {
    this.clearMessages();
    this.engineService.upsertExchangeRate(this.newRateForm).subscribe({
      next: () => {
        this.successMessage = `Exchange rate for ${this.newRateForm.baseCurrency}/${this.newRateForm.quoteCurrency} updated.`;
        this.reloadConfigurations();
        this.cdr.markForCheck();
      },
      error: (err) => this.handleHttpError(err),
    });
  }

  public onReport(modifiedFilters?: StatementFilterRequest): void {
    this.clearMessages();

    const send = modifiedFilters ? modifiedFilters : this.reportFilters;

    this.engineService.listSettlementStatements(send).subscribe({
      next: (res) => {
        this.reportRows = res.content;
        this.successMessage = `Settlement statements retrieved successfully. Count: ${res.content.length}`;
        this.cdr.markForCheck();
      },
      error: (err) => this.handleHttpError(err),
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
      if (err.error && err.error.detail) {
        this.errorMessage = err.error.detail;
      } else if (typeof err.error === 'string') {
        this.errorMessage = err.error;
      } else if (err.status === 0) {
        this.errorMessage = 'Backend server is unreachable. Please check your connection or CORS settings.';
      } else {
        this.errorMessage = `Error ${err.status}: ${err.statusText}`;
      }
    } else {
      this.errorMessage = err.message || 'An unexpected error occurred.';
    }
    this.cdr.markForCheck();
  }
}

import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ExchangeRateResponse, ReceivableTypeResponse } from '../../../models/credit-engine.model';
@Component({
  selector: 'app-dashboard-config',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './config.html',
  styleUrl: './config.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Config {
  public typeForm = input<{ monthlySpread: number; name: string }>();
  public rateForm = input<{ baseCurrency: string; quoteCurrency: string; rate: number }>();
  public receivableTypes = input<ReceivableTypeResponse[]>();
  public exchangeRates = input<ExchangeRateResponse[]>();
  public createType = output<void>();
  public upsertRate = output<void>();
}

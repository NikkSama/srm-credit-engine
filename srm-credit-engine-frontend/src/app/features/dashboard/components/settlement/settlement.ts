import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  input,
  Input,
  output,
  Output,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { ReceivableTypeResponse, SettlementRequest } from '../../../models/credit-engine.model';

@Component({
  selector: 'app-dashboard-settlement',
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
  templateUrl: './settlement.html',
  styleUrls: ['./settlement.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Settlement {
  public settlementForm = input<SettlementRequest>();
  public receivableTypes = input<ReceivableTypeResponse[]>();
  public simulate = output<void>();
  public create = output<void>();
}

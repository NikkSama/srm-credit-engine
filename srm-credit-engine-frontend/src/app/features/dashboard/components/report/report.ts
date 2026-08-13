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
import { MatTableModule } from '@angular/material/table';
import { SettlementStatementRow, StatementFilterRequest } from '../../../models/credit-engine.model';

@Component({
  selector: 'app-dashboard-report',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
  ],
  templateUrl: './report.html',
  styleUrls: ['./report.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Report {
  public reportFilters = input<StatementFilterRequest>();
  public reportRows = input<SettlementStatementRow[]>([]);
  public columns = input<string[]>([]);
  public report = output<StatementFilterRequest | undefined>();

  public submitReport(): void {
    const filters = this.reportFilters();
    
    if (filters) {
      const payload: StatementFilterRequest = { ...filters };

      if (payload.from && payload.from.length === 10) {
        payload.from = `${payload.from}T00:00:00.000Z`;
      }
      if (payload.to && payload.to.length === 10) {
        payload.to = `${payload.to}T23:59:59.999Z`;
      }

      this.report.emit(payload);
      return;
    }

    this.report.emit(undefined);
  }
}

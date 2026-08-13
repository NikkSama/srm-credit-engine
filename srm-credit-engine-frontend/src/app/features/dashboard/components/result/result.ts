import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { SettlementResponse } from '../../../models/credit-engine.model';
@Component({
  selector: 'app-dashboard-result',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatDividerModule],
  template: `
    <mat-card class="panel">
      <mat-card-header>
        <mat-card-title>Calculation Engine Metrics</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        @if (settlementResult()) {
          <div class="results-display">
            <div class="metric">
              <span class="label">Applied Spread:</span>
              <span class="val">{{ settlementResult()!.appliedSpread }}</span>
            </div>
            <div class="metric">
              <span class="label">Present Value:</span>
              <span class="valHighlight">{{
                settlementResult()!.presentValue | number: '1.2-2'
              }}</span>
            </div>
            <div class="metric border-top">
              <span class="label">Net Disbursed Amount:</span>
              <span class="valFinal">{{ settlementResult()!.netValuePaid | number: '1.2-2' }}</span>
            </div>
          </div>
        } @else {
          <p class="placeholder-text">
            Fill out the request and click simulate or execute to invoke pricing rules.
          </p>
        }
      </mat-card-content>
    </mat-card>
  `,
  styleUrl: './result.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Result {
  public settlementResult = input<SettlementResponse | null>(null);
}

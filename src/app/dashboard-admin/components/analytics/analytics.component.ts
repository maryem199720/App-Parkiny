// analytics.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { AdminService } from 'src/app/services/admin.service';
import { ToastrService } from 'ngx-toastr';
import pdfMake from 'pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Notification } from 'src/app/types/types';

interface AnalyticsData {
  totalVehicles: number;
  occupancyRate: number;
  dailyRevenue: number;
  averageParkingTime: number;
}

pdfMake.vfs = pdfFonts;

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {
  analyticsData: AnalyticsData | null = null;
  period: 'day' | 'week' | 'month' = 'week';
  chartData: ChartData<'bar' | 'line'> = { labels: [], datasets: [] };
  chartType: ChartType = 'bar';
  pieChartData: ChartData<'pie'> = { labels: [], datasets: [] };
  notifications: Notification[] = [];
  adminName: string = 'admin@gmail.com';
  errorMessage: string = ''; // Added for user feedback

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false },
      title: { display: true, text: '', color: '#1E0D2B' },
      tooltip: {
        backgroundColor: '#6A1B9A',
        titleFont: { size: 14 },
        bodyFont: { size: 12 },
        titleColor: '#D4AF37',
        bodyColor: '#D4AF37'
      }
    },
    scales: {
      y: { beginAtZero: true, ticks: { color: '#6A1B9A' }, grid: { color: '#E5E7EB' } },
      x: { ticks: { color: '#6A1B9A' }, grid: { display: false } }
    }
  };

  pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { position: 'top', labels: { color: '#6A1B9A' } },
      title: { display: true, text: 'Occupation du Parking', color: '#1E0D2B' }
    }
  };

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAnalyticsData();
    this.loadChartData();
    this.loadNotifications();
  }

  changePeriod(period: 'day' | 'week' | 'month'): void {
    this.period = period;
    this.loadChartData();
  }

  loadAnalyticsData(): void {
    this.adminService.getAnalyticsData().subscribe({
      next: (data: any) => {
        this.analyticsData = {
          totalVehicles: data?.totalVehicles ?? 0,
          occupancyRate: data?.occupancyRate ?? 0,
          dailyRevenue: data?.dailyRevenue ?? 0,
          averageParkingTime: data?.averageParkingTime ?? 0
        };
        this.errorMessage = '';
      },
      error: (error) => {
        console.error('Erreur lors du chargement des données analytiques:', error);
        if (error.status === 401) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
        } else if (error.status === 404) {
          this.errorMessage = 'Données analytiques non disponibles.';
        } else {
          this.errorMessage = 'Erreur lors du chargement des données.';
        }
        this.analyticsData = {
          totalVehicles: 0,
          occupancyRate: 0,
          dailyRevenue: 0,
          averageParkingTime: 0
        };
      }
    });
  }

  loadChartData(): void {
    this.adminService.getChartData(this.period).subscribe({
      next: (chartData) => {
        if (this.period === 'day') {
          this.chartType = 'line';
          this.chartData = {
            labels: chartData.occupancyTrend.map((item: any) => item.hour),
            datasets: [
              {
                data: chartData.occupancyTrend.map((item: any) => item.rate),
                label: "Taux d'Occupation (%)",
                borderColor: '#6A1B9A',
                backgroundColor: 'rgba(106, 27, 154, 0.2)',
                fill: true,
                tension: 0.4
              }
            ]
          };
        } else {
          this.chartType = 'bar';
          this.chartData = {
            labels: chartData.dailyRevenue.map((item: any) => item.day),
            datasets: [
              {
                data: chartData.dailyRevenue.map((item: any) => item.revenue),
                label: 'Revenus (TND)',
                backgroundColor: '#6A1B9A',
                borderColor: '#1E0D2B',
                borderWidth: 1
              }
            ]
          };
        }
        this.pieChartData = {
          labels: ['Occupé', 'Libre'],
          datasets: [
            {
              data: [chartData.occupancy.occupied, chartData.occupancy.free],
              backgroundColor: ['#6A1B9A', '#D4AF37'],
              borderColor: '#1E0D2B',
              borderWidth: 1
            }
          ]
        };
        this.errorMessage = '';
      },
      error: (error) => {
        console.error('Erreur lors du chargement des données de graphique:', error);
        if (error.status === 401) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
        } else if (error.status === 404) {
          this.errorMessage = 'Données de graphique non disponibles.';
        } else {
          this.errorMessage = 'Erreur lors du chargement des graphiques.';
        }
      }
    });
  }

  loadNotifications(): void {
    this.adminService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.errorMessage = '';
      },
      error: (error) => {
        console.error('Erreur lors du chargement des notifications:', error);
        if (error.status === 401) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
        } else if (error.status === 404) {
          this.errorMessage = 'Notifications non disponibles.';
        } else {
          this.errorMessage = 'Erreur lors du chargement des notifications.';
        }
      }
    });
  }

  exportReport(): void {
    if (!this.analyticsData) {
      console.error('Les données analytiques ne sont pas disponibles.');
      this.errorMessage = 'Aucune donnée à exporter.';
      return;
    }

    const data: AnalyticsData = this.analyticsData;
    const recentNotifications = this.notifications.slice(0, 5);
    const periodLabel = this.period === 'day' ? 'Jour' : this.period === 'week' ? 'Semaine' : 'Mois';

    const documentDefinition = {
      content: [
        { text: 'Rapport Analytique - Smart Park', style: 'header' },
        { text: `Généré par: ${this.adminName}`, style: 'subheader' },
        { text: `Date: ${new Date().toLocaleDateString('fr-FR')}`, style: 'subheader' },
        { text: `Période: ${periodLabel}`, style: 'subheader' },
        { text: '\nIndicateurs Clés\n', style: 'sectionHeader' },
        {
          table: {
            widths: ['*', '*'],
            body: [
              ['Métrique', 'Valeur'],
              ['Véhicules Aujourd\'hui', (data.totalVehicles ?? 0).toString()],
              ['Taux d\'Occupation (%)', (data.occupancyRate ?? 0).toString()],
              ['Revenu Quotidien', (data.dailyRevenue ?? 0).toString()],
              ['Temps Moyen Stationnement (h)', (data.averageParkingTime ?? 0).toString()]
            ]
          }
        },
        { text: `\nDonnées ${this.period === 'day' ? 'Tendance Occupation' : 'Revenus'}\n`, style: 'sectionHeader' },
        {
          table: {
            widths: ['*', '*'],
            body: [
              [this.period === 'day' ? 'Heure' : 'Jour', this.period === 'day' ? 'Taux d\'Occupation (%)' : 'Revenus (TND)'],
              ...(this.chartData.labels?.map((label: any, index: number) => [
                label,
                this.chartData.datasets[0]?.data[index]?.toString() ?? 'N/A'
              ]) ?? [])
            ]
          }
        },
        { text: '\nOccupation du Parking\n', style: 'sectionHeader' },
        {
          table: {
            widths: ['*', '*'],
            body: [
              ['Statut', 'Nombre de Places'],
              ['Occupé', this.pieChartData.datasets[0]?.data[0]?.toString() ?? 'N/A'],
              ['Libre', this.pieChartData.datasets[0]?.data[1]?.toString() ?? 'N/A']
            ]
          }
        },
        { text: '\nDernières Notifications\n', style: 'sectionHeader' },
        {
          ul: recentNotifications.map(n => ({
            text: `${n.message} - ${new Date(n.timestamp).toLocaleDateString('fr-FR')} ${n.is_read ? '(Lue)' : '(Non lue)'}`,
            margin: [0, 0, 0, 5]
          }))
        },
        { text: '\nAnalyse Sommaire\n', style: 'sectionHeader' },
        {
          text: `Le taux d'occupation est de ${data.occupancyRate ?? 0}%, ce qui indique une utilisation ${(data.occupancyRate ?? 0) > 80 ? 'élevée' : 'modérée'}. Le revenu quotidien de ${data.dailyRevenue ?? 0} TND est stable.`,
          style: 'analysis'
        }
      ],
      styles: {
        header: { fontSize: 22, bold: true, alignment: 'center', margin: [0, 0, 0, 10], color: '#6A1B9A' },
        subheader: { fontSize: 14, alignment: 'center', margin: [0, 0, 0, 10], color: '#1E0D2B' },
        sectionHeader: { fontSize: 16, bold: true, margin: [0, 10, 0, 5], color: '#6A1B9A' },
        analysis: { fontSize: 12, italics: true, margin: [0, 0, 0, 10], color: '#D4AF37' }
      }
    };

    pdfMake.createPdf(documentDefinition).download('rapport_analyse.pdf');
  }
}
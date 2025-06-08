import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AdminService } from 'src/app/services/admin.service';
import { ParkingSettings, SubscriptionOffer } from 'src/app/types/types';
import { HttpErrorResponse } from '@angular/common/http';

interface OfferChange {
  action: 'added' | 'updated' | 'deleted';
  old?: SubscriptionOffer;
  new?: SubscriptionOffer;
}

@Component({
  selector: 'app-parking-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './parking-settings.component.html',
  styleUrls: ['./parking-settings.component.css']
})
export class ParkingSettingsComponent implements OnInit {
  settingsForm: FormGroup;
  settingsHistory: { timestamp: string; changes: { maxSlots?: { old: number; new: number }; reservedPremiumSlots?: { old: number; new: number }; subscriptionOffers?: OfferChange[] } }[] = [];
  offerForm: FormGroup;
  selectedOfferIndex: number | null = null;
  isLoading: boolean = true;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.settingsForm = this.fb.group({
      id: [null],
      maxSlots: [0, [Validators.required, Validators.min(1)]],
      reservedPremiumSlots: [0, [Validators.required, Validators.min(0)]],
      subscriptionOffers: this.fb.array([])
    });
    this.offerForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      duration: [0, [Validators.required, Validators.min(1)]],
      active: [true],
      subscribers: [0]
    });
  }

  ngOnInit(): void {
    this.loadSettings();
    this.loadHistory();
  }

  get subscriptionOffers(): FormArray {
    return this.settingsForm.get('subscriptionOffers') as FormArray;
  }

  loadSettings(): void {
    console.log('Tentative de chargement des paramètres...');
    this.isLoading = true;
    this.settingsForm.disable();
    this.adminService.getParkingSettings().subscribe({
      next: (settings: ParkingSettings) => {
        console.log('Paramètres reçus:', settings);
        this.settingsForm.patchValue({
          id: settings.id,
          maxSlots: settings.maxSlots,
          reservedPremiumSlots: settings.reservedPremiumSlots
        });
        while (this.subscriptionOffers.length) {
          this.subscriptionOffers.removeAt(0);
        }
        if (settings.subscriptionOffers && settings.subscriptionOffers.length > 0) {
          settings.subscriptionOffers.forEach((offer: SubscriptionOffer) => {
            this.subscriptionOffers.push(this.fb.group({
              id: [offer.id],
              name: [offer.name, Validators.required],
              price: [offer.price, [Validators.required, Validators.min(0)]],
              duration: [offer.duration, [Validators.required, Validators.min(1)]],
              active: [offer.active],
              subscribers: [offer.subscribers]
            }));
          });
          console.log('Offres chargées:', this.subscriptionOffers.value);
        } else {
          console.log('Aucune offre trouvée dans la réponse.');
        }
        this.toastr.success('Paramètres chargés.', 'Succès');
        this.isLoading = false;
        this.settingsForm.enable();
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des paramètres:', err);
        this.toastr.error('Erreur chargement paramètres.', 'Erreur');
        this.isLoading = false;
        this.settingsForm.enable();
      }
    });
  }

  loadHistory(): void {
    this.adminService.getSettingsHistory().subscribe({
      next: (history: { timestamp: string; changes: any }[]) => {
        console.log('Données brutes de l\'historique:', history);
        this.settingsHistory = history
          .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())
          .map(entry => ({
            ...entry,
            changes: this.normalizeChanges(entry.changes)
          }));
        console.log('Historique normalisé:', this.settingsHistory);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur lors du chargement de l\'historique:', err);
        this.toastr.error('Erreur chargement historique.', 'Erreur');
      }
    });
  }

  normalizeChanges(changes: any): { maxSlots?: { old: number; new: number }; reservedPremiumSlots?: { old: number; new: number }; subscriptionOffers?: OfferChange[] } {
    const normalized: { maxSlots?: { old: number; new: number }; reservedPremiumSlots?: { old: number; new: number }; subscriptionOffers?: OfferChange[] } = {};

    // Handle maxSlots
    if (changes?.maxSlots && 'old' in changes.maxSlots && 'new' in changes.maxSlots) {
      normalized.maxSlots = { old: changes.maxSlots.old, new: changes.maxSlots.new };
    }

    // Handle reservedPremiumSlots
    if (changes?.reservedPremiumSlots && 'old' in changes.reservedPremiumSlots && 'new' in changes.reservedPremiumSlots) {
      normalized.reservedPremiumSlots = { old: changes.reservedPremiumSlots.old, new: changes.reservedPremiumSlots.new };
    }

    // Handle subscriptionOffers
    if (changes?.subscriptionOffers && Array.isArray(changes.subscriptionOffers)) {
      normalized.subscriptionOffers = changes.subscriptionOffers.map((offerChange: any) => ({
        action: offerChange.action,
        old: offerChange.old ? { ...offerChange.old } : undefined,
        new: offerChange.new ? { ...offerChange.new } : undefined
      }));
    }

    return normalized;
  }

  saveSettings(): void {
    if (this.settingsForm.valid) {
      const settingsData: ParkingSettings = this.settingsForm.value;
      console.log('Données à sauvegarder:', settingsData);
      this.adminService.saveParkingSettings(settingsData).subscribe({
        next: (savedSettings: ParkingSettings) => {
          this.toastr.success('Paramètres sauvegardés.', 'Succès');
          this.loadSettings();
          this.loadHistory();
        },
        error: (err: HttpErrorResponse) => {
          console.error('Erreur lors de la sauvegarde:', err);
          this.toastr.error('Erreur sauvegarde.', 'Erreur');
        }
      });
    } else {
      this.toastr.error('Formulaire invalide.', 'Erreur');
      console.log('Formulaire invalide, erreurs:', this.settingsForm.errors);
    }
  }

  addOffer(): void {
    if (this.offerForm.valid) {
      const newOffer: SubscriptionOffer = this.offerForm.value;
      console.log('Nouvelle offre à ajouter:', newOffer);
      this.subscriptionOffers.push(this.fb.group({
        id: [newOffer.id],
        name: [newOffer.name, Validators.required],
        price: [newOffer.price, [Validators.required, Validators.min(0)]],
        duration: [newOffer.duration, [Validators.required, Validators.min(1)]],
        active: [newOffer.active],
        subscribers: [newOffer.subscribers]
      }));
      this.offerForm.reset({
        id: null,
        name: '',
        price: 0,
        duration: 0,
        active: true,
        subscribers: 0
      });
      this.selectedOfferIndex = null;
      this.saveSettings();
    } else {
      this.toastr.error('Formulaire d\'offre invalide.', 'Erreur');
      console.log('Formulaire d\'offre invalide, erreurs:', this.offerForm.errors);
    }
  }

  editOffer(index: number): void {
    this.selectedOfferIndex = index;
    const offer = this.subscriptionOffers.at(index);
    this.offerForm.patchValue({
      id: offer.get('id')?.value,
      name: offer.get('name')?.value,
      price: offer.get('price')?.value,
      duration: offer.get('duration')?.value,
      active: offer.get('active')?.value,
      subscribers: offer.get('subscribers')?.value
    });
  }

  updateOffer(): void {
    if (this.offerForm.valid && this.selectedOfferIndex !== null) {
      const updatedOffer: SubscriptionOffer = this.offerForm.value;
      console.log('Offre à mettre à jour:', updatedOffer, 'à l\'index:', this.selectedOfferIndex);
      this.subscriptionOffers.at(this.selectedOfferIndex).patchValue(updatedOffer);
      this.offerForm.reset({
        id: null,
        name: '',
        price: 0,
        duration: 0,
        active: true,
        subscribers: 0
      });
      this.selectedOfferIndex = null;
      this.saveSettings();
    } else {
      this.toastr.error('Formulaire invalide ou aucune offre sélectionnée.', 'Erreur');
      console.log('Formulaire d\'offre invalide ou index null, erreurs:', this.offerForm.errors);
    }
  }

  deleteOffer(index: number): void {
    if (confirm('Supprimer cette offre ?')) {
      console.log('Suppression de l\'offre à l\'index:', index);
      this.subscriptionOffers.removeAt(index);
      this.saveSettings();
    }
  }

  getError(controlName: string, requiredMsg: string, invalidMsg: string): string {
    const control = this.settingsForm.get(controlName);
    if (control?.errors?.['required']) return requiredMsg;
    if (control?.errors?.['min']) return invalidMsg;
    return '';
  }

  get maxSlotsError(): string {
    return this.getError('maxSlots', 'Requis.', '> 0');
  }

  get reservedPremiumSlotsError(): string {
    return this.getError('reservedPremiumSlots', 'Requis.', '≥ 0');
  }

  getOfferError(controlName: string, requiredMsg: string, invalidMsg: string): string {
    const control = this.offerForm.get(controlName);
    if (control?.errors?.['required']) return requiredMsg;
    if (control?.errors?.['min']) return invalidMsg;
    return '';
  }

  get offerNameError(): string {
    return this.getOfferError('name', 'Requis.', 'Valeur invalide');
  }

  get offerPriceError(): string {
    return this.getOfferError('price', 'Requis.', '≥ 0 TND');
  }

  get offerDurationError(): string {
    return this.getOfferError('duration', 'Requis.', '> 0');
  }
}
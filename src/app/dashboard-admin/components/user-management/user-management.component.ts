import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { AdminService } from 'src/app/services/admin.service';
import { User, Reservation } from 'src/app/types/types';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule ],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  userForm: FormGroup;
  selectedUser: User | null = null;
  showUserModal = false;
  showReservationsModal = false;
  selectedUserReservations: Reservation[] = [];
  reservationFilter: 'all' | 'confirmed' | 'cancelled' | 'completed' | 'expired' = 'all';
  reservationDateFilter: string = '';
  isEditMode = false;
  isLoading: boolean | undefined;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.userForm = this.fb.group({
      id: [null],
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^(\+\d{1,3}[- ]?)?\d{8,15}$/)]],
      password: ['', [Validators.minLength(6), Validators.maxLength(120)]],
      active: [true],
    });
    console.log('userForm initialized:', this.userForm);
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.adminService.getUsers().subscribe({
      next: (users: User[]) => {
        this.users = users;
        this.toastr.success('Utilisateurs chargés', 'Succès');
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading users:', error);
        if (error.status === 401) {
          this.toastr.error('Session expirée, veuillez vous reconnecter.', 'Erreur');
        } else if (error.status === 404) {
          this.toastr.error('Endpoint utilisateurs introuvable.', 'Erreur');
        } else {
          this.toastr.error('Erreur chargement utilisateurs', 'Erreur');
        }
        this.isLoading = false;
      }
    });
  }

  testToastr(): void {
    this.toastr.success('Test notification', 'Succès');
  }
  openUserModal(user?: User): void {
    this.selectedUser = user || null;
    this.isEditMode = !!user;
    if (user) {
      this.userForm.patchValue({
        id: user.id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        password: '',
        active: user.active,
      });
    } else {
      this.userForm.reset();
      this.userForm.patchValue({ active: true });
    }
    this.showUserModal = true;
  }

  closeUserModal(): void {
    this.showUserModal = false;
    this.isEditMode = false;
    this.selectedUser = null;
    this.userForm.reset();
    this.userForm.patchValue({ active: true });
  }

  saveUser(): void {
    if (this.userForm.valid) {
      if (this.isEditMode && this.selectedUser && this.selectedUser.id) {
        const updateData: Partial<User> = {
          email: this.userForm.value.email,
          phone: this.userForm.value.phone
        };
        console.log('Sending update request for user ID', this.selectedUser.id, 'with updateData:', updateData);
        this.adminService.updateUser(this.selectedUser.id, updateData).subscribe({
          next: (updatedUser: User) => {
            this.toastr.success(`Utilisateur ${updatedUser.firstName} ${updatedUser.lastName} modifié avec succès !`, 'Succès');
            this.loadUsers();
            this.closeUserModal();
          },
          error: (err) => {
            console.error('Update user error:', err);
            const errorMessage = err.error?.error || err.error?.message || 'Erreur lors de la mise à jour de l’utilisateur.';
            this.toastr.error(errorMessage, 'Erreur');
          }
        });
      } else {
        if (!this.userForm.value.password) {
          this.toastr.error('Le mot de passe est requis pour un nouvel utilisateur.', 'Erreur');
          return;
        }
        const userData: User = {
          id: 0,
          firstName: this.userForm.value.firstName,
          lastName: this.userForm.value.lastName,
          email: this.userForm.value.email,
          phone: this.userForm.value.phone,
          password: this.userForm.value.password,
          role: 'ROLE_USER',
          active: this.userForm.value.active,
          reservations: []
        };
        this.adminService.createUser(userData).subscribe({
          next: (newUser: User) => {
            this.toastr.success(`Utilisateur ${newUser.firstName} ${newUser.lastName} ajouté avec succès !`, 'Succès');
            this.loadUsers();
            this.closeUserModal();
          },
          error: (err) => {
            console.error('Create user error:', err);
            const errorMessage = err.error?.error || err.error?.message || 'Erreur lors de la création de l’utilisateur.';
            this.toastr.error(errorMessage, 'Erreur');
          }
        });
      }
    } else {
      this.toastr.error('Veuillez corriger les erreurs dans le formulaire.', 'Erreur');
    }
  }

  deleteUser(userId: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet utilisateur ?')) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => {
          this.toastr.success('Utilisateur supprimé avec succès !', 'Succès');
          this.loadUsers();
        },
        error: (err) => {
          console.error('Delete user error:', err);
          this.toastr.error('Erreur lors de la suppression de l’utilisateur.', 'Erreur');
        }
      });
    }
  }

  openReservationsModal(user: User): void {
    this.selectedUser = user;
    this.adminService.getUserReservations(user.id).subscribe({
      next: (reservations: Reservation[]) => {
        this.selectedUserReservations = reservations;
        this.showReservationsModal = true;
        this.toastr.success(`Historique des réservations de ${user.firstName} ${user.lastName} chargé !`, 'Succès');
      },
      error: (err) => {
        console.error('Load reservations error:', err);
        this.toastr.error('Erreur lors du chargement de l’historique des réservations.', 'Erreur');
      }
    });
  }

  closeReservationsModal(): void {
    this.showReservationsModal = false;
    this.selectedUserReservations = [];
    this.reservationFilter = 'all';
    this.reservationDateFilter = '';
  }

  filterReservations(): Reservation[] {
    let filtered = this.selectedUserReservations;
    if (this.reservationFilter !== 'all') {
      filtered = filtered.filter(r => r.status === this.reservationFilter);
    }
    if (this.reservationDateFilter) {
      filtered = filtered.filter(r => r.startTime.includes(this.reservationDateFilter));
    }
    return filtered;
  }

  get firstNameError(): string {
    const control = this.userForm.get('firstName');
    if (control?.errors?.['required']) return 'Le prénom est requis.';
    if (control?.errors?.['minlength']) return 'Minimum 2 caractères.';
    if (control?.errors?.['maxlength']) return 'Maximum 50 caractères.';
    return '';
  }

  get lastNameError(): string {
    const control = this.userForm.get('lastName');
    if (control?.errors?.['required']) return 'Le nom de famille est requis.';
    if (control?.errors?.['minlength']) return 'Minimum 2 caractères.';
    if (control?.errors?.['maxlength']) return 'Maximum 50 caractères.';
    return '';
  }

  get emailError(): string {
    const control = this.userForm.get('email');
    if (control?.errors?.['required']) return 'L’email est requis.';
    if (control?.errors?.['email']) return 'Email invalide.';
    return '';
  }

  get phoneError(): string {
    const control = this.userForm.get('phone');
    if (control?.errors?.['required']) return 'Le numéro de téléphone est requis.';
    if (control?.errors?.['pattern']) return 'Format invalide (ex: +21612345678).';
    return '';
  }

  get passwordError(): string {
    const control = this.userForm.get('password');
    if (control?.errors?.['required']) return 'Le mot de passe est requis.';
    if (control?.errors?.['minlength']) return 'Minimum 6 caractères.';
    if (control?.errors?.['maxlength']) return 'Maximum 120 caractères.';
    return '';
  }
}
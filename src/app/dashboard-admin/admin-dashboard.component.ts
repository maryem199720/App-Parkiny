import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from 'src/app/services/admin.service';
import { Notification, AdminProfile, PasswordUpdate } from '../types/types';
import { animate, style, transition, trigger } from '@angular/animations';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class AdminDashboardComponent implements OnInit {
  sidebarOpen = true;
  showNotifications = false;
  notifications: Notification[] = [];
  unreadCount = 0;
  showProfile = false;
  isEditingProfile = false;
  adminProfile: AdminProfile = { email: 'admin@gmail.com' };
  editedProfile: AdminProfile = { ...this.adminProfile };
  currentPassword = '';
  newPassword = '';
  errorMessage = '';

  constructor(
    private adminService: AdminService, 
    private toastr: ToastrService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.loadProfile();
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  formatDate(timestamp: string): string {
    return new Date(timestamp).toLocaleString('fr-FR', {
      dateStyle: 'medium',
      timeStyle: 'short'
    });
  }

  markAsRead(id: number): void {
    this.adminService.markAsRead(id).subscribe({
      next: () => {
        const notification = this.notifications.find(n => n.id === id);
        if (notification) notification.is_read = true;
        this.unreadCount = this.notifications.filter(n => !n.is_read).length;
        this.toastr.success('Notification marquée comme lue.', 'Succès');
      },
      error: (err) => {
        console.error('Erreur lors du marquage:', err);
        this.toastr.error('Erreur lors de la mise à jour de la notification.', 'Erreur');
      }
    });
  }

  loadNotifications(): void {
    this.adminService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.unreadCount = notifications.filter(n => !n.is_read).length;
        this.errorMessage = '';
      },
      error: (err) => {
        console.error('Erreur chargement notifications:', err);
        if (err.status === 401) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
        } else if (err.status === 404) {
          this.errorMessage = 'Notifications non disponibles.';
        } else {
          this.errorMessage = 'Erreur lors du chargement des notifications.';
        }
        this.toastr.error(this.errorMessage, 'Erreur');
      }
    });
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }

  toggleEditProfile(): void {
    this.isEditingProfile = !this.isEditingProfile;
    this.errorMessage = '';
    if (!this.isEditingProfile) {
      this.editedProfile = { ...this.adminProfile };
      this.currentPassword = '';
      this.newPassword = '';
    }
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  saveProfile(): void {
    this.errorMessage = '';

    if (this.editedProfile.email && !this.isValidEmail(this.editedProfile.email)) {
      this.errorMessage = 'Veuillez entrer un email valide.';
      this.toastr.error(this.errorMessage, 'Erreur');
      return;
    }

    const passwordProvided = !!(this.currentPassword && this.newPassword);
    if (passwordProvided) {
      if (!this.currentPassword || !this.newPassword) {
        this.errorMessage = 'Les deux mots de passe sont requis.';
        this.toastr.error(this.errorMessage, 'Erreur');
        return;
      }
      if (this.newPassword.length < 8) {
        this.errorMessage = 'Le nouveau mot de passe doit avoir au moins 8 caractères.';
        this.toastr.error(this.errorMessage, 'Erreur');
        return;
      }
    }

    const emailChanged = this.editedProfile.email && this.editedProfile.email !== this.adminProfile.email;
    if (!emailChanged && !passwordProvided) {
      this.errorMessage = 'Aucune modification à sauvegarder.';
      this.toastr.error(this.errorMessage, 'Erreur');
      return;
    }

    if (emailChanged) {
      this.adminService.updateAdminProfile(this.editedProfile).subscribe({
        next: (updatedProfile) => {
          this.adminProfile = { ...updatedProfile };
          this.toastr.success('Email mis à jour avec succès.', 'Succès');
          this.handleCompletion(passwordProvided);
        },
        error: (err) => {
          this.errorMessage = err.message || 'Erreur lors de la mise à jour de l\'email.';
          this.toastr.error(this.errorMessage, 'Erreur');
          console.error('Erreur profil:', err);
        }
      });
    } else {
      this.handleCompletion(passwordProvided);
    }
  }

  private handleCompletion(passwordProvided: boolean): void {
    if (passwordProvided) {
      const passwordUpdate: PasswordUpdate = {
        currentPassword: this.currentPassword,
        newPassword: this.newPassword
      };
      this.adminService.updateAdminPassword(passwordUpdate).subscribe({
        next: (response) => {
          this.toastr.success(response.message || 'Mot de passe mis à jour avec succès.', 'Succès');
          this.finalizeUpdate();
        },
        error: (err) => {
          this.errorMessage = err.message || 'Erreur lors de la mise à jour du mot de passe.';
          this.toastr.error(this.errorMessage, 'Erreur');
          console.error('Erreur mot de passe:', err);
        }
      });
    } else {
      this.finalizeUpdate();
    }
  }

  private finalizeUpdate(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.isEditingProfile = false;
    this.showProfile = false;
    this.errorMessage = '';
    this.loadProfile();
  }

  private loadProfile(): void {
    this.adminService.getAdminProfile().subscribe({
      next: (profile) => {
        this.adminProfile = { ...profile };
        this.editedProfile = { ...profile };
        this.errorMessage = '';
      },
      error: (err) => {
        console.error('Erreur lors du chargement du profil:', err);
        if (err.status === 401) {
          this.errorMessage = 'Session expirée. Veuillez vous reconnecter.';
        } else if (err.status === 404) {
          this.errorMessage = 'Profil non disponible.';
        } else {
          this.errorMessage = 'Erreur lors du chargement du profil.';
        }
        this.toastr.error(this.errorMessage, 'Erreur');
      }
    });
  }

  logout(): void {
    this.adminService.logout().subscribe({
      next: () => {
        localStorage.removeItem('token'); // Clear JWT token
        this.toastr.success('Déconnexion réussie.', 'Succès');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Erreur lors de la déconnexion:', err);
        localStorage.removeItem('token'); // Clear token even if server fails
        this.toastr.error('Erreur lors de la déconnexion.', 'Erreur');
        this.router.navigate(['/login']);
      }
    });
  }
}
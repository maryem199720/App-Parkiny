// src/app/navbar/navbar.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/services/auth/auth.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn$: Observable<boolean>;
  userInitials$: Observable<string>;
  userName$: Observable<string | undefined>;
  showProfileDropdown = false;

  constructor(private router: Router, private authService: AuthService) {
    this.isLoggedIn$ = this.authService.isLoggedIn();
    this.userInitials$ = this.authService.getUser().pipe(
      map(user => user?.initials ?? 'UN')
    );
    this.userName$ = this.authService.getUser().pipe(
      map(user => user ? `${user.firstName} ${user.lastName}` : undefined)
    );
  }

  ngOnInit(): void {
    console.log('NavbarComponent initialized');
  }

  onHomeClick(): void {
    console.log('Home link clicked');
    this.router.navigate(['/home']);
  }

  toggleProfileDropdown(event: Event): void {
    event.stopPropagation(); // Prevent event bubbling
    this.showProfileDropdown = !this.showProfileDropdown;
  }

  closeProfileDropdown(): void {
    this.showProfileDropdown = false;
  }

  navigateToProfileSection(section: string): void {
    this.router.navigate([`/profile/${section}`]);
    this.closeProfileDropdown();
  }

  logout(): void {
    this.authService.logout();
    this.closeProfileDropdown();
    this.router.navigate(['/auth']);
  }

  handleRestrictedLink(): void {
    this.authService.getUser().subscribe(user => {
      if (!user) {
        this.router.navigate(['/auth']);
      }
    });
  }
}
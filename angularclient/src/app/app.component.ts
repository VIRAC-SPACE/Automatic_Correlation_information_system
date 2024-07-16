import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {filter, map} from 'rxjs/operators';
import {BnNgIdleService} from 'bn-ng-idle';
import {AuthenticationService} from './_services/authentication.service';

import {User} from './_models/user';
import {Roles} from './_models/roles';

@Component({selector: 'app-root', templateUrl: 'app.component.html'})
export class AppComponent implements OnInit {
  user: User | undefined;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private titleService: Title,
    private activatedRoute: ActivatedRoute,
    private bnIdle: BnNgIdleService
  ) {
    this.authenticationService.user.subscribe(x => this.user = x);
    if (localStorage.getItem('isLoggedIn') === 'true') {
      this.bnIdle.startWatching(600).subscribe((res) => {
        if (res && localStorage.getItem('correlationStarted') == "false") {
          alert("session expired");
          localStorage.setItem('isLoggedIn', "false");
          this.logout();
        }
      })
    }
  }

  get isAdmin() {
    // @ts-ignore
    return this.user && Roles.admin === this.user.role.name;
  }

  ngOnInit() {
    const appTitle = this.titleService.getTitle();
    this.router
      .events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        const child = this.activatedRoute.firstChild;
        // @ts-ignore
        if (child.snapshot.data['title']) {
          // @ts-ignore
          return child.snapshot.data['title'];
        }
        return appTitle;
      })
    ).subscribe((ttl: string) => {
      this.titleService.setTitle(ttl);
    });
  }

  logout() {
    this.authenticationService.logout();
  }
}

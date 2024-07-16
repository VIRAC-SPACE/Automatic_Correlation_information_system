import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import {AuthenticationService} from '../_services/authentication.service';
import {Roles} from '../_models/roles';

@Injectable({providedIn: 'root'})
export class AuthGuard  {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {}

  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const user = this.authenticationService.userValue;
    if (user) {
      if (user.roles) {
        let tmp = user.roles[0];
        if (tmp) {
          // @ts-ignore
          if (route.data['roles'] && route.data['roles'].indexOf(tmp.name) === -1) {
            // @ts-ignore
            if (tmp) {
              var role_name = tmp.name;
              if (role_name === Roles.admin) {
                this.router.navigate(["/admin"]);
              } else {
                this.router.navigate(["/startpage"]);
              }
            }
            return false;
          }
        }
        return true;
      }
    }
    this.router.navigate(['/index']);
    return false;
  }

  public isLoggedIn(): boolean {
    let status = false;
    status = localStorage.getItem('isLoggedIn') == "true";
    return status;
  }

}

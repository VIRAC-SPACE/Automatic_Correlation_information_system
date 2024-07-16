import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {User} from '../_models/user';
import {InitIndex} from '../_models/init_index';
import {environment} from '../../environments/environment';


@Injectable({providedIn: 'root'})
export class AuthenticationService {
  public user: Observable<User>;
  private userSubject: BehaviorSubject<User>;

  constructor(
    private router: Router,
    private http: HttpClient
  ) {
    // @ts-ignore
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
    this.user = this.userSubject.asObservable();
  }

  public get userValue(): User {
    return this.userSubject.value;
  }

  login(email: string, password: string): Observable<any> {
    localStorage.setItem('isLoggedIn', "true");
    const config = {
      params: {
        email: email,
        password: password
      },
      ignoreAuthModule: 'ignoreAuthModule'
    };
    return this.http.post<any>(environment.serverData + 'authenticate', '', config)
      .pipe(map(user => {
        // store user details and basic auth credentials in local storage to keep user logged in between page refreshes
        user.authdata = window.btoa(email + ':' + password);
        localStorage.setItem('user', JSON.stringify(user));
        this.userSubject.next(user);
        return user;
      }));
  }



  logout(): void {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    // @ts-ignore
    this.userSubject.next(null);
    localStorage.setItem('isLoggedIn', "false");
    InitIndex.link = '/index';
    window.location.reload();
    this.router.navigate(['/index']);
  }

  getAccount(): Observable<User> {
    return this.http.get<User>(environment.serverData + 'security/account');
  }
}

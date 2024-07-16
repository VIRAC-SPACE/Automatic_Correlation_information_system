import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {first} from 'rxjs/operators';

import {AuthenticationService} from '../_services/authentication.service';
import {Roles} from '../_models/roles';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  email = "";
  password = "";
  loading = false;
  submitted = false;
  error = '';
  loginForm: UntypedFormGroup | undefined;


  constructor(private formBuilder: UntypedFormBuilder, private router: Router, private authenticationService: AuthenticationService
  ) {
    //redirect to home if already logged in
    const user = this.authenticationService.userValue;

    if (user) {
      if (user.roles) {
        var tmp = user.roles[0];
        if (tmp){
          var role_name = tmp.name;
          localStorage.setItem('isLoggedIn', "true");
          if (role_name === Roles.admin) {
            this.router.navigate(["/admin"]);
          } else {
            this.router.navigate(["/startpage"]);
          }
        }
      }
    }
  }

  get f() { // @ts-ignore
    return this.loginForm.controls;
  }

  test_email(email: string) {
    return email.search("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$") == 0;
  }

  ngOnInit() {
    localStorage.setItem('isLoggedIn', "false");
    this.loginForm = this.formBuilder.group({
      email: [''],
      password: ['']
    });
  }

  login(login_data: { email: string; password: string; }) {
    this.submitted = true;

    this.authenticationService.login(login_data.email, login_data.password)
      .pipe(first())
      .subscribe(
        data => {
          const user = this.authenticationService.userValue;
          if (user.isNew) {
            localStorage.setItem('correlationStarted', "false");
            this.router.navigate(["/changepassword"]);
          } else {
            // @ts-ignore
            localStorage.setItem('isLoggedIn', "true");
            window.location.reload();
            // @ts-ignore
            if (user.role.name === Roles.admin) {
              localStorage.setItem('correlationStarted', "false");
              this.router.navigate(["/admin"]);
            } else {
              localStorage.setItem('correlationStarted', "false");
              this.router.navigate(["/startpage"]);
            }
          }
        },
        error => {
          this.error = error;
          this.loading = false;
          localStorage.setItem('isLoggedIn', "false");
          window.location.reload();
        });
  }


  ForgetYourPassword() {
    this.router.navigate(["/forgetyourpassword"]);
  }

}

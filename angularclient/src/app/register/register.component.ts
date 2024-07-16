import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';

import {UserService} from '../_services/user.service';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-user',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  email = "";
  password = "";
  name = "";
  lastname = "";
  role = "";

  constructor(private userService: UserService, private router: Router, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.role = "user"
  }

  test_email(email: string) {
    return email.search("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$") == 0;
  }

  registration() {
    this.userService.getUserByEmail(this.email).subscribe((user) => {

      if (user === null) {
        var config = {
          headers: {
            'Content-Type': 'application/json'
          }
        };
        var data = JSON.stringify({
          "email": this.email,
          "name": this.name,
          "last_name": this.lastname,
          "role_string": this.role,
          "password": this.password
        });
        this.http.post<any>(environment.serverData + "registration/senduserdatatoadmis", data, config)
          .subscribe({
            next: data => {

            },
            error: error => {
            }
          });
        alert("Your request has been sent to admin. Please wait until you receive email");
        this.router.navigate(["/index"]);
      } else {
        window.alert("User with email " + user.email + " already exist");

      }
    });
  }
}

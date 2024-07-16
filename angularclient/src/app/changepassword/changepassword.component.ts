import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

import {UserService} from '../_services/user.service';
import {Roles} from '../_models/roles';

@Component({
  selector: 'app-changepassword',
  templateUrl: './changepassword.component.html',
  styleUrls: ['./changepassword.component.css']
})

export class ChangepasswordComponent implements OnInit {
  email = "";
  password = "";

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
  }

  test_email(email: string) {
    return email.search("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$") == 0;
  }

  change_password() {
    this.userService.getUserByEmail(this.email).subscribe((user) => {
      if (user === null) {
        alert("The email you entered does not exit. Please re-enter.");
      } else {
        this.userService.ChangePassword(this.email, this.password).subscribe((user_change) => {
          // @ts-ignore
          if (user.role.name === Roles.admin) {
            this.router.navigate(["/admin"]);
          } else {
            this.router.navigate(["/startpage"]);
          }
        });
      }
    });
  }
}

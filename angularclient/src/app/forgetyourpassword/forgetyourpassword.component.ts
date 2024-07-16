import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

import {UserService} from '../_services/user.service';

@Component({
  selector: 'app-forgetyourpassword',
  templateUrl: './forgetyourpassword.component.html',
  styleUrls: ['./forgetyourpassword.component.css']
})
export class ForgetyourpasswordComponent implements OnInit {
  email = "";

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
  }

  test_email(email: string) {
    return email.search("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$") == 0;
  }

  forgotyourpassword() {
    this.userService.getUserByEmail(this.email).subscribe((user) => {
      if (user === null) {
        alert("The email you entered does not exit. Please re-enter.");
      } else {
        this.userService.resetPassword(this.email).subscribe((reset_password) => {
          this.router.navigate(["/index"]);
        });
      }
    });
  }
}

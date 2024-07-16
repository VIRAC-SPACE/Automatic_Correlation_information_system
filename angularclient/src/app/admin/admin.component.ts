import {Component, OnInit} from '@angular/core';
import {first} from 'rxjs/operators';

import {UserService} from '../_services/user.service';
import {ExperimentService} from '../_services/experiment.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  create_email = "";
  create_password = "";
  create_first_name = "";
  create_last_name = "";
  create_role = "";
  error = "";
  create_data: any = "";
  expenseEntries: any = "";
  users: any = "";
  changeuser = "";
  edit_email = "";
  edit_password = "";
  edit_role = "";
  edit_first_name = "";
  edit_last_name = "";
  fullusernames = []
  deleteeuser = "";
  experiments: any = "";
  deleteexperiment = "";
  private _selected_user: any;

  constructor(private userService: UserService, private experimentService: ExperimentService) {
  }

  ngOnInit(): void {
    localStorage.setItem('isLoggedIn', "true");
    this.userService.getAll().subscribe((users) => {
      this.users = users;

      for (var user_index = 0; user_index < this.users.length; user_index++) {
        // @ts-ignore
        this.fullusernames.push(this.users[user_index].fullname);
      }

      this.create_role = "user";
      this.changeuser = this.users[0].fullname;
      this.edit_email = this.users[0].email;
      this.edit_role = this.users[0].role.name;
      this.edit_first_name = this.users[0].name;
      this.edit_last_name = this.users[0].last_name;
      this.deleteeuser = this.users[0].fullname;
    });

    this.experimentService.getAll().subscribe((experiments) => {
      this.experiments = experiments;
      if (this.experiments.length != 0) {
        this.deleteexperiment = this.experiments[0].name;
      }
    })

  }

  test_email(email: string) {
    return email.search("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$") == 0;

  }

  createuser() {
    this.userService.CreateUser(this.create_email, this.create_password, this.create_first_name, this.create_last_name, this.create_role)
      .pipe(first())
      .subscribe(
        data => {
          this.create_data = data;
        },
        error => {
          this.error = error;
        });
  }

  useeditchange(selected_user: any) {
    this._selected_user = selected_user;
    // @ts-ignore
    const id = this.fullusernames.indexOf(this.changeuser);
    this.edit_email = this.users[id].email;
    this.edit_role = this.users[id].role.name;
    this.edit_first_name = this.users[id].name;
    this.edit_last_name = this.users[id].last_name;
  }

  editeuser() {
    // @ts-ignore
    const id = this.fullusernames.indexOf(this.changeuser);
    this.userService.UpdateUser(this.edit_email, this.edit_password, this.edit_first_name, this.edit_last_name, this.edit_role, id)
      .pipe(first())
      .subscribe(
        data => {
          this.create_data = data;
        },
        error => {
          this.error = error;
        });
  }

  deleteuser() {
    // @ts-ignore
    var id = this.fullusernames.indexOf(this.deleteeuser)
    this.userService.DeleteeUser(this.users[id].id);
  }

  delete_experiment() {
    this.experimentService.DeleteExperiment(this.deleteexperiment);
  }

}

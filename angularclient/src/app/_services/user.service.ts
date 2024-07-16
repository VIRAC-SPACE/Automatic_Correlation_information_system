import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

import {User} from '../_models/user';
import {environment} from '../../environments/environment';


@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private http: HttpClient) {
  }

  getAll() {
    var url = environment.serverData + 'users/all';
    var config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };

    return this.http.get(url, config).pipe(map((res) => {
      return res;
    }))
  }


  getUserByEmail(email: string) {
    var url = environment.serverData +'/users/usersemail?email=' + email;
    return this.http.get<User>(url).pipe(map((res) => {
      return res;
    }));
  }

  getById(id: number) {
    return this.http.get<User>(environment.serverData + 'users/usersid?id=' + id);
  }

  CreateUser(Email: any, Password: any, Name: any, LastName: any, Role: any) {
    var url = environment.serverData + 'users/create';
    var data = JSON.stringify({
      "email": Email,
      "password": Password,
      "name": Name,
      "last_name": LastName,
      "role_string": Role,
      "test": "test"
    })

    var config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };

    return this.http.post<any>(url, data, config)
      .pipe(map(user => {
        return user;
      }));
  }

  UpdateUser(Email: any, Password: any, Name: any, LastName: any, Role: any, usersID: any) {
    var url = environment.serverData + 'users/update';
    var data = JSON.stringify({
      "email": Email,
      "password": Password,
      "name": Name,
      "last_name": LastName,
      "role_string": Role,
      "id": usersID,
      "test": "test"
    })
    var config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<any>(url, data, config);
  }

  ChangePassword(email: any, newPassword: any) {
    var url = environment.serverData + 'users/changepassword';
    var data = JSON.stringify({"email": email, "password": newPassword});

    var config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<any>(url, data, config);
  }

  resetPassword(email: any) {
    var url = environment.serverData + 'users/forgetyourpassword';
    var data = JSON.stringify({"email": email});
    var config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<any>(url, data, config);
  }

  DeleteeUser(usersID: string) {
    var url = environment.serverData + 'users/delete?id=' + usersID;
    this.http.delete(url)
      .subscribe({
        next: data => {

        },
        error: error => {
        }
      });
  }
}

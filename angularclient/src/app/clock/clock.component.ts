import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import * as $ from 'jquery';

import {HomeClass} from '../_models/homeclass';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-clock',
  templateUrl: './clock.component.html',
  styleUrls: ['./clock.component.css']
})
export class ClockComponent implements OnInit {
  AllStations: any;
  // tslint:disable-next-line:variable-name
  clocks_valid: boolean | undefined;
  clockButtonName = "";
  private _event: any;

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.clocks_valid = false;
    this.http.get(environment.serverData + 'vex/getallstations').subscribe((stations) => {
      // @ts-ignore
      this.AllStations = stations['AllStations'];
    });
    // @ts-ignore
    this.clockButtonName = localStorage.getItem('clockButtonName');
  }

  addclocks(): void {
    let clocks: any[];
    clocks = [];
    for (const item of this.AllStations) {
      let tmpClockEarly: string;
      tmpClockEarly = '#ClockEarly-' + item;
      let tmpRate: string;
      tmpRate = '#Rate-' + item;
      clocks.push($(tmpClockEarly).val());
      clocks.push($(tmpRate).val());
    }
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    const data = JSON.stringify({clocks: clocks});

    if (localStorage.getItem('observation',) === 'new') {
      this.http.post(environment.serverData + 'vex/getclocks', data, config).subscribe((clock) => {
      });
      HomeClass.vex_class = 'btn btn-primary btn-lg disabled';
      HomeClass.ctrl_class = 'btn btn-primary btn-lg active';

      if (localStorage.getItem('multihome') == 'true'){
        HomeClass.cor_class = 'btn btn-success btn-lg';
        this.router.navigate(['/multihome']);
      }
      else {
        this.router.navigate(['/home']);
      }

    } else if (localStorage.getItem('observation',) === 'old') {
      this.http.post(environment.serverData + 'vex/changeClocks', data, config).subscribe((clock) => {
      });
      this.router.navigate(['/viewexperiment']);
    }
  }

  validclocks(event: any): void {
    this._event = event;
    // tslint:disable-next-line:variable-name
    let clock_count = 0;
    // tslint:disable-next-line:variable-name
    let test_positive = true;
    if (this.AllStations) {
      for (const item of this.AllStations) {
        let tmpClockEarly: string;
        tmpClockEarly = '#ClockEarly-' + item;
        let tmpRate: string;
        tmpRate = '#Rate-' + item;
        if (parseFloat(<string>$(tmpClockEarly).val()) < 0) {
          test_positive = false;
          this.clocks_valid = false;
        }
        if (parseFloat(<string>$(tmpRate).val()) < 0) {
          test_positive = false;
          this.clocks_valid = false;
        }

        if ($(tmpClockEarly).val() != '') {
          clock_count += 1;
        }
        if ($(tmpRate).val() != '') {
          clock_count += 1;
        }
      }

      if (test_positive) {
        if (this.AllStations.length * 2 === clock_count) {
          this.clocks_valid = true;
        }
      }

    }
  }

}

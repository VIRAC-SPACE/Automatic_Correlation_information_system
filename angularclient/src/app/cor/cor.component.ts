import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

import {environment} from '../../environments/environment';

@Component({
  selector: 'app-cor',
  templateUrl: './cor.component.html',
  styleUrls: ['./cor.component.css']
})
export class CorComponent implements OnInit {
  correlationStarted = false;
  exitValue: Boolean | undefined;
  errorMessage = "";
  obs_index = 0;
  obs = JSON.parse(localStorage.getItem("obs")|| '{}');
  currentOBS = this.obs[this.obs_index].exp_code
  correlator = this.obs[this.obs_index].correlator.toLowerCase()
  returnvexfileurl = environment.serverData + "vex/returnvexfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;
  returncontrolfileurl: string= environment.serverData + "ctlr/returncontrolfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    this.currentOBS = this.obs[this.obs_index].exp_code
    this.correlator = this.obs[this.obs_index].correlator.toLowerCase()
    this.returnvexfileurl = environment.serverData + "vex/returnvexfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;
    this.returncontrolfileurl =environment.serverData + "ctlr/returncontrolfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;
  }

  start_correlation() {

    // @ts-ignore
    document.getElementById('correlation').className = 'btn btn-success btn-lg disabled';
    this.correlationStarted = true;
    let corr_type = localStorage.getItem('corrType');
    let pass_type = localStorage.getItem('passType');
    const url = environment.serverData + 'ctlr/corfile?exp_code='+this.currentOBS+"&experiment_type="
      + this.correlator + "&corr_type=" + corr_type + "&pass_type=" + pass_type;
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    this.http.get(url).subscribe((response) => {
      this.correlationStarted = false;
    });

    this.obs_index += 1;
    if (this.obs_index  < this.obs.length) {
      this.currentOBS = this.obs[this.obs_index].exp_code
      this.correlator = this.obs[this.obs_index].correlator.toLowerCase()
      this.returnvexfileurl = environment.serverData + "vex/returnvexfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;
      this.returncontrolfileurl =environment.serverData + "ctlr/returncontrolfile?exp_code="+this.currentOBS+"&experiment_type=" + this.correlator;
    }
    else{
      this.router.navigate(['/startpage']);
    }
  }
}

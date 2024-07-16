import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-multicor',
  templateUrl: './multicor.component.html',
  styleUrls: ['./multicor.component.css']
})
export class MulticorComponent implements OnInit {
  correlationStarted = false;
  exitValue: Boolean | undefined;
  errorMessage = "";
  obs = JSON.parse(localStorage.getItem("obs")|| '{}');
  obs_index = 0;
  currentOBS = this.obs[this.obs_index].exp_code
  correlator = this.obs[this.obs_index].correlator.toLowerCase()

  constructor(private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    this.obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    this.currentOBS = this.obs[this.obs_index].exp_code
    this.correlator = this.obs[this.obs_index].correlator.toLowerCase()
  }

  start_correlation() {
    // @ts-ignore
    document.getElementById('correlation').className = 'btn btn-success btn-lg disabled';
    this.correlationStarted = true;
    localStorage.setItem('correlationStarted', "true");

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
      localStorage.setItem('correlationStarted', "false");

      let url2 = environment.serverData + "data-processing/savedataprocessingresults?expcode=" + this.currentOBS + "&check=4"
      this.http.get(url2).subscribe((response) => {})

      this.router.navigate(['/multicorfile']);
    });

  }
}

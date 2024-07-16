import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";

import {environment} from '../../environments/environment';
import {ExperimentService} from "../_services/experiment.service";

@Component({
  selector: 'app-view experiment',
  templateUrl: './view-experiment.component.html',
  styleUrls: ['./view-experiment.component.css']
})
export class ViewExperimentComponent implements OnInit {
  experimentType = "";
  correlationStarted = false;
  plottype = 'fringe';
  passType = '';
  scans: any;
  passType2 = "Contiuum"
  sources: any;
  html: string | undefined;
  plottypes : any = [];
  experimentName = "";
  source: string | undefined;
  plot_names: any = [];
  aipsPlotNames: any = [];
  standartPlots = new Map<string, []>();
  correlator_type: string = "multi";
  serverData = environment.serverData;
  start_data: any;
  project: any;

  constructor(private http: HttpClient, private router: Router, private experimentService: ExperimentService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.experimentName = params["expcode"]
      this.start_data = params["dateTimeLST"]
      this.project = params["project"]
      this.passType = params["passType"]
      this.experimentType = params["experimentType"]
    })

    localStorage.setItem('clockButtonName', 'Change clocks');
    localStorage.setItem('ctrlButtonName', 'Change control file');
    this.passType2 = "Contiuum"

    if (this.passType == "Contiuum_Line") {
      this.plottypes = ["fringe", "standartplots", "aips"]
    } else {
      this.plottypes = ["fringe", "standartplots"]
    }

    let data = JSON.stringify( {
      "correlator": this.experimentType,
      "start_data": this.start_data,
      "exp_code":  this.experimentName,
      "project": this.project
    })

    const url = environment.serverData + 'ctlr/returncontrolfiledata';
    this.http.post(url, data).subscribe((response) => {
      // @ts-ignore
      this.scans = response["scans"];
      // @ts-ignore
      this.sources = response["sources"];
      this.source = this.sources[0]

      if (this.correlator_type == "multi") {
        for (let s of this.sources) {
          if (this.passType != "Contiuum_Line") {
            let url2 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType + "&source=" + s + "&exp_code=" + this.experimentName;

            this.http.get(url2).subscribe((response) => {
              // @ts-ignore
              this.standartPlots[s] = response;
            });
          } else {
            let url2 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType2 + "&source=" + s + "&exp_code=" + this.experimentName;
            this.http.get(url2).subscribe((response) => {
              // @ts-ignore
              this.standartPlots[s] = response;
            });
          }

        }
      }
      // @ts-ignore
      this.plot_names = this.standartPlots[this.sources[0]]
    });

    if (this.correlator_type == "multi") {
      let urlAIPS = environment.serverData + 'results/getaipsnames?experimentNAME=' + this.experimentName + "&correlator=" + this.experimentType;
      this.http.get(urlAIPS).subscribe((response) => {
        // @ts-ignore
        this.aipsPlotNames = response;
      });
    }

    let scan = localStorage.getItem('scan');
    if (this.correlator_type == "multi") {
      if (this.passType != "Contiuum_Line") {
        let url3 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType;
        this.http.get(url3).subscribe((response) => {
          // @ts-ignore
          this.html = response['plotContent'];
        });
      } else {
        let url3 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
        this.http.get(url3).subscribe((response) => {
          // @ts-ignore
          this.html = response['plotContent'];
        });
      }

    }
  }

  start_correlation() {
    // @ts-ignore
    if (this.experimentType === 'SFXC'){
      // @ts-ignore
      document.getElementById('correlation').className = 'btn btn-success btn-lg disabled';
    }
    else if (this.experimentType === 'KANA'){
      // @ts-ignore
      document.getElementById('correlationKana').className = 'btn btn-success btn-lg disabled';
    }

    this.correlationStarted = true;

    const url = environment.serverData + 'ctlr/corfile';
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    this.http.get(url).subscribe((response) => {
      this.correlationStarted = false;
      this.router.navigate(['/corfile']);
    });
  }

  onPassChange(passtype: string) {

    if (this.plottype=="fringe") {
      if (passtype == "Line") {
        // @ts-ignore
        document.getElementById("fringeLine").className = 'btn btn-success btn-lg active';
        // @ts-ignore
        document.getElementById("fringeContiuum").className = 'btn btn-primary btn-lg active';
      } else if (passtype == "Contiuum") {
        // @ts-ignore
        document.getElementById("fringeContiuum").className = 'btn btn-success btn-lg active';
        // @ts-ignore
        document.getElementById("fringeLine").className = 'btn btn-primary btn-lg active';
      }
    }

    if (this.plottype=="standartplots") {
      if (passtype == "Line") {
        // @ts-ignore
        document.getElementById("standartplotsLine").className = 'btn btn-success btn-lg active';
        // @ts-ignore
        document.getElementById("standartplotsContiuum").className = 'btn btn-primary btn-lg active';
      } else if (passtype == "Contiuum") {
        // @ts-ignore
        document.getElementById("standartplotsContiuum").className = 'btn btn-success btn-lg active';
        // @ts-ignore
        document.getElementById("standartplotsLine").className = 'btn btn-primary btn-lg active';
      }
    }

    this.passType2 = passtype
    let scan = localStorage.getItem('scan');
    let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
    this.http.get(url2).subscribe((response) => {
      // @ts-ignore
      this.html = response['plotContent'];
    });
    for (let s of this.sources) {
      let url3 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType2 + "&source=" + s + "&exp_code=" + this.experimentName;
      this.http.get(url3).subscribe((response) => {
        // @ts-ignore
        this.standartPlots[s] = response;
      });
    }
  }

  onScanChange(scan: String) {
    // @ts-ignore
    localStorage.setItem('scan', scan);
    let passType = localStorage.getItem('passType');

    if (this.passType != "Contiuum_Line") {
      let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + passType;
      this.http.get(url2).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    } else {
      let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
      this.http.get(url2).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }
  }

  selectPlots(plottype: string) {
    this.plottype = plottype

    for (let t of this.plottypes){
      // @ts-ignore
      document.getElementById(t).className = 'btn btn-primary btn-lg active';
    }
    // @ts-ignore
    document.getElementById(this.plottype).className = 'btn btn-success btn-lg active';
  }

  onSourceChange(source: string) {
    this.source = source;
    // @ts-ignore
    this.plot_names = this.standartPlots[this.source]

    for (let s of this.sources){
      // @ts-ignore
      document.getElementById(this.passType+s).className = 'btn btn-primary btn-lg active';
    }
    // @ts-ignore
    document.getElementById(this.passType+ this.source).className = 'btn btn-success btn-lg active';
  }

  getPDFsrc(name: string){
    let pdfSrc;
    return pdfSrc = environment.serverData + "results/getplotsaips?name=" + name + "&exp_code=" + this.experimentName;
  }

}

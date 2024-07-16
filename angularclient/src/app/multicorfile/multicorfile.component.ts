import {Component, OnInit} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

// @ts-ignore
@Component({
  selector: 'app-multicorfile',
  templateUrl: './multicorfile.component.html',
  styleUrls: ['./multicorfile.component.css']
})
export class MulticorfileComponent implements OnInit {
  plottype = 'fringe';
  plottypes : any = [];
  scans: any;
  passType = localStorage.getItem('passType');
  passType2 = "Contiuum"

  sources: any;

  source: string | undefined;

  html: string | undefined;

  plot_names: any = [];

  aipsPlotNames: any = [];

  standartPlots = new Map<string, []>();

  obs = JSON.parse(localStorage.getItem("obs")|| '{}');
  obs_index = 0;
  experimentName  = this.obs[this.obs_index].exp_code
  getstandartplotsmultiurl  = environment.serverData + "results/getstandartplotsmulti?name=";

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    this.getstandartplotsmultiurl = environment.serverData + "results/getstandartplotsmulti?name=";

    this.passType = localStorage.getItem('passType');
    this.passType2 = "Contiuum"
    this.experimentName  = this.obs[this.obs_index].exp_code
    if (this.passType=="Contiuum_Line"){
      this.plottypes = ["fringe", "standartplots", "aips"]
    }
    else{
      this.plottypes = ["fringe", "standartplots"]
    }
    const url = environment.serverData + 'ctlr/returncontrolfiledata';

    let obs = JSON.parse(localStorage.getItem("obs")|| '{}');

    let data = JSON.stringify( {
      "correlator": obs[this.obs_index].correlator,
      "start_data": obs[this.obs_index].start_data,
      "exp_code": obs[this.obs_index].exp_code,
      "project": obs[this.obs_index].project
    })

    this.http.post<any>(url, data).subscribe((response) => {
      // @ts-ignore
      this.scans = response["scans"];
      // @ts-ignore
      this.sources = response["sources"];
      this.source = this.sources[0]

      for (let s of this.sources) {
        if (this.passType != "Contiuum_Line"){
          let url2 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType + "&source=" + s + "&exp_code=" + obs[this.obs_index].exp_code;
          this.http.get(url2).subscribe((response) => {
            // @ts-ignore
            this.standartPlots[s] = response;
          });
        }
        else{
          let url2 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType2 + "&source=" + s + "&exp_code=" + obs[this.obs_index].exp_code;
          this.http.get(url2).subscribe((response) => {
            // @ts-ignore
            this.standartPlots[s] = response;
          });
        }

      }
      // @ts-ignore
      this.plot_names = this.standartPlots[this.sources[0]]
    });

    let urlAIPS = environment.serverData + 'results/getaipsnames?experimentNAME=' + obs[this.obs_index].exp_code + "&correlator=" + this.obs[this.obs_index].correlator;
    this.http.get(urlAIPS).subscribe((response) => {
      // @ts-ignore
      this.aipsPlotNames = response;
    });

    let scan = localStorage.getItem('scan');

    if (this.passType != "Contiuum_Line"){
      let url3 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + obs[this.obs_index].exp_code + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType;
      this.http.get(url3).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }
    else{
      let url3 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + obs[this.obs_index].exp_code + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
      this.http.get(url3).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }

    // @ts-ignore
    document.getElementById("fringe").className = 'btn btn-success btn-lg active';
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

  onScanChange(scan: String) {
    // @ts-ignore
    localStorage.setItem('scan', scan);
    let obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    this.experimentName = obs[this.obs_index].exp_code;
    let passType = localStorage.getItem('passType');

    if (this.passType != "Contiuum_Line"){
      let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + passType;
      this.http.get(url2).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }
    else {
      let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + this.experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
      this.http.get(url2).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }


    for (let s of this.scans){
      // @ts-ignore
      document.getElementById(this.passType+s).className = 'btn btn-primary btn-lg active';
    }
    // @ts-ignore
    document.getElementById(this.passType+scan).className = 'btn btn-success btn-lg active';
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
    let obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    let scan = localStorage.getItem('scan');
    let experimentName = obs[this.obs_index].exp_code
    let url2 = environment.serverData + 'experiments/getplothtmlmulti?experimentNAME=' + experimentName + '&' + 'scan=' + scan + '&' + 'passType=' + this.passType2;
    this.http.get(url2).subscribe((response) => {
      // @ts-ignore
      this.html = response['plotContent'];
    });
    for (let s of this.sources) {
      let url3 = environment.serverData + 'results/getstandartplotnamesmulti?passType=' + this.passType2 + "&source=" + s + "&exp_code=" + obs[this.obs_index].exp_code;
      this.http.get(url3).subscribe((response) => {
        // @ts-ignore
        this.standartPlots[s] = response;
      });
    }
  }

  getPDFsrc(name: string){
    let pdfSrc;
    let obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    let experimentName = obs[this.obs_index].exp_code
    return pdfSrc = environment.serverData + "results/getplotsaips?name=" + name + "&exp_code=" + experimentName;
  }

}


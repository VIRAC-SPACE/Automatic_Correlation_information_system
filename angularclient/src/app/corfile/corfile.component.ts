import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-corfile',
  templateUrl: './corfile.component.html',
  styleUrls: ['./corfile.component.css']
})
export class CorfileComponent implements OnInit {
  result_type = 'fringe';

  html: string | undefined;
  experimentType: string | undefined;
  kana_plot_names: any = [];
  kana_plot_src: any[] = [];

  plot_names: any = [];
  serverData = environment.serverData;

  constructor(private http: HttpClient) {
    const url = environment.serverData + 'results/getstandartplotnames';
    this.http.get(url).subscribe((response) => {
      // @ts-ignore
      this.plot_names = response;
    });

    let experimentName: string;
    // @ts-ignore
    experimentName = localStorage.getItem('experimentName');
    // @ts-ignore
    this.experimentType = localStorage.getItem('experimentType');

    if (this.experimentType==='SFXC') {
      let scan: string;
      // @ts-ignore
      scan = localStorage.getItem('scan');
      const url = environment.serverData + 'experiments/getplothtml?experimentNAME=' + experimentName + '&' + 'scan=' + scan;
      this.http.get(url).subscribe((response) => {
        // @ts-ignore
        this.html = response['plotContent'];
      });
    }

    else if (this.experimentType==='KANA'){
      const url = environment.serverData + "results/getkanaplotnames";
      this.http.get(url).subscribe((response) => {
        this.kana_plot_names = response;
        for (let name in this.kana_plot_names) {
          this.kana_plot_src.push(environment.serverData + "/results/getkanaplot?name=" + this.kana_plot_names[name] )
        }
      });
    }

  }

  ngOnInit(): void {

  }

  check_results(results_type: string) {
   this.result_type = results_type
  }
}


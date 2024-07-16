import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

import {environment} from '../../environments/environment';

@Injectable({providedIn: 'root'})
export class ExperimentService {
  constructor(private http: HttpClient) {
  }

  getAll() {
    const url = environment.serverData + 'experiments/all';
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };

    return this.http.get(url, config).pipe(map((res) => {
      return res;
    }))
  }

  DeleteExperiment(ExperimentName: string) {
    var url = environment.serverData + 'experiments/delete' + '?experimentNAME=' + ExperimentName;
    this.http.delete(url)
      .subscribe({
        next: data => {

        },
        error: error => {
        }
      });
  }

  GetExperimentByNameAndType(ExperimentName: string, ExperimentType: string) {
    const url = environment.serverData + 'experiments/getexperimentbynameandtype' + '?experimentNAME=' + ExperimentName + '&experimentTYPE=' + ExperimentType;
    return this.http.get(url).pipe(map((res) => {
      return res;
    }))
  }

  SetExperimentName(experimentName: any) {
    const url = environment.serverData + 'experiments/setexperimentname' + '?experimentNAME=' + experimentName;
    // @ts-ignore
    return this.http.post(url).pipe(map((res) => {
      return res;
    }))
  }

  SetExperimentType(experimentType: string) {
    const url = environment.serverData + 'experiments/setexperimenttype' + '?experimentTYPE=' + experimentType;
    // @ts-ignore
    return this.http.post(url).pipe(map((res) => {
      return res;
    }))
  }


  GetExperimentCorrType(experimentName: string) {
    const url = environment.serverData + 'experiments/getexperimentcorrtype' + '?experimentNAME=' + experimentName;
    return this.http.get(url).pipe(map((res) => {
      return res;
    }))
  }

}


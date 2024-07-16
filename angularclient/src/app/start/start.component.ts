import {Component, OnInit, ElementRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AuthenticationService } from '../_services/authentication.service';
import { environment } from '../../environments/environment';
import {ObsClass} from '../_models/obs';
import {Router} from "@angular/router";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html',
  styleUrls: ['./start.component.css']
})
export class StartComponent implements OnInit {
  loggedUserName: string = '';
  observations: any[] = [];
  observationsAll: any[] = [];
  antennas: string[] = [];
  projects: string[] = [];
  observationStatuses: string[] = [];
  dataProcessingStatuses: string[] = [];
  selectedAntenna: string = '';
  selectedProject: string = '';
  selectedStatus: string = '';
  selectedDataProcessingStatus: string = '';
  titleFilter: string = '';
  pipelines: any[] = [];
  selectedPipeline: any;
  correlator: string = "SFXC"
  correlator_type="single";
  passType = "Contiuum_Line";

  myObservationActual: boolean = false;

  constructor(
    private authenticationService: AuthenticationService,
    private http: HttpClient,
    private router: Router,
    private elementRef: ElementRef
  ) {
  }

  ngOnInit(): void {
    localStorage.setItem('observation', 'new');
    localStorage.setItem('clockButtonName', 'Add clocks');
    localStorage.setItem('ctrlButtonName', 'Create control file');
    localStorage.setItem('multihome', String(false));

    this.correlator_type="single";
    this.passType = "Contiuum_Line"
    localStorage.setItem('isLoggedIn', "true");
    localStorage.setItem('passType', this.passType);
    this.correlator = "SFXC"

    const loggedUser = localStorage.getItem('user');
    if (loggedUser) {
      const user = JSON.parse(loggedUser);
      this.loggedUserName = `${user.name} ${user.last_name}`;
    }
    this.getMyObservations();
  }


  getMyObservations() {
    const user = this.authenticationService.userValue;
    if (!user.email) {
      console.error('Username not found in session.');
      return;
    }

    const headers = new HttpHeaders().set('Authorization', user.email);
    this.http.get<any[]>(environment.serverData + 'planning/observation', { headers }).subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.observations = data;
          this.observationsAll = data;
          this.antennas = this.extractUniqueValues(data, 'antennas');
          this.projects = this.extractUniqueValues(data, 'projects');
          this.observationStatuses = ["Not started yet", "In progress", "Done"];
          this.dataProcessingStatuses = ["Not started yet", "Done"];
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching observations:', error);
      }
    );
  }

  applyFilters() {
    this.observations = this.observationsAll;
    const selectedAntenna = this.selectedAntenna;
    const selectedProject = this.selectedProject;
    const selectedStatus = this.selectedStatus;
    const selectedDataProcessingStatus = this.selectedDataProcessingStatus;
    const selectedTitle  = this.titleFilter;

    if (this.myObservationActual) {
      const today = new Date();
      const sevenDaysAgo = new Date(today);
      sevenDaysAgo.setDate(today.getDate() - 7);
      const sevenDaysAhead = new Date(today);
      sevenDaysAhead.setDate(today.getDate() + 7);

      this.observations = this.observations.filter(observation => {

          const observationDate = new Date(observation.dateTimeUTC);
          return observationDate >= sevenDaysAgo && observationDate <= sevenDaysAhead;
      });

  }

    if (selectedAntenna || selectedProject || selectedStatus || selectedDataProcessingStatus || selectedTitle) {
      this.observations = this.observations.filter(observation => {
        let matchAntenna = true;
        let matchProject = true;
        let matchStatus = true;
        let matchDataProcessingStatus = true;
  	let matchTitle= true;

        if (selectedAntenna) {
          matchAntenna = observation.antennas.includes(selectedAntenna);
        }
        if (selectedTitle) {
          matchTitle = observation.expcode.toLowerCase().includes(selectedTitle.toLowerCase());
        }

        if (selectedProject) {
          matchProject = observation.projects.some((project: { title: string; }) => project.title === selectedProject);
        }
        if (selectedStatus) {
          matchStatus = this.getObservationStatus(observation) === selectedStatus;
        }

        if (selectedDataProcessingStatus) {
          matchDataProcessingStatus = this.getDataProcessingStatus(observation) === selectedDataProcessingStatus;
        }

        return matchAntenna && matchProject && matchStatus && matchDataProcessingStatus && matchTitle;
      });
    } else {
     // this.observations = this.observationsAll;
    }
  }

  getObservationStatus(observation: any): string {
    if (this.isNotStarted(observation)) {
      return "Not started yet";
    } else if (this.isInProgress(observation)) {
      return "In progress";
    } else {
      return "Done";
    }
  }

  getDataProcessingStatus(observation: any): string {
    if (this.isDataProcessingDone(observation)) {
      return "Done";
    } else {
      return "Not started yet";
    }
  }
  extractUniqueValues(data: any[], key: string): string[] {
    const values: string[] = [];
    data.forEach(observation => {
      if (observation[key] instanceof Array) {
        observation[key].forEach((value: { title: string; }) => {
          if (!values.includes(value.title)) {
            values.push(value.title);
          }
        });
      } else {
        if (!values.includes(observation[key])) {
          values.push(observation[key]);
        }
      }
    });
    return values;
  }

  isNotStarted(observation: any): boolean {
    const observationDate = new Date(observation.dateTimeUTC);
    const currentUtcDate = new Date();

    return observationDate > currentUtcDate;
  }

  isInProgress(observation: any): boolean {
    const observationDate = new Date(observation.dateTimeUTC);
    const observationEndDate = new Date(observationDate.getTime() + observation.duration * 1000);
    const currentUtcDate = new Date();

    return observationDate <= currentUtcDate && currentUtcDate <= observationEndDate;
  }

  clearFilters() {
    this.selectedAntenna = '';
    this.selectedProject = '';
    this.selectedStatus = '';
    this.selectedDataProcessingStatus = '';
    this.titleFilter = '';
    this.observations = this.observationsAll;
    this.myObservationActual = false;
  }

  isDataProcessingDone(observation: any): boolean {
    return observation.acceptance.accept4;
  }

  startDataProcessing() {
    const selectedObservations = this.observations.filter(observation => observation.isChecked);
    const observationsJson = JSON.stringify(selectedObservations);
    //TODO change link
    this.router.navigate(['/start-data-processing'], { state: { observationsJson: observationsJson } });
  }

  onCorrelationTypeChange(correlator_type: any){
    this.correlator_type= correlator_type
    localStorage.setItem('corrType', this.correlator_type);
  }


  //INFO about localStorage
  //observations -> all selected observations
  //passType -> Line, Continum, Continum_Line, All
  //corrType -> multi, single
  //pipelines -> selected pipelines from DB
  select_correlation_type() {

    const selectedObservations = this.observations.filter(observation => observation.isChecked);

    let obs :ObsClass[] = [];
    for (let i = 0; i < selectedObservations.length; i++){
      let obs_ :ObsClass = new ObsClass();
      obs_.correlator=this.correlator;
      obs_.start_data=selectedObservations[i].dateTimeUTC;
      obs_.exp_code=selectedObservations[i].expcode;
      obs_.project=selectedObservations[i].projects[0].title;
      obs.push(obs_);
    }

    localStorage.setItem('obs', JSON.stringify(obs));
    this.http.post<any>(environment.serverData + 'ctlr/createdirgetvexfile', obs).subscribe(
      (createDataProcessingDirInfo) => {
        console.log('Data processing directory created');
      },
      (error) => {
        console.error('Error creating directory for data processing', error);
      })

    const user = this.authenticationService.userValue;
    if (!user.email) {
      console.error('Username not found in session.');
      return;
    }
    localStorage.setItem('observations', JSON.stringify(this.observations));
    localStorage.setItem('corrType', this.correlator_type);

    const data = {
      userEmail: user.email,
      passType: this.passType,
      pipelines: [this.selectedPipeline],
      expCodes: selectedObservations.map(observation => observation.expcode)  // Pārtaisīt šo daļū tā lai tā saglābā arī kurš korelātors KANA vai SFXC tiek izmantots
    };

    const headers = new HttpHeaders().set('Authorization', user.email);

    this.http.post<any>(environment.serverData +'data-processing/save', data ,{ headers }).subscribe(
      (dataProcessingInfo) => {
        console.log('Data processing info stored in DB', dataProcessingInfo);

      },
      (error) => {
        console.error('Error save data processing info:', error);
      }
    );
    if (this.correlator_type == "single") {
      this.router.navigate(['/ctrl']);
    }
    else{
      this.router.navigate(['/multicor']);
    }
  }

  onCorrelationChange(correlator: string){
    this.correlator = correlator;
  }

  onCorrelationPassChange(passType: string) {
    this.passType = passType;
    localStorage.setItem('passType', this.passType);
  }

  isAnyObservationChecked(): boolean {
    return this.observations.some(observation => observation.isChecked);
  }

  getPipelines() {

    this.http.get<any[]>(environment.serverData + 'planning/pipelines').subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.pipelines = data;
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching observations:', error);
      }
    );
  }

  onPipelineSelect(pipeline: any): void {
    this.selectedPipeline = pipeline;
    localStorage.setItem('pipelines', pipeline);
  }

  openModalAndLoadData() {
    this.getPipelines();
  }

  getDataprocessingLink(observation: any){
    return environment.clientData + "viewexperiment/" + observation.expcode + "/" + observation.projects[0].title + "/" + observation.dateTimeLST + "/" + this.passType + "/" + this.correlator;
  }

}


import {Component, OnInit} from '@angular/core';
import {ExperimentService} from "../_services/experiment.service";
import * as $ from 'jquery';
import {Router} from "@angular/router";

@Component({
  selector: 'app-chooseexperiment',
  templateUrl: './choose-experiment.component.html',
  styleUrls: ['./choose-experiment.component.css']
})
export class ChooseExperimentComponent implements OnInit {
  experiments: [] = [];
  selectExperimentName = "";
  experimentDate = ""
  experimentType = ""

  constructor(private experimentService: ExperimentService, private router: Router) {
  }

  ngOnInit(): void {
    localStorage.setItem('observation', 'old');

    this.experimentService.getAll().subscribe((experiments) => {
      // @ts-ignore
      this.experiments = experiments;
      if (this.experiments.length != 0) {
        // @ts-ignore
        this.experimentType = this.experiments[0]['experiment_type']
        // @ts-ignore
        this.experimentDate = this.experiments[0]['experiment_date']
        // @ts-ignore
        this.selectExperimentName = this.experiments[0]['name'];

        $("#chooseExperiment").attr('class', 'btn btn-success navbar-right btn-lg active');
      } else {
        $("#chooseExperiment").attr('class', 'btn btn-success navbar-right btn-lg disabled hidden');
      }
    })
  }

  select_experiment($event: Event) {
    const experimentNames: string[] = []
    for (let i = 0; i < this.experiments.length; i++) {
      experimentNames.push(this.experiments[i]['name'])
    }
    const experimentIndex = experimentNames.indexOf(this.selectExperimentName);
    this.experimentType = this.experiments[experimentIndex]['experiment_type']
    this.experimentDate = this.experiments[experimentIndex]['experiment_date']
  }

  chooseExperiment() {
    this.experimentService.SetExperimentName(this.selectExperimentName).subscribe((experiments) => {
    });
    this.experimentService.SetExperimentType(this.experimentType).subscribe((experiments) => {
    });
    localStorage.setItem('experiment_type', this.experimentType);
    localStorage.setItem('experiment_name', this.selectExperimentName);
    this.router.navigate(['/viewexperiment']);
  }

}

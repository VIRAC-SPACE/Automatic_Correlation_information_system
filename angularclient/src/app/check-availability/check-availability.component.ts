import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {Availability} from '../_models/availability'


@Component({
  selector: 'app-check-availability',
  templateUrl: './check-availability.component.html',
  styleUrls: ['./check-availability.component.css']
})



export class CheckAvailabilityComponent implements OnInit{

  startDateTime: string = "";
  endDateTime: string = "";
  selectedOptions: string[] = [];
  availabilityResults: Availability = new Availability();
  showTable: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
   
  }

  validateAndSubmit() {
    if (!this.startDateTime || !this.endDateTime) {
        alert('Both Start Date and Time and End Date and Time are required.');
        return;
    }
    if (!this.isValidDateRange()) {
        alert('End Date and Time must be later than Start Date and Time.');
        return;
    }
    if (this.selectedOptions.length === 0) {
        alert('Select at least one option.');
        return;
    }

    this.submitData();
}
isValidDateRange(): boolean {
  const startDate = new Date(this.startDateTime);
  const endDate = new Date(this.endDateTime);

  return startDate < endDate;
}

submitData() {
  const requestData = {
      startDateTime: this.startDateTime,
      endDateTime: this.endDateTime,
      antennas: this.selectedOptions
  };

  this.http.post<Availability>(environment.serverData + 'planning/antenna/check-availability', requestData)
    .subscribe((result: Availability) => {
      this.availabilityResults = result;
      this.showTable = true;
    });
}
}

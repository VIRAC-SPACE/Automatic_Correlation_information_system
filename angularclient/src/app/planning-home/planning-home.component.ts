import { Component, OnInit, OnDestroy, NgZone, ChangeDetectorRef, ApplicationRef   } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { AuthenticationService } from '../_services/authentication.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import * as moment from 'moment';

@Component({
  selector: 'app-planning-home',
  templateUrl: './planning-home.component.html',
  styleUrls: ['./planning-home.component.css']
})
export class PlanningHomeComponent implements OnInit, OnDestroy {
  observations: any[] = [];
  newTableData: any[] = []; // New table data array
  addGroupButtonClicked = false;
  sendDataToServer: boolean = false; // Variable to control data sending
  dataLoaded: boolean = false;
  selectedFile: File | undefined;
  projects: any[] = [];
  selectedProjects: any[] = [];
  groups: any[] = [];

  constructor(
    private http: HttpClient,
    private router: Router,
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private cdRef: ChangeDetectorRef,
    private zone: NgZone,
    private appRef: ApplicationRef
  ) {}

  ngOnInit(): void {
    this.getMyObservations();
    this.getProjects();
    this.getGroups();
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
          this.cdRef.detectChanges();
          this.appRef.tick(); 
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching observations:', error);
      }
    );
  }

  onProjectSelect() {
    //TODO need to finish
  }
  getProjects() {

    this.http.get<any[]>(environment.serverData + 'planning/projects').subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.projects = data;
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching project:', error);
      }
    );
  }

  createCopyInNewTable(observation: any) {
    if (observation.createCopy && observation.newGroupDateTime) {
      const copyData = {
        expcode: observation.expcode,
        dateTime: observation.newGroupDateTime,
        durationInHours: observation.durationInHours,
	status32: null,
	status16: null      };
  
      this.newTableData.push(copyData);
  
    } else if (observation.copyToNextWeek) {
      const currentDateTime = moment.utc(observation.dateTimeLST).toDate();
      currentDateTime.setDate(currentDateTime.getDate() + 7);
      const newGroupDateTimeForNextWeek = currentDateTime.toISOString().slice(0, 16);
  
      const copyData = {
        expcode: observation.expcode,
        dateTime: newGroupDateTimeForNextWeek,
        durationInHours: observation.durationInHours,
	status32: null,
	status16: null
      };
  
      // Add the copied data to the new table
      this.newTableData.push(copyData);  
    }
  }
  
  checkAvailabilityForNewRow(rowData: any) {
    // Call the server to check availability for the new row
    
    this.http.post<any>(environment.serverData +'check', rowData).subscribe(
      (availabilityData) => {
        // Update the corresponding row in the new table with availability information
        const rowIndex = this.newTableData.findIndex(row => row.expcode === rowData.expcode && row.dateTime === rowData.dateTime);
        if (rowIndex !== -1) {
          this.newTableData[rowIndex].availability = availabilityData.availability;
        }
      },
      (error) => {
        console.error('Error checking availability:', error);
      }
    );
  }
  calculateUtcTimeForCopiedObservations() {
    const copiedObservationsData = this.newTableData.map(row => {
      return {
        title: row.expcode,
        dateTime: row.dateTime,
        durationInHours: row.durationInHours,	
	status32: null,
	status16: null
      };
    });
  
    const requestData = {
      observations: copiedObservationsData
    };
    
    // Send the data to the server
    this.http.post<any>(environment.serverData + 'planning/obs/check-availability', requestData).subscribe(
      (response) => {
  
        // Update the status in the newTableData based on the response
        this.newTableData.forEach((row, index) => {
          if (response[index] && response[index].status32 !== undefined && response[index].status16 !== undefined) {
            row.status32 = response[index].status32;
	    row.status16 = response[index].status16;	
            row.utcDateTimeStart = response[index].utcDateTimeStart;
            row.utcDateTimeEnd = response[index].utcDateTimeEnd;
          }
        });
        this.dataLoaded = true;
      },
      (error) => {
        console.error('Error calculating UTC time:', error);
      }
    );
  }

  onFileChange(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.selectedFile = fileList[0];
    }
  }

  uploadFile() {
    if (this.selectedFile) {
      const formData: FormData = new FormData();
      formData.append('file', this.selectedFile, this.selectedFile.name);
      const user = this.authenticationService.userValue;
      if (!user.email) {
        console.error('Username not found in session.');
        return;
      }
  
      if (this.selectedProjects.length > 0) {
        formData.append('projects', JSON.stringify(this.selectedProjects));
      }
      const headers = new HttpHeaders().set('Authorization', user.email);

      this.http.post<any>(environment.serverData +'planning/read/key', formData, { headers }).subscribe(
        (response) => {
          if (response && response.observation) {
            this.zone.run(() => {
              this.getMyObservations()             
            });
          }
        },
        (error) => {
          console.error('Error uploading file:', error);
        }
      );
    }
  }


  sendDataToEndpoint() {
const isCheckboxChecked = this.newTableData.some(row => row.createObservation);

    if (!isCheckboxChecked || this.selectedProjects.length == 0) {
      alert('Select at least one observation and at least one project.');
      return;
    }  
  if (this.newTableData.length > 0) {

      if (this.newTableData.length > 0) {
        const requestData = {
          observations: this.newTableData
            .filter(row => row.createObservation === true)
            .map(row => ({
              title: row.expcode,
              dateTime: row.dateTime,
              projects: this.selectedProjects
            }))
        };
      const user = this.authenticationService.userValue;
      if (!user.email) {
        console.error('Username not found in session.');
        return;
      }
  
      const headers = new HttpHeaders({
        'Authorization': user.email,
        'Content-Type': 'application/json' 
      });

      this.http.post<any>(environment.serverData +'planning/create-using', requestData, { headers }).subscribe(
        (response) => {
          console.log('Data sent successfully:', response);
        },
        (error) => {
          console.error('Error sending data:', error);
        }
      );
    }
  }
}
  onCreateCopyCheckboxChange(observation: any) {
    if (observation.createCopy) {
      // Turn off the other checkbox
      observation.copyToNextWeek = false;
    }
  }

  onCopyToNextWeekCheckboxChange(observation: any) {
    if (observation.copyToNextWeek) {
      // Turn off the other checkbox
      observation.createCopy = false;
    }
  }

  // Function to enable data sending
  enableDataSending() {
    this.sendDataToServer = true;
  }

  ngOnDestroy(): void {
    // Cleanup code if needed
  }

  clearTable() 
  {
    this.newTableData = [];
  }
getGroups() {

    this.http.get<any[]>(environment.serverData + 'planning/groups').subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.groups = data;
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching groups:', error);
      }
    );
  }

  planObservationForAllGroupsNextWeek() {
    const currentDate = new Date();
    const daysInWeek = 7;
    const millisecondsInDay = 24 * 60 * 60 * 1000;
    const nextWeekStart = new Date(currentDate.getTime() + daysInWeek * millisecondsInDay); 
    
    for (let i = 0; i < daysInWeek; i++) {
      const nextDay = new Date(nextWeekStart.getTime() + i * millisecondsInDay);
      const nextDayFormatted = nextDay.toISOString().slice(0, 10);
  
      for (const item of this.groups) {
        const newRecord = {
          expcode: item.groupObsTitle,
          dateTime: nextDayFormatted + 'T' + item.lstime.split(':').slice(0, 2).join(':'),
          durationInHours: 2,
          dateTimeUTC: null, 
          status32: null, 
          status16: null
        };
        this.newTableData.push(newRecord);
      }
    }
  }
  
}

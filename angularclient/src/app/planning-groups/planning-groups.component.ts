import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AuthenticationService } from '../_services/authentication.service';

interface Group {
  groupId: number;
  groupObsTitle: string;
  lstime: string;
  duration: number;
  counter: number;
}

interface Project {
  projectId: number;
  title: string;
}
interface ApiResponseObservation {
  title: string;
  dateTime: string;
  utcDateTimeStart: string | null;
  utcDateTimeEnd: string | null;
  status32: boolean;
  status16: boolean;
  durationInHours: number;
  projects: Project[];
}

@Component({
  selector: 'app-planning-groups',
  templateUrl: './planning-groups.component.html',
  styleUrls: ['./planning-groups.component.css']
})
export class PlanningGroupsComponent implements OnInit {
  groups: Group[] = [];
  formattedGroups: { lstime: string, groupObsTitles: string, duration: number }[] = [];
  startDate: string = "";
  endDate: string = "";
  newTableData: any[] = [];
  dataLoaded: boolean = false;
  errorMessage: string = "";
  newTableDataByDate: any[] = [];
  projects: any[] = [];
  selectedProjects: any[] = [];

  constructor(
    private http: HttpClient,
    private authenticationService: AuthenticationService,
  ) {}

  ngOnInit(): void {
    this.getGroups();
    this.startDate = this.getTodayDate();
    this.getProjects();
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

  getGroups(): void {
    this.http.get<Group[]>(`${environment.serverData}planning/groups`).subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.groups = data;
          this.formatGroups();
        } else {
          console.error('Invalid data format received from server.');
        }
      },
      (error) => {
        console.error('Error fetching groups:', error);
      }
    );
  }

  formatGroups(): void {
    const grouped = this.groups.reduce((acc, group) => {
      const lstime = group.lstime;
      if (!acc[lstime]) {
        acc[lstime] = { lstime, groupObsTitles: [], duration: 0 };
      }
      acc[lstime].groupObsTitles.push(group.groupObsTitle);
      acc[lstime].duration = group.duration; 
      return acc;
    }, {} as { [key: string]: { lstime: string, groupObsTitles: string[], duration: number } });

    this.formattedGroups = Object.keys(grouped).map(lstime => ({
      lstime,
      groupObsTitles: grouped[lstime].groupObsTitles.join(', '),
      duration: grouped[lstime].duration
    }));
  }

  getTodayDate(): string {
    const today = new Date(); 
    const year = today.getFullYear(); 
    const month = today.getMonth() + 1; 
    const day = today.getDate(); 
    const formattedDate = `${year}-${this.addLeadingZero(month)}-${this.addLeadingZero(day)}`;

    return formattedDate;
  }

  addLeadingZero(value: number): string {
    return value < 10 ? `0${value}` : `${value}`;
  }

  calculateUtcTimeForCopiedObservations() {
    if (!this.startDate || !this.endDate) {
      alert("Both start date and end date are required.");
      return;
    }

    const startDateTime = new Date(this.startDate);
    const endDateTime = new Date(this.endDate);

    if (startDateTime > endDateTime) {
      alert("Start date cannot be later than end date.");
      return;
    }

    const dateDifference = (endDateTime.getTime() - startDateTime.getTime()) / (1000 * 3600 * 24);
    if (dateDifference > 10) {
      alert("The date range cannot exceed 10 days.");
      return;
    }

    const dateRange = this.getDateRange(startDateTime, endDateTime);

    this.newTableDataByDate = dateRange.map(date => ({
      date,
      observations: this.formattedGroups.map(group => ({
        title: group.groupObsTitles,
        dateTime: this.formatDate(date) + "T" + this.removeSeconds(group.lstime),
        utcDateTimeStart: "",
        utcDateTimeEnd: "",
        durationInHours: group.duration,
        status32: null,
        status16: null,
        selectedOption: this.getGroupWithMinCounter(group.groupObsTitles)
      }))
    }));

    const requestData = {
      observations: this.newTableDataByDate.flatMap(dateGroup => dateGroup.observations.map((observation: { title: string; dateTime: any; durationInHours: any; }) => ({
        title: observation.title,
        dateTime: observation.dateTime,
        durationInHours: observation.durationInHours
      })))
    };



    this.http.post<any[]>(`${environment.serverData}planning/obs/check-availability`, requestData).subscribe(
      (response) => {
          console.log('Observation data:', response);
          
          if (Array.isArray(response)) {
            const groupedByDate: { [key: string]: any[] } = {};
              response.forEach(observation => {
                  const date = observation.dateTime.split("T")[0];
                  if (!groupedByDate[date]) {
                      groupedByDate[date] = [];
                  }
                  groupedByDate[date].push(observation);
              });
  
              this.newTableDataByDate = Object.keys(groupedByDate).map(date => {
                  return {
                      date: date,
                      observations: groupedByDate[date].map(observation => ({
                          title: observation.title,
                          dateTime: observation.dateTime,
                          utcDateTimeStart: observation.utcDateTimeStart,
                          utcDateTimeEnd: observation.utcDateTimeEnd,
                          status32: observation.status32,
                          status16: observation.status16,
                          durationInHours: observation.durationInHours,
                          selectedOption: this.getGroupWithMinCounter(observation.title),
                          check: observation.status32 && observation.status16
                      }))
                  };
              });

              console.log('for saving:', JSON.stringify(this.newTableDataByDate, null, 2));
          } else {
              console.error('Invalid response format received from server.');
          }
  
          this.dataLoaded = true;
      },
      (error) => {
          console.error('Error fetching observation data:', error);
      }
  );
  }

  getCounter(title: string): number {
    const group = this.groups.find(g => g.groupObsTitle === title);
    return group ? group.counter : 0;
  }

  getGroupWithMinCounter(observationTitles: string): string {
    let minCounter = Infinity;
    let minGroup = '';

    observationTitles.split(',').forEach((title: string) => {
      const group = this.groups.find(g => g.groupObsTitle === title.trim());
      if (group && group.counter < minCounter) {
        minCounter = group.counter;
        minGroup = title.trim();
      }
    });

    return minGroup;
  }

  getOptionValue(option: string): string {
    const parts = option.split('(');
    return parts[0].trim();
  }

  sendDataToEndpoint() {

     const selectedObservations = this.newTableDataByDate
    .flatMap(dateGroup => dateGroup.observations)
    .filter(observation => observation.check)
    .map(observation => ({
      title: this.transformExpcode(observation.selectedOption), 
      dateTime: observation.dateTime,
      utcDateTimeStart: observation.utcDateTimeStart,
      utcDateTimeEnd: observation.utcDateTimeEnd,
      status32: observation.status32,
      status16: observation.status16,
      durationInHours: observation.durationInHours
    }));

    if (selectedObservations.length === 0 || this.selectedProjects.length === 0) {
      alert('Select at least one observation and at least one project.');
      return;
    }

    const requestData = {
      observations: selectedObservations.map(selectedObservations => ({
        title: selectedObservations.title,
        dateTime: selectedObservations.dateTime,
        projects: this.selectedProjects
      }))
    };


    console.log('Requested  data:', JSON.stringify(requestData, null, 2));
    const user = this.authenticationService.userValue;
    if (!user.email) {
      console.error('Username not found in session.');
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': user.email,
      'Content-Type': 'application/json'
    });

    console.log('Observation data:', JSON.stringify(requestData, null, 2));

    this.http.post<any>(environment.serverData + 'planning/create-using', requestData, { headers }).subscribe(
      (response) => {
        console.log('Data sent successfully:', response);
        this.updateObservationsWithResponseTitles(response);
      },
      (error) => {
        console.error('Error sending data:', error);
      }
    );
  }

  updateObservationsWithResponseTitles(observationResponse: ApiResponseObservation[]) {
    console.log('Response:', JSON.stringify(observationResponse, null, 2));

    observationResponse.forEach(respObservation => {
      this.newTableDataByDate.forEach(dateGroup => {
        dateGroup.observations.forEach((observation: { dateTime: string; selectedOption: string; isOptionSet: boolean }) => {
          if (observation.dateTime === respObservation.dateTime) {
            observation.selectedOption = respObservation.title;
            observation.isOptionSet = true;
          }
        });
      });
    });
  }


  clearTable() 
  {
    this.newTableDataByDate = [];
  }
  getNextLetterCounter(selectedOption: string): string {
    const letter = selectedOption[selectedOption.length - 1]; 
    const counter = this.getCounter(selectedOption.trim());
    const nextCounter = counter + 1;
    const paddedCounter = nextCounter.toString().padStart(3, '0'); 
    return letter + paddedCounter; 
 }
  
  getDateRange(startDate: Date, endDate: Date): Date[] {
    const dateRange: Date[] = [];
    const currentDate = new Date(startDate);
    while (currentDate <= endDate) {
      dateRange.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1); 
    }
    return dateRange;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = this.addLeadingZero(date.getMonth() + 1);
    const day = this.addLeadingZero(date.getDate());
    return `${year}-${month}-${day}`;
  }
  
  transformExpcode(expcode: string): string {
    if (!expcode) {
      return '';
    }
    
    const parts = expcode.split(',');
    const transformedPart = parts[0].trim() + '001';    
    return transformedPart;
  }

  transformExpcodeFromCounter(expcode: string): string {
    if (!expcode) {
      return '';
    }
    
    const parts = expcode.split('(');
    const transformedPart = parts[0].trim() + '001';    
    return transformedPart;
  }

  removeSeconds(time: string): string {
    return time.substring(0, 5); 
  }

  onProjectSelect()
  {
    
  }
  toggleObservation(observation: any) {
    if (observation.status32 && observation.status16) {
        observation.createObservation = !observation.createObservation;
    } 
  }

  isBothStatusChecked(observation: any): boolean {
    return observation.status32 && observation.status16;
  }

  shouldLoadMinGroup(observation: any): boolean {
    return this.isBothStatusChecked(observation) && observation.createObservation;
  }
}


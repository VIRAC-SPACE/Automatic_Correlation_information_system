import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { debounceTime, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { Source } from '../_models/source';
import { Group } from '../_models/group';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-planning',
  templateUrl: './planning.component.html',
  styleUrls: ['./planning.component.css']
})
export class PlanningComponent implements OnInit {
  searchTerm = new FormControl();
  searchResults: Source[] = [];
  newGroupName: string = '';
  newGroupDateTime: string = ''; // Mainīgs tagad saturs gan datumu, gan laiku
  selectedGroups: Group[] = [];
  selectedGroup: Group | null = null;
  expcode: string | undefined = undefined;

  constructor(private http: HttpClient, private router: Router,  private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.expcode = this.route.snapshot.queryParamMap.get('expcode') ?? undefined;
    this.searchTerm.valueChanges
      .pipe(
        debounceTime(300),
        switchMap((term: string) => this.search(term) as any[]) 
      )
      .subscribe((results: any[]) => {
        this.searchResults = results;
      });
  }

  search(term: string) {
    if (term.length >= 2) {
      const apiUrl = environment.serverData + 'planning/search/source/' + term;
      return this.http.get(apiUrl).pipe(
        tap((data: any) => {
          console.log('Response from server:', data);
        })
      );
    } else {
      return [];
    }
  }

  addToSelected(item: Source) {
    if (this.selectedGroup) {
      this.selectedGroup.sources = this.selectedGroup.sources || [];
      this.selectedGroup.sources.push(item);
    } else {
      console.error('Selected group is null. Please create a group first.');
    }
    console.log('Item added:', item, 'to group:', this.newGroupName);
  }

  createNewGroup() {
    if (this.newGroupName.trim() !== '' && this.newGroupDateTime !== '') {
      const newGroup: Group = {
        title: this.newGroupName,
        startDateTime: this.newGroupDateTime,
        sources: []
      };
      this.selectedGroups.push(newGroup);
      this.selectedGroup = newGroup; 
      this.newGroupName = '';
      this.newGroupDateTime = ''; // Notīrīt datumu un laiku pēc grupas izveides
    } else {
      console.error('Group name, and start date and time cannot be empty. Please enter the required information.');
    }
  }

  addAllGroupsToObservation() {
    const observationData = this.selectedGroups;

    this.http.post(environment.serverData + 'planning/add-groups?expcode='+this.expcode, observationData)
      .subscribe(response => {
        console.log('Response from server:', response);
      });
  }

  
}

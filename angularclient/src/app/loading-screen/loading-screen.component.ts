import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {debounceTime} from 'rxjs/operators';


import {LoadingScreenService} from "../_services/loading-screen.service";


@Component({
  selector: 'app-loading-screen',
  templateUrl: './loading-screen.component.html',
  styleUrls: ['./loading-screen.component.css']
})
export class LoadingScreenComponent implements OnInit, OnDestroy {
  loading: boolean = false;
  loadingSubscription: Subscription | undefined;

  constructor(private loadingScreenService: LoadingScreenService) {
  }

  ngOnInit(): void {

    this.loadingSubscription = this.loadingScreenService.loadingStatus.pipe(
      debounceTime(200)
    ).subscribe((value) => {
      this.loading = value;
    });
  }

  ngOnDestroy() {
    // @ts-ignore
    this.loadingSubscription.unsubscribe();
  }


}

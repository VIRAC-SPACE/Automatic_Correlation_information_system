import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoadingScreenService {

  loadingStatus: Subject<any> = new Subject();

  private _loading: boolean = false;

  get loading(): boolean {
    return this._loading;
  }

  set loading(value) {
    this._loading = value;
    // @ts-ignore
    this.loadingStatus.next();
  }

  startLoading() {
    this.loading = true;
  }

  stopLoading() {
    this.loading = false;
  }
}

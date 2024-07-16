import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

import * as $ from 'jquery';

import {HomeClass} from '../_models/homeclass';
import {environment} from '../../environments/environment';
import {ObsClass} from "../_models/obs";

@Component({
  selector: 'app-ctrl',
  templateUrl: './ctrl.component.html',
  styleUrls: ['./ctrl.component.css']
})
export class CtrlComponent implements OnInit {
  stations = [];
  scans = [];
  experimentname = '';
  refStation = '';
  allscans = [];
  channelArray = [];
  channelArray0 = '';
  // tslint:disable-next-line:variable-name
  choose_scan = '';
  stationsForThisScan = [];
  // tslint:disable-next-line:variable-name
  scan_index = 0;
  // tslint:disable-next-line:variable-name
  selectedStation = [];
  // tslint:disable-next-line:variable-name
  selected_station2 = [];
  datasourcedatafirstpart = '';
  datasourcedatasecondpart = '';
  selectedscan = '';
  scanStartTime = '';
  scanStopTime = '';
  crosspolarization = ['True', 'False'];
  multiphase = ['False', 'True'];
  multiphaseOption = '';
  multiphaseshow = false;
  channels: number | undefined;
  // tslint:disable-next-line:variable-name
  fft_size_correlation = 1024;
  // tslint:disable-next-line:variable-name
  fft_size_delay = 1024;
  chArray = [];
  dataSources = {};
  msglvl: number | undefined;
  intt: number | undefined;
  subintt: number | undefined;
  sep: any;
  // tslint:disable-next-line:variable-name
  multi_phase_scans: any;
  // tslint:disable-next-line:variable-name
  stations_selected = false;
  // tslint:disable-next-line:variable-name
  cross_polarize = '';
  ctrlButtonName = "";
  private _multi: any;
  experimentType: string | null | undefined;
  bufbegin = "";
  bufbend = "";
  jobs = [];
  selectedJob = "";
  delaybegin = 0;
  delayend = 1;
  bufferError = "";
  realTimeKana = 'False';
  realTimeKanaChoose = ['True', 'False'];
  kanaDataDirectory = "";
  kanaInAddress = "";
  kanaInPort: number | null | undefined;
  kanaOutAddress = "";
  kanaOutPort: number | null | undefined;
  rt_batch_length: number | null | undefined;
  currentOBS = ""
  obs_index = 0;
  obs :any;

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.experimentType = localStorage.getItem('experimentType')
    this.obs = JSON.parse(localStorage.getItem("obs")|| '{}');
    this.set_initial_control_parametrs();

    this.multiphaseOption = 'False';
    this.channels = 1024;
    this.fft_size_correlation = 1024;
    this.fft_size_delay = 1024;
    this.msglvl = 1;
    this.intt = 2;
    this.subintt = 15000;
    // @ts-ignore
    this.selected_station2.push('');
    this.cross_polarize = 'True';
    this.currentOBS = this.obs[this.obs_index].exp_code
    localStorage.setItem('ctrlButtonName', 'Create control file for observation ' + this.currentOBS);
    // @ts-ignore
    this.ctrlButtonName = localStorage.getItem('ctrlButtonName');
    // @ts-ignore
    this.jobs = ["M1", "M2", "M3"];
    this.selectedJob = "M1";
    this.bufbegin = "00s";
    this.bufbend = "01s";
    this.delaybegin = 0;
    this.delayend = 1;
    this.realTimeKana = 'False';
    this.kanaDataDirectory = "";
    this.kanaInAddress = "localhost";
    this.kanaInPort = 8080;
    this.kanaOutAddress = "localhost";
    this.kanaOutPort = 6060;
    this.rt_batch_length = 128;
  }

  selectscan(scan: string): void {
    this.choose_scan = scan;
    // @ts-ignore
    this.scan_index = this.scans.indexOf(this.choose_scan);
    this.selectedscan = this.choose_scan.replace('No', '');
    this.stationsForThisScan = this.allscans[this.scan_index]['stationLists'];
    this.datasourcedatasecondpart = '_' + 'no' + this.selectedscan + '.m5a';
    this.scanStartTime = this.allscans[this.scan_index]['starTime'];
    this.scanStopTime = this.allscans[this.scan_index]['stopTime'];
  }

  selectstations(stations: any[]): void {
    // @ts-ignore
    this.selected_station2 = [''];
    // @ts-ignore
    this.selectedStation = stations;
    this.dataSources = {};
    stations.forEach(item => {
      // @ts-ignore
      return this.selected_station2.push(item);
    });
    this.selectedStation.forEach(item => {
      // @ts-ignore
      this.dataSources[item] = this.datasourcedatafirstpart + this.stationNameToLower(item) + this.datasourcedatasecondpart;
    });
    this.stations_selected = true;
    this.sep = this.selectedStation[0];
  }

  stationNameToLower(station_name: string): string {
    return station_name.toLocaleLowerCase();
  }

  select_multi(multi: any): void {
    this._multi = multi;
    this.multiphaseshow = this.multiphaseOption === 'True';
  }

  IsPowerOfTwo(x: number | undefined): boolean {
    // @ts-ignore
    return (x != 0) && ((x & (x - 1)) == 0);
  }

  isSmallerTonumbersOfchannels(y: number): boolean {
    // @ts-ignore
    return y < this.channels;
  }

  datasourcevalid(id: string): boolean {
    // @ts-ignore
    return $('#data-' + this.stationNameToLower(id)).val().length !== 0;
  }

  test_ctrl(): boolean {
    if (this.selectedStation.length === 0) {
      return false;
    } else {
      // tslint:disable-next-line:variable-name prefer-for-of
      for (let s_index = 0; s_index < this.selectedStation.length; s_index++) {
        if ($('#data-' + this.stationNameToLower(this.selectedStation[s_index])).val() != undefined) {
          // @ts-ignore
          if ($('#data-' + this.stationNameToLower(this.selectedStation[s_index])).val().length === 0) {
            return false;
          }
        }
      }
    }
    if (this.scanStartTime.length === 0) {
      return false;
    }

    if (this.scanStopTime.length === 0) {
      return false;
    }

    const year = '20[0-9][0-9]y';
    const day = '([0-9][0-9][0-9])d';
    const hour = '([0-9][0-9])h';
    const minute = '([0-9][0-9])m';
    const second = '([0-9][0-9])s';

    const dateRegex = new RegExp(year + day + hour + minute + second);
    if (dateRegex.test(this.scanStartTime) && dateRegex.test(this.scanStopTime)) {
      const startDateArr = [0, 0, 0, 0, 0];
      const stopDateArr = [0, 0, 0, 0, 0];
      const scanStartDateArr = [0, 0, 0, 0, 0];
      const scanStopDateArr = [0, 0, 0, 0, 0];

      let j = -1;
      for (let i = 0; i < startDateArr.length; i++) {
        while (true) {
          j++;
          if (this.scanStartTime[j] === 'y' || this.scanStartTime[j] === 'd' || this.scanStartTime[j] === 'h' || this.scanStartTime[j] === 'm' || this.scanStartTime[j] === 's') {
            break;
          }
          stopDateArr[i] *= 10;
          // tslint:disable-next-line:radix
          stopDateArr[i] += parseInt(this.scanStopTime[j]);
          startDateArr[i] *= 10;
          // tslint:disable-next-line:radix
          startDateArr[i] += parseInt(this.scanStartTime[j]);
          scanStartDateArr[i] *= 10;
          // tslint:disable-next-line:radix
          scanStartDateArr[i] += parseInt(this.scanStartTime[j]);
          scanStopDateArr[i] *= 10;
          // tslint:disable-next-line:radix
          scanStopDateArr[i] += parseInt(this.scanStopTime[j]);
        }
      }
      if (startDateArr[0] > stopDateArr[0]) {
        return false;
      } else {
        let i = 1;
        let dateCheck = false;
        while (i <= 4) {
          dateCheck = false;
          // tslint:disable-next-line:radix
          if (parseInt(String(startDateArr[i])) === parseInt(String(stopDateArr[i]))) {
            i++;
          }
          // tslint:disable-next-line:radix
          else if (parseInt(String(startDateArr[i])) > parseInt(String(stopDateArr[i]))) {
            break;
          } else {
            dateCheck = true;
            break;
          }
        }
        if (!dateCheck) {
          return false;
        }
      }
      // tslint:disable-next-line:triple-equals
      if (startDateArr[0] != scanStartDateArr[0] || stopDateArr[0] != scanStopDateArr[0]) {
        return false;
      }
    } else {
      return false;
    }

    if (this.experimentname.length === 0) {
      return false;
    }

    if (this.intt === null) {
      return false;
    }
    if (this.channels === null || !this.IsPowerOfTwo(this.channels)) {
      return false;
    }

    if (this.multiphaseOption) {
      if (this.fft_size_delay === null || !this.IsPowerOfTwo(this.fft_size_delay)) {
        return false;
      }
      if (this.fft_size_correlation === null || !this.IsPowerOfTwo(this.fft_size_correlation)) {
        return false;
      }
    }

    return true;
  }

  send_ctrl(): void {

    // tslint:disable-next-line:variable-name
    let dataSources_send: any[];
    dataSources_send = [];
    this.selectedStation.forEach(item => {
      // tslint:disable-next-line:variable-name
      let datasourceTMP_Array: any[];
      datasourceTMP_Array = [];
      datasourceTMP_Array.push($('#data-' + this.stationNameToLower(item)).val());
      dataSources_send[item] = [$('#data-' + this.stationNameToLower(item)).val()];
      let jsonDataSource: { [p: number]: any[] };
      jsonDataSource = {[item]: datasourceTMP_Array};
      dataSources_send.push(jsonDataSource);
    });
    // tslint:disable-next-line:variable-name
    let multi_phase_scans_tmp: string | number | string[] | undefined = [];
    if ($('#multi_phase_scans').val() === undefined) {
      multi_phase_scans_tmp = [];
    } else {
      multi_phase_scans_tmp = $('#multi_phase_scans').val();
    }
    const data = JSON.stringify({
      scan: this.choose_scan,
      channels: this.chArray,
      message_level: this.msglvl,
      start: this.scanStartTime,
      stop: this.scanStopTime,
      exper_name: this.experimentname,
      integr_time: this.intt,
      reference_station: $('#ref').val(),
      setup_station: $('#sep').val(),
      cross_polarize: this.cross_polarize,
      number_channels: this.channels,
      stations: this.selectedStation,
      data_sources: dataSources_send,
      multi_phase_scans: multi_phase_scans_tmp,
      sub_integr_time: this.subintt,
      multi_phase_center: this.multiphaseOption, // True or false
      fft_size_correlation: this.fft_size_correlation,
      fft_size_delaycor: this.fft_size_delay,
      correlator: this.obs[this.obs_index].correlator
    });
    const url = environment.serverData + 'ctlr/sendcontrolfiledata';
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    this.http.post(url, data, config).subscribe((response) => {
    });
    HomeClass.vex_class = 'btn btn-primary btn-lg disabled';
    HomeClass.ctrl_class = 'btn btn-primary btn-lg disabled';
    HomeClass.cor_class = 'btn btn-primary btn-lg active';
    localStorage.setItem('scan', this.choose_scan);

    this.obs_index += 1;
    if (this.obs_index  < this.obs.length) {
      this.currentOBS = this.obs[this.obs_index].exp_code
      localStorage.setItem('ctrlButtonName', 'Create control file for observation ' + this.currentOBS);
      // @ts-ignore
      this.ctrlButtonName = localStorage.getItem('ctrlButtonName');
      this.set_initial_control_parametrs()
    }
    else{
      if (localStorage.getItem('observation',) === 'new') {
        this.router.navigate(['/cor']);
      } else if (localStorage.getItem('observation',) === 'old') {
        this.router.navigate(['/viewexperiment']);
      }
    }
  }

  checkNumber(num: any): boolean {
    if(num === null){
      return false;
    }
    else{
      return num.toString().length !== 0;
    }
  }

  test_ctrl_kana():boolean {
    const year = "20[0-9][0-9]y";
    const day = "([0-9][0-9][0-9])d";
    const hour = "([0-9][0-9])h";
    const minute = "([0-9][0-9])m";
    const second = "([0-9][0-9])s";

    const dateRegexSec = new RegExp(second);
    const dateRegexMin = new RegExp(minute + second);
    const dateRegexHour = new RegExp(hour + minute + second);
    const dateRegexDay = new RegExp(day + hour + minute + second);
    const dateRegexYear = new RegExp(year + day + hour + minute + second);

    const BufferBeginArray = this.bufbegin.split(/[ydhms]+/);
    BufferBeginArray.pop();
    const BufferEndArray = this.bufbend.split(/[ydhms]+/);
    BufferEndArray.pop();

    if (this.realTimeKana == "True") {
      if (this.kanaInAddress.length == 0){
        return false;
      }

      if (this.kanaOutAddress.length == 0){
        return false;
      }

      if(!this.checkNumber(this.kanaInPort)){
        return false;
      }

      if(!this.checkNumber(this.kanaOutPort)){
        return false;
      }

      if(!this.checkNumber(this.rt_batch_length)){
        return false;
      }
    }

    if(this.kanaDataDirectory.length == 0){
      return false;
    }

    if (this.delayend=== null){
      return  false;
    }
    else if (this.delaybegin === null){
      return false
    }
    else if (this.delaybegin < 0){
      return false;
    }
    else if (this.delayend < 0){
      return false;
    }
    else if (this.delaybegin>=this.delayend){
      return false;
    }
    else if (this.delayend>=240){
      return false;
    }
    else if (this.experimentname.length === 0){
      return false;
    }
    else if (this.bufbegin.length === 0){
      return false;
    }
    else if (this.bufbend.length === 0){
      return false;
    }
    else if (!dateRegexSec.test(this.bufbend) && !dateRegexSec.test(this.bufbegin) &&
      !dateRegexMin.test(this.bufbend) && !dateRegexMin.test(this.bufbegin) &&
      !dateRegexHour.test(this.bufbend) && !dateRegexHour.test(this.bufbegin) &&
      !dateRegexDay.test(this.bufbend) && !dateRegexDay.test(this.bufbegin) &&
      !dateRegexYear.test(this.bufbend) && !dateRegexYear.test(this.bufbegin)){
        return false;
    }

    else if (BufferBeginArray.length != BufferEndArray.length){
      return false;
    }

    for (let i=0; i<BufferBeginArray.length; i++){
      if (parseInt(BufferEndArray[i])<parseInt(BufferBeginArray[i])){
        return false;
      }
    }
      return true;
  }

  // @ts-ignore
  checkBuffer(): boolean {
    const year = "20[0-9][0-9]y";
    const day = "([0-9][0-9][0-9])d";
    const hour = "([0-9][0-9])h";
    const minute = "([0-9][0-9])m";
    const second = "([0-9][0-9])s";

    const dateRegexSec = new RegExp(second);
    const dateRegexMin = new RegExp(minute + second);
    const dateRegexHour = new RegExp(hour + minute + second);
    const dateRegexDay = new RegExp(day + hour + minute + second);
    const dateRegexYear = new RegExp(year + day + hour + minute + second);

    let dateCheck;
    if (!dateRegexSec.test(this.bufbend) && dateRegexSec.test(this.bufbegin)) {
      dateCheck = true
    } else if (dateRegexMin.test(this.bufbend) && dateRegexMin.test(this.bufbegin)) {
      dateCheck = true
    } else if (dateRegexHour.test(this.bufbend) && dateRegexHour.test(this.bufbegin)) {
      dateCheck = true
    } else if (dateRegexDay.test(this.bufbend) && dateRegexDay.test(this.bufbegin)) {
      dateCheck = true
    } else if (dateRegexYear.test(this.bufbend) && dateRegexYear.test(this.bufbegin)) {
      dateCheck = true
    } else {
      this.bufferError = "Buffer must be in 0000y000d00h00m00s format !";
      dateCheck = false;
    }

    const BufferBeginArray = this.bufbegin.split(/[ydhms]+/);
    BufferBeginArray.pop();
    const BufferEndArray = this.bufbend.split(/[ydhms]+/);
    BufferEndArray.pop();
    if (BufferBeginArray.length != BufferEndArray.length) {
      this.bufferError = "Buffer begin and end must be in the same format!";
      dateCheck = false;
    } else {
      dateCheck = false;
      for (let i = 0; i < BufferBeginArray.length; i++) {
        if (parseInt(BufferEndArray[i]) > parseInt(BufferBeginArray[i])) {
          dateCheck = true;
        } else if (parseInt(BufferEndArray[i]) < parseInt(BufferBeginArray[i])) {
          dateCheck = false;
          break;
        }
      }
      if (!dateCheck) {
        this.bufferError = "Buffer begin must be smaller than bufffer end";
        return false
      }
      else{
        return true;
      }
    }
  }

  send_kana_ctrl (): void{
    let data;
    if (this.selectedJob == "M3"){
      data = JSON.stringify({
        "exper_name" : this.experimentname,
        "job" : this.selectedJob,
        "bufferBegin" : this.bufbegin,
        "bufferEnd" : this.bufbend,
        "delayBegin" : this.delaybegin,
        "delayEnd" : this.delayend,
        "dataDir": this.kanaDataDirectory,
        "realTime": this.realTimeKana,
        "kanaInAddress": this.kanaInAddress,
        "kanaInPort": this.kanaInPort,
        "kanaOutAddress": this.kanaOutAddress,
        "kanaOutPort": this.kanaOutPort,
        "rt_batch_length": this.rt_batch_length,
        "correlator": this.obs[this.obs_index].correlator
      });
    }
    else {
      data = JSON.stringify({
        "exper_name": this.experimentname,
        "job": this.selectedJob,
        "bufferBegin": this.bufbegin,
        "bufferEnd": this.bufbend,
        "delayBegin": -1,
        "delayEnd": -1,
        "dataDir": this.kanaDataDirectory,
        "realTime": this.realTimeKana,
        "kanaInAddress": this.kanaInAddress,
        "kanaInPort": this.kanaInPort,
        "kanaOutAddress": this.kanaOutAddress,
        "kanaOutPort": this.kanaOutPort,
        "rt_batch_length": this.rt_batch_length,
        "correlator": this.obs[this.obs_index].correlator
      });
    }
    const url = environment.serverData + 'ctlr/sendcontrolfiledata', config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    this.http.post(url, data, config).subscribe((response) => {
    });
    HomeClass.vex_class = 'btn btn-primary btn-lg disabled';
    HomeClass.ctrl_class = 'btn btn-primary btn-lg disabled';
    HomeClass.cor_class = 'btn btn-primary btn-lg active';
    localStorage.setItem('scan', this.choose_scan);

    if (localStorage.getItem('observation',) === 'new') {
      this.router.navigate(['/home']);
    } else if (localStorage.getItem('observation',) === 'old') {
      this.router.navigate(['/viewexperiment']);
    }
  }

  set_initial_control_parametrs(){
    let obs_ :ObsClass = new ObsClass();
    obs_.correlator=this.obs[this.obs_index].correlator;
    obs_.start_data=this.obs[this.obs_index].start_data;
    obs_.exp_code=this.obs[this.obs_index].exp_code;
    obs_.project=this.obs[this.obs_index].project

    const url = environment.serverData + 'ctlr/returncontrolfiledata';
    this.http.post(url, obs_).subscribe((response) => {
      const data = response;
      // @ts-ignore
      this.stations = data["stations"];
      // @ts-ignore
      this.scans = data["scans"];
      this.choose_scan = this.scans[0];
      // @ts-ignore
      this.experimentname = data["experimentName"];
      this.refStation = this.stations[0];
      // @ts-ignore
      this.allscans = data["dataArray"];
      // @ts-ignore
      this.channelArray = data["channelArray"];
      this.scan_index = 0;
      this.stationsForThisScan = this.allscans[this.scan_index]['stationLists'];
      // @ts-ignore
      this.selectedscan = this.scans[this.scan_index].replace('No', '');
      this.datasourcedatafirstpart = 'file:///mnt/VLBI/data/' + this.experimentname + '/' + this.experimentname + '_';
      this.datasourcedatasecondpart = '_' + 'no' + this.selectedscan + '.m5a';
      this.scanStartTime = this.allscans[this.scan_index]['starTime'];
      this.scanStopTime = this.allscans[this.scan_index]['stopTime'];
      this.chArray = this.channelArray;
      this.selectedStation.forEach(item => {
        // @ts-ignore
        this.dataSources[item] = this.datasourcedatafirstpart + this.stationNameToLower(item) + this.datasourcedatasecondpart;
      });
      this.sep = this.selectedStation[0];
      // @ts-ignore
      this.kanaDataDirectory = "/mnt/VLBI/correlations/" +  this.experimentname + "/";
    });

  }

}

import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

import {HomeClass} from '../_models/homeclass';
import {ExperimentService} from '../_services/experiment.service';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-vex',
  templateUrl: './vex.component.html',
  styleUrls: ['./vex.component.css']
})
export class VexComponent implements OnInit {
  filename = '';
  progress_show = false;
  type = '';
  vex_upload_started = false;
  addClock = false;
  file: any;

  constructor(private http: HttpClient, private experimentService: ExperimentService, private router: Router) {
  }

  ngOnInit(): void {
    this.addClock = false;
    this.type = 'SFXC';
  }

  vexFileSelection(event: Event): void {
    // @ts-ignore
    this.file = event.target.files[0];
    if (this.file) {
      this.filename = this.file.name;
      if (!this.filename.endsWith('.vex')) {
        alert('Please upload vex file');
      } else {
        if (this.file.size === 0) {
          alert('Please upload non empty file');
        }
      }
    }
  }

  testvex_file(): boolean {
    return !!(this.file && this.filename.endsWith('.vex') && this.file.size != 0);
  }

  set_vex_upload_progress(progress: number): void {
    // tslint:disable-next-line:variable-name
    // @ts-ignore
    const progress_bar: HTMLElement = document.getElementById('progress');
    progress_bar.setAttribute('aria-valuenow', String(progress));
    progress_bar.setAttribute('style', 'width:' + progress + '%');
  }

  uploadvex(): void {
    this.vex_upload_started = true;
    this.progress_show = true;
    this.set_vex_upload_progress(0);

    this.experimentService.GetExperimentByNameAndType(this.filename, this.type).subscribe((experiment) => {
      if (experiment === null) {
        const fileReader = new FileReader();
        fileReader.onloadend = (e) => {
          // tslint:disable-next-line:variable-name
          // @ts-ignore
          const vex_file_content = e.target.result;
          const data = JSON.stringify({vexfile: vex_file_content});
          const config = {headers: {'Content-Type': 'application/json'}};
          const url = environment.serverData + 'vex/validatevex';
          this.http.post(url, data, config).subscribe({
            // tslint:disable-next-line:no-shadowed-variable
            next: data => {
              // tslint:disable-next-line:variable-name
              let vex_data;
              // @ts-ignore
              if (data["vex_file_valid"]) {
                this.set_vex_upload_progress(50);
                // tslint:disable-next-line:no-shadowed-variable
                const url = environment.serverData + 'vex/getvexfile';
                if (this.addClock) {
                  vex_data = JSON.stringify({
                    vexfile: vex_file_content,
                    addClock: 1,
                    vexName: this.filename,
                    type: this.type,
                    corrType: localStorage.getItem('corrType'),
                    passType: localStorage.getItem('passType')

                  });
                } else {
                  vex_data = JSON.stringify({
                    vexfile: vex_file_content,
                    addClock: 0,
                    vexName: this.filename,
                    type: this.type,
                    corrType: localStorage.getItem('corrType'),
                    passType: localStorage.getItem('passType')
                  });
                }
                this.http.post(url, vex_data, config).subscribe({
                  next: get_vex_file_data => {
                    this.set_vex_upload_progress(100);
                    if (this.addClock) {
                      localStorage.setItem('experimentName', this.filename.replace(/\.[^/.]+$/, ''));
                      this.router.navigate(['/clock']);
                    } else {
                      HomeClass.vex_class = 'btn btn-primary btn-lg disabled';
                      HomeClass.ctrl_class = 'btn btn-primary btn-lg active';
                      localStorage.setItem('experimentName', this.filename.replace(/\.[^/.]+$/, ''));
                      localStorage.setItem('experimentType', this.type);

                      if (localStorage.getItem('multihome') == 'true'){
                        HomeClass.cor_class = 'btn btn-success btn-lg';
                        this.router.navigate(['/multihome']);
                      }
                      else {
                        this.router.navigate(['/home']);
                      }
                    }

                  },
                  error: error => {
                    console.error('There was an error!', error);
                    this.vex_upload_started = false;
                    this.progress_show = false;
                    this.set_vex_upload_progress(0);
                  }
                });
              } else {
                alert('The vex file you uploaded is invalid. Please-upload!');
                this.vex_upload_started = false;
                this.progress_show = false;
                this.set_vex_upload_progress(0);
              }
            },
            error: error => {
              console.error('There was an error!', error);
              this.vex_upload_started = false;
              this.progress_show = false;
              this.set_vex_upload_progress(0);
            }
          });

        };
        fileReader.readAsBinaryString(this.file);
      } else {
        this.vex_upload_started = false;
        this.progress_show = false;
        this.set_vex_upload_progress(0);
        alert('Experiment with name ' + this.filename + ' and type ' + this.type + ' already exist! \n Please upload vex file with different name or select a different type! ');
      }
    });

  }

}

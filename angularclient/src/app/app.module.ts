import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {UntypedFormBuilder, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

import {AppComponent} from './app.component';
import {appRoutingModule} from './app-routing.module';
import {BnNgIdleService} from 'bn-ng-idle';
import { PdfViewerModule } from 'ng2-pdf-viewer';

import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {StartComponent} from './start/start.component';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {ErrorInterceptor} from './_helpers/error.interceptor';
import {AdminComponent} from './admin/admin.component';
import {ChangepasswordComponent} from './changepassword/changepassword.component';
import {ForgetyourpasswordComponent} from './forgetyourpassword/forgetyourpassword.component';
import {LoadingScreenComponent} from './loading-screen/loading-screen.component';
import {LoadingScreenInterceptor} from './_helpers/loading.interceptor';
import {VexComponent} from './vex/vex.component';
import {ClockComponent} from './clock/clock.component';
import {CtrlComponent} from './ctrl/ctrl.component';
import {CorComponent} from './cor/cor.component';
import {CorfileComponent} from './corfile/corfile.component';
import {ChooseExperimentComponent} from './chooseexperiment/choose-experiment.component';
import {ViewExperimentComponent} from './viewexperiment/view-experiment.component';
import {PlanningComponent} from './planning/planning.component';
import {PlanningHomeComponent} from './planning-home/planning-home.component';
import {PlanningUseObsComponent} from './planning-use-obs/planning-use-obs.component';
import {CheckAvailabilityComponent} from './check-availability/check-availability.component';
import {IndexComponent} from './index/index.component';
import { MulticorComponent } from './multicor/multicor.component';
import { MulticorfileComponent } from './multicorfile/multicorfile.component';
import {PlanningGroupsComponent} from './planning-groups/planning-groups.component';



@NgModule({
  imports: [
    BrowserModule,
    appRoutingModule,
    MatAutocompleteModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    PdfViewerModule
  ],
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    StartComponent,
    PageNotFoundComponent,
    AdminComponent,
    ChangepasswordComponent,
    ForgetyourpasswordComponent,
    LoadingScreenComponent,
    VexComponent,
    ClockComponent,
    CtrlComponent,
    CorComponent,
    PlanningGroupsComponent,
    CorfileComponent,
    ChooseExperimentComponent,
    ViewExperimentComponent,
    PlanningComponent,
    PlanningHomeComponent,
    PlanningUseObsComponent,
    CheckAvailabilityComponent,
    IndexComponent,
    MulticorComponent,
    MulticorfileComponent
  ],
  providers: [
    UntypedFormBuilder,
    Validators,
    BnNgIdleService,
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: LoadingScreenInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}



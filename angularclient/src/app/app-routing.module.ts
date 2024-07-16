import {RouterModule, Routes} from '@angular/router';

import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {StartComponent} from './start/start.component';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {AdminComponent} from './admin/admin.component';
import {AuthGuard} from './_helpers/auth.guard';
import {Roles} from './_models/roles';
import {InitIndex} from './_models/init_index';
import {ChangepasswordComponent} from './changepassword/changepassword.component';
import {ForgetyourpasswordComponent} from './forgetyourpassword/forgetyourpassword.component';
import {LoadingScreenComponent} from './loading-screen/loading-screen.component';
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
import {MulticorComponent} from './multicor/multicor.component';
import {MulticorfileComponent} from './multicorfile/multicorfile.component';
import { PlanningGroupsComponent } from './planning-groups/planning-groups.component';


// tslint:disable-next-line:variable-name
let init_componet: any = IndexComponent;
if (InitIndex.link === '/startpage') {
  // tslint:disable-next-line:indent
  init_componet = StartComponent;
} else if (InitIndex.link === '/admin') {
  // tslint:disable-next-line:indent
  init_componet = AdminComponent;
} else {
  init_componet = IndexComponent;
}

const routes: Routes = [
  {path: '', component: init_componet, canActivate: [AuthGuard]},
  {path: 'admin', component: AdminComponent, canActivate: [AuthGuard], data: {roles: [Roles.admin], title: 'Admin'}},
  {path: 'startpage', component: StartComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Start'}},
  {path: 'vex', component: VexComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Vex File Upload'}},
  {path: 'clock', component: ClockComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'clock'}},
  {path: 'ctrl', component: CtrlComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Control file configuration'}},
  {path: 'cor', component: CorComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Correlation'}},
  {path: 'multicor', component: MulticorComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Correlation'}},
  {path: 'corfile', component: CorfileComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Correlation display'}},
  {path: 'multicorfile', component: MulticorfileComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Correlation display'}},
  {path: 'chooseexperiment', component: ChooseExperimentComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Choose Experiment'}},
  {path: 'viewexperiment/:expcode/:project/:dateTimeLST/:passType/:experimentType', component: ViewExperimentComponent, canActivate: [AuthGuard], data: {roles: [Roles.user], title: 'Experiment'}},
  {path: 'login', component: LoginComponent, data: {title: 'Login'}},
  {path: 'registration', component: RegisterComponent, data: {roles: [Roles.user, Roles.admin], title: 'Registration'}},
  {path: 'changepassword', component: ChangepasswordComponent, canActivate: [AuthGuard], data: {roles: [Roles.user, Roles.admin], title: 'Change password'}},
  {path: 'forgetyourpassword', component: ForgetyourpasswordComponent, data: {roles: [Roles.user, Roles.admin], title: 'Forget your password'}},
  {path: 'group', component: PlanningComponent, data: {title:  'Planning - create a group'}},
  {path: 'plan-group', component: PlanningGroupsComponent, data: {title:  'Planning - groups'}},
  {path: 'index', component: IndexComponent, data: {title:  'Welcome at ACor system  '}},
  {path: 'check-availability', component: CheckAvailabilityComponent, data: {title:  'Planning - check availability of antennas'}},
  {path: 'obs-copy', component: PlanningUseObsComponent, data: {title:  'Planning - create a copy of observation'}},
  {path: 'plan-home', component: PlanningHomeComponent, data: {title:  'Planning - home'}},
  {path: 'loading', component: LoadingScreenComponent, data: {roles: [Roles.user, Roles.admin], title: 'loading'}},
  {path: '**', component: PageNotFoundComponent, data: {roles: [Roles.user, Roles.admin], title: 'error'}}
];

export const appRoutingModule = RouterModule.forRoot(routes, {});

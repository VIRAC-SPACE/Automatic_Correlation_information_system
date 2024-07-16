import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewExperimentComponent} from './view-experiment.component';

describe('ViewexperimentComponent', () => {
  let component: ViewExperimentComponent;
  let fixture: ComponentFixture<ViewExperimentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewExperimentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewExperimentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

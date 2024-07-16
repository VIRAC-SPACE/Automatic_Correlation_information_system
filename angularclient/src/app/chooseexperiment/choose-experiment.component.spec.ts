import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChooseExperimentComponent} from './choose-experiment.component';

describe('ChooseexperimentComponent', () => {
  let component: ChooseExperimentComponent;
  let fixture: ComponentFixture<ChooseExperimentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChooseExperimentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseExperimentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

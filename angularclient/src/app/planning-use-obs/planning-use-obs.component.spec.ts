import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanningUseObsComponent } from './planning-use-obs.component';

describe('PlanningUseObsComponent', () => {
  let component: PlanningUseObsComponent;
  let fixture: ComponentFixture<PlanningUseObsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanningUseObsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PlanningUseObsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

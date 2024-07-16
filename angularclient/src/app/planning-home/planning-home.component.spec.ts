import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanningHomeComponent } from './planning-home.component';

describe('PlanningHomeComponent', () => {
  let component: PlanningHomeComponent;
  let fixture: ComponentFixture<PlanningHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanningHomeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PlanningHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

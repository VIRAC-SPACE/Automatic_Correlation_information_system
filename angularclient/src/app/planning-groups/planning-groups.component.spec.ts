import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanningGroupsComponent } from './planning-groups.component';

describe('PlanningGroupsComponent', () => {
  let component: PlanningGroupsComponent;
  let fixture: ComponentFixture<PlanningGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanningGroupsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PlanningGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});


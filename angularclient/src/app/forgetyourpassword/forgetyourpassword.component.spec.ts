import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ForgetyourpasswordComponent} from './forgetyourpassword.component';

describe('ForgetyourpasswordComponent', () => {
  let component: ForgetyourpasswordComponent;
  let fixture: ComponentFixture<ForgetyourpasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ForgetyourpasswordComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgetyourpasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

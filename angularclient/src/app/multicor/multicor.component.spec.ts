import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MulticorComponent } from './multicor.component';

describe('MulticorComponent', () => {
  let component: MulticorComponent;
  let fixture: ComponentFixture<MulticorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MulticorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MulticorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

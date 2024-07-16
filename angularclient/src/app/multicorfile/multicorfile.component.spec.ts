import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MulticorfileComponent } from './multicorfile.component';

describe('MulticorfileComponent', () => {
  let component: MulticorfileComponent;
  let fixture: ComponentFixture<MulticorfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MulticorfileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MulticorfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CorComponent} from './cor.component';

describe('CorComponent', () => {
  let component: CorComponent;
  let fixture: ComponentFixture<CorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CorComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CorfileComponent} from './corfile.component';

describe('CorfileComponent', () => {
  let component: CorfileComponent;
  let fixture: ComponentFixture<CorfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CorfileComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CorfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});


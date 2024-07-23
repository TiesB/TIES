import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';

interface LetContext<T> {
  ngLet: T;
}

@Directive({
  selector: '[ngLet]',
  standalone: true,
})
export class LetDirective<T> {
  private letContext: LetContext<T | null> = { ngLet: null };

  constructor(
    viewContainerRef: ViewContainerRef,
    templateRef: TemplateRef<LetContext<T>>
  ) {
    viewContainerRef.createEmbeddedView(templateRef, this.letContext);
  }

  @Input()
  public set ngLet(value: T) {
    this.letContext.ngLet = value;
  }
}

// Generated by Dagger (https://dagger.dev).
package com.example.mvctutorial.di_pre.ex5bind;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import javax.inject.Provider;

@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class Student_Factory implements Factory<Student> {
  private final Provider<String> nameProvider;

  public Student_Factory(Provider<String> nameProvider) {
    this.nameProvider = nameProvider;
  }

  @Override
  public Student get() {
    Student instance = newInstance();
    Student_MembersInjector.injectName(instance, nameProvider.get());
    return instance;
  }

  public static Student_Factory create(Provider<String> nameProvider) {
    return new Student_Factory(nameProvider);
  }

  public static Student newInstance() {
    return new Student();
  }
}
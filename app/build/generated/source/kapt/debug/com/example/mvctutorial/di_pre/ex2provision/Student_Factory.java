// Generated by Dagger (https://dagger.dev).
package com.example.mvctutorial.di_pre.ex2provision;

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
    return newInstance(nameProvider.get());
  }

  public static Student_Factory create(Provider<String> nameProvider) {
    return new Student_Factory(nameProvider);
  }

  public static Student newInstance(String name) {
    return new Student(name);
  }
}
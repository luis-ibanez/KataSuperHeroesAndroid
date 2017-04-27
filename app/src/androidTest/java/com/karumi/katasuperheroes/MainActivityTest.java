/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.presenter.Presenter;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  public static SuperHero ANY_SUPER_HERO = new SuperHero("_","_",false,"_");
  public static SuperHero ANY_AVENGER_SUPER_HERO = new SuperHero("_","_",true,"_");

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test public void doNotShowsEmptyCaseIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

  @Test public void ShowsNameIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withText(ANY_SUPER_HERO.getName())).check(matches(isDisplayed()));
  }

  @Test public void ShowsSuperHeroesNamesIfThereAreSuperHeroes() {
    List<SuperHero> superHeroes = givenThereAreSuperHeroes(10);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override
              public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(withText(superHero.getName()))).check(view, e);
              }
            });
  }

  @Test public void ShowsSuperHeroesBadgeIfThereAreSuperHeroesFromAvengers() {
    List<SuperHero> superHeroes = givenThereAreSuperHeroesWithAvengers(10);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override
              public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(withId(R.id.iv_avengers_badge))).check(view, e);
              }
            });
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private void givenThereAreSuperHeroesWithAvengers() {
    givenThereAreSuperHeroesWithAvengers(1);
  }

  private List<SuperHero> givenThereAreSuperHeroesWithAvengers(int heroesNumber) {
    List<SuperHero> heroes = generateListOfSuperHeroesWithAvengers(heroesNumber);
    when(repository.getAll()).thenReturn(heroes);
    return heroes;
  }

  private List<SuperHero> generateListOfSuperHeroesWithAvengers(int heroesNumber) {
    List<SuperHero> superHeroList = new ArrayList<>();
    if(heroesNumber > 0){
      for (int i  = 0 ; i < heroesNumber ; i ++){
        superHeroList.add(ANY_AVENGER_SUPER_HERO);
      }
    }
    return superHeroList;
  }

  private void givenThereAreSuperHeroes() {
    givenThereAreSuperHeroes(1);
  }

  private List<SuperHero> givenThereAreSuperHeroes(int heroesNumber) {
    List<SuperHero> heroes = generateListOfSuperHeroes(heroesNumber);
    when(repository.getAll()).thenReturn(heroes);
    return heroes;
  }

  private List<SuperHero> generateListOfSuperHeroes(int listSize){
    List<SuperHero> superHeroList = new ArrayList<>();
    if(listSize > 0){
      for (int i  = 0 ; i < listSize ; i ++){
        superHeroList.add(ANY_SUPER_HERO);
      }
    }
    return superHeroList;
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}
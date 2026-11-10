package br.com.cristhferr.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public final class ShoppingScenarioHooks {
  private final AdvantageShoppingActions actions;

  public ShoppingScenarioHooks(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @Before
  public void setUp(Scenario scenario) {
    actions.setUp(scenario);
  }

  @After
  public void tearDown(Scenario scenario) {
    actions.tearDown(scenario);
  }
}

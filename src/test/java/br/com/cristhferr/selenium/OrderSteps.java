package br.com.cristhferr.selenium;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class OrderSteps {
  private final AdvantageShoppingActions actions;

  public OrderSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @When("finalizo uma compra autenticada de três unidades")
  public void placeAuthenticatedOrder() {
    actions.placeAuthenticatedOrder();
  }

  @Then("a confirmação e o histórico devem exibir o pedido realizado")
  public void orderShouldAppearInHistory() {
    actions.orderShouldAppearInHistory();
  }
}

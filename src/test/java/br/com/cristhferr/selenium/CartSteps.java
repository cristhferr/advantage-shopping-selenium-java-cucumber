package br.com.cristhferr.selenium;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class CartSteps {
  private final AdvantageShoppingActions actions;

  public CartSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @When("adiciono duas unidades do alto-falante ao carrinho e inicio o checkout como visitante")
  public void addSpeakerAndStartGuestCheckout() {
    actions.addSpeakerAndStartGuestCheckout();
  }

  @When("tento selecionar quantidade zero para o alto-falante")
  public void attemptZeroQuantity() {
    actions.attemptZeroQuantity();
  }

  @Then("o checkout deve solicitar os dados de login e manter os itens no carrinho")
  public void guestCheckoutShouldRequireLogin() {
    actions.guestCheckoutShouldRequireLogin();
  }

  @Then("a quantidade do produto deve permanecer em um")
  public void productQuantityShouldStayOne() {
    actions.productQuantityShouldStayOne();
  }
}

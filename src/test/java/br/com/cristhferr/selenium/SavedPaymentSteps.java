package br.com.cristhferr.selenium;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class SavedPaymentSteps {
  private final AdvantageShoppingActions actions;

  public SavedPaymentSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @When("salvo um cartão na conta")
  public void saveCardToAccount() {
    actions.saveCardToAccount();
  }

  @When("saio e entro novamente na conta")
  public void signOutAndSignInAgain() {
    actions.signOutAndSignInAgain();
  }

  @When("atualizo os dados do cartão salvo")
  public void updateSavedCard() {
    actions.updateSavedCard();
  }

  @Then("os dados do cartão salvo devem continuar disponíveis")
  public void savedCardShouldRemainAvailable() {
    actions.savedCardShouldRemainAvailable();
  }

  @Then("os dados atualizados do cartão devem continuar disponíveis")
  public void updatedCardShouldRemainAvailable() {
    actions.updatedCardShouldRemainAvailable();
  }
}

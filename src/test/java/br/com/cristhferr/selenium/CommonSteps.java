package br.com.cristhferr.selenium;

import io.cucumber.java.en.Given;

public final class CommonSteps {
  private final AdvantageShoppingActions actions;

  public CommonSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @Given("que acesso a página inicial da loja")
  public void accessStoreHomePage() {
    actions.accessStoreHomePage();
  }

  @Given("preparo credenciais inéditas")
  public void prepareUniqueCredentials() {
    actions.prepareUniqueCredentials();
  }
}

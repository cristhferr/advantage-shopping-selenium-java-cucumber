package br.com.cristhferr.selenium;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class AuthenticationSteps {
  private final AdvantageShoppingActions actions;

  public AuthenticationSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @When("saio da conta")
  public void signOut() {
    actions.signOut();
  }

  @When("entro com a conta criada")
  public void signIn() {
    actions.signIn();
  }

  @When("tento entrar com credenciais que não foram cadastradas")
  public void attemptLoginWithUnknownCredentials() {
    actions.attemptLoginWithUnknownCredentials();
  }

  @Then("a conta criada deve aparecer autenticada")
  public void createdAccountShouldBeAuthenticated() {
    actions.createdAccountShouldBeAuthenticated();
  }

  @Then("o formulário de login deve continuar visível e a sessão permanecer encerrada")
  public void loginShouldRemainRejected() {
    actions.loginShouldRemainRejected();
  }
}

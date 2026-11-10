package br.com.cristhferr.selenium;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class RegistrationSteps {
  private final AdvantageShoppingActions actions;

  public RegistrationSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @When("crio uma conta com perfil completo e ofertas habilitadas")
  public void registerWithFullProfileAndOffers() {
    actions.registerWithFullProfileAndOffers();
  }

  @When("preencho o cadastro com confirmação de senha incorreta")
  public void fillRegistrationWithMismatchedPassword() {
    actions.fillRegistrationWithMismatchedPassword();
  }

  @When("preencho o cadastro sem aceitar os termos")
  public void fillRegistrationWithoutTerms() {
    actions.fillRegistrationWithoutTerms();
  }

  @When("crio uma conta com perfil completo")
  public void registerWithFullProfile() {
    actions.registerWithFullProfile();
  }

  @When("crio uma conta sem preencher o perfil")
  public void registerWithoutProfile() {
    actions.registerWithoutProfile();
  }

  @When("tento cadastrar novamente o mesmo username com outro email")
  public void attemptDuplicateUsernameRegistration() {
    actions.attemptDuplicateUsernameRegistration();
  }

  @Then("o botão de cadastro deve estar desabilitado")
  public void registrationButtonShouldBeDisabled() {
    actions.registrationButtonShouldBeDisabled();
  }

  @Then("a mensagem de username já existente deve aparecer")
  public void duplicateUsernameMessageShouldAppear() {
    actions.duplicateUsernameMessageShouldAppear();
  }
}

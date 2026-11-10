package br.com.cristhferr.selenium;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public final class PaymentSteps {
  private final AdvantageShoppingActions actions;

  public PaymentSteps(AdvantageShoppingActions actions) {
    this.actions = actions;
  }

  @Given("que estou na página de pagamento autenticado")
  public void openAuthenticatedPaymentPage() {
    actions.openAuthenticatedPaymentPage();
  }

  @When("preencho o pagamento com CVV incompleto")
  public void fillPaymentWithIncompleteCvv() {
    actions.fillPaymentWithIncompleteCvv();
  }

  @When("preencho o pagamento com número de cartão inválido")
  public void fillPaymentWithInvalidCard() {
    actions.fillPaymentWithInvalidCard();
  }

  @When("preencho o pagamento sem o nome do titular")
  public void fillPaymentWithoutCardholder() {
    actions.fillPaymentWithoutCardholder();
  }

  @When("preencho o pagamento sem o número do cartão")
  public void fillPaymentWithoutCardNumber() {
    actions.fillPaymentWithoutCardNumber();
  }

  @When("preencho o pagamento com validade do mês anterior")
  public void fillPaymentWithLastMonthExpiration() {
    actions.fillPaymentWithLastMonthExpiration();
  }

  @Then("o pagamento não deve poder ser enviado")
  public void paymentShouldBeDisabled() {
    actions.paymentShouldBeDisabled();
  }

  @Then("o seletor de validade não deve oferecer anos anteriores ao atual")
  public void expirationYearsShouldNotBePast() {
    actions.expirationYearsShouldNotBePast();
  }

  @Then("o pagamento não deve poder ser enviado quando o ano expirado estiver disponível")
  public void expiredPaymentShouldBeDisabledWhenYearExists() {
    actions.expiredPaymentShouldBeDisabledWhenYearExists();
  }
}

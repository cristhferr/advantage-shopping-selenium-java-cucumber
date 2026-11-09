package br.com.cristhferr.selenium;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.UUID;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static br.com.cristhferr.selenium.AdvantageShoppingLocators.*;

public class AdvantageShoppingSteps {
  private static final String DEFAULT_BASE_URL =
      "https://advantageonlineshopping.com/#/";
  private static final String SPEAKER_SHORT_NAME = "HP S9500";
  private static final String SPEAKER_PRODUCT_PATH = "#/product/19";
  private static final DateTimeFormatter SCREENSHOT_TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

  private WebDriver driver;
  private WebDriverWait wait;
  private Credentials credentials;
  private Payment savedPayment;
  private Payment updatedPayment;
  private int purchaseQuantity;
  private ScenarioVideoRecorder videoRecorder;
  private boolean videoStarted;

  @Before
  public void setUp(Scenario scenario) {
    boolean recordVideo = Boolean.parseBoolean(
        System.getProperty("video", "false")
    );
    boolean headless = Boolean.parseBoolean(
        System.getProperty("headless", "true")
    );
    if (recordVideo && headless) {
      throw new IllegalStateException(
          "A gravação requer o Chrome visível. Execute com -Dheadless=false."
      );
    }

    ChromeOptions options = new ChromeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }

    driver = new ChromeDriver(options);
    driver.manage().window().setSize(new Dimension(1280, 880));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
    wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    if (recordVideo) {
      videoRecorder = new ScenarioVideoRecorder(scenario.getName());
    }
  }

  @After
  public void tearDown(Scenario scenario) {
    RuntimeException teardownFailure = null;
    if (driver != null
        && Boolean.parseBoolean(System.getProperty("screenshots", "true"))) {
      try {
        saveScenarioScreenshot(scenario);
      } catch (RuntimeException exception) {
        teardownFailure = exception;
      }
    }
    if (videoRecorder != null && videoStarted) {
      try {
        videoRecorder.stop();
      } catch (RuntimeException exception) {
        if (teardownFailure == null) {
          teardownFailure = exception;
        } else {
          teardownFailure.addSuppressed(exception);
        }
      }
    }
    try {
      if (driver != null) {
        driver.quit();
      }
    } catch (RuntimeException exception) {
      if (teardownFailure == null) {
        teardownFailure = exception;
      } else {
        teardownFailure.addSuppressed(exception);
      }
    } finally {
      videoRecorder = null;
      videoStarted = false;
    }
    if (teardownFailure != null) {
      throw teardownFailure;
    }
  }

  @Given("que acesso a página inicial da loja")
  public void accessStoreHomePage() {
    navigateTo(baseUrl());
    wait.until(webDriver ->
        webDriver.getTitle().contains("Advantage Shopping")
    );
    waitForLoaderToDisappear();
  }

  @Given("preparo credenciais inéditas")
  public void prepareUniqueCredentials() {
    int seed = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
    String username = "sel" + seed;
    credentials = new Credentials(
        username,
        username + "@example.com",
        "Pass" + seed + "a"
    );
  }

  @Given("que estou na página de pagamento autenticado")
  public void openAuthenticatedPaymentPage() {
    accessStoreHomePage();
    prepareUniqueCredentials();
    createAccount(true, false, true);
    signOut();
    signIn();
    openPaymentForSpeaker();
  }

  @When("crio uma conta com perfil completo e ofertas habilitadas")
  public void registerWithFullProfileAndOffers() {
    registerAccount(true, true, true);
  }

  @When("preencho o cadastro com confirmação de senha incorreta")
  public void fillRegistrationWithMismatchedPassword() {
    openRegistrationForm();
    fillRegistrationForm(false, false, false, true);
  }

  @When("preencho o cadastro sem aceitar os termos")
  public void fillRegistrationWithoutTerms() {
    openRegistrationForm();
    fillRegistrationForm(false, false, false, false);
  }

  @When("crio uma conta com perfil completo")
  public void registerWithFullProfile() {
    registerAccount(true, false, true);
  }

  @When("saio da conta")
  public void signOut() {
    click(USER_MENU);
    WebElement signOutButton = wait.until(
        ExpectedConditions.elementToBeClickable(
            SIGN_OUT_BUTTON
        )
    );
    clickElement(signOutButton);
    wait.until(driver ->
        driver.findElement(AUTHENTICATED_USER)
            .getAttribute("class").contains("ng-hide")
    );
  }

  @When("entro com a conta criada")
  public void signIn() {
    openLoginForm();
    type(USERNAME_INPUT, credentials.username());
    type(PASSWORD_INPUT, credentials.password());
    click(SIGN_IN_BUTTON);
    waitForAuthenticatedUser();
  }

  @When("tento entrar com credenciais que não foram cadastradas")
  public void attemptLoginWithUnknownCredentials() {
    click(USER_MENU);
    type(USERNAME_INPUT, credentials.username());
    type(PASSWORD_INPUT, credentials.password());
    click(SIGN_IN_BUTTON);
  }

  @When("crio uma conta sem preencher o perfil")
  public void registerWithoutProfile() {
    registerAccount(false, false, true);
  }

  @When("tento cadastrar novamente o mesmo username com outro email")
  public void attemptDuplicateUsernameRegistration() {
    openRegistrationForm();
    fillRegistrationForm(false, false, true, false);
    clearAndType(
        REGISTRATION_EMAIL_INPUT,
        credentials.username() + "-different@example.com"
    );
    click(REGISTER_BUTTON);
  }

  @When("adiciono duas unidades do alto-falante ao carrinho e inicio o checkout como visitante")
  public void addSpeakerAndStartGuestCheckout() {
    chooseSpeakerAndQuantity(2);
    hover(SHOPPING_CART_LINK);
    click(CHECKOUT_POPUP_BUTTON);
  }

  @When("tento selecionar quantidade zero para o alto-falante")
  public void attemptZeroQuantity() {
    openSpeakerDetails();
    WebElement quantity = waitVisible(QUANTITY_INPUT);
    quantity.sendKeys(Keys.chord(Keys.CONTROL, "a"), "0", Keys.TAB);
  }

  @When("preencho o pagamento com CVV incompleto")
  public void fillPaymentWithIncompleteCvv() {
    fillCardNumber(randomCardNumber());
    fillCvvAndExpiration("12", "2030");
    type(CARDHOLDER_INPUT, "Selenium Tester");
  }

  @When("preencho o pagamento com número de cartão inválido")
  public void fillPaymentWithInvalidCard() {
    clearAndType(CARD_NUMBER_INPUT, "1234");
    fillCvvAndExpiration(randomCvv(), "2030");
    type(CARDHOLDER_INPUT, "Selenium Tester");
  }

  @When("preencho o pagamento sem o nome do titular")
  public void fillPaymentWithoutCardholder() {
    fillCardNumber(randomCardNumber());
    fillCvvAndExpiration(randomCvv(), "2030");
  }

  @When("preencho o pagamento sem o número do cartão")
  public void fillPaymentWithoutCardNumber() {
    fillCvvAndExpiration(randomCvv(), "2030");
    type(CARDHOLDER_INPUT, "Selenium Tester");
  }

  @When("preencho o pagamento com validade do mês anterior")
  public void fillPaymentWithLastMonthExpiration() {
    java.time.LocalDate previousMonth = java.time.LocalDate.now().minusMonths(1);
    String expiredYear = String.valueOf(previousMonth.getYear());
    List<String> availableYears = optionValues(
        EXPIRATION_YEAR_OPTIONS,
        value -> value.matches("\\d{4}")
    );

    if (!availableYears.contains(expiredYear)) {
      return;
    }

    fillCardNumber(randomCardNumber());
    fillCvv(randomCvv());
    select(EXPIRATION_YEAR_SELECT, expiredYear);
    select(
        EXPIRATION_MONTH_SELECT,
        String.format(Locale.ROOT, "%02d", previousMonth.getMonthValue())
    );
    type(CARDHOLDER_INPUT, "Selenium Tester");
  }

  @When("salvo um cartão na conta")
  public void saveCardToAccount() {
    credentials = uniqueCredentials();
    createAccount(true, false, true);
    openAccountPaymentEditor();
    savedPayment = new Payment(
        randomCardNumber(),
        "123",
        "12",
        "2030",
        "Selenium Tester"
    );
    saveMasterCredit(savedPayment);
  }

  @When("saio e entro novamente na conta")
  public void signOutAndSignInAgain() {
    signOut();
    signIn();
    openMyAccount();
  }

  @When("atualizo os dados do cartão salvo")
  public void updateSavedCard() {
    updatedPayment = new Payment(
        nextCardNumber(savedPayment.cardNumber()),
        "456",
        "11",
        "2031",
        "Selenium Updated"
    );
    saveMasterCredit(updatedPayment);
  }

  @When("finalizo uma compra autenticada de três unidades")
  public void placeAuthenticatedOrder() {
    credentials = uniqueCredentials();
    createAccount(true, false, true);
    purchaseQuantity = 3;
    chooseSpeakerAndQuantity(purchaseQuantity);
    hover(SHOPPING_CART_LINK);
    click(CHECKOUT_POPUP_BUTTON);

    assertVisibleText("SHIPPING DETAILS");
    assertVisibleText("Cypress");
    assertVisibleText("Tester");
    assertVisibleText("123 Cypress Lane");
    assertVisibleText("Springfield");
    assertVisibleText("California");
    assertVisibleText("90210");
    assertVisibleText("United States");
    assertCartContains(purchaseQuantity, "$600.00");
    assertCartContains(purchaseQuantity, "$180.00");
    assertCartContains(purchaseQuantity, "$780.00");

    click(NEXT_BUTTON);
    chooseManualPayment();
    fillPaymentDetails(randomCardNumber(), randomCvv());
    assertCartContains(purchaseQuantity, "$780.00");
    click(PAY_NOW_BUTTON);
    assertVisibleText("Thank you for buying with Advantage");
    assertVisibleText("Shipping to");
    assertVisibleText("Cypress Tester");
    assertVisibleText("123 Cypress Lane");
    assertVisibleText("Payment method");
    assertVisibleText("Order Summary");
    assertVisibleText(SPEAKER_SHORT_NAME);
    assertVisibleText("QTY: " + purchaseQuantity);
    assertVisibleText("$780.00");
  }

  @Then("a conta criada deve aparecer autenticada")
  public void createdAccountShouldBeAuthenticated() {
    waitForAuthenticatedUser();
  }

  @Then("o botão de cadastro deve estar desabilitado")
  public void registrationButtonShouldBeDisabled() {
    wait.until(driver ->
        !driver.findElement(REGISTER_BUTTON).isEnabled()
    );
  }

  @Then("o formulário de login deve continuar visível e a sessão permanecer encerrada")
  public void loginShouldRemainRejected() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        SIGN_IN_BUTTON
    ));
    if (!driver.findElement(AUTHENTICATED_USER)
        .getAttribute("class").contains("ng-hide")) {
      throw new AssertionError("A sessão não deveria estar autenticada.");
    }
  }

  @Then("a mensagem de username já existente deve aparecer")
  public void duplicateUsernameMessageShouldAppear() {
    assertVisibleText("User name already exists");
  }

  @Then("o checkout deve solicitar os dados de login e manter os itens no carrinho")
  public void guestCheckoutShouldRequireLogin() {
    assertVisibleText("ORDER PAYMENT");
    waitVisible(ORDER_REGISTRATION_BUTTON);
    waitVisible(ORDER_USERNAME_INPUT);
    waitVisible(ORDER_PASSWORD_INPUT);
    assertCartContains(2, "$400.00");
  }

  @Then("a quantidade do produto deve permanecer em um")
  public void productQuantityShouldStayOne() {
    wait.until(driver ->
        driver.findElement(QUANTITY_INPUT)
            .getAttribute("value").equals("1")
    );
  }

  @Then("o pagamento não deve poder ser enviado")
  public void paymentShouldBeDisabled() {
    wait.until(driver ->
        !driver.findElement(PAY_NOW_BUTTON)
            .isEnabled()
    );
  }

  @Then("o seletor de validade não deve oferecer anos anteriores ao atual")
  public void expirationYearsShouldNotBePast() {
    List<Integer> years = optionValues(
        EXPIRATION_YEAR_OPTIONS,
        value -> value.matches("\\d{4}")
    ).stream()
        .filter(value -> value.matches("\\d{4}"))
        .map(Integer::parseInt)
        .toList();

    if (years.isEmpty()) {
      throw new AssertionError("O seletor não apresentou anos de validade.");
    }
    if (years.stream().anyMatch(year -> year < Year.now().getValue())) {
      throw new AssertionError("O seletor apresentou um ano expirado: " + years);
    }
  }

  @Then("o pagamento não deve poder ser enviado quando o ano expirado estiver disponível")
  public void expiredPaymentShouldBeDisabledWhenYearExists() {
    String expiredYear = String.valueOf(Year.now().getValue() - 1);
    if (!optionValues(
        EXPIRATION_YEAR_OPTIONS,
        value -> value.matches("\\d{4}")
    )
        .contains(expiredYear)) {
      return;
    }
    paymentShouldBeDisabled();
  }

  @Then("os dados do cartão salvo devem continuar disponíveis")
  public void savedCardShouldRemainAvailable() {
    assertSavedPayment(savedPayment);
  }

  @Then("os dados atualizados do cartão devem continuar disponíveis")
  public void updatedCardShouldRemainAvailable() {
    assertSavedPayment(updatedPayment);
  }

  @Then("a confirmação e o histórico devem exibir o pedido realizado")
  public void orderShouldAppearInHistory() {
    click(USER_MENU_ICON);
    click(MY_ORDERS_MENU_ITEM);
    wait.until(ExpectedConditions.urlContains("/MyOrders"));
    assertVisibleText("MY ORDERS");
    String orderHistory = driver.findElement(PAGE_BODY).getText();
    if (orderHistory.contains("- No orders -")) {
      throw new AssertionError(
          "A compra foi confirmada, mas a loja não registrou o pedido em My Orders."
      );
    }
    assertContains(orderHistory, SPEAKER_SHORT_NAME);
    assertContains(orderHistory, String.valueOf(purchaseQuantity));
  }

  private String baseUrl() {
    return System.getProperty("baseUrl", DEFAULT_BASE_URL);
  }

  private void navigateTo(String url) {
    driver.get(url);
    if (videoRecorder != null && !videoStarted) {
      videoRecorder.start();
      videoStarted = true;
    }
  }

  private void saveScenarioScreenshot(Scenario scenario) {
    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    String status = scenario.getStatus().name().toLowerCase(Locale.ROOT);
    String baseName = safeFileName(scenario.getName())
        + "-"
        + status
        + "-"
        + LocalDateTime.now().format(SCREENSHOT_TIMESTAMP)
        + "-"
        + UUID.randomUUID().toString().substring(0, 8);
    Path outputDirectory = Path.of(
        System.getProperty("screenshot.dir", "target/screenshots")
    );
    Path screenshotFile = outputDirectory.resolve(baseName + ".png");

    try {
      Files.createDirectories(outputDirectory);
      Files.write(screenshotFile, screenshot);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Não foi possível salvar o print do cenário em " + screenshotFile + ".",
          exception
      );
    }
    scenario.attach(screenshot, "image/png", baseName);
  }

  private String safeFileName(String value) {
    String safeName = value.replaceAll("[^a-zA-Z0-9._-]+", "-")
        .replaceAll("^-|-$", "");
    return safeName.isBlank() ? "scenario" : safeName;
  }

  private Credentials uniqueCredentials() {
    int seed = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
    String username = "sel" + seed;
    return new Credentials(username, username + "@example.com", "Pass" + seed + "a");
  }

  private void registerAccount(
      boolean includeProfile,
      boolean allowOffers,
      boolean acceptTerms
  ) {
    openRegistrationForm();
    fillRegistrationForm(includeProfile, allowOffers, acceptTerms, false);
    clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(
        REGISTER_BUTTON
    )));
    waitForAuthenticatedUser();
  }

  private void openRegistrationForm() {
    String appUrl = baseUrl().split("#", 2)[0];
    navigateTo(appUrl + "#/register");
    waitVisible(REGISTRATION_USERNAME_INPUT);
  }

  private void fillRegistrationForm(
      boolean includeProfile,
      boolean allowOffers,
      boolean acceptTerms,
      boolean mismatchPassword
  ) {
    type(REGISTRATION_USERNAME_INPUT, credentials.username());
    type(REGISTRATION_EMAIL_BY_NAME, credentials.email());
    type(REGISTRATION_PASSWORD_INPUT, credentials.password());
    String confirmation = mismatchPassword
        ? credentials.password().substring(0, credentials.password().length() - 1) + "b"
        : credentials.password();
    type(REGISTRATION_PASSWORD_CONFIRMATION_INPUT, confirmation);

    if (includeProfile) {
      type(FIRST_NAME_INPUT, "Cypress");
      type(LAST_NAME_INPUT, "Tester");
      type(PHONE_NUMBER_INPUT, "2025550147");
      wait.until(driver -> driver.findElements(
          COUNTRY_OPTIONS
      ).stream().anyMatch(option ->
          "United States".equals(option.getText().trim())
      ));
      new Select(waitVisible(COUNTRY_SELECT))
          .selectByVisibleText("United States");
      type(CITY_INPUT, "Springfield");
      type(ADDRESS_INPUT, "123 Cypress Lane");
      type(STATE_INPUT, "California");
      type(POSTAL_CODE_INPUT, "90210");
    }

    if (allowOffers) {
      check(ALLOW_OFFERS_CHECKBOX);
    }
    if (acceptTerms) {
      check(TERMS_CHECKBOX);
    }
  }

  private void createAccount(
      boolean includeProfile,
      boolean allowOffers,
      boolean acceptTerms
  ) {
    registerAccount(includeProfile, allowOffers, acceptTerms);
    navigateTo(baseUrl());
    waitForAuthenticatedUser();
  }

  private void openLoginForm() {
    if (!isVisible(USERNAME_INPUT)) {
      click(USER_MENU);
    }
    waitVisible(USERNAME_INPUT);
    waitVisible(PASSWORD_INPUT);
  }

  private void waitForAuthenticatedUser() {
    wait.until(ExpectedConditions.textToBePresentInElementLocated(
        USER_MENU,
        credentials.username()
    ));
  }

  private void openSpeakerDetails() {
    String appUrl = baseUrl().split("#", 2)[0];
    navigateTo(appUrl + SPEAKER_PRODUCT_PATH);
    wait.until(ExpectedConditions.urlContains("/product/19"));
    waitVisible(QUANTITY_INPUT);
  }

  private void chooseSpeakerAndQuantity(int quantity) {
    openSpeakerDetails();
    By quantityLocator = QUANTITY_INPUT;
    WebElement quantityInput = waitVisible(quantityLocator);
    String expectedQuantity = String.valueOf(quantity);
    quantityInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), expectedQuantity, Keys.TAB);
    wait.until(webDriver ->
        expectedQuantity.equals(
            webDriver.findElement(quantityLocator).getAttribute("value")
        )
    );
    waitForLoaderToDisappear();
    click(ADD_TO_CART_BUTTON);
    wait.until(ExpectedConditions.textToBePresentInElementLocated(
        CART_QUANTITY,
        expectedQuantity
    ));
  }

  private void openPaymentForSpeaker() {
    chooseSpeakerAndQuantity(1);
    hover(SHOPPING_CART_LINK);
    click(CHECKOUT_POPUP_BUTTON);
    assertVisibleText("SHIPPING DETAILS");
    click(NEXT_BUTTON);
    chooseManualPayment();
  }

  private void chooseManualPayment() {
    List<WebElement> methods = wait.until(
        ExpectedConditions.visibilityOfAllElementsLocatedBy(
            PAYMENT_METHODS
        )
    );
    if (methods.size() < 2) {
      throw new AssertionError("Método de pagamento manual não foi encontrado.");
    }
    clickElement(methods.get(1));
    waitVisible(CARD_NUMBER_INPUT);
  }

  private void fillPaymentDetails(String cardNumber, String cvv) {
    fillCardNumber(cardNumber);
    fillCvvAndExpiration(cvv, "2030");
    type(CARDHOLDER_INPUT, "Cypress Tester");
  }

  private void fillCardNumber(String cardNumber) {
    By locator = CARD_NUMBER_INPUT;
    WebElement input = waitVisible(locator);
    input.clear();
    input.sendKeys(cardNumber);
    wait.until(driver -> normalizeDigits(
        driver.findElement(locator).getAttribute("value")
    ).endsWith(cardNumber.substring(Math.max(0, cardNumber.length() - 4))));
  }

  private void fillCvvAndExpiration(String cvv, String year) {
    select(EXPIRATION_MONTH_SELECT, "12");
    select(EXPIRATION_YEAR_SELECT, year);
    fillCvv(cvv);
  }

  private void fillCvv(String cvv) {
    WebElement input = waitVisible(CVV_INPUT);
    clickElement(input);
    input.clear();
    input.sendKeys(cvv);
    input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    input.sendKeys(cvv);
    wait.until(driver ->
        driver.findElement(CVV_INPUT)
            .getAttribute("value").equals(cvv)
    );
  }

  private void openMyAccount() {
    click(USER_MENU_ICON);
    click(MY_ACCOUNT_MENU_ITEM);
    wait.until(ExpectedConditions.urlContains("/myAccount"));
  }

  private void openAccountPaymentEditor() {
    openMyAccount();
    click(ACCOUNT_PAYMENT_EDIT_LINK);
    wait.until(ExpectedConditions.urlContains("/accountPaymentEdit"));
    click(MASTER_CREDIT_IMAGE);
    waitVisible(CREDIT_CARD_SECTION);
  }

  private void saveMasterCredit(Payment payment) {
    setInputValue(CARD_NUMBER_INPUT, payment.cardNumber());
    select(EXPIRATION_MONTH_SELECT, payment.month());
    select(EXPIRATION_YEAR_SELECT, payment.year());
    clearAndType(
        CARDHOLDER_INPUT,
        payment.cardholder()
    );
    check(SAVE_MASTER_CREDIT_CHECKBOX);
    fillCvv(payment.cvv());
    waitVisible(CVV_INPUT).sendKeys(Keys.TAB);
    wait.until(driver ->
        payment.cvv().equals(
            driver.findElement(CVV_INPUT)
                .getAttribute("value")
        )
    );
    clickFirstVisible(SAVE_BUTTON);
    String expectedTail = payment.cardNumber().substring(
        payment.cardNumber().length() - 4
    );
    String articleText = wait.until(driver ->
        (String) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('article'))" +
                ".filter(element => element.getBoundingClientRect().width)" +
                ".map(element => element.innerText)" +
                ".find(text => text.includes(arguments[0]) && text.includes(arguments[1])) || null;",
            "MasterCredit",
            expectedTail
        )
    );
    assertContains(articleText, "MasterCredit");
    assertContains(articleText, expectedTail);
  }

  private void assertSavedPayment(Payment payment) {
    WebElement article = waitVisible(PAYMENT_ARTICLE);
    assertContains(article.getText(), "MasterCredit");
    assertContains(article.getText(), payment.cardNumber().substring(
        payment.cardNumber().length() - 4
    ));
    click(ACCOUNT_PAYMENT_EDIT_LINK);
    wait.until(ExpectedConditions.urlContains("/accountPaymentEdit"));

    String visibleCard = normalizeDigits(
        waitVisible(CARD_NUMBER_INPUT)
            .getAttribute("value")
    );
    if (!visibleCard.endsWith(payment.cardNumber().substring(
        payment.cardNumber().length() - 4
    ))) {
      throw new AssertionError("Os últimos dígitos do cartão salvo não conferem.");
    }
    assertEquals(
        payment.cvv(),
        waitVisible(CVV_INPUT).getAttribute("value"),
        "CVV salvo"
    );
    assertEquals(
        payment.month(),
        new Select(waitVisible(EXPIRATION_MONTH_SELECT))
            .getFirstSelectedOption().getText().trim(),
        "Mês de validade salvo"
    );
    assertEquals(
        payment.year(),
        new Select(waitVisible(EXPIRATION_YEAR_SELECT))
            .getFirstSelectedOption().getText().trim(),
        "Ano de validade salvo"
    );
    assertEquals(
        payment.cardholder(),
        waitVisible(CARDHOLDER_INPUT)
            .getAttribute("value"),
        "Nome do titular salvo"
    );
  }

  private void assertCartContains(int quantity, String expectedText) {
    WebElement cart = waitVisible(USER_CART);
    String cartText = cart.getText();
    assertContains(cartText, SPEAKER_SHORT_NAME);
    assertContains(cartText, "QTY: " + quantity);
    assertContains(cartText, expectedText);
  }

  private void assertVisibleText(String expectedText) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//*[contains(normalize-space(.), " + xpathLiteral(expectedText) + ")]")
    ));
  }

  private List<String> optionValues(
      By locator,
      Predicate<String> readyCondition
  ) {
    return wait.until(webDriver -> {
      List<String> values = webDriver.findElements(locator).stream()
          .map(option -> {
            String value = option.getAttribute("value");
            String text = option.getText().trim();
            return value != null && value.matches("\\d{4}") ? value : text;
          })
          .toList();
      return values.stream().anyMatch(readyCondition) ? values : null;
    });
  }

  private void select(By locator, String value) {
    Select select = new Select(waitVisible(locator));
    boolean hasValue = select.getOptions().stream()
        .anyMatch(option -> value.equals(option.getAttribute("value")));
    if (hasValue) {
      select.selectByValue(value);
    } else {
      select.selectByVisibleText(value);
    }
  }

  private void check(By locator) {
    WebElement checkbox = waitVisible(locator);
    if (!checkbox.isSelected()) {
      clickElement(checkbox);
    }
  }

  private void type(By locator, String value) {
    waitVisible(locator).sendKeys(value);
  }

  private void clearAndType(By locator, String value) {
    WebElement element = waitVisible(locator);
    element.clear();
    element.sendKeys(value);
  }

  private void setInputValue(By locator, String value) {
    WebElement input = waitVisible(locator);
    ((JavascriptExecutor) driver).executeScript(
        "const input = arguments[0];"
            + "const setter = Object.getOwnPropertyDescriptor("
            + "input.ownerDocument.defaultView.HTMLInputElement.prototype, 'value').set;"
            + "setter.call(input, arguments[1]);"
            + "input.dispatchEvent(new Event('input', { bubbles: true }));"
            + "input.dispatchEvent(new Event('change', { bubbles: true }));",
        input,
        value
    );
  }

  private void click(By locator) {
    clickElement(wait.until(ExpectedConditions.elementToBeClickable(locator)));
  }

  private void clickFirstVisible(By locator) {
    WebElement element = wait.until(webDriver ->
        webDriver.findElements(locator).stream()
            .filter(WebElement::isDisplayed)
            .filter(WebElement::isEnabled)
            .findFirst()
            .orElse(null)
    );
    clickElement(element);
  }

  private void clickElement(WebElement element) {
    waitForLoaderToDisappear();
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});",
        element
    );
    wait.until(ExpectedConditions.elementToBeClickable(element));
    element.click();
    waitForLoaderToDisappear();
  }

  private void hover(By locator) {
    WebElement element = waitVisible(locator);
    new Actions(driver).moveToElement(element).perform();
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        CART_TOOLTIP
    ));
  }

  private WebElement waitVisible(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  private void waitForLoaderToDisappear() {
    wait.until(ExpectedConditions.invisibilityOfElementLocated(
        LOADER
    ));
  }

  private boolean isVisible(By locator) {
    return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
  }

  private String randomCardNumber() {
    return String.valueOf(
        ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L)
    );
  }

  private String nextCardNumber(String current) {
    long next = (Long.parseLong(current.substring(0, 8))
        * 10_000L
        + (Long.parseLong(current.substring(8)) + 1) % 10_000L);
    return String.format(Locale.ROOT, "%012d", next);
  }

  private String randomCvv() {
    return String.valueOf(ThreadLocalRandom.current().nextInt(100, 1_000));
  }

  private String normalizeDigits(String value) {
    return value == null ? "" : value.replaceAll("\\D", "");
  }

  private String xpathLiteral(String value) {
    if (!value.contains("'")) {
      return "'" + value + "'";
    }
    if (!value.contains("\"")) {
      return "\"" + value + "\"";
    }
    return "concat('" + value.replace("'", "',\"'\",'") + "')";
  }

  private void assertContains(String actual, String expected) {
    if (actual == null || !actual.contains(expected)) {
      throw new AssertionError(
          "Texto esperado não encontrado: " + expected + "; texto recebido: " + actual
      );
    }
  }

  private void assertEquals(String expected, String actual, String field) {
    if (!expected.equals(actual)) {
      throw new AssertionError(
          field + " esperado: " + expected + "; recebido: " + actual
      );
    }
  }

  private record Credentials(String username, String email, String password) {
  }

  private record Payment(
      String cardNumber,
      String cvv,
      String month,
      String year,
      String cardholder
  ) {
  }
}

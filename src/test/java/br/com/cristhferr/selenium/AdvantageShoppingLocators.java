package br.com.cristhferr.selenium;

import org.openqa.selenium.By;

final class AdvantageShoppingLocators {
  static final By USER_MENU = By.cssSelector("#menuUserLink");
  static final By SIGN_OUT_BUTTON =
      By.cssSelector("#loginMiniTitle [translate='Sign_out']");
  static final By AUTHENTICATED_USER =
      By.cssSelector("#menuUserLink .hi-user");
  static final By USERNAME_INPUT =
      By.cssSelector("input[name='username']");
  static final By PASSWORD_INPUT =
      By.cssSelector("input[name='password']");
  static final By SIGN_IN_BUTTON = By.cssSelector("#sign_in_btn");
  static final By REGISTRATION_EMAIL_INPUT =
      By.cssSelector("input[name='emailRegisterPage']");
  static final By REGISTER_BUTTON = By.cssSelector("#register_btn");
  static final By SHOPPING_CART_LINK = By.cssSelector("#shoppingCartLink");
  static final By CHECKOUT_POPUP_BUTTON =
      By.cssSelector("#toolTipCart #checkOutPopUp");
  static final By QUANTITY_INPUT =
      By.cssSelector("input[name='quantity']");
  static final By CARDHOLDER_INPUT =
      By.cssSelector("input[name='cardholder_name']");
  static final By CARD_NUMBER_INPUT =
      By.cssSelector("input[name='card_number']");
  static final By EXPIRATION_YEAR_OPTIONS =
      By.cssSelector("[name='yyyyListbox'] option");
  static final By EXPIRATION_YEAR_SELECT = By.name("yyyyListbox");
  static final By EXPIRATION_MONTH_SELECT = By.name("mmListbox");
  static final By NEXT_BUTTON =
      By.xpath("//button[contains(normalize-space(.), 'NEXT')]");
  static final By PAY_NOW_BUTTON =
      By.cssSelector("#pay_now_btn_ManualPayment");
  static final By ORDER_REGISTRATION_BUTTON =
      By.cssSelector("#registration_btn");
  static final By ORDER_USERNAME_INPUT =
      By.cssSelector("input[name='usernameInOrderPayment']");
  static final By ORDER_PASSWORD_INPUT =
      By.cssSelector("input[name='passwordInOrderPayment']");
  static final By USER_MENU_ICON = By.cssSelector("#menuUser");
  static final By MY_ORDERS_MENU_ITEM =
      By.cssSelector("#loginMiniTitle [translate='My_Orders']");
  static final By PAGE_BODY = By.tagName("body");
  static final By REGISTRATION_USERNAME_INPUT =
      By.name("usernameRegisterPage");
  static final By REGISTRATION_EMAIL_BY_NAME =
      By.name("emailRegisterPage");
  static final By REGISTRATION_PASSWORD_INPUT =
      By.name("passwordRegisterPage");
  static final By REGISTRATION_PASSWORD_CONFIRMATION_INPUT =
      By.name("confirm_passwordRegisterPage");
  static final By FIRST_NAME_INPUT = By.name("first_nameRegisterPage");
  static final By LAST_NAME_INPUT = By.name("last_nameRegisterPage");
  static final By PHONE_NUMBER_INPUT = By.name("phone_numberRegisterPage");
  static final By COUNTRY_OPTIONS =
      By.cssSelector("[name='countryListboxRegisterPage'] option");
  static final By COUNTRY_SELECT = By.name("countryListboxRegisterPage");
  static final By CITY_INPUT = By.name("cityRegisterPage");
  static final By ADDRESS_INPUT = By.name("addressRegisterPage");
  static final By STATE_INPUT =
      By.name("state_/_province_/_regionRegisterPage");
  static final By POSTAL_CODE_INPUT = By.name("postal_codeRegisterPage");
  static final By ALLOW_OFFERS_CHECKBOX = By.name("allowOffersPromotion");
  static final By TERMS_CHECKBOX = By.name("i_agree");
  static final By ADD_TO_CART_BUTTON =
      By.xpath("//button[contains(normalize-space(.), 'ADD TO CART')]");
  static final By CART_QUANTITY =
      By.cssSelector("#shoppingCartLink span.cart");
  static final By PAYMENT_METHODS =
      By.cssSelector(".paymentMethods .imgRadioButton");
  static final By CVV_INPUT = By.cssSelector("input[name='cvv_number']");
  static final By MY_ACCOUNT_MENU_ITEM =
      By.cssSelector("#loginMiniTitle > [translate='My_account']");
  static final By ACCOUNT_PAYMENT_EDIT_LINK =
      By.cssSelector("a[href='#/accountPaymentEdit']");
  static final By MASTER_CREDIT_IMAGE =
      By.cssSelector("img[alt='Master credit']");
  static final By CREDIT_CARD_SECTION = By.cssSelector("#creditCard");
  static final By SAVE_MASTER_CREDIT_CHECKBOX =
      By.cssSelector("input[name='save_master_credit']");
  static final By SAVE_BUTTON = By.cssSelector("[id='save_btn']");
  static final By PAYMENT_ARTICLE = By.cssSelector("article");
  static final By USER_CART = By.cssSelector("#userCart");
  static final By CART_TOOLTIP = By.cssSelector("#toolTipCart");
  static final By LOADER = By.cssSelector(".loader");

  private AdvantageShoppingLocators() {
  }
}

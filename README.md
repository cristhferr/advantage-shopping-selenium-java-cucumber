# Selenium Java + Cucumber - Advantage Shopping

[![Cucumber scenarios](https://github.com/cristhferr/advantage-shopping-selenium-java-cucumber/actions/workflows/cucumber-tests.yml/badge.svg?branch=main)](https://github.com/cristhferr/advantage-shopping-selenium-java-cucumber/actions/workflows/cucumber-tests.yml)

Projeto de automação web com Selenium WebDriver, Java 17+, Maven, Cucumber JVM, JUnit Platform e Chrome. Os cenários são escritos em Gherkin nos arquivos `.feature`.

[Repositório no GitHub](https://github.com/cristhferr/advantage-shopping-selenium-java-cucumber)

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.8 ou superior
- Google Chrome instalado
- Acesso à internet para baixar dependências Maven e acessar o site de demonstração

O Selenium Manager resolve o ChromeDriver automaticamente na primeira execução.

> **Atenção:** os cenários acessam uma loja pública e alguns criam contas, salvam cartões de teste ou enviam pedidos. Execute-os sabendo que esses dados podem ser gravados no serviço remoto.

## Executar os testes

Abra o terminal na raiz do projeto e execute:

```powershell
mvn test
```

O Chrome roda em modo headless por padrão. Para abrir a janela do navegador:

```powershell
mvn test -Dheadless=false
```

Para testar outra URL:

```powershell
mvn test "-DbaseUrl=https://advantageonlineshopping.com/#/"
```

### Prints dos cenários

Por padrão, o projeto salva um print PNG ao final de cada cenário, tanto em caso de sucesso quanto de falha. Os arquivos ficam em `target/screenshots`, com o nome do cenário, o status e um identificador exclusivo; também são anexados ao cenário no relatório do Cucumber.

Para desativar a captura:

```powershell
mvn test -Dscreenshots=false
```

Para alterar a pasta de saída:

```powershell
mvn test "-Dscreenshot.dir=target/evidencias"
```

### Gravar um vídeo por cenário (opcional)

A gravação fica desativada por padrão. Para gravar cada cenário em um arquivo MP4, instale o FFmpeg, deixe `ffmpeg.exe` no `PATH` e execute com o Chrome visível:

```powershell
mvn test -Dvideo=true -Dheadless=false
```

Os vídeos são salvos em `target/videos`, com o nome do cenário e um identificador exclusivo. A gravação captura somente a janela do Chrome identificada pelo título configurado; mantenha essa janela aberta e visível durante o teste. Se o FFmpeg não localizar a janela, informe o título exato com `-Dvideo.windowTitle`. Para configurar o título ou caminhos personalizados:

```powershell
mvn test -Dvideo=true -Dheadless=false "-Dvideo.windowTitle=Advantage Shopping" "-Dffmpeg.path=C:\tools\ffmpeg\bin\ffmpeg.exe" "-Dvideo.dir=target/videos"
```

Para não gravar, basta omitir `-Dvideo=true` (ou usar `-Dvideo=false`). A gravação não funciona em modo headless e a execução falha com uma mensagem explícita se for habilitada sem o FFmpeg.

Os cenários ficam em `src/test/resources/features/advantage-shopping.feature`; use as tags `@cadastro`, `@autenticacao`, `@carrinho`, `@checkout`, `@pagamento`, `@conta`, `@compra` e `@validacao` para identificar os fluxos. Para executar, por exemplo, apenas os cenários de pagamento:

```powershell
mvn test "-Dcucumber.filter.tags=@pagamento"
```

Também é possível executar o filtro `@carrinho` pelo VS Code em **Terminal > Run Task... > Cucumber: @carrinho**. A tarefa do workspace está em `.vscode/tasks.json`.

As definições Cucumber estão separadas por domínio em `CommonSteps`, `RegistrationSteps`, `AuthenticationSteps`, `CartSteps`, `PaymentSteps`, `SavedPaymentSteps` e `OrderSteps`. A lógica Selenium compartilhada fica em `AdvantageShoppingActions`, e o ciclo de vida do navegador fica em `ShoppingScenarioHooks`. O PicoContainer mantém essas classes ligadas ao mesmo contexto durante cada cenário; os seletores da interface continuam centralizados em `AdvantageShoppingLocators`.

No VS Code, use a extensão **Test Runner for Java** e execute `RunCucumberTest` no painel Testes. O projeto alinha o JUnit Platform à versão usada pelo runner do VS Code. Se o painel ainda mostrar uma falha de descoberta, atualize a extensão **Extension Pack for Java** e a extensão **Test Runner for Java**; como alternativa, execute `mvn test` no terminal integrado.

### GitHub Actions

O workflow `.github/workflows/cucumber-tests.yml` executa todos os cenários em modo headless em pushes e pull requests para `main`. Também pode ser iniciado manualmente pela aba **Actions** do GitHub, escolhendo **Cucumber scenarios** e clicando em **Run workflow**. Ele executa a suíte `RunCucumberTest`, que seleciona os cenários do recurso `features`, e publica relatórios e prints como artefato por sete dias, tanto em execuções aprovadas quanto em falhas. Os anexos de cenários com erro incluem URL, título e estado do documento para ajudar no diagnóstico. A gravação de vídeo continua desativada e não é suportada pelo workflow atual em Linux.

Os fluxos reproduzem cenários Cypress de cadastro, autenticação, carrinho, pagamento e pedidos. Como dependem de uma aplicação pública, os resultados também podem variar conforme a disponibilidade do site e da conexão de rede.

## Git: fluxo básico

Crie uma branch para cada alteração:

```powershell
git switch -c feature/nome-da-alteracao
```

Depois de editar, revise e registre as mudanças:

```powershell
git status
git add .
git commit -m "Descreve a alteração"
```

Envie a branch e seus commits ao GitHub:

```powershell
git push -u origin feature/nome-da-alteracao
```

O push envia somente commits; alterações ainda não commitadas permanecem locais. Abra um pull request da branch para `main` para que o workflow execute antes da integração.

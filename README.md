# Selenium Java + Cucumber - Advantage Shopping

Projeto de automação web com Selenium WebDriver, Java 17+, Maven, Cucumber JVM, JUnit Platform e Chrome. Os cenários são escritos em Gherkin nos arquivos `.feature`.

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.8 ou superior
- Google Chrome instalado
- Acesso à internet para baixar dependências Maven e acessar o site de demonstração

O Selenium Manager resolve o ChromeDriver automaticamente na primeira execução.

## Executar os testes

No terminal, entre nesta pasta e rode:

```powershell
cd selenium-java
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

Os 16 cenários ficam em `src/test/resources/features/advantage-shopping.feature`; os passos Selenium e os hooks ficam em `src/test/java/br/com/cristhferr/selenium/AdvantageShoppingSteps.java`, e os seletores da interface ficam centralizados em `src/test/java/br/com/cristhferr/selenium/AdvantageShoppingLocators.java`.

No VS Code, use a extensão **Test Runner for Java** e execute `RunCucumberTest` no painel Testes. O projeto alinha o JUnit Platform à versão usada pelo runner do VS Code. Se o painel ainda mostrar uma falha de descoberta, atualize a extensão **Extension Pack for Java** e a extensão **Test Runner for Java**; como alternativa, execute `mvn test` no terminal integrado.

### GitHub Actions

O workflow `.github/workflows/cucumber-tests.yml` executa todos os cenários em modo headless em pushes para `main`. Também pode ser iniciado manualmente pela aba **Actions** do GitHub, escolhendo **Cucumber scenarios** e clicando em **Run workflow**. Ele executa a suíte `RunCucumberTest`, que seleciona os cenários do recurso `features`.

Os fluxos reproduzem os cenários Cypress de cadastro, autenticação, carrinho, pagamento e pedidos. Como usam um site público e criam contas/pedidos remotos, dependem da disponibilidade do site e da conexão de rede e podem alterar dados nesse serviço.

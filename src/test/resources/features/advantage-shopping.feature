# language: pt

Funcionalidade: Fluxos da Advantage Shopping
  Como visitante ou cliente da loja
  Quero navegar, criar conta e realizar compras
  Para validar os principais fluxos de compra

  @cadastro @autenticacao
  Cenário: Criar uma conta e manter a sessão autenticada
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando crio uma conta com perfil completo e ofertas habilitadas
    Então a conta criada deve aparecer autenticada

  @cadastro @validacao
  Cenário: Recusar cadastro quando as senhas não coincidem
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando preencho o cadastro com confirmação de senha incorreta
    Então o botão de cadastro deve estar desabilitado

  @cadastro @validacao
  Cenário: Impedir cadastro sem aceitar os termos de uso
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando preencho o cadastro sem aceitar os termos
    Então o botão de cadastro deve estar desabilitado

  @autenticacao
  Cenário: Entrar novamente após sair da conta
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando crio uma conta com perfil completo
    E saio da conta
    E entro com a conta criada
    Então a conta criada deve aparecer autenticada

  @autenticacao @validacao
  Cenário: Recusar login com credenciais incorretas
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando tento entrar com credenciais que não foram cadastradas
    Então o formulário de login deve continuar visível e a sessão permanecer encerrada

  @cadastro @validacao
  Cenário: Impedir criar novamente uma conta com o mesmo username
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando crio uma conta sem preencher o perfil
    E saio da conta
    E tento cadastrar novamente o mesmo username com outro email
    Então a mensagem de username já existente deve aparecer

  @carrinho @checkout
  Cenário: Iniciar uma compra sem login
    Dado que acesso a página inicial da loja
    Quando adiciono duas unidades do alto-falante ao carrinho e inicio o checkout como visitante
    Então o checkout deve solicitar os dados de login e manter os itens no carrinho

  @carrinho @validacao
  Cenário: Impedir adicionar ao carrinho quantidade igual a zero
    Dado que acesso a página inicial da loja
    Quando tento selecionar quantidade zero para o alto-falante
    Então a quantidade do produto deve permanecer em um

  @pagamento @validacao
  Cenário: Impedir pagamento com CVV incompleto
    Dado que estou na página de pagamento autenticado
    Quando preencho o pagamento com CVV incompleto
    Então o pagamento não deve poder ser enviado

  @pagamento @validacao
  Cenário: Impedir pagamento com número de cartão inválido
    Dado que estou na página de pagamento autenticado
    Quando preencho o pagamento com número de cartão inválido
    Então o pagamento não deve poder ser enviado

  @pagamento @validacao
  Cenário: Impedir pagamento sem o nome do titular
    Dado que estou na página de pagamento autenticado
    Quando preencho o pagamento sem o nome do titular
    Então o pagamento não deve poder ser enviado

  @pagamento @validacao
  Cenário: Não oferecer anos de validade de cartão expirados
    Dado que estou na página de pagamento autenticado
    Então o seletor de validade não deve oferecer anos anteriores ao atual

  @pagamento @validacao
  Cenário: Impedir pagamento sem o número do cartão
    Dado que estou na página de pagamento autenticado
    Quando preencho o pagamento sem o número do cartão
    Então o pagamento não deve poder ser enviado

  @pagamento @validacao
  Cenário: Impedir pagamento com cartão expirado no mês anterior
    Dado que estou na página de pagamento autenticado
    Quando preencho o pagamento com validade do mês anterior
    Então o pagamento não deve poder ser enviado quando o ano expirado estiver disponível

  @conta @pagamento
  Cenário: Manter o cartão salvo após sair, entrar e editar os dados
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando salvo um cartão na conta
    E saio e entro novamente na conta
    Então os dados do cartão salvo devem continuar disponíveis
    Quando atualizo os dados do cartão salvo
    E saio e entro novamente na conta
    Então os dados atualizados do cartão devem continuar disponíveis

  @compra @checkout
  Cenário: Finalizar uma compra autenticada e conferir o pedido
    Dado que acesso a página inicial da loja
    E preparo credenciais inéditas
    Quando finalizo uma compra autenticada de três unidades
    Então a confirmação e o histórico devem exibir o pedido realizado

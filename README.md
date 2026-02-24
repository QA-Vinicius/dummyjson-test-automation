# 🚀 Automação de APIs - DummyJson
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![RESTASSURED](https://img.shields.io/badge/RESTASSURED-green?style=for-the-badge) ![image](https://user-images.githubusercontent.com/108882560/177828797-63612075-002c-49f9-85fd-66c1dc491d63.png)

Este projeto, desenvolvido por [Vinícius Alves Martins](https://www.linkedin.com/in/viniciusalvesmartins/), consiste na implementação de uma suíte de testes automatizados para a API DummyJson (https://dummyjson.com), a fim de analisar o comportamento dos endpoints, validar se está de acordo com a documentação proposta e listar potenciais bugs ou pontos de melhorias para melhor funcionamento.


## 💻 Principais Tecnologias
* **Java 17**
* **Maven**
* **REST Assured**
* **JUnit 5**
* **GitLab CI**

## 🛠️ Como Executar
Para executar este projeto, é necessário atender a alguns requisitos prévios, como a instalação e configuração do Java (versão 17 ou superior) e do Maven (versão 3.11 ou superior).

#### Execução Local 
1. Clonar o projeto na máquina local
2. Na pasta raíz do projeto, executar os comandos
    ```
    mvn clean test
    mvn surefire-report:report 
    ```
3. Validar resultado dos testes na pasta /target

## 🧪 GitLab CI
O arquivo .gitlab-ci.yml foi configurado da seguinte forma:
```plaintext
stages:
  - test

dummyjson-test-job:
  stage: test
  image: maven:3.9.6-eclipse-temurin-17
  script:
    - echo "Executando os cenarios de teste..."
    - mvn -B clean test
    - echo "Gerando relatorio de testes em HTML..."
    - mvn -B surefire-report:report
    - echo "Relatorio HTML gerado."
  artifacts:
    when: always
    expire_in: 2 days
    reports:
      junit: target/surefire-reports/TEST-*.xml
    paths:
      - target/surefire-reports/
      - target/site/
  only:
    - feature/automation-tests
    - merge_requests
    - main
```

Essa configuração assegura:
   - Execução automática a cada push realizado na branch feature/automation-tests, merge request, e merge na branch main.
   - Job configurado com os mesmos comandos utilizados para execução local, com adição da publicação do artifacts na pipeline.

Basta acessar, dentro do projeto no GitLab, a sessão Build → Pipelines → Tests para encontrar o relatório JUnit e Build → Pipelines → Jobs para baixar os artifacts.  

## 🎯 Plano de Testes
O projeto tem como objetivo realizar os testes para os endpoints:
- GET /test
- GET /users 
- GET /products 
- GET /products/{id} 
- POST /products/add 
- POST /auth/login 
- GET /auth/products

A cobertura dos testes inclui cenários positivos e negativos, bem como os cenários de exceção e validação de contrato. Com isso, foi analisado se os endpoints realizam de forma correta a consulta e inserção dos dados, tratamento de erros, bem como o comportamento adequado de acordo com a documentação proposta.

## 🧠 Estratégia de Testes
A automação foi construída contemplando validações como:

**1. Testes funcionais:**
   - Status code
   - Consultas e inserção de dados 
   - Campos obrigatórios
   - Retorno de valores
   - Estrutura do JSON

**2. Testes negativos:**
   - Parâmetros inválidos e/ou inexistentes
   - Campos inválidos
   - Métodos não permitidos
   - Payloads inconsistentes

**3. Testes de autenticação**
   - Autenticação com credenciais corretas
   - Geração de token de acesso
   - Acesso à endpoint protegido (GET /auth/products)
   - Tentativa de autenticação com credenciais inválidas
   - Tentativa de acesso sem usuário e/ou senha
   - Tentativa de acesso com token de acesso expirado ou inválido
   - Tentativa de acesso à endpoint protegido sem token de acesso

**4. Testes de contrato**
   - Validação de campos obrigatórios
   - Tamanho mínimo de listas
   - Estrutura de response

**5. Testes de cobertura adicional (exploratório)**
  - Validação extra de comportamento de busca por parâmetro com os endpoints GET /users/search e GET /products/search (não afetam o escopo principal)

## ⚙️ Estrutura do Projeto
Para atender ao plano de testes elaborado, foi desenhada a seguinte arquitetura para o projeto:

```plaintext
 test
 └── java
     ├── config
     │   └── BaseTest.java
     ├── endpoints
     │   └── ApiPaths.java
     ├── payloads
     │   ├── AuthPayloads.java
     │   └── ProductsAddPayloads.java
     ├── services
     │   └── AuthService.java
     └── tests
         ├── AuthLoginApiTest.java
         ├── AuthProductsApiTest.java
         ├── ProductsApiTest.java
         ├── TestApiTest.java
         └── UsersApiTest.java
```

Essa árvore foi estruturada a fim de facilitar o reuso de classes e métodos, a separação de responsabilidades e, consequentemente, permitir que a automação seja escalável e de fácil manutenção. Segue a lógica instituida para cada pacote:

- **Pacote config:** Armazena a classe BaseTest, instanciando o @BeforeAll com a baseURI da API do dummyjson para utilização das classes de teste.
- **Pacote endpoints:** Armazena os endpoints testados em variáveis, facilitando reuso e legibilidade do código.
- **Pacote payloads:** Armazena classes com métodos para manipulação dos corpos de requisições utilizadas, permitindo adição, alteração e remoção de campos de forma simples fora das classes de teste.
- **Pacote service:** Contém a classe realiza a autenticação fora externa ao teste, para geração do accessToken necessário para os testes do endpoint que requer autenticação.
- **Pacote tests:** Contém as principais classes do projeto, nas quais estão localizados os testes. As classes foram divididas por endpoints para melhor divisão e clareza dos testes. 


## 📊 Relatório dos Testes
Durante a realização dos testes descritos nas sessões anteriores, foram identificados alguns comportamentos relevantes, que devem ser analisados a fim de propor possíveis planos de ação. 

### 🐞 Bug Identificado
#### Endpoint /products/add não faz validação correta de tipo de campo
- **Situação:** Endpoint tem como função realizar a criação de produtos de acordo com os parâmetros enviados na requisição. Porém não está realizando validação para o tipo dos campos, e ao informar um valor não numérico para o campo *price*, não houve nenhum tratamento de erro.

- **Tarefa:** Cenário enviando parâmetros necessários para criação de produto, porém informando no JSON a chave *"price"* com valor *"Number"*.

- **Resultado Atual:** Status code 201 CREATED e produto criado com "price": "Number".

- **Resultado Esperado:** Status code 400 Bad Request e mensagem de erro indicando que o tipo informado não é válido para o campo.

### ✨ Pontos Levantados para Melhorias
#### 1. Validações de ID com divergência entre endpoints /products/{id} e /users/{id}
Foi realizado o mesmo tipo de teste para os dois endpoints, o qual consistia em validar o comportamento ao informar, no campo *id*, um valor não numérico. Com isso, foi constatado que os endpoints retornam tratamentos de erro diferentes para a mesma situação.  

- **/users/{id}:** 
  - Valor enviado para id: "emilia" 
  - Status code retornado: 400 Bad Request
  - Mensagem de erro retornada: "Invalid user id 'emilia'"

  
- **/products/{id}:**
  - Valor enviado para id: "essence"
  - Status code retornado: 404 Not Found
  - Mensagem de erro retornada: "Product with id 'essence' not found"

A recomendação sugerida consiste em padronizar o tratamento de tipo inválido para ambos os endpoints:
   - Status code: 400 Bad Request
   - Template de mensagem de erro: "Invalid user id 'emilia'" ou "Invalid product id 'essence'"

#### 2. Retorno 404 para testes com método não permitido nos endpoints /products/{id} e /users/{id}
Os endpoints /products/{id} e /users/{id} realizam requisições do tipo GET, pois se trata de uma consulta de produtos e usuários, respectivamente. Ao realizar um teste muito comum em APIs, alterando o método da requisição para POST, o status code retornado foi 404 Not Found, enquanto o retorno adequado seria 405 Method Not Allowed.

A recomendação é alterar a regra de negócio dos endpoints em questão para que passe a retornar 405 Method Not Allowed, para que esteja de acordo com a validação de contrato e semântica correta para essa situação.

#### 3. Comportamento de retorno 403 Forbidden descrito na documentação mas não validado em teste.
Para o endpoint /auth/products, a documentação exibe um response com retorno 403 Forbidden e mensagem de erro: "Authentication Problem". No entanto, ao realizar os testes negativos e de exceção para esse endpoint, esse erro não foi exibido.

Foi realizado um teste de tentativa de acesso ao endpoint sem informar o token de acesso, porém essa ação retornou status code 401 Unauthorized e mensagem de erro: "Access Token is required". 

Em análise mais detalhada do endpoint e seu response padrão, foi encontrado, para cada usuário da lista retornada, o atributo "role", podendo ser declarado como "admin", "moderator" ou "user". Foram realizados testes com usuários dos três tipos de "role", especialmente com o "role": "user", visto que tende a ser uma função com menos privilégios e permissões, porém em nenhum dos casos foi possível obter o retorno do status code 403 Forbidden conforme esperado.

Como recomendação, deve se informar, na documentação, qual a regra de negócio do endpoint que reflete o comportamento descrito, para que possa ser replicado em testes.
***

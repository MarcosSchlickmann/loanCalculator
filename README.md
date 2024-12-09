# Teste Prático de Engenharia Backend - Simulador de Crédito

# Simulador de Crédito

## Visão Geral

Este projeto é uma aplicação de Simulador de Crédito construída usando Kotlin e Spring Boot. Ela fornece endpoints para calcular detalhes de empréstimos com base nos parâmetros de entrada fornecidos. A aplicação suporta o manuseio de solicitações de alto volume e cálculos em massa.

## Tecnologias

- **Kotlin**: A linguagem de programação principal usada na aplicação.
- **Spring Boot**: O framework usado para construir a aplicação.
- **Gradle**: A ferramenta de build usada para gerenciar dependências e construir o projeto.
- **Docker**: Usado para containerizar a aplicação.

## Arquitetura

A aplicação segue uma arquitetura de camadas com os seguintes componentes:

- **Controladores**: Responsáveis por lidar com solicitações HTTP e chamar os serviços apropriados.
- **Serviços**: Responsáveis por conter a lógica de negócios da aplicação.
- **DTOs**: Responsáveis por representar os objetos de transferência de dados.
- **Testes**: Contém testes unitários para os serviços da aplicação.

Essa arquitetura ajuda a manter a aplicação organizada e facilita a adição de novos recursos,
seguindo um padrão simples de projeto.

## Executando a Aplicação

### Pré-requisitos

- Docker
- Docker Compose

### Passos

1. Clone o repositório:
    ```sh
    git clone <repository-url>
    cd loanCalculator
    ```

2. Construa e execute a aplicação usando Docker Compose:
    ```sh
    docker-compose up --build
    ```

3. A aplicação estará disponível em `http://localhost:8080`.

## Endpoints

### Calcular Empréstimo

- **URL**: `/api/loan-calculator/calculate`
- **Método**: `POST`
  - **Corpo da Solicitação**:
      ```json
      {
        "loanAmount": 100,
        "birthDate": "13/01/1999",
        "installments": 10
      }
      ```
- **Resposta**:
    ```json
    {
      "totalRepaymentAmount": 102.31,
      "totalInterest": 2.31,
      "monthlyPaymentAmount": 10.23
    }
    ```

### Calcular Empréstimos em Massa

- **URL**: `/api/loan-calculator/bulk-calculate`
- **Método**: `POST`
- **Corpo da Solicitação**:
    ```json
    [
	  {
        "loanAmount": 100,
        "birthDate": "13/01/1999",
        "installments": 10
	  },
	  {
        "loanAmount": 100,
        "birthDate": "13/01/1999",
        "installments": 10
	  },
	  {
        "loanAmount": 100,
        "birthDate": "13/01/1999",
        "installments": 10
	  }
    ]
    ```
- **Resposta**:
    ```json
    [
      {
        "totalRepaymentAmount": 102.31,
        "totalInterest": 2.31,
        "monthlyPaymentAmount": 10.23
      },
      {
        "totalRepaymentAmount": 102.31,
        "totalInterest": 2.31,
        "monthlyPaymentAmount": 10.23
      },
      {
        "totalRepaymentAmount": 102.31,
        "totalInterest": 2.31,
        "monthlyPaymentAmount": 10.23
      }
    ]
    ```

## Estrutura do Projeto

- `src/main/kotlin`: Contém o código principal da aplicação.
- `src/test/kotlin`: Contém os casos de teste da aplicação.
- `docker-compose.yml`: Configuração do Docker Compose para executar a aplicação.
- `build.gradle.kts`: Configuração de build do Gradle.

## Executando Testes

Para executar os testes, use o seguinte comando:
```sh
./gradlew test
```

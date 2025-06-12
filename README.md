-----

## README.md - Guia de Execução do Projeto

Este guia fornecerá as instruções necessárias para configurar e executar a aplicação **MeuEstoque** (tanto o backend quanto o frontend) localmente e como acessá-la publicamente utilizando o Ngrok.

### Pré-requisitos

Antes de iniciar, certifique-se de ter os seguintes softwares instalados em sua máquina:

  * **Java Development Kit (JDK) 17 ou superior**: [Baixar JDK](https://www.oracle.com/java/technologies/downloads/) (recomenda-se a versão LTS, como a 17 ou 21).
  * **Maven**: Geralmente incluído com a maioria das IDEs Java (IntelliJ IDEA, Eclipse, VS Code com extensões Java). Se não, [instale o Maven](https://maven.apache.org/download.cgi).
  * **Git**: Para clonar o repositório. [Baixar Git](https://git-scm.com/downloads).
  * **Ngrok**: Ferramenta para criar um túnel seguro para sua aplicação local. [Baixar Ngrok](https://ngrok.com/download).

-----

### 1\. Clonar o Repositório

Abra seu terminal ou prompt de comando e execute o seguinte comando para clonar o projeto:

```bash
git clone https://github.com/gu1334/MeuEstoque.git
cd MeuEstoque # Navegue para o diretório do projeto
```

-----

### 2\. Configurar o Ngrok

O Ngrok é essencial para que a aplicação rodando em sua máquina possa ser acessada via uma URL pública temporária.

1.  **Crie uma Conta Ngrok (se ainda não tiver):**

      * Acesse [https://ngrok.com/signup](https://ngrok.com/signup) e crie uma conta gratuita.
      * Após o cadastro, faça login no seu Dashboard.

2.  **Autentique o Ngrok em sua Máquina:**

      * No Dashboard do Ngrok, procure pela seção "Connect your account". Você verá um comando parecido com:
        ```bash
        ngrok config add-authtoken <SEU_TOKEN_DE_AUTENTICACAO>
        ```
      * Copie esse comando e execute-o em seu terminal. Isso configurará o Ngrok localmente.

-----

### 3\. Executar o Backend (Spring Boot)

O backend é a API que o frontend (HTML/CSS/JS) se comunica.

1.  **Navegue até o Diretório do Projeto:**
    Se você já clonou o repositório, certifique-se de estar na raiz do diretório do projeto (onde está o `pom.xml`).

2.  **Compile e Execute a Aplicação Spring Boot:**
    No terminal, execute o seguinte comando Maven:

    ```bash
    ./mvnw spring-boot:run
    ```

      * **Observação para Windows:** Se você estiver no Windows e tiver problemas com `./mvnw`, tente `mvnw.cmd spring-boot:run`.
      * A aplicação será iniciada e, por padrão, estará acessível em `http://localhost:8080`.
      * Aguarde até ver uma mensagem como "Started MeuEstoqueApplication in X.XXX seconds (JVM running for Y.YYY)" indicando que o servidor está de pé.

-----

### 4\. Abrir um Túnel Ngrok para o Backend

Com o backend rodando em `http://localhost:8080`, vamos criar um túnel público:

1.  **Abra um NOVO terminal ou prompt de comando.**

      * **Importante:** Não feche o terminal onde o Spring Boot está rodando.

2.  **Inicie o túnel Ngrok:**
    Execute o comando:

    ```bash
    ngrok http 8080
    ```

      * Se seu Spring Boot estiver rodando em uma porta diferente de 8080, substitua `8080` pela porta correta (ex: `ngrok http 8081`).

3.  **Obtenha a URL Pública:**
    O Ngrok exibirá informações no terminal. Procure pela linha que começa com `Forwarding`. Ela terá uma URL `https` que se parece com:

    ```
    Forwarding                    https://<alguns_caracteres_aleatorios>.ngrok-free.app -> http://localhost:8080
    ```

    **Esta é a URL pública temporária do seu backend.** Anote-a, pois você precisará dela.

-----

### 5\. Acessar o Frontend

O frontend da aplicação (`index.html`) já está configurado para fazer requisições para os endpoints do backend. Como o Spring Boot serve o `index.html` e o Ngrok está redirecionando o tráfego para ele, você pode acessar o sistema diretamente pela URL do Ngrok.

1.  **Abra seu navegador** (Chrome, Firefox, Edge, etc.).
2.  **Cole a URL HTTPS do Ngrok** que você obteve no passo anterior (ex: `https://<alguns_caracteres_aleatorios>.ngrok-free.app`) e pressione Enter.

Você deverá ver a interface do sistema **MeuEstoque** no navegador, e todas as suas interações (adicionar, listar, registrar saída/entrada, editar, excluir) estarão se comunicando com o backend rodando em sua máquina, através do túnel Ngrok.

-----

### Considerações Finais

  * **Sessão Ngrok:** A URL do Ngrok (na versão gratuita) é temporária. Se você fechar o terminal do Ngrok e iniciá-lo novamente, uma nova URL será gerada. Mantenha o terminal do Ngrok aberto enquanto estiver demonstrando o sistema.
  * **Aplicação Spring Boot:** O terminal onde a aplicação Spring Boot está rodando também precisa permanecer aberto durante a demonstração.
  * **Dados:** A base de dados H2 está configurada para ser em memória (`jdbc:h2:mem:estoquedb`). Isso significa que os dados são perdidos cada vez que a aplicação Spring Boot é reiniciada.

-----



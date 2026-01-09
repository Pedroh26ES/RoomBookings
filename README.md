# RoomBookings - Sistema de Gestão de Salas

[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=18830993&assignment_repo_type=AssignmentRepo)

O **RoomBookings** é um sistema desktop desenvolvido em Java para o gerenciamento e reserva de salas corporativas. A aplicação permite o cadastro de clientes, gestão de diferentes tipos de salas (Standard, Premium, VIP) e recursos (como projetores e Wi-Fi), além da emissão de relatórios e controlo financeiro das reservas.

## 🚀 Funcionalidades

O sistema possui uma interface gráfica intuitiva construída com Swing e oferece as seguintes funcionalidades principais:

* **Gestão de Clientes:** Cadastro e manutenção de clientes (pessoas singulares ou corporativas).
* **Gestão de Salas:** Cadastro de salas com definição de tipo (Standard, Premium, VIP) e capacidade.
* **Gestão de Reservas:** Criação de novas reservas, listagem e visualização de status (Ativa, Cancelada, Concluída).
* **Recursos:** Associação de recursos específicos (ex: Ar-condicionado, Videoconferência) a cada sala.
* **Relatórios:** Módulo dedicado para geração de relatórios administrativos.

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 11
* **Gerenciamento de Dependências:** Maven
* **Interface Gráfica:** Java Swing (AWT/Swing)
* **Base de Dados:** MySQL 8.0 / SQL Server (via JDBC)

## 🗄️ Estrutura da Base de Dados

O sistema utiliza um banco de dados relacional para persistir as informações. As principais tabelas incluem:

* `clientes`: Armazena dados pessoais e tipo de cliente (corporativo ou não).
* `salas` & `tipo_sala`: Define as salas físicas e as suas categorias de preço.
* `reservas`: Regista os agendamentos, datas, custos calculados e status.
* `recursos` & `sala_recursos`: Gere os equipamentos disponíveis em cada sala.

## ⚙️ Como Executar o Projeto

### Pré-requisitos
* Java JDK 11 instalado.
* Maven instalado.
* MySQL Server em execução.

### Passo a Passo

1.  **Configurar a Base de Dados:**
    Execute o script SQL localizado em `database/scripts/scriptMYSQL.sql` no seu servidor MySQL para criar as tabelas e inserir os dados iniciais.

2.  **Compilar o Projeto:**
    Na raiz do projeto, execute o comando Maven:
    ```bash
    mvn clean install
    ```

3.  **Executar a Aplicação:**
    Após a compilação, execute a classe principal `view.MainView`.

## 👥 Responsável pelo Projeto

* **Pedro**

---
*Projeto pessoal para praticar, espero que goste :).*
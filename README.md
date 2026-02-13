# 🏥 Gerenciador de Exames e Certificados Ocupacionais

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-Interface-blue?style=for-the-badge&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven)

**Sistema completo para gestão de exames ocupacionais, certificados e controle de vencimentos**

[Sobre](#-sobre) •
[Funcionalidades](#-funcionalidades) •
[Regras de Negócio](#-regras-de-negócio) •
[Tecnologias](#-tecnologias) •
[Instalação](#-instalação) •
[Como Usar](#-como-usar) •
[Licença](#-licença)

</div>

---

## 📋 Sobre

O **Gerenciador de Exames e Certificados Ocupacionais** é uma aplicação desktop desenvolvida em Java com interface JavaFX, projetada para facilitar o controle e gestão de exames médicos ocupacionais e certificados de funcionários. O sistema permite gerenciar prazos, emitir alertas de vencimento e manter um histórico completo de todos os registros.

### 🎯 Objetivo

Proporcionar uma solução eficiente para empresas controlarem os exames ocupacionais obrigatórios de seus funcionários, evitando vencimentos e garantindo conformidade com as normas de segurança do trabalho.

---

## ✨ Funcionalidades

- **👥 Gestão de Funcionários**
  - Cadastro completo de funcionários
  - Atualização de informações
  - Inativação de funcionários

- **🏢 Gestão de Setores**
  - Cadastro de setores/departamentos
  - Vinculação com funcionários

- **🔬 Gestão de Exames e Certificados 📜**
  - Cadastro de tipos de exames e de tipos de certificados
  - Periodicidade de realização
  - Requisitos por setor
  - Histórico de exames realizados
  - Controle de validade
  - Rastreabilidade completa
 
- **🔍 Particularidades**
  - Cadastro de Particularidades
  - Vinculo de funcionário com particularidades

- **⏰ Controle de Prazos e Alertas de Vencimento 🔔**
  - Dashboard de vencimentos
  - Agendamento automatizado
  - Pendências automáticas
  - Lançamento facilitado

- **📊 Relatórios**
  - Relatórios gerenciais
  - Exportação para PDF/Excel

---

## 🧠 Regras de Negócio

- Um funcionário pode possuir múltiplos tipos de exames obrigatórios
- A obrigatoriedade depende:
  - do setor
  - da idade
  - da periodicidade definida
- A validade é calculada automaticamente com base na regra aplicável
- Particularidades podem sobrescrever regras gerais

---
## 🖼️ Screenshots

---

### 🏠 Telas Principais

<div align="center">
  <img src="https://github.com/user-attachments/assets/f1abc771-5cbc-44f5-94cf-9c5ec395e9cb" width="48%" />
  <img src="https://github.com/user-attachments/assets/489ad528-bf9d-4a12-ac71-2790d449e34a" width="48%" />
</div>

---

### 🏢 Setores

<div align="center">
  <img src="https://github.com/user-attachments/assets/531fd36f-eb56-4e3a-a134-58b70898a309" width="45%" />
  <img src="https://github.com/user-attachments/assets/1d4aea5e-6e76-4cde-b6ca-c0281453092e" width="45%" />
</div>

---

### 👤 Funcionários

<div align="center">
  <img src="https://github.com/user-attachments/assets/3b404a00-7e38-4a7b-be3e-ee22ca1864b9" width="45%" />
  <img src="https://github.com/user-attachments/assets/baa6b1fd-c841-4b29-9da0-69b64dddb832" width="45%" />
</div>

---

### 🔍 Particularidades

<div align="center">
  <img src="https://github.com/user-attachments/assets/13629e07-90bc-433e-9833-6fe03527ae28" width="45%" />
  <img src="https://github.com/user-attachments/assets/2ab1e019-41c1-4874-b75b-9afafa3cc94c" width="45%" />
</div>

<br>

<div align="center">
  <img src="https://github.com/user-attachments/assets/b796a879-46d2-4835-a53c-465ec1d022d4" width="45%" />
  <img src="https://github.com/user-attachments/assets/577e7e08-6e35-48ca-9b20-7249c8013856" width="45%" />
</div>

---

### 🔬 Exames

<div align="center">
  <img src="https://github.com/user-attachments/assets/8ef0a659-48be-4365-84f2-f7e141b3510d" width="45%" />
  <img src="https://github.com/user-attachments/assets/3f767e8a-263e-4e3c-8daf-bb67f3e31139" width="45%" />
</div>

<br>

<div align="center">
  <img src="https://github.com/user-attachments/assets/b187318e-d1bb-40fa-91c2-fab07a1f1754" width="45%" />
  <img src="https://github.com/user-attachments/assets/ab44a79a-698b-4809-bf7a-709ed6e237ff" width="45%" />
</div>

---

### 📜 Certificados

<div align="center">
  <img src="https://github.com/user-attachments/assets/4e4ef52a-a880-4443-9e99-5df41a31c121" width="45%" />
  <img src="https://github.com/user-attachments/assets/dc645881-ebb3-4929-8530-18196c20fb95" width="45%" />
</div>

<br>

<div align="center">
  <img src="https://github.com/user-attachments/assets/458a15ba-e33e-4b71-9c7f-abb0200d2c59" width="45%" />
</div>

---

### 📊 Relatórios

<div align="center">
  <img src="https://github.com/user-attachments/assets/f2182e7c-a501-4713-99ca-ac2229c4d399" width="48%" />
  <img src="https://github.com/user-attachments/assets/85844eb7-5dfa-4135-b088-523ed51e621a" width="48%" />
</div>

---


## 🛠️ Tecnologias

### Backend
- **Java 21** - Linguagem de programação principal
- **Maven** - Gerenciamento de dependências e build
- **SQLite** - Banco de dados local
- **JDBC** - Conexão com banco de dados

### Frontend
- **JavaFX** - Framework para interface gráfica
- **FXML** - Arquitetura MVC para layouts

### Arquitetura
- **MVC (Model-View-Controller)** - Padrão arquitetural
- **DAO (Data Access Object)** - Padrão de acesso a dados
- **Repository Pattern** - Abstração de persistência

---

## 📦 Instalação e Boilerplate

### Pré-requisitos

- **Java JDK 21** ou superior
- **Maven 3.8+**
- **Git**

### Passo a Passo

1. **Clone o repositório**
```bash
git clone https://github.com/JoaoNardi/GerenciadorDeExamesOcupacionais.git
cd GerenciadorDeExamesOcupacionais
```

2. **Compile e baixe as dependências**
```bash
mvn clean install
```
> Ao fazer mvn clean install será compilado e gerado um arquivo executavel (target>dist) para instalação da aplicação na máquina


3. **Execute o projeto**
```bash
mvn javafx:run
```

### Executável (Alternativa)

Para usuários finais, há um executável pré-compilado disponível:

1. Baixe o arquivo `GerenciadorOcupacional-1.0.exe`
2. Execute diretamente no Windows
3. Nenhuma instalação adicional necessária

---

## 💻 Como Usar

### Primeiro Acesso

1. **Configuração Inicial**
   - Configure os setores da empresa
   - Cadastre os tipos de exames e de certificados necessários
   - Defina as regras de períodos de validade

2. **Regras de Condições de Tipos Exames**
   - Preencha o nome do tipo exame
   - Clique em `Adicionar Regra` e escolha uma periodicidade
   - Clique em `Adicionar Condições` e defina a regra para aquela periodicidade selecionada
   > OBS: Funcionários que contemplarem as regras terão obrigatoriedade para aquele Tipo Exame  
   > Dica: Coloque apenas um setor por Periodicidade  
   > Dicas: Evite sobrepor intervalos nas condições por idade  
  
3. **Cadastro de Funcionários**
   - Acesse o módulo de Funcionários
   - Preencha as informações pessoais
   - Vincule ao setor correspondente

4. **Registro de Exames**
   - Selecione o funcionário
   - Escolha o tipo de exame
   - Informe a data de realização e validade será preenchida automaticamente com base na regra cadastrada

5. **Emissão de Certificados**
   - Vincule certificados aos exames realizados
   - Anexe documentos comprobatórios
   - Acompanhe o status de validade

### Navegação
```
📁 Módulos Principais
├── 👥 Funcionários     → Gestão de colaboradores
├── 🏢 Setores          → Organização departamental
├── 🔍 Particularidades → Regras Específicas 
├── 🔬 Exames           → Tipos e histórico de exames
├── 📜 Certificados     → Tipos e histórico de certificados
├── 📊 Relatórios       → Relatórios de funcionário por tipos
└── 🆘 Ajuda            → Ajudas/Bugs/Dúvidas
```

---

## 📁 Estrutura do Projeto
```
GerenciadorDeExamesOcupacionais/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/gerenciador/
│       │       ├── controller/     # Controladores JavaFX
│       │       ├── model/          # Entidades do domínio
│       │       ├── dao/            # Acesso a dados
│       │       ├── service/        # Lógica de negócio
│       │       ├── util/           # Utilitários
│       │       └── Main.java       # Classe principal
│       └── resources/
│           ├── fxml/               # Arquivos de layout
│           └── database/           # Banco de dados SQLite
├── icons/                          # Ícones da aplicação
├── .idea/                          # Configurações IntelliJ
├── pom.xml                         # Configuração Maven
├── GerenciadorOcupacional-1.0.exe  # Executável
└── README.md
```

---

## 🗃️ Banco de Dados

O sistema utiliza **SQLite** como banco de dados local, garantindo:
- ✅ Portabilidade
- ✅ Não requer servidor
- ✅ Arquivo único de dados
- ✅ Backup simplificado

### Principais Tabelas

- `funcionarios` - Dados dos colaboradores
- `setores` - Departamentos da empresa
- `exames` - Tipos e histórico de exames
- `certificados` - Certificações ocupacionais
- `tipos_certificado` - Controle de validade
- `tipos_exame` - Controle de validade em conjunto com a tabela `conjuntos`
- `conjuntos` - Agrupador de regras de tipo de exame em conjunto com a tabela `condicoes`
- `condicoes` - Regras de validade que são iteradas sobre as caracteristicas dos funcionários
- `particularidades` - Regras únicas por funcionários
- `vinculos_particularidades` - Correlaciona particularidades para cada funcionário

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. Abra um **Pull Request**

## 🐛 Reportar Problemas

Encontrou um bug? Tem uma sugestão? 

1. Verifique se já não existe uma [issue aberta](https://github.com/JoaoNardi/GerenciadorDeExamesOcupacionais/issues)
2. Abra uma nova issue com:
   - Descrição clara do problema
   - Passos para reproduzir
   - Comportamento esperado vs atual
   - Screenshots/clip (se aplicável)

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
```
MIT License

Copyright (c) 2024 João Vitor Nardi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```

---

## 👨‍💻 Autor

**João Vitor Nardi**

- GitHub: [@JoaoNardi](https://github.com/JoaoNardi)
- LinkedIn: [João Vitor Nardi](https://linkedin.com/in/joao-nardi)

---

## 📞 Suporte

Para questões e suporte:
- 📧 Abra uma [issue](https://github.com/JoaoNardi/GerenciadorDeExamesOcupacionais/issues)
- 💬 Discussões no GitHub

---

<div align="center">

**⭐ Se este projeto foi útil, considere dar uma estrela! ⭐**

Desenvolvido por [João Vitor Nardi](https://github.com/JoaoNardi)

</div>

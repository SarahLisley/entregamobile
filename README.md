# NutriLivre Android - App de Receitas com IA

## 📱 Visão Geral

O NutriLivre é um aplicativo Android moderno que permite aos usuários criar, gerenciar e compartilhar receitas com a ajuda de inteligência artificial. O app integra tecnologias avançadas como Google Gemini AI, Cloudflare Workers e serviços em nuvem para oferecer uma experiência única de culinária.

## ✨ Funcionalidades Principais

### 🗣️ Chat com IA
- **Chef Gemini**: Assistente culinário personalizado
- **Conversação natural**: Descreva o que quer cozinhar
- **Geração automática**: Receitas criadas a partir da conversa
- **Análise nutricional**: Informações detalhadas dos ingredientes

### 🖼️ Geração de Imagens com IA
- **Imagens únicas**: Cada receita tem sua própria imagem gerada por IA
- **Cloudflare Workers**: Processamento rápido e eficiente
- **Supabase Storage**: Armazenamento seguro das imagens
- **Compatibilidade total**: URLs otimizadas para Android

### 📊 Gestão de Receitas
- **Feed personalizado**: Receitas recomendadas para você
- **Busca inteligente**: Encontre receitas por nome ou ingredientes
- **Favoritos**: Marque suas receitas preferidas
- **Edição completa**: Modifique receitas existentes

### 🔄 Sincronização em Nuvem
- **Firebase**: Sincronização em tempo real
- **Supabase**: Armazenamento de imagens
- **Modo offline**: Funciona sem internet
- **Backup automático**: Dados sempre seguros

## 🏗️ Arquitetura

### Módulos do Projeto
```
entregamobile/
├── app/                    # Módulo principal do app
├── core-data/             # Camada de dados e serviços
├── core-ui/               # Componentes de UI reutilizáveis
└── feature-receitas/      # Funcionalidades específicas de receitas
```

### Tecnologias Utilizadas
- **Kotlin**: Linguagem principal
- **Jetpack Compose**: UI moderna e declarativa
- **Room Database**: Banco de dados local
- **Firebase**: Autenticação e sincronização
- **Supabase**: Armazenamento de imagens
- **Google Gemini AI**: Inteligência artificial
- **Cloudflare Workers**: Processamento de imagens

## 🚀 Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 11 ou superior
- Android SDK API 26+
- Conta Google para Firebase
- Conta Supabase para armazenamento

### Configuração
1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/nutrilivre-android.git
   cd nutrilivre-android
   ```

2. **Configure as credenciais**
   - Adicione `google-services.json` na pasta `app/`
   - Configure as credenciais no arquivo `local.properties`:
   ```properties
   SUPABASE_URL=https://zfbkkrtpnoteapbxfuos.supabase.co
   SUPABASE_KEY=sua-chave-anonima-aqui
   GEMINI_API_KEY=sua-chave-gemini-aqui
   ```

3. **Execute o projeto**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📱 Telas Principais

### 🏠 Tela Inicial
- Feed de receitas recomendadas
- Receitas em destaque
- Navegação rápida para funcionalidades

### 💬 Chat com IA
- Conversação com Chef Gemini
- Geração de receitas personalizadas
- Análise nutricional automática

### 🔍 Busca
- Busca por nome de receita
- Filtro por ingredientes
- Resultados em tempo real

### ⭐ Favoritos
- Receitas marcadas como favoritas
- Organização personalizada
- Acesso rápido

### �� Perfil
- Configurações do usuário
- Preferências de tema
- Gerenciamento de conta

## 🔧 Configurações

### Firebase
```json
{
  "project_id": "appsdisciplinamobile",
  "api_key": "AIzaSyB6iUgScQHXyYoZ_EL0kkpX2IuiunfKz0w"
}
```

### Supabase
```kotlin
private const val SUPABASE_URL = "https://zfbkkrtpnoteapbxfuos.supabase.co"
private const val SUPABASE_KEY = "sua-chave-anonima"
private const val BUCKET = "receitas"
```

### Cloudflare Worker (Serviço Externo)
```
URL: https://text-to-image-template.izaelnunesred.workers.dev
Status: ✅ Funcionando em produção
Serviço: Gerador de imagens com IA
```

## 🧪 Testes

### Teste de Geração de Imagens
1. Abra o app
2. Clique no ícone de imagem na barra superior
3. Digite o nome de uma receita
4. Clique em "🚀 Testar"
5. Verifique os logs no Android Studio

### Logs Esperados
```
🧪 INICIANDO TESTE DE GERAÇÃO DE IMAGEM
Receita de teste: Bolo de Chocolate
✅ IMAGEM GERADA COM SUCESSO!
URL da imagem: https://zfbkkrtpnoteapbxfuos.supabase.co/storage/v1/object/public/receitas/...
✅ TESTE CONCLUÍDO!
```

## 📊 Métricas de Performance

- **Tempo de carregamento**: ~2-3 segundos
- **Geração de imagens**: ~10-15 segundos
- **Sincronização**: A cada 15 minutos
- **Cache de imagens**: Automático via Coil
- **Tamanho do app**: ~15MB

## 🔒 Segurança

- **Autenticação**: Firebase Auth
- **Dados sensíveis**: Criptografados localmente
- **Upload de imagens**: Controlado pelo Worker
- **URLs públicas**: Apenas para leitura
- **Backup**: Automático na nuvem

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 📚 Documentação Adicional

Para informações mais detalhadas sobre APIs e serviços, consulte:
- [APIs e Serviços](docs/APIS_SERVICOS.md) - Documentação completa das APIs
- [Gerador de Imagens](docs/GERADOR_IMAGENS.md) - Detalhes sobre geração de imagens com IA

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/seu-usuario/nutrilivre-android/issues)
- **Documentação**: [Wiki do Projeto](https://github.com/seu-usuario/nutrilivre-android/wiki)
- **Email**: suporte@nutrilivre.com

---

**Desenvolvido com ❤️ pela equipe NutriLivre** 
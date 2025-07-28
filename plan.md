Problemas Críticos (Bloqueadores e Segurança)
Estes são os problemas mais urgentes que precisam ser resolvidos.
1. Inconsistência na Injeção de Dependência (Hilt)
Este é o problema mais grave e provavelmente impede o projeto de compilar corretamente.
Problema: O Hilt está configurado de forma inconsistente.
Os módulos core-ui e feature-receitas têm o plugin e as dependências do Hilt habilitados em seus build.gradle.kts.
O módulo app e core-data têm as dependências do Hilt comentadas.
Vários arquivos (MainActivity.kt, NutriLivreApplication.kt) têm anotações do Hilt como @AndroidEntryPoint e @Inject comentadas, e em vez disso, fazem a inicialização manual de dependências.
Impacto: O projeto não pode ser construído de forma confiável. Ou o Hilt falhará por não encontrar dependências, ou o código falhará por tentar usar dependências que não foram injetadas.
Solução (Escolha UMA):
Opção A (Recomendado): Usar Hilt consistentemente.
Descomente as dependências do Hilt em app/build.gradle.kts e core-data/build.gradle.kts.
Descomente as anotações @HiltAndroidApp em NutriLivreApplication.kt e @AndroidEntryPoint em MainActivity.kt.
Remova a inicialização manual de dependências (como ReceitasRepository, ViewModelFactory) e use @Inject onde for necessário.
Opção B: Remover Hilt completamente.
Remova o plugin e as dependências do Hilt de todos os arquivos build.gradle.kts.
Continue usando o padrão de ViewModelFactory e inicialização manual, mas garanta que seja feito de forma limpa e consistente. Problemas de Arquitetura e Boas Práticas
3. Duplicação de Código e Refatoração Incompleta
Problema: Existem classes duplicadas ou com responsabilidades sobrepostas entre o módulo app e o módulo core-data.
app/src/main/java/com/example/myapplication/data/GeminiService.kt é quase uma cópia exata de core-data/src/main/java/com/example/myapplication/core/data/network/GeminiServiceImpl.kt.
app/src/main/java/com/example/myapplication/data/ConnectivityObserver.kt é uma versão simplificada do core-data/src/main/java/com/example/myapplication/core/data/network/ConnectivityObserver.kt.
Arquivos como GeminiChatService.kt e GeminiNutritionService.kt no módulo app parecem ser resquícios de uma arquitetura anterior e apenas delegam chamadas, adicionando complexidade desnecessária.
Solução:
Delete a pasta app/src/main/java/com/example/myapplication/data.
Faça com que todos os componentes no módulo app (e outros módulos) usem as implementações fornecidas pelo core-data. Por exemplo, use GeminiServiceImpl e ConnectivityObserver do core-data em todos os lugares.
4. Documentação Redundante e Conflitante
Problema: Existem múltiplos arquivos de documentação que se sobrepõem: README.md, README_ATUALIZADO.md, MELHORIAS_IMPLEMENTADAS.md, RESUMO_FINAL_MELHORIAS.md.
Impacto: Causa confusão sobre qual é a fonte da verdade e aumenta o trabalho de manutenção da documentação.
Solução:
Consolide tudo em um único README.md.
Crie uma pasta docs/ para mover documentações mais detalhadas e específicas (como APIS_SERVICOS.md e GERADOR_IMAGENS.md) se quiser manter o README.md mais enxuto.
Delete os arquivos redundantes.
5. Gerenciamento de Coroutine Scope
Problema: Em core-data/src/main/java/com/example/myapplication/core/data/repository/ReceitasRepository.kt, a função escutarReceitas cria um ValueEventListener do Firebase e usa CoroutineScope(Dispatchers.IO).launch para atualizar o banco de dados Room.
Impacto: Este CoroutineScope não está atrelado a nenhum ciclo de vida. Se o repositório for destruído, a corrotina (e o listener do Firebase) pode continuar rodando, causando vazamentos de memória (memory leaks) e consumo de recursos.
Solução: O repositório não deve gerenciar seu próprio escopo de longa duração. A chamada para escutarReceitas deve ser feita a partir de um ViewModel (usando viewModelScope) ou de um singleton cujo ciclo de vida seja o da aplicação.
6. Design do ViewModel
Problema: ChatViewModel e ReceitasViewModel recebem currentUserId e currentUserEmail em seus construtores.
Impacto: Isso torna os ViewModels mais difíceis de testar e menos flexíveis. Um ViewModel deve ser agnóstico em relação ao usuário específico no momento da sua criação.
Solução: Em vez de passar os dados do usuário no construtor, crie um AuthRepository que expõe o estado de autenticação (por exemplo, via um Flow). O ViewModel pode então observar esse Flow para obter as informações do usuário atual quando necessário.
# Plano: Feed de Receitas Multiusuário

## Funcionalidade
- A aba de receitas funciona como um feed público de receitas.
- Usuários logados podem:
  - Criar novas receitas (com imagem, nome, etc).
  - Curtir receitas de qualquer usuário (ícone de curtida diferente de coração, ex: polegar para cima).
  - Favoritar receitas de qualquer usuário (ícone de coração).
  - Editar e deletar **apenas** as receitas que eles mesmos publicaram.
- Receitas criadas por outros usuários só podem ser curtidas e favoritedas, não editadas ou deletadas.
- No feed, ao lado de cada receita, deve aparecer o nome (ou email) do usuário que criou a receita.

## Requisitos Técnicos
- Cada receita salva no Firebase deve conter o campo `userId` e, opcionalmente, `userName` ou `userEmail` do autor.
- O usuário logado é identificado pelo Firebase Auth.
- O campo de curtidas pode ser um contador ou uma lista de userIds que curtiram.
- O campo de favoritos pode ser uma lista de userIds que favoritaram.

## Passos de Implementação
1. **Salvar o autor ao criar receita**
   - Salvar `userId` e `userName`/`userEmail` junto com a receita.
2. **Exibir autor no feed**
   - Mostrar o nome/email do autor ao lado de cada receita.
3. **Permitir curtir/favoritar qualquer receita**
   - Botões de curtir (ícone diferente de coração) e favoritar (ícone de coração) disponíveis para todos.
   - Atualizar o contador/lista no Firebase.
4. **Permitir editar/deletar apenas receitas do usuário logado**
   - Mostrar botões de editar/deletar apenas se `userId` da receita == usuário logado.
5. **Impedir edição/remoção de receitas de outros usuários**
   - Botões não aparecem para receitas de outros.
6. **(Opcional) Exibir número de curtidas**
   - Mostrar contador de curtidas no card da receita.
   - **Não mostrar o número de favoritos no feed**.

## Observações
- O feed deve ser atualizado em tempo real.
- O usuário deve estar logado para criar, curtir ou favoritar.
- O fluxo de deleção deve remover a imagem do Supabase apenas se o usuário for o autor.
- **Na tela de detalhes, remover o botão 'ouvir/ver'.**

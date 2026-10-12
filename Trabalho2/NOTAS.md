# Trabalho 2 — notas de trabalho

Decisões de modelagem, o que a verificação cobre e como reproduzir os números do relatório.
Última atualização: 12/10/2026.

## Estado atual

**Concluído.** Entrega em 11/11/2026. Solver, verificação, medições e relatório em LaTeX estão
prontos. Em 12/10/2026 o relatório foi compilado e conferido: 11 páginas, sem referências
quebradas, e tabelas, gráfico e figuras batem com `medicoes-referencia.txt` e com o tabuleiro do
enunciado. Não há pendências.

Para reproduzir:

```bash
cd Trabalho2/src
javac CavaloHiperpulos.java && java CavaloHiperpulos       # resolve os 8 casos
javac ValidacaoHiperpulos.java && java ValidacaoHiperpulos # confere tudo (50 verificações)
javac MedicoesHiperpulos.java && java -Xms2g -Xmx2g MedicoesHiperpulos   # números do relatório (~1 min)
```

Resultados (movimentos): caso40 = 4, caso80 = 6, caso100 = 8, caso150 = 11, caso200 = 9,
caso400 = 19, caso800 = 43, caso1500 = 34. Nenhum caso deu "impossível". Os tempos estão em
`medicoes-referencia.txt` (melhor de 15 execuções); os do README são de uma execução só.

### Medições

`MedicoesHiperpulos` não altera o solver: conta casas visitadas, mede o custo da exploração
completa (sem parar em S) e compara duas variantes (parar ao descobrir S; casas como inteiros).
A saída usada no relatório está em `medicoes-referencia.txt` (Apple M2, 16 GB, macOS 26.7,
Java 24.0.1, heap fixa `-Xms2g -Xmx2g`).

O tempo **absoluto** da exploração completa varia entre execuções: 88 a 108 ns por casa na
referência e 56 a 76 ns numa nova execução com heap fixa em 11/10/2026 (macOS 27.0.1). Os demais
números (movimentos, casas, razões entre tamanhos, faixas de A/B e A/C) reproduzem. Por isso o
relatório diz que o absoluto varia e que só as razões são estáveis.

## Modelagem

**Grafo implícito**: cada casa do tabuleiro é um vértice; cada pulo permitido é uma aresta de
peso 1. Menor número de movimentos = menor caminho em grafo não ponderado, ou seja, BFS. O toro
entra só na hora de calcular o destino do pulo, com `Math.floorMod` nas duas coordenadas. Custo
Θ(N²); N=1500 (2,25 milhões de casas) resolve em centésimos de segundo.

O enunciado **não exige recursão** (diferente do Trabalho 1), então a BFS é iterativa, com
fila. Trocar a fila por pilha viraria uma DFS e perderia a garantia do menor caminho.

### Regra do pulo

O enunciado descreve o tamanho do pulo por uma figura (L para os dígitos 0, 1 e 2), sem fórmula.

- **Tamanho do L**: na figura as pernas têm 2 e 1 passos (d=0), 3 e 2 (d=1) e 4 e 3 (d=2); as
  casas em vermelho são o acréscimo de `d` casas em cada perna. Logo, **pernas `(1+d, 2+d)` nas
  8 orientações**. Com `d = 0` cai no pulo normal de xadrez. Para os dígitos 3 a 9, que a figura
  não mostra, o padrão é estendido; o relatório avisa isso (Seção 2.3 e Limitações).
- **De qual casa vem o dígito**: o da casa onde o cavalo **está**, não o da casa onde ele vai
  chegar. Consta do enunciado atual.
- **Casa `C`**: o marcador cobre o dígito daquela casa, e ali o tamanho do pulo é **0**: o
  primeiro pulo é um pulo comum de xadrez (`CavaloHiperpulos.DIGITO_EM_C`). A casa `S` não
  influi, porque a busca termina ao chegar nela.

O exemplo numérico do enunciado (**C → S em 3 pulos**) confere com essa regra. As duas outras
famílias de pernas testadas, `(2, 1+d)`/`(1+d, 2)` e `(1, 2+d)`/`(2+d, 1)`, também dão 3 pulos
no exemplo, então o exemplo não as distingue; quem as descarta é a figura (a primeira daria
pernas iguais, 2 e 2, para d=1, que não é um L; na segunda só uma perna cresce). O relatório
traz isso na Tabela 1. Não há no relatório tabela de leituras alternativas do enunciado.

## O que a verificação cobre

`ValidacaoHiperpulos` reimplementa a busca do zero (casas codificadas como `linha*n+coluna`,
fila em array de int) para que um erro no solver não se repita igual na conferência:

- **Formato dos 8 arquivos**: quadrado, tamanho igual ao nome, exatamente um `C` e um `S`.
- **Exemplo do enunciado**: bate os 3 pulos, e a busca exaustiva confirma que 2 não bastam.
- **Tabuleiros sintéticos**: pulo padrão, pulo que só fecha dando a volta no toro, dígito 9
  num tabuleiro 7×7 (pernas 10 e 11 maiores que N, testa o módulo), dígitos variados.
- **389 tabuleiros pequenos aleatórios** (semente fixa): a distância da BFS é atingível e
  não existe caminho menor, segundo busca exaustiva em profundidade limitada.
- **Casos reais**: solver × busca independente × Dijkstra (até N=400) dão a mesma distância;
  o caminho ótimo é reconstruído e conferido pulo a pulo.
- **Saída "impossível"**: exercitada por um tabuleiro sem solução achado por sorteio.

Essas conferências testam a implementação da regra adotada, não a extensão da figura aos
dígitos 3 a 9.

## Relatório (`relatorio/`)

LaTeX para o Overleaf; `preambulo.tex` é o modelo fixo, reaproveitável. Instruções em
`relatorio/LEIA-ME.txt`. O zip de upload (`relatorio-overleaf.zip`) e o PDF de rascunho são
gerados e ficam fora do git. A data está fixa em 11 de novembro de 2026.

Decisões:

- **Sem capa, sem travessões, sem bibliografia** (não é exigida; as afirmações sobre BFS e
  Dijkstra valem como conhecimento da disciplina).
- **Voz impessoal, sem primeira pessoa.** O modelo do professor
  (`Trabalho1/enunciado/03-exemplo-artigo-modelo.pdf`, p. 2) proíbe o singular, mesmo em trabalho
  individual, e diz que a voz passiva/impessoal ("faz-se", "analisa-se") é melhor que o plural.
- **Melhorias da conclusão foram medidas**: parar ao descobrir S (1,5 a 1,9×) e casas como
  inteiros (2,9 a 4,4×). Busca bidirecional e vetor de bits aparecem como ideias não testadas.
- Os números das tabelas de variantes e de resultados vêm de medições separadas da mesma função
  e diferem alguns por cento; a legenda avisa.
- A rubrica (`Trabalho1/enunciado/02-criterios-avaliacao.pdf`) pesa, em ordem: Desenvolvimento
  2,5; Algoritmos 2,0; Análise/Conclusão 2,0; Apresentação, Eficiência e Testes 1,0 cada;
  Figuras/Tabelas 0,5.

### Checklist de escrita (lições do relatório do Trabalho 1)

1. **Sem capa.** Título e autor no topo da primeira página.
2. **Toda afirmação ("eficiente", "mais rápido") precisa de "como" e "por quê"** logo ao lado,
   com dado ou dedução.
3. **Cada pseudo-código com explicação das etapas e um exemplo rodado** (trace), com resultado
   intermediário.
4. **Complexidade em Θ(·), de tempo e de espaço**, deduzida e relacionada com os tempos medidos.
5. **Justificar a técnica e o que se descartou**, inclusive BFS iterativa em vez de recursiva.
6. **Figuras e tabelas para explicar o algoritmo**, não só para exibir resultados.
7. **Conclusão com melhorias concretas e explicadas** (como funcionaria, por que ajudaria, o
   que custa) e com limitações.
8. **Conferir cada explicação com um contraexemplo antes de escrever.**
9. Gráficos com o eixo ajustado aos dados; não repetir o mesmo resultado em duas seções.
10. **Manter a validação independente dos resultados.**

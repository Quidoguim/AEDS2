# Trabalho 2 — notas de trabalho

Onde paramos, o que foi decidido e por quê. Última atualização: 01/10/2026.

## Estado atual

- **Implementação**: pronta e validada (`src/CavaloHiperpulos.java`).
- **Verificação**: bateria completa passando (`src/ValidacaoHiperpulos.java`).
- **Regra do pulo**: fechada, com o esclarecimento do professor de 01/10/2026 (dígito da casa
  onde o cavalo está; em `C` o pulo é comum). O solver já fazia isso; nenhum número mudou. Ver
  "Regra do pulo" abaixo.
- **Relatório**: rascunho completo em LaTeX em `relatorio/`. Seguiu a checklist do feedback do
  Trabalho 1 (mais abaixo). As duas figuras estão prontas em TikZ (`relatorio/figuras/*.tex`).
  A edição de 01/10/2026 (regra do pulo tratada como parte do enunciado, sem tabela de
  leituras alternativas) foi feita sem compilar: o Windows onde ela foi feita não tem LaTeX.
  Compilar no Overleaf/Mac e conferir. Falta a data de entrega e baixar a nova versão do
  enunciado (ver Pendências).
  Para o Overleaf: `relatorio-overleaf.zip` (gerado, fora do git) ou `relatorio/LEIA-ME.txt`.

Para retomar:

```bash
cd Trabalho2/src
javac CavaloHiperpulos.java && java CavaloHiperpulos       # resolve os 8 casos
javac ValidacaoHiperpulos.java && java ValidacaoHiperpulos # confere tudo
javac MedicoesHiperpulos.java && java -Xms2g -Xmx2g MedicoesHiperpulos   # números do relatório (~1 min)
```

A saída usada no relatório está salva em `medicoes-referencia.txt`. Heap fixa (`-Xms2g -Xmx2g`)
porque, com heap automática, o tempo absoluto da exploração completa oscilou entre ~55 e ~105 ns
por casa de uma execução para outra (as razões entre tamanhos não mudaram); com heap fixa deu
103 a 106 ns nas três execuções testadas.

`MedicoesHiperpulos` (30/09/2026) não altera o solver: mede casas visitadas, tempo de
exploração completa e duas variantes otimizadas (parada ao descobrir S; casas como inteiros).
Todo tempo é o melhor de 15 execuções. Ambiente das medições: Apple M2, 16 GB, macOS 26.7,
Java 24.0.1. Em 01/10/2026 foi retirada a seção de sensibilidade às leituras alternativas;
conferido num Windows que o resto da saída (exemplo, casas retiradas, casas alcançadas) é
idêntico ao de `medicoes-referencia.txt`, que só perdeu essa seção. Os tempos desse arquivo
continuam sendo os do Mac e não foram refeitos.

## Resultados

| Caso | N | Movimentos | Tempo |
| --- | --- | --- | --- |
| caso40 | 40 | 4 | 0,001s |
| caso80 | 80 | 6 | 0,004s |
| caso100 | 100 | 8 | 0,005s |
| caso150 | 150 | 11 | 0,006s |
| caso200 | 200 | 9 | 0,004s |
| caso400 | 400 | 19 | 0,014s |
| caso800 | 800 | 43 | 0,073s |
| caso1500 | 1500 | 34 | 0,033s |

Nenhum caso deu "impossível". Os tempos não crescem de forma monótona com N porque a BFS
para assim que alcança `S`: quanto mais perto a saída, menos tabuleiro é explorado.

## Modelagem

**Grafo implícito**: cada casa do tabuleiro é um vértice; cada pulo permitido é uma aresta
de peso 1. Menor número de movimentos = menor caminho em grafo não ponderado, ou seja, BFS.
O toro entra só na hora de calcular o destino do pulo, com `Math.floorMod` nas duas
coordenadas. Custo O(N²) numa única passada; N=1500 (2,25 milhões de casas) resolve em
centésimos de segundo.

O enunciado do Trabalho 2 **não exige recursão** (diferente do Trabalho 1), então a BFS é
iterativa, com fila.

### Regra do pulo

O enunciado descreve o pulo por uma figura (L para os dígitos 0, 1 e 2), sem fórmula. Regra
adotada e usada em todo o projeto:

- **Tamanho do L**: na figura as pernas têm 2 e 1 passos (d=0), 3 e 2 (d=1) e 4 e 3 (d=2); as
  casas em vermelho são o acréscimo de `d` casas em cada perna. Logo, **pernas `(1+d, 2+d)` nas
  8 orientações** (`(±(1+d), ±(2+d))` e `(±(2+d), ±(1+d))`). Com `d = 0` cai no pulo normal de
  xadrez. Vem da figura, não de confirmação do professor.
- **De qual casa vem o dígito**: o da casa onde o cavalo **está**, não o da casa onde ele vai
  chegar. Confirmado pelo professor (resposta repassada em 01/10/2026).
- **Casa `C`**: o marcador cobre o dígito daquela casa, e o professor definiu que ali o tamanho
  do pulo é **0**, ou seja, o primeiro pulo é um pulo comum de xadrez. A casa `S` não influi,
  porque a busca termina ao chegar nela. Constante `CavaloHiperpulos.DIGITO_EM_C`.
- O professor disse que vai incluir os dois esclarecimentos no enunciado e publicar a nova
  versão no Moodle. O PDF em `enunciado/01-enunciado.pdf` ainda é o antigo (pendência 3b).

O exemplo numérico do enunciado (**C → S em 3 pulos**) confere com essa regra. As duas outras
famílias de pernas testadas, `(2, 1+d)`/`(1+d, 2)` e `(1, 2+d)`/`(2+d, 1)`, também dão 3 pulos
no exemplo com o dígito 0 em `C`, então o exemplo não as distingue; a figura as descarta (a
primeira daria pernas iguais, 2 e 2, para d=1, que não é um L; na segunda só uma perna
cresce). O relatório traz isso na tabela `tab:hipoteses` de `02-modelagem.tex`.

O relatório trata a regra como parte do enunciado, sem mencionar dúvida (decisão do usuário,
01/10/2026): a tabela que comparava as leituras alternativas foi retirada do relatório, do
código de medição e do arquivo de referência. Não reintroduzir.

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

## O que o feedback do Trabalho 1 pede do relatório

O professor devolveu o relatório do Trabalho 1 com correções à mão e a rubrica preenchida. O
detalhe (notas e anotações) fica em arquivo local, fora do git, porque o repositório é público.
O que vale para o Trabalho 2, em resumo:

1. **Sem capa.** Título e autor no topo da primeira página.
2. **Todo "percebemos", "eficiente" ou "mais rápido" precisa de "como" e "por quê"** logo ao lado,
   com dado ou dedução. É o padrão de quase todas as anotações.
3. **Cada pseudo-código com explicação das etapas e um exemplo rodado** (trace). As tabelas de
   trace devem mostrar o resultado intermediário, não só as condições.
4. **Complexidade em Θ(·), de tempo e de espaço**, deduzida, e relacionada com os tempos medidos.
5. **Justificar a técnica e o que se descartou**, inclusive a escolha de BFS iterativa (o T2 não
   exige recursão): por que fila e não pilha, e por que recursão não serviria (profundidade até
   N² estouraria a pilha, a mesma causa do estouro da versão ingênua do T1).
6. **Figuras e tabelas para explicar o algoritmo**, não só para exibir resultados: um tabuleiro
   pequeno com a expansão da BFS por níveis e o "dar a volta" do toro valem mais pontos do que
   mais uma tabela de tempos.
7. **Conclusão com melhorias concretas e explicadas** (como funcionaria, por que ajudaria, o
   que custa), não uma frase solta.
8. **Conferir cada explicação com um contraexemplo antes de escrever**: uma justificativa que só
   vale para um caso particular induz o leitor ao erro.
9. Gráficos com o eixo ajustado aos dados; não repetir o mesmo resultado em duas seções.
10. **Manter a validação independente dos resultados**, que funcionou no T1. A
    `ValidacaoHiperpulos` já cobre isso.

## Pendências

1. ~~Anotar o feedback do Trabalho 1~~ — feito (detalhe em arquivo local; resumo na seção acima).
2. ~~Escrever o relatório~~ — rascunho completo em `relatorio/`. A rubrica
   (`02-criterios-avaliacao.pdf`) pesa, em ordem: Desenvolvimento 2,5; Algoritmos 2,0;
   Análise/Conclusão 2,0; Apresentação, Eficiência e Testes 1,0 cada; Figuras/Tabelas 0,5.
   **Eficiência vale só 1,0**: o que pesa é justificar decisões e a conclusão com melhorias.
3. ~~Resolver a leitura do enunciado~~ — **resolvido em 01/10/2026** (seção "Regra do pulo").
   Solver e números intactos; relatório, README e CLAUDE.md ajustados.
3b. **Baixar a nova versão do enunciado** que o professor vai publicar no Moodle e, se for
   diferente de `enunciado/01-enunciado.pdf`, substituir (mesmo nome) e conferir se mais alguma
   coisa mudou além dos dois esclarecimentos. Se o enunciado novo citar a regra com palavras
   próprias, vale ecoar a formulação em `02-modelagem.tex`.
4. ~~Figuras do relatório~~ — prontas em TikZ (`fig-pulos-em-L`, `fig-camadas-bfs`). Os 100
   pares dígito/distância da segunda foram conferidos por script contra o tabuleiro do enunciado
   e contra `medicoes-referencia.txt`. Falta só o usuário conferir se estão do seu gosto.
5. **Antes de entregar**: trocar `\today` por uma data fixa e conferir o PDF contra a checklist
   "O que o feedback do Trabalho 1 pede do relatório". Não restam lembretes `\pendente` no texto.

### Decisões do rascunho do relatório (30/09/2026)

- **Sem capa, sem travessões** (o T1 teve os travessões removidos na revisão final).
- **Voz impessoal, sem primeira pessoa.** O modelo do professor
  (`Trabalho1/enunciado/03-exemplo-artigo-modelo.pdf`, p. 2) diz para nunca usar o singular
  ("fiz", "analisei"), mesmo em trabalho individual, e que a voz passiva/impessoal ("faz-se",
  "analisa-se") é "melhor ainda" que o plural. O usuário notou que "pensamos/comparamos" sugeria
  trabalho em dupla e pediu singular; foi explicado que o professor proíbe e o texto foi passado
  para a voz impessoal ("modela-se", "foi conferido"). Ao escrever novas seções, evitar também
  "nós".
- **Afirmação que o relatório NÃO faz**, porque não se sustenta: que o exemplo do enunciado
  distingue as regras de perna (as três dão 3 pulos com o dígito 0 em `C`; quem decide é a
  figura).
- **Melhorias da Conclusão foram medidas**, não só propostas: parar ao descobrir S (1,5 a 1,9×) e
  casas como inteiros (2,9 a 4,4×). A busca bidirecional aparece como ideia não testada, com o
  motivo (exige o grafo inverso).
- Os tempos vêm de `medicoes-referencia.txt` (heap fixa de 2 GB). Os números da Tabela de
  variantes e da Tabela de resultados são medições separadas da mesma função e diferem alguns
  por cento; o texto avisa.

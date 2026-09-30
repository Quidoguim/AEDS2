# Trabalho 2 — notas de trabalho

Onde paramos, o que foi decidido e por quê. Última atualização: 30/09/2026.

## Estado atual

- **Implementação**: pronta e validada (`src/CavaloHiperpulos.java`).
- **Verificação**: bateria completa passando (`src/ValidacaoHiperpulos.java`).
- **Relatório**: rascunho completo em LaTeX em `relatorio/` (compila; 11 páginas). Seguiu a
  checklist do feedback do Trabalho 1 (mais abaixo). As duas figuras estão prontas em TikZ
  (`relatorio/figuras/*.tex`). Faltam um lembrete `\pendente` sobre a leitura do enunciado e a
  data de entrega.
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
exploração completa, duas variantes otimizadas (parada ao descobrir S; casas como inteiros) e a
sensibilidade às duas ambiguidades do enunciado. Todo tempo é o melhor de 15 execuções.
Ambiente das medições: Apple M2, 16 GB, macOS 26.7, Java 24.0.1.

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

### Regra do pulo: o que está estabelecido e o que não está

O enunciado descreve o pulo só por uma figura (L para os dígitos 0, 1 e 2), sem fórmula.

**Estabelecido: o tamanho do L.** Na figura as pernas têm 2 e 1 passos (d=0), 3 e 2 (d=1) e
4 e 3 (d=2); as casas em vermelho são o acréscimo de `d` casas em cada perna. Regra: **pernas
`(1+d, 2+d)` nas 8 orientações** (`(±(1+d), ±(2+d))` e `(±(2+d), ±(1+d))`). Com `d = 0` cai no
pulo normal de xadrez. O exemplo numérico do enunciado (**C → S em 3 pulos**) confere: a regra
dá 3 pulos para **qualquer** dígito que esteja escondido sob o C.

Alternativas para as pernas, testadas no exemplo com o dígito sob o C variando de 0 a 9
(refeito em 30/09/2026):

| Pernas | Pulos no exemplo | Leitura |
| --- | --- | --- |
| `(1+d, 2+d)` | 3 com todos os dígitos | adotada; bate com a figura |
| `(2, 1+d)` ou `(1+d, 2)` | 3 com nove dos dez dígitos, 1 com o dígito 4 | o exemplo **não** as descarta; a figura sim (d=1 daria pernas iguais, 2 e 2, que não é um L) |
| `(1, 2+d)` ou `(2+d, 1)` | 2, 3 ou 4 conforme o dígito | descartadas pela figura; a resposta dependeria do dígito |

> **Correção de registro (30/09/2026).** Uma versão anterior desta tabela afirmava que
> `(2, 1+d)` "dá 3 só para alguns dígitos" e que a leitura "o dígito do destino define o
> pulo" deixava o tabuleiro "desconexo, sem solução, contradizendo o exemplo". Nenhuma das duas
> afirmações se reproduz. A primeira está na tabela acima. A segunda é falsa: a leitura do
> destino também dá 3 pulos no exemplo (ver abaixo). **Não repetir nenhuma das duas no
> relatório.**

**Não estabelecido: de qual casa vem o dígito.** Há duas leituras, e o exemplo do enunciado
(3 pulos) é compatível com as duas:

- **Origem (adotada)**: o dígito da casa onde o cavalo *está* define o pulo que ele dá.
- **Destino**: o dígito da casa onde o cavalo vai *cair* define o pulo.

Adotamos a de origem porque o enunciado diz que o número de cada casa "afeta o quanto o
cavalo pode pular", e quem pula sai da casa em que está. É uma leitura do texto, não uma
demonstração. A sensibilidade é grande (número de movimentos; `MedicoesHiperpulos` imprime a
tabela, e o Python independente conferiu os casos 40 a 200 da leitura destino):

| Caso | Origem, C=0 (adotada) | Origem, C qualquer | Destino, S=0 | Destino, S qualquer |
| --- | --- | --- | --- | --- |
| caso40 | 4 | 4 | 2 | 2 |
| caso80 | 6 | 4 | 6 | 4 |
| caso100 | 8 | 6 | 8 | 6 |
| caso150 | 11 | 9 | 11 | 11 |
| caso200 | 9 | 9 | 11 | 9 |
| caso400 | 19 | 17 | 17 | 17 |
| caso800 | 43 | 43 | 43 | 41 |
| caso1500 | 34 | 34 | 34 | 34 |

Só o caso1500 dá o mesmo resultado nas quatro combinações. **Isso precisa ser resolvido com
o professor** (ou procurando respostas esperadas na página da disciplina, onde o enunciado diz
que os casos de teste foram publicados) antes de tratar os números como definitivos. No
relatório, a decisão deve aparecer como suposição explícita, com esta tabela.

### O dígito escondido sob o C

O marcador `C` cobriu o dígito original daquela casa, então o tamanho do primeiro pulo não
está no arquivo. **Decisão: o primeiro pulo usa o dígito 0**, o "pulo normal de xadrez" que
o próprio enunciado define como caso base — casa sem número visível, pulo padrão.

Isso importa: a resposta de 5 dos 8 casos muda conforme a suposição.

| Caso | dígito 0 (adotado) | união dos dígitos 0-9 | depende do dígito? |
| --- | --- | --- | --- |
| caso40 | 4 | 4 | sim (dígito 5 dá 6) |
| caso80 | 6 | 4 | sim (dígito 6 dá 4) |
| caso100 | 8 | 6 | sim |
| caso150 | 11 | 9 | sim |
| caso200 | 9 | 9 | não |
| caso400 | 19 | 17 | sim |
| caso800 | 43 | 43 | não |
| caso1500 | 34 | 34 | não |

A alternativa "união" trata o primeiro pulo como podendo ter qualquer tamanho, já que o
dígito é desconhecido — responde "o menor número de movimentos possível entre todas as
hipóteses", que é uma pergunta diferente. Para trocar, basta mudar `DIGITO_SUPOSTO_EM_C`
para `UNIAO_DOS_DIGITOS` em `CavaloHiperpulos.java`; a bateria de verificação acompanha a
constante sozinha. **Vale confirmar a intenção com o professor** — se ele disser outra
coisa, é uma constante e rodar de novo.

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
3. **Resolver a leitura do enunciado** (seção "Regra do pulo"): de qual casa vem o dígito e
   qual dígito está sob `C`/`S`. Sete dos oito casos mudam de resposta conforme a escolha.
   Perguntar ao professor ou procurar respostas esperadas na página da disciplina. Se a
   leitura mudar, ajustar `relatorio/secoes/02-modelagem.tex` (Tabela 2 e texto), o Resumo, a
   Tabela de resultados e os números do solver.
4. ~~Figuras do relatório~~ — prontas em TikZ (`fig-pulos-em-L`, `fig-camadas-bfs`). Os 100
   pares dígito/distância da segunda foram conferidos por script contra o tabuleiro do enunciado
   e contra `medicoes-referencia.txt`. Falta só o usuário conferir se estão do seu gosto.
5. **Antes de entregar**: resolver os `\pendente`, trocar `\today` por uma data fixa e conferir
   o PDF contra a checklist "O que isso pede do relatório do Trabalho 2".

### Decisões do rascunho do relatório (30/09/2026)

- **Sem capa, sem travessões** (o T1 teve os travessões removidos na revisão final).
- **Voz impessoal, sem primeira pessoa.** O modelo do professor
  (`Trabalho1/enunciado/03-exemplo-artigo-modelo.pdf`, p. 2) diz para nunca usar o singular
  ("fiz", "analisei"), mesmo em trabalho individual, e que a voz passiva/impessoal ("faz-se",
  "analisa-se") é "melhor ainda" que o plural. O usuário notou que "pensamos/comparamos" sugeria
  trabalho em dupla e pediu singular; foi explicado que o professor proíbe e o texto foi passado
  para a voz impessoal ("modela-se", "foi conferido"). Ao escrever novas seções, evitar também
  "nós".
- **Afirmações que o relatório NÃO faz**, porque não se sustentam: que o exemplo do enunciado
  descarta a leitura "dígito do destino"; que `(2, 1+d)` só funciona para alguns dígitos.
- **Melhorias da Conclusão foram medidas**, não só propostas: parar ao descobrir S (1,5 a 1,9×) e
  casas como inteiros (2,9 a 4,4×). A busca bidirecional aparece como ideia não testada, com o
  motivo (exige o grafo inverso).
- Os tempos vêm de `medicoes-referencia.txt` (heap fixa de 2 GB). Os números da Tabela de
  variantes e da Tabela de resultados são medições separadas da mesma função e diferem alguns
  por cento; o texto avisa.

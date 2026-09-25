# Trabalho 2 — notas de trabalho

Onde paramos, o que foi decidido e por quê. Última atualização: 25/09/2026.

## Estado atual

- **Implementação**: pronta e validada (`src/CavaloHiperpulos.java`).
- **Verificação**: bateria completa passando (`src/ValidacaoHiperpulos.java`).
- **Relatório**: não iniciado — esperando o feedback do professor sobre o Trabalho 1.

Para retomar:

```bash
cd Trabalho2/src
javac CavaloHiperpulos.java && java CavaloHiperpulos       # resolve os 8 casos
javac ValidacaoHiperpulos.java && java ValidacaoHiperpulos # confere tudo
```

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

### Regra do pulo: como foi descoberta

O enunciado descreve o pulo só por uma figura (L crescendo para 0, 1, 2), sem fórmula.
A regra foi determinada rodando BFS sobre o tabuleiro-exemplo do próprio enunciado, cuja
resposta ele informa (**C → S em 3 pulos**), e testando hipóteses:

| Hipótese | Resultado |
| --- | --- |
| pernas `(1+d, 2+d)` — as duas crescem juntas | **3 pulos, para qualquer dígito suposto em C** ✅ |
| pernas `(2, 1+d)` ou `(1+d, 2)` | dá 3 só para alguns dígitos supostos — coincidência |
| pernas `(1, 2+d)` ou `(2+d, 1)` | dá 2, 3 ou 4 conforme o dígito — não bate |
| dígito do **destino** define o pulo | tabuleiro fica desconexo, sem solução — contradiz o exemplo |

Adotada: **dígito `d` na casa atual → pernas `(1+d, 2+d)`, nas 8 orientações** (`(±(1+d), ±(2+d))`
e `(±(2+d), ±(1+d))`). Com `d = 0` cai no pulo normal de xadrez, como o enunciado diz.

Também foi testada a leitura "o dígito é um limite, pode-se pular de 0 até `d`": ela
igualmente reproduz os 3 pulos do exemplo, mas a figura mostra **uma** forma de L por
número, não um conjunto de formas — por isso ficou a leitura "exatamente `d`".

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

## Pendências

1. **Anotar aqui o feedback do professor sobre o Trabalho 1** (nota 6,5/10, correções feitas
   à mão no relatório impresso) — é o insumo para não repetir os mesmos erros.
2. **Escrever o relatório**, cobrindo: problema, modelagem, processo de solução com exemplos
   e algoritmos, resultados dos casos de teste e conclusões. Pontos que o professor cobrou no
   relatório-exemplo anotado (`Trabalho1/enunciado/04-exemplo-relatorio-anotado.pdf`):
   mostrar pseudo-código, justificar as afirmações de complexidade/eficiência e explicar
   **por que** cada escolha de implementação foi feita, não só o que ela faz. A rubrica
   (`02-criterios-avaliacao.pdf`) pesa mais análise de eficiência e conclusão com ideias
   concretas de melhoria do que capricho de texto.
3. Material bom para o relatório que já está pronto: a tabela de hipóteses da regra do pulo
   (mostra o processo de modelagem) e a tabela de sensibilidade ao dígito sob o `C`
   (justifica uma decisão de projeto com dado, não com achismo).

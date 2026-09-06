# AEDS2 — Algoritmos e Estruturas de Dados II

Repositório dedicado à disciplina de **Algoritmos e Estruturas de Dados II** (PUCRS – Escola Politécnica, Prof. João Batista Souza de Oliveira). Contém os enunciados fornecidos pelo professor e as implementações desenvolvidas ao longo do curso.

## Estrutura

```
Trabalho1/
├── enunciado/                           # material fornecido pelo professor
│   ├── 01-enunciado.pdf                  # enunciado do trabalho
│   ├── 02-criterios-avaliacao.pdf        # rubrica de avaliação do relatório
│   ├── 03-exemplo-artigo-modelo.pdf      # exemplo comentado de como escrever o relatório (escrito pelo próprio professor)
│   └── 04-exemplo-relatorio-anotado.pdf  # relatório de um trabalho anterior, corrigido e anotado pelo professor
└── src/
    └── NumerosDecompostos.java           # solução recursiva: contagem de decomposições + busca em cada faixa
```

### Como executar

```bash
cd Trabalho1/src
javac NumerosDecompostos.java && java NumerosDecompostos
```

## Trabalho 1 — Números Decompostos

Uma **decomposição** de um inteiro `n` é uma sequência de inteiros positivos e consecutivos cuja soma é `n`. Por exemplo, para `n = 15`:

```
15 = 1 + 2 + 3 + 4 + 5
15 = 4 + 5 + 6
15 = 7 + 8
15 = 15
```

O algoritmo — que deve usar **recursão** — precisa encontrar, para cada uma das faixas abaixo, o(s) número(s) com o **maior número de decomposições**.

Vale notar (e é usado na solução): o número de decomposições de `n` é igual ao número de **divisores ímpares** de `n` — dá pra conferir no próprio exemplo, já que 15 = 3×5 tem os divisores {1, 3, 5, 15}, todos ímpares, exatamente 4. Por isso basta percorrer os tamanhos de sequência `k` (no máximo `~√(2n)` deles) em vez de testar cada possível ponto de partida.

### Resultados

| # | Faixa | Máx. decomposições | Vencedor(es) | Tempo |
| --- | --- | --- | --- | --- |
| 1 | 2 – 100.000 | 48 | 19 números (ver abaixo) | 0,097s |
| 2 | 100.000 – 200.000 | 64 | 135135, 176715, 197505 | 0,181s |
| 3 | 200.000 – 400.000 | 72 | 8 números (ver abaixo) | 0,518s |
| 4 | 400.000 – 800.000 | 96 | 675675, 765765 | 1,489s |
| 5 | 800.000 – 1.000.000 | 96 | 855855, 883575, 945945, 987525 | 0,920s |
| 6 | 1.000.000 – 1.500.000 | 96 | 20 números (ver abaixo) | 2,716s |
| 7 | 1.500.000 – 2.000.000 | 108 | 1576575 | 3,216s |

Vencedores completos das faixas 1, 3 e 6:
- **Faixa 1** (48): 45045, 51975, 58905, 61425, 65835, 69615, 72765, 75075, 77805, 79695, 80325, 85995, 86625, 89775, 90090, 93555, 94185, 96525, 98175
- **Faixa 3** (72): 225225, 294525, 315315, 329175, 348075, 363825, 389025, 398475
- **Faixa 6** (96): 1036035, 1044225, 1119195, 1126125, 1167075, 1195425, 1216215, 1237005, 1276275, 1306305, 1322685, 1351350, 1354815, 1382535, 1396395, 1412775, 1426425, 1461915, 1472625, 1486485

Tempo total: ~9,1s (execução única, sem otimizações além da fórmula acima — ver [`NumerosDecompostos.java`](Trabalho1/src/NumerosDecompostos.java)).

**Trabalho individual.** Entrega até **09/09/2026**.

### Entrega

Um relatório descrevendo:
- Qual o problema sendo resolvido;
- Como o problema foi modelado;
- Como é o processo de solução, apresentando exemplos e algoritmos;
- Os resultados dos casos de teste;
- Conclusões.

Ver [`01-enunciado.pdf`](Trabalho1/enunciado/01-enunciado.pdf) para o texto completo, [`02-criterios-avaliacao.pdf`](Trabalho1/enunciado/02-criterios-avaliacao.pdf) para a rubrica de correção, e os dois exemplos em `enunciado/` como referência de como escrever (e do que evitar) no relatório.


## Licença

Este repositório está sob a licença MIT — veja [LICENSE](LICENSE).

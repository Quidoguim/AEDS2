# AEDS2 — Algoritmos e Estruturas de Dados II

Repositório dedicado à disciplina de **Algoritmos e Estruturas de Dados II** (PUCRS – Escola Politécnica). Contém os enunciados fornecidos pelo professor e as implementações desenvolvidas ao longo do curso.

## Estrutura

```
Trabalho1/
└── enunciado/                           # material fornecido pelo professor
    ├── 01-enunciado.pdf                  # enunciado do trabalho
    ├── 02-criterios-avaliacao.pdf        # rubrica de avaliação do relatório
    ├── 03-exemplo-artigo-modelo.pdf      # exemplo comentado de como escrever o relatório
    └── 04-exemplo-relatorio-anotado.pdf  # relatório de um trabalho anterior, corrigido e anotado pelo professor
```

## Trabalho 1 — Números Decompostos

Uma **decomposição** de um inteiro `n` é uma sequência de inteiros positivos e consecutivos cuja soma é `n`. Por exemplo, para `n = 15`:

```
15 = 1 + 2 + 3 + 4 + 5
15 = 4 + 5 + 6
15 = 7 + 8
15 = 15
```

O algoritmo — que deve usar **recursão** — precisa encontrar, para cada uma das faixas abaixo, o(s) número(s) com o **maior número de decomposições**:

| # | Faixa | Status |
| --- | --- | --- |
| 1 | 2 – 100.000 | ⬜ Não iniciado |
| 2 | 100.000 – 200.000 | ⬜ Não iniciado |
| 3 | 200.000 – 400.000 | ⬜ Não iniciado |
| 4 | 400.000 – 800.000 | ⬜ Não iniciado |
| 5 | 800.000 – 1.000.000 | ⬜ Não iniciado |
| 6 | 1.000.000 – 1.500.000 | ⬜ Não iniciado |
| 7 | 1.500.000 – 2.000.000 | ⬜ Não iniciado |

**Trabalho individual.**

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

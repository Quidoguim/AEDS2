# AEDS2 — Algoritmos e Estruturas de Dados II

Repositório dedicado à disciplina de **Algoritmos e Estruturas de Dados II** (PUCRS – Escola Politécnica, Prof. João Batista Souza de Oliveira). Contém os enunciados fornecidos pelo professor e as implementações desenvolvidas ao longo do curso.

## Estrutura

```
Trabalho1/
├── enunciado/                           # material fornecido pelo professor
│   ├── 01-enunciado.pdf
│   ├── 02-criterios-avaliacao.pdf
│   ├── 03-exemplo-artigo-modelo.pdf
│   └── 04-exemplo-relatorio-anotado.pdf
└── src/
    └── NumerosDecompostos.java          # solução recursiva

Trabalho2/
├── enunciado/
│   └── 01-enunciado.pdf
└── casos-teste/                         # 8 tabuleiros (40×40 a 1500×1500)
    └── caso*.txt
```

Cada `TrabalhoN/` traz o enunciado do professor em `enunciado/` (renomeado de forma clara) e a implementação em `src/`. Todo relatório precisa cobrir: problema, modelagem, processo de solução (com exemplos e algoritmos), resultados dos casos de teste e conclusões — ver rubrica e exemplos em `Trabalho1/enunciado/`.

## Trabalho 1 — Números Decompostos

Uma **decomposição** de `n` é uma soma de inteiros positivos consecutivos igual a `n` (`n = n` conta). Achar, para 7 faixas de valores, o(s) número(s) com **mais decomposições** — solução obrigatoriamente **recursiva**.

Atalho usado: nº de decomposições de `n` = nº de divisores ímpares de `n` (ex.: 15 = 3×5 → divisores ímpares {1,3,5,15} → 4 decomposições, confere).

| Faixa | Máx. decomposições | Tempo |
| --- | --- | --- |
| 2 – 100.000 | 48 | 0,097s |
| 100.000 – 200.000 | 64 | 0,181s |
| 200.000 – 400.000 | 72 | 0,518s |
| 400.000 – 800.000 | 96 | 1,489s |
| 800.000 – 1.000.000 | 96 | 0,920s |
| 1.000.000 – 1.500.000 | 96 | 2,716s |
| 1.500.000 – 2.000.000 | 108 | 3,216s |

Tempo total ~9,1s. Vencedores completos e discussão no relatório ([`NumerosDecompostos.java`](Trabalho1/src/NumerosDecompostos.java)). **Individual.** Entregue em 09/09/2026, nota 6,5/10.

## Trabalho 2 — O Cavalo e os Hiperpulos

Tabuleiro toroidal (bordas se encostam) com dígitos 0–9 em cada casa. Um cavalo parte de `C` e precisa chegar a `S` no **menor número de movimentos possível**. O pulo é em L, mas o alcance cresce com o dígito da casa onde o cavalo está: 0 = L padrão de xadrez, dígitos maiores esticam o L. 8 casos de teste, tabuleiros de 40×40 a 1500×1500.

Status: em planejamento, linguagem ainda não definida. **Individual.**


## Licença

Este repositório está sob a licença MIT — veja [LICENSE](LICENSE).

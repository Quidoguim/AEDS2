# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository purpose

Coursework repository for the PUCRS discipline *Algoritmos e Estruturas de Dados II*. Each assignment lives in its own `TrabalhoN/` folder at the repo root; professor-provided material for an assignment goes under `TrabalhoN/enunciado/`, renamed to a clear, numbered, kebab-case filename (see `Trabalho1/enunciado/` for the pattern) — never keep the professor's original filenames (scanner/export names like `t1.pdf` or `20230519144606932.pdf`).

## Trabalho 1 — Números Decompostos

Constraints that any implementation and report must satisfy (from `Trabalho1/enunciado/01-enunciado.pdf`):

- **Recursão é obrigatória** — a solução deve ser implementada de forma recursiva, não iterativa.
- The task is to find, for each of the 7 ranges listed in the README, the integer(s) with the **maximum number of decompositions** (a decomposition = a sum of consecutive positive integers equal to `n`; single-element sums like `n = n` count).
- **Individual work** — not done in pairs, unlike some other courses in this account's other repos.
- Deliverable is a **relatório**, not just code — it must cover: the problem, how it was modeled, the solution process (with examples and algorithms), test-case results, and conclusions.

Two files in `Trabalho1/enunciado/` are report-writing aids, not part of the problem statement — read them before drafting the report, not before implementing the algorithm:
- `03-exemplo-artigo-modelo.pdf`: a deliberately annotated example (LaTeX/LyX) showing report structure and academic-writing conventions in Portuguese (plural/impersonal voice, figure/table usage, citation style).
- `04-exemplo-relatorio-anotado.pdf`: a real student report from a previous assignment with the professor's handwritten corrections — useful as a "what graders actually flag" reference (e.g., it was marked down for not showing pseudo-code, not justifying complexity/efficiency claims, and not explaining *why* an implementation choice was made, not just *what* it does).
- `02-criterios-avaliacao.pdf`: the grading rubric — weights efficiency analysis, justified algorithm/pseudo-code choices, and a real conclusion (with concrete ideas for improvement) noticeably higher than prose polish.

Trabalho 1 was delivered on 09/09/2026 and graded **6,5/10**. The professor's corrections were handwritten on the printed report and have **not been transcribed yet** — when the user provides them, record them in `Trabalho2/NOTAS.md` (pending item #1 there) before drafting the Trabalho 2 report.

## Trabalho 2 — O Cavalo e os Hiperpulos

Constraints (from `Trabalho2/enunciado/01-enunciado.pdf`): find the minimum number of knight moves from `C` to `S` on a toroidal N×N board whose digits (0-9) set the jump size; individual work; same five report topics as Trabalho 1. **Recursion is not required here** — unlike Trabalho 1.

Two modelling decisions are load-bearing and were settled empirically, not read off the statement. Do not change them silently — the reasoning, the evidence and the discarded alternatives are written up in **`Trabalho2/NOTAS.md`**, which is the file to read before touching this assignment:

- **Jump rule**: standing on a digit `d`, both legs of the L grow to `(1+d, 2+d)`, in the 8 usual orientations. Derived by testing hypotheses against the worked example the statement itself answers (C→S in 3 jumps).
- **Digit hidden under the `C` marker**: the first jump uses digit 0 (the "pulo normal de xadrez" the statement defines as the base case). This changes the answer in 5 of the 8 test cases, so it is a real assumption, not a detail — `CavaloHiperpulos.DIGITO_SUPOSTO_EM_C` switches it, and `ValidacaoHiperpulos` prints the sensitivity table. Worth confirming with the professor.

`ValidacaoHiperpulos.java` is a companion verification suite (independent reimplementation, cross-check against Dijkstra and exhaustive search, path legality, synthetic edge cases). Run it after any change to the solver.

## Conventions

- **README.md**: keep it updated as the work progresses — it is the live status of the assignment (structure tree, per-range status table, deliverable checklist), not a one-time snapshot.
- **Commits**: short imperative Portuguese subject line (e.g. "Adiciona", "Cria", "Renomeia"), no conventional-commit prefix; add a body paragraph when the *why* isn't obvious from the diff. Matches the convention used across this account's other course repos (`SO`, `PSB`, `SMA`, `CSW`).
- **Java** is the implementation language for both assignments: plain files in `TrabalhoN/src/`, default package, no build tool, run with `javac`/`java` from inside `src/`. Portuguese identifiers, `System.nanoTime()` for timing, results printed with `printf` in a shape that can be pasted into the README table. No test framework — verification lives in a companion class with its own `main`.

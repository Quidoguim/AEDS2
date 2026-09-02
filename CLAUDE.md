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

## Conventions

- **README.md**: keep it updated as the work progresses — it is the live status of the assignment (structure tree, per-range status table, deliverable checklist), not a one-time snapshot.
- **Commits**: short imperative Portuguese subject line (e.g. "Adiciona", "Cria", "Renomeia"), no conventional-commit prefix; add a body paragraph when the *why* isn't obvious from the diff. Matches the convention used across this account's other course repos (`SO`, `PSB`, `SMA`, `CSW`).
- No implementation language has been chosen yet for Trabalho 1 — once one is picked, extend `.gitignore` accordingly (it currently only covers macOS cruft and local Claude Code settings).

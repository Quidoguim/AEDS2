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
- `02-criterios-avaliacao.pdf`: the grading rubric. Weights, highest first: Desenvolvimento 2.5, Algoritmos 2.0, Análise/Conclusão 2.0, then Apresentação, Eficiência and Testes at 1.0 each, Figuras/Tabelas 0.5. Justified decisions and a conclusion with concrete improvement ideas count far more than prose polish; efficiency analysis on its own is only 1.0.

Trabalho 1 was delivered on 09/09/2026 and graded **6,5/10**. The professor's handwritten corrections are scanned in `Trabalho1/enunciado/05-feedback-professor-trabalho1.pdf` and transcribed in detail (per-criterion grades and each handwritten note) in `Trabalho2/feedback-trabalho1-local.md`. **Both are local only and gitignored: never commit them. The repo is public and the user asked to keep this feedback private.** The distilled checklist, safe to share, is in `Trabalho2/NOTAS.md` (section "O que o feedback do Trabalho 1 pede do relatório"); read it before drafting the Trabalho 2 report. Headline rules: **no cover page**, every claim needs its "how" and "why", every pseudo-code needs a step-by-step explanation plus a worked example, and complexity must be stated in Θ(·).

## Trabalho 2 — O Cavalo e os Hiperpulos

Constraints (from `Trabalho2/enunciado/01-enunciado.pdf`): find the minimum number of knight moves from `C` to `S` on a toroidal N×N board whose digits (0-9) set the jump size; individual work; same five report topics as Trabalho 1. **Recursion is not required here** — unlike Trabalho 1.

The jump rule is settled; details and the pending items are in **`Trabalho2/NOTAS.md`**, the file to read before touching this assignment. Do not change it silently:

- **Size of the L**: both legs grow to `(1+d, 2+d)`, in the 8 usual orientations. Read off the statement's figure (legs 2/1, 3/2, 4/3 for d=0,1,2); the worked example (C→S in 3 jumps) is consistent with it but does not single it out (the other leg families also give 3). This comes from the figure, not from the professor.
- **Whose digit sets the jump**: the digit of the cell the knight stands on, not the landing cell. Confirmed by the professor (answer relayed on 01/10/2026).
- **Casa `C`**: the jump size there is 0, i.e. the first jump is an ordinary chess jump (`CavaloHiperpulos.DIGITO_EM_C`). Same confirmation. `S` never matters, the search stops on reaching it.

**The user decided the report must treat this rule as part of the statement, as if the doubt never existed**: no mention of ambiguity, no table comparing alternative readings, no sensitivity analysis (all removed from the report, `MedicoesHiperpulos`, `ValidacaoHiperpulos`, `medicoes-referencia.txt` on 01/10/2026). Do not reintroduce them. The professor said he would publish a new version of the statement on Moodle; `Trabalho2/enunciado/01-enunciado.pdf` is still the old one until the user provides the new file (pending item 3b in `NOTAS.md`).

`ValidacaoHiperpulos.java` is a companion verification suite (independent reimplementation, cross-check against Dijkstra and exhaustive search, path legality, synthetic edge cases). Run it after any change to the solver. `MedicoesHiperpulos.java` produces the numbers for the report's efficiency analysis (visited cells, best-of-15 timings, two optimised variants) without touching the solver.

The Trabalho 2 report is written in **LaTeX for Overleaf**, in `Trabalho2/relatorio/`: `preambulo.tex` is the fixed template (reusable for later reports), `main.tex` holds title/author/abstract, `secoes/` has one file per section, `figuras/` the images (figures are TikZ files in `figuras/`; `\espacofigura` draws a dashed placeholder box for a figure not yet made; `\pendente{...}` marks reminders to resolve before submission). The text is in impersonal voice (the professor's model forbids first-person singular even in individual work and prefers passive/impersonal forms over plural), cover-less and avoids em dashes. Compile check locally with `tectonic -X compile main.tex --outdir <dir>` (installed via Homebrew; XeTeX-only, so the pdfLaTeX font branch of the preamble is untested locally). `Trabalho2/relatorio-overleaf.zip` (upload bundle) and `relatorio-rascunho.pdf` are generated and gitignored. Every number in the report comes from `Trabalho2/medicoes-referencia.txt`; regenerate it with the command in its header and update the `.tex` tables if the measurements change.

## Conventions

- **README.md**: keep it updated as the work progresses — it is the live status of the assignment (structure tree, per-range status table, deliverable checklist), not a one-time snapshot.
- **Commits**: short imperative Portuguese subject line (e.g. "Adiciona", "Cria", "Renomeia"), no conventional-commit prefix; add a body paragraph when the *why* isn't obvious from the diff. Matches the convention used across this account's other course repos (`SO`, `PSB`, `SMA`, `CSW`).
- **Java** is the implementation language for both assignments: plain files in `TrabalhoN/src/`, default package, no build tool, run with `javac`/`java` from inside `src/`. Portuguese identifiers, `System.nanoTime()` for timing, results printed with `printf` in a shape that can be pasted into the README table. No test framework — verification lives in a companion class with its own `main`.

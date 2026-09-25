import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

// Bateria de verificações do solver de CavaloHiperpulos. Os algoritmos daqui são
// reescritos do zero (casas codificadas como linha*n+coluna, fila em array de int)
// justamente para que um erro no solver não se repita igual na conferência.
public class ValidacaoHiperpulos {

    // Convenção em vigor no solver para o dígito escondido sob o C: tudo aqui é conferido
    // sob ela, e validarSensibilidadeDoDigitoDeC mede o efeito de trocá-la.
    private static final int CONVENCAO = CavaloHiperpulos.DIGITO_SUPOSTO_EM_C;

    private static int falhas = 0;

    public static void main(String[] args) throws IOException {
        validarFormatoDosArquivos();
        validarExemploDoEnunciado();
        validarCasosSinteticos();
        validarContraBuscaExaustiva();
        procurarTabuleiroSemSolucao();
        validarCasosReais();
        validarSensibilidadeDoDigitoDeC();

        System.out.println();
        if (falhas > 0) {
            throw new IllegalStateException(falhas + " verificação(ões) falharam");
        }
        System.out.println("Todas as verificações passaram.");
    }

    private static void conferir(String descricao, boolean condicao) {
        System.out.printf("  [%s] %s%n", condicao ? "ok" : "FALHOU", descricao);
        if (!condicao) {
            falhas++;
        }
    }

    private static void validarFormatoDosArquivos() throws IOException {
        System.out.println("== formato dos arquivos de entrada ==");
        for (String nomeArquivo : CavaloHiperpulos.CASOS) {
            char[][] tabuleiro = CavaloHiperpulos.lerTabuleiro(CavaloHiperpulos.PASTA_CASOS.resolve(nomeArquivo));
            int n = tabuleiro.length;
            int tamanhoNoNome = Integer.parseInt(nomeArquivo.replaceAll("\\D", ""));

            boolean quadrado = true;
            int marcadoresC = 0;
            int marcadoresS = 0;
            int caracteresInvalidos = 0;
            for (char[] linha : tabuleiro) {
                if (linha.length != n) {
                    quadrado = false;
                }
                for (char casa : linha) {
                    if (casa == 'C') {
                        marcadoresC++;
                    } else if (casa == 'S') {
                        marcadoresS++;
                    } else if (casa < '0' || casa > '9') {
                        caracteresInvalidos++;
                    }
                }
            }

            conferir(nomeArquivo + ": tabuleiro " + n + "x" + n + " quadrado e do tamanho anunciado",
                    quadrado && n == tamanhoNoNome);
            conferir(nomeArquivo + ": exatamente um C e um S, resto só dígitos",
                    marcadoresC == 1 && marcadoresS == 1 && caracteresInvalidos == 0);
        }
    }

    private static void validarExemploDoEnunciado() {
        System.out.println("\n== exemplo do enunciado (resposta conhecida: "
                + CavaloHiperpulos.MOVIMENTOS_DO_EXEMPLO + " pulos) ==");
        char[][] exemplo = CavaloHiperpulos.paraTabuleiro(CavaloHiperpulos.EXEMPLO_ENUNCIADO);
        int esperado = CavaloHiperpulos.MOVIMENTOS_DO_EXEMPLO;

        conferir("solver bate com a resposta do enunciado",
                CavaloHiperpulos.menorNumeroDeMovimentos(exemplo) == esperado);
        conferir("busca independente bate com a resposta do enunciado",
                distancia(exemplo, CONVENCAO) == esperado);
        conferir("busca exaustiva confirma que não dá em menos de " + esperado,
                !alcancaEm(exemplo, esperado - 1) && alcancaEm(exemplo, esperado));

        boolean invariante = true;
        for (int digito = 0; digito <= 9; digito++) {
            invariante &= distancia(exemplo, digito) == esperado;
        }
        conferir("resposta não muda qualquer que seja o dígito suposto sob o C (0-9)", invariante);

        int[] caminho = caminhoOtimo(exemplo);
        conferir("caminho reconstruído tem " + esperado + " pulos e todos são legais",
                caminho != null && caminho.length - 1 == esperado && caminhoValido(exemplo, caminho));
    }

    private static void validarCasosSinteticos() {
        System.out.println("\n== tabuleiros sintéticos com resposta conferível na mão ==");

        // Pulo padrão de xadrez (1,2) a partir de C, sem encostar na borda.
        char[][] puloDireto = tabuleiroUniforme(8, '0', 0, 0, 1, 2);
        conferir("8x8 zerado, C(0,0) -> S(1,2): 1 movimento",
                CavaloHiperpulos.menorNumeroDeMovimentos(puloDireto) == 1);

        // Mesmo pulo, mas para trás e para cima: só chega dando a volta pelo toro.
        char[][] puloComVolta = tabuleiroUniforme(8, '0', 0, 0, 7, 6);
        conferir("8x8 zerado, C(0,0) -> S(7,6): 1 movimento usando o wraparound",
                CavaloHiperpulos.menorNumeroDeMovimentos(puloComVolta) == 1);

        conferirContraOraculo("8x8 zerado, C(0,0) -> S(3,5)", tabuleiroUniforme(8, '0', 0, 0, 3, 5));

        // Dígito 9 num tabuleiro menor que a perna do pulo (10 e 11 contra N=7):
        // só passa se o módulo estiver certo dos dois lados.
        conferirContraOraculo("7x7 todo de dígito 9 (pernas 10 e 11 maiores que N)",
                tabuleiroUniforme(7, '9', 0, 0, 3, 4));

        // Tabuleiro pequeno com dígitos variados: o tamanho do pulo muda a cada casa.
        conferirContraOraculo("9x9 com dígitos variados", CavaloHiperpulos.paraTabuleiro(new String[]{
            "C12345678",
            "234567801",
            "345678012",
            "456780123",
            "5678S0234",
            "678012345",
            "780123456",
            "801234567",
            "012345678",
        }));
    }

    // Confere a distância da BFS contra a busca exaustiva: tem que existir caminho com
    // aquele número de pulos e não existir nenhum com um pulo a menos.
    private static void conferirContraOraculo(String descricao, char[][] tabuleiro) {
        int daBusca = CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro);
        if (daBusca < 0 || daBusca > 4) {
            System.out.printf("  [pulado] %s: distância %d fora do alcance da busca exaustiva%n",
                    descricao, daBusca);
            return;
        }
        conferir(descricao + ": " + daBusca + " pulo(s), confirmado pela busca exaustiva",
                alcancaEm(tabuleiro, daBusca) && !alcancaEm(tabuleiro, daBusca - 1));
    }

    private static void validarContraBuscaExaustiva() {
        System.out.println("\n== BFS contra busca exaustiva em tabuleiros pequenos aleatórios ==");
        Random sorteio = new Random(42);
        int conferidos = 0;
        boolean todosBatem = true;

        for (int amostra = 0; amostra < 400; amostra++) {
            int n = 4 + sorteio.nextInt(5);
            char[][] tabuleiro = tabuleiroAleatorio(n, sorteio);
            int daBusca = CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro);
            if (daBusca < 0 || daBusca > 3) {
                continue; // acima disso a busca exaustiva fica cara demais
            }
            conferidos++;
            boolean alcancaNoLimite = alcancaEm(tabuleiro, daBusca);
            boolean alcancaAntes = daBusca > 0 && alcancaEm(tabuleiro, daBusca - 1);
            if (!alcancaNoLimite || alcancaAntes) {
                todosBatem = false;
            }
        }

        conferir(conferidos + " tabuleiros: distância da BFS é atingível e não existe caminho menor",
                conferidos > 0 && todosBatem);
    }

    private static void procurarTabuleiroSemSolucao() {
        System.out.println("\n== procura por tabuleiro sem solução (exercita a saída \"impossível\") ==");
        Random sorteio = new Random(7);
        char[][] semSolucao = null;

        for (int amostra = 0; amostra < 4000 && semSolucao == null; amostra++) {
            int n = 3 + sorteio.nextInt(8);
            char[][] tabuleiro = tabuleiroAleatorio(n, sorteio);
            if (CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro) == -1) {
                semSolucao = tabuleiro;
            }
        }

        if (semSolucao == null) {
            System.out.println("  [nota] nenhum dos 4000 tabuleiros aleatórios (N de 3 a 10) ficou sem solução;");
            System.out.println("         o ramo \"impossível\" segue como defesa, previsto pelo próprio enunciado.");
        } else {
            conferir("tabuleiro sem solução encontrado: busca independente também devolve -1",
                    distancia(semSolucao, CONVENCAO) == -1);
            conferir("busca exaustiva até 6 pulos também não acha caminho",
                    !alcancaEm(semSolucao, 6));
        }
    }

    private static void validarCasosReais() throws IOException {
        System.out.println("\n== casos de teste reais ==");
        for (String nomeArquivo : CavaloHiperpulos.CASOS) {
            char[][] tabuleiro = CavaloHiperpulos.lerTabuleiro(CavaloHiperpulos.PASTA_CASOS.resolve(nomeArquivo));

            int doSolver = CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro);
            int daBuscaIndependente = distancia(tabuleiro, CONVENCAO);
            int[] caminho = caminhoOtimo(tabuleiro);

            conferir(nomeArquivo + ": solver e busca independente concordam (" + doSolver + ")",
                    doSolver == daBuscaIndependente);
            conferir(nomeArquivo + ": caminho ótimo reconstruído é legal pulo a pulo",
                    caminho != null && caminho.length - 1 == doSolver && caminhoValido(tabuleiro, caminho));

            if (tabuleiro.length <= 400) {
                conferir(nomeArquivo + ": Dijkstra (algoritmo diferente) dá a mesma distância",
                        distanciaPorDijkstra(tabuleiro) == doSolver);
            }
        }
    }

    // Levantamento (não é teste): quanto a resposta de cada caso depende da suposição
    // feita para o dígito que o marcador C cobriu. É a tabela que sustenta a escolha
    // do modelo no relatório.
    private static void validarSensibilidadeDoDigitoDeC() throws IOException {
        System.out.println("\n== sensibilidade ao dígito escondido sob o C ==");
        System.out.println("  (convenção em vigor: "
                + (CONVENCAO == CavaloHiperpulos.UNIAO_DOS_DIGITOS ? "união dos dígitos" : "dígito " + CONVENCAO) + ")");

        for (String nomeArquivo : CavaloHiperpulos.CASOS) {
            char[][] tabuleiro = CavaloHiperpulos.lerTabuleiro(CavaloHiperpulos.PASTA_CASOS.resolve(nomeArquivo));

            int[] porDigito = new int[10];
            boolean todosIguais = true;
            for (int digito = 0; digito <= 9; digito++) {
                porDigito[digito] = distancia(tabuleiro, digito);
                todosIguais &= porDigito[digito] == porDigito[0];
            }

            System.out.printf("  %-14s em vigor=%-3d união=%-3d dígitos 0-9=%s%s%n",
                    nomeArquivo, distancia(tabuleiro, CONVENCAO),
                    distancia(tabuleiro, CavaloHiperpulos.UNIAO_DOS_DIGITOS), Arrays.toString(porDigito),
                    todosIguais ? "  (não depende do dígito)" : "  <- depende do dígito");
        }
    }

    // ---- maquinaria independente -------------------------------------------------

    private static int[][] deslocamentos(int digito) {
        int pernaCurta = digito + 1;
        int pernaLonga = digito + 2;
        return new int[][]{
            {pernaCurta, pernaLonga}, {pernaCurta, -pernaLonga},
            {-pernaCurta, pernaLonga}, {-pernaCurta, -pernaLonga},
            {pernaLonga, pernaCurta}, {pernaLonga, -pernaCurta},
            {-pernaLonga, pernaCurta}, {-pernaLonga, -pernaCurta},
        };
    }

    private static int[][] deslocamentosDaCasa(char[][] tabuleiro, int casa, boolean naOrigem, int digitoSupostoDeC) {
        int n = tabuleiro.length;
        if (naOrigem) {
            if (digitoSupostoDeC != CavaloHiperpulos.UNIAO_DOS_DIGITOS) {
                return deslocamentos(digitoSupostoDeC);
            }
            int[][] uniao = new int[80][];
            int indice = 0;
            for (int digito = 0; digito <= 9; digito++) {
                for (int[] deslocamento : deslocamentos(digito)) {
                    uniao[indice++] = deslocamento;
                }
            }
            return uniao;
        }
        return deslocamentos(tabuleiro[casa / n][casa % n] - '0');
    }

    private static int[] percorrer(char[][] tabuleiro, int digitoSupostoDeC, int[] anterior) {
        int n = tabuleiro.length;
        int origem = localizar(tabuleiro, 'C');
        int destino = localizar(tabuleiro, 'S');

        int[] distancia = new int[n * n];
        Arrays.fill(distancia, -1);
        if (anterior != null) {
            Arrays.fill(anterior, -1);
        }

        int[] fila = new int[n * n];
        int inicio = 0;
        int fim = 0;
        distancia[origem] = 0;
        fila[fim++] = origem;

        while (inicio < fim) {
            int atual = fila[inicio++];
            if (atual == destino) {
                break;
            }
            for (int[] deslocamento : deslocamentosDaCasa(tabuleiro, atual, atual == origem, digitoSupostoDeC)) {
                int vizinho = Math.floorMod(atual / n + deslocamento[0], n) * n
                        + Math.floorMod(atual % n + deslocamento[1], n);
                if (distancia[vizinho] == -1) {
                    distancia[vizinho] = distancia[atual] + 1;
                    if (anterior != null) {
                        anterior[vizinho] = atual;
                    }
                    fila[fim++] = vizinho;
                }
            }
        }
        return distancia;
    }

    private static int distancia(char[][] tabuleiro, int digitoSupostoDeC) {
        return percorrer(tabuleiro, digitoSupostoDeC, null)[localizar(tabuleiro, 'S')];
    }

    private static int[] caminhoOtimo(char[][] tabuleiro) {
        int n = tabuleiro.length;
        int[] anterior = new int[n * n];
        int[] distancia = percorrer(tabuleiro, CONVENCAO, anterior);
        int destino = localizar(tabuleiro, 'S');
        if (distancia[destino] == -1) {
            return null;
        }

        int[] caminho = new int[distancia[destino] + 1];
        int casa = destino;
        for (int passo = caminho.length - 1; passo >= 0; passo--) {
            caminho[passo] = casa;
            casa = anterior[casa];
        }
        return caminho;
    }

    // Refaz o caminho pulo a pulo conferindo que cada salto é mesmo permitido pelo
    // dígito da casa de onde ele parte, sem confiar no que a busca registrou.
    private static boolean caminhoValido(char[][] tabuleiro, int[] caminho) {
        int n = tabuleiro.length;
        int origem = localizar(tabuleiro, 'C');
        if (caminho[0] != origem || caminho[caminho.length - 1] != localizar(tabuleiro, 'S')) {
            return false;
        }

        for (int passo = 0; passo + 1 < caminho.length; passo++) {
            int de = caminho[passo];
            boolean permitido = false;
            for (int[] deslocamento : deslocamentosDaCasa(tabuleiro, de, de == origem, CONVENCAO)) {
                int chegada = Math.floorMod(de / n + deslocamento[0], n) * n
                        + Math.floorMod(de % n + deslocamento[1], n);
                if (chegada == caminho[passo + 1]) {
                    permitido = true;
                    break;
                }
            }
            if (!permitido) {
                return false;
            }
        }
        return true;
    }

    // Dijkstra com todas as arestas de peso 1: algoritmo diferente da BFS, resposta igual.
    private static int distanciaPorDijkstra(char[][] tabuleiro) {
        int n = tabuleiro.length;
        int origem = localizar(tabuleiro, 'C');
        int destino = localizar(tabuleiro, 'S');

        int[] custo = new int[n * n];
        Arrays.fill(custo, Integer.MAX_VALUE);
        custo[origem] = 0;

        PriorityQueue<int[]> fila = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        fila.add(new int[]{origem, 0});

        while (!fila.isEmpty()) {
            int[] topo = fila.poll();
            int atual = topo[0];
            if (topo[1] > custo[atual]) {
                continue;
            }
            if (atual == destino) {
                return custo[atual];
            }
            for (int[] deslocamento : deslocamentosDaCasa(tabuleiro, atual, atual == origem, CONVENCAO)) {
                int vizinho = Math.floorMod(atual / n + deslocamento[0], n) * n
                        + Math.floorMod(atual % n + deslocamento[1], n);
                if (custo[atual] + 1 < custo[vizinho]) {
                    custo[vizinho] = custo[atual] + 1;
                    fila.add(new int[]{vizinho, custo[vizinho]});
                }
            }
        }
        return -1;
    }

    // Oráculo independente: testa exaustivamente todo caminho até o limite de pulos.
    private static boolean alcancaEm(char[][] tabuleiro, int limiteDePulos) {
        int origem = localizar(tabuleiro, 'C');
        return alcancaEm(tabuleiro, origem, localizar(tabuleiro, 'S'), origem, limiteDePulos);
    }

    private static boolean alcancaEm(char[][] tabuleiro, int casa, int destino, int origem, int pulosRestantes) {
        if (casa == destino) {
            return true;
        }
        if (pulosRestantes == 0) {
            return false;
        }
        int n = tabuleiro.length;
        for (int[] deslocamento : deslocamentosDaCasa(tabuleiro, casa, casa == origem, CONVENCAO)) {
            int vizinho = Math.floorMod(casa / n + deslocamento[0], n) * n
                    + Math.floorMod(casa % n + deslocamento[1], n);
            if (alcancaEm(tabuleiro, vizinho, destino, origem, pulosRestantes - 1)) {
                return true;
            }
        }
        return false;
    }

    private static int localizar(char[][] tabuleiro, char alvo) {
        int n = tabuleiro.length;
        for (int linha = 0; linha < n; linha++) {
            for (int coluna = 0; coluna < n; coluna++) {
                if (tabuleiro[linha][coluna] == alvo) {
                    return linha * n + coluna;
                }
            }
        }
        throw new IllegalStateException("casa '" + alvo + "' não encontrada no tabuleiro");
    }

    private static char[][] tabuleiroUniforme(int n, char digito, int linhaC, int colunaC, int linhaS, int colunaS) {
        char[][] tabuleiro = new char[n][n];
        for (char[] linha : tabuleiro) {
            Arrays.fill(linha, digito);
        }
        tabuleiro[linhaC][colunaC] = 'C';
        tabuleiro[linhaS][colunaS] = 'S';
        return tabuleiro;
    }

    private static char[][] tabuleiroAleatorio(int n, Random sorteio) {
        char[][] tabuleiro = new char[n][n];
        for (char[] linha : tabuleiro) {
            for (int coluna = 0; coluna < n; coluna++) {
                linha[coluna] = (char) ('0' + sorteio.nextInt(10));
            }
        }
        int origem = sorteio.nextInt(n * n);
        int destino;
        do {
            destino = sorteio.nextInt(n * n);
        } while (destino == origem);

        tabuleiro[origem / n][origem % n] = 'C';
        tabuleiro[destino / n][destino % n] = 'S';
        return tabuleiro;
    }
}

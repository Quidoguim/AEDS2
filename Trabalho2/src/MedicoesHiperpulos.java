import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.IntSupplier;

// Medições que sustentam a análise de eficiência do relatório. O solver em si
// (CavaloHiperpulos) não é alterado: aqui ficam só as contagens e os tempos que ele não
// expõe, mais duas variantes do mesmo algoritmo para medir quanto cada melhoria ganha.
//
//   1. exemplo do enunciado: caminho mínimo e casas por nível da BFS;
//   2. por caso: quantas casas a BFS retira da fila até chegar a S;
//   3. exploração completa (sem parar em S): mostra que o custo cresce com N², que é o
//      comportamento de pior caso e não depende de onde S está;
//   4. variantes do solver: parada ao descobrir S e casas codificadas como inteiros;
//   5. sensibilidade às duas ambiguidades do enunciado: de qual casa vem o dígito do pulo
//      (onde o cavalo está ou onde vai cair) e qual dígito está escondido sob C ou sob S.
//
// Todo tempo é o melhor de REPETICOES execuções na mesma JVM. Em uma execução só, as
// primeiras passadas pagam a compilação JIT e o tempo de casos pequenos vira ruído.
//
// Rodar com a heap fixa, para a JVM não redimensionar a memória no meio das medições:
//   java -Xms2g -Xmx2g MedicoesHiperpulos
// Com heap automática, o tempo absoluto da exploração completa oscilou em até 1,8x entre
// execuções (55 a 105 ns por casa); com heap fixa ficou em 103 a 106 ns nas três que testamos.
public class MedicoesHiperpulos {

    private static final int REPETICOES = 15;

    // Dígito escondido "qualquer": o pulo é liberado para todos os dígitos de 0 a 9.
    private static final int UNIAO = CavaloHiperpulos.UNIAO_DOS_DIGITOS;
    private static final int OCULTO = -1;

    // Acumula os resultados medidos para a JIT não descartar as chamadas como código morto.
    private static long sumidouro;

    public static void main(String[] args) throws IOException {
        if (CavaloHiperpulos.DIGITO_SUPOSTO_EM_C == CavaloHiperpulos.UNIAO_DOS_DIGITOS) {
            throw new IllegalStateException("As variantes medidas aqui supõem um dígito fixo sob o C");
        }

        String[] nomes = CavaloHiperpulos.CASOS;
        char[][][] tabuleiros = new char[nomes.length][][];
        for (int i = 0; i < nomes.length; i++) {
            tabuleiros[i] = CavaloHiperpulos.lerTabuleiro(CavaloHiperpulos.PASTA_CASOS.resolve(nomes[i]));
        }
        char[][] exemplo = CavaloHiperpulos.paraTabuleiro(CavaloHiperpulos.EXEMPLO_ENUNCIADO);

        conferirVariantes(exemplo, tabuleiros);
        aquecer(tabuleiros[4]);

        imprimirExemplo(exemplo);
        imprimirPorCaso(nomes, tabuleiros);
        imprimirExploracaoCompleta(nomes, tabuleiros);
        imprimirVariantes(nomes, tabuleiros);
        imprimirSensibilidade(nomes, tabuleiros);

        if (sumidouro == Long.MIN_VALUE) {
            System.out.println();
        }
    }

    // ------------------------------------------------------------------ conferência

    // As variantes só valem como comparação se devolverem a mesma resposta do solver.
    private static void conferirVariantes(char[][] exemplo, char[][][] tabuleiros) {
        conferir("exemplo", exemplo);
        for (int i = 0; i < tabuleiros.length; i++) {
            conferir(CavaloHiperpulos.CASOS[i], tabuleiros[i]);
        }
        // O enunciado informa 3 pulos no exemplo: as duas leituras do pulo precisam respeitar isso.
        for (int digitoEmS : new int[]{0, UNIAO}) {
            int obtido = comDigitoDoDestino(exemplo, digitoEmS);
            if (obtido != CavaloHiperpulos.MOVIMENTOS_DO_EXEMPLO) {
                throw new IllegalStateException("Leitura 'destino' (S=" + digitoEmS + ") dá " + obtido
                        + " no exemplo, esperado " + CavaloHiperpulos.MOVIMENTOS_DO_EXEMPLO);
            }
        }
        System.out.println("Variantes conferidas contra o solver: exemplo e os "
                + tabuleiros.length + " casos.");
        System.out.println("Exemplo do enunciado confere (3 pulos) nas duas leituras do pulo.");
        System.out.println();
    }

    private static void conferir(String nome, char[][] tabuleiro) {
        int esperado = CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro);
        int[] obtidos = {
            explorar(tabuleiro, true).movimentos,
            explorar(tabuleiro, false).movimentos,
            comParadaAoDescobrir(tabuleiro),
            comInteiros(tabuleiro),
        };
        for (int obtido : obtidos) {
            if (obtido != esperado) {
                throw new IllegalStateException(nome + ": esperado " + esperado + ", obtido "
                        + Arrays.toString(obtidos));
            }
        }
    }

    private static void aquecer(char[][] tabuleiro) {
        for (int i = 0; i < 3; i++) {
            sumidouro += CavaloHiperpulos.menorNumeroDeMovimentos(tabuleiro);
            sumidouro += explorar(tabuleiro, false).movimentos;
            sumidouro += comParadaAoDescobrir(tabuleiro);
            sumidouro += comInteiros(tabuleiro);
        }
    }

    // ------------------------------------------------------------------ relatórios

    private static void imprimirExemplo(char[][] exemplo) {
        int n = exemplo.length;
        int[] origem = localizar(exemplo, 'C');
        Exploracao completa = explorar(exemplo, false);
        Exploracao ateS = explorar(exemplo, true);
        int[][] caminho = caminhoMinimo(exemplo, ateS.distancia);

        System.out.println("== Exemplo do enunciado (" + n + "x" + n + ") ==");
        System.out.printf("C = (%d,%d)   S = (%d,%d)   menor caminho = %d pulos%n",
                origem[0], origem[1], caminho[caminho.length - 1][0], caminho[caminho.length - 1][1],
                caminho.length - 1);
        System.out.println("| pulo | origem | dígito | pernas | deslocamento | sem módulo | destino (mod " + n + ") |");
        System.out.println("| --- | --- | --- | --- | --- | --- | --- |");
        for (int i = 0; i + 1 < caminho.length; i++) {
            int[] de = caminho[i];
            int[] para = caminho[i + 1];
            int digito = digitoDaCasa(exemplo, de, origem);
            int[] deslocamento = deslocamentoQueChega(de, para, digito, n);
            System.out.printf("| %d | (%d,%d) | %d | (%d,%d) | (%+d,%+d) | (%d,%d) | (%d,%d) |%n",
                    i + 1, de[0], de[1], digito, 1 + digito, 2 + digito,
                    deslocamento[0], deslocamento[1],
                    de[0] + deslocamento[0], de[1] + deslocamento[1], para[0], para[1]);
        }

        int maiorNivel = 0;
        for (int[] linha : completa.distancia) {
            for (int nivel : linha) {
                maiorNivel = Math.max(maiorNivel, nivel);
            }
        }
        int[] porNivel = new int[maiorNivel + 1];
        for (int[] linha : completa.distancia) {
            for (int nivel : linha) {
                if (nivel >= 0) {
                    porNivel[nivel]++;
                }
            }
        }
        System.out.println("Casas descobertas por nível da BFS (exploração completa): "
                + Arrays.toString(porNivel) + "  total alcançável = " + Arrays.stream(porNivel).sum()
                + " de " + n * n);
        System.out.println("Casas retiradas da fila até S (parada em S): " + ateS.retiradas);
        System.out.println("Distância de cada casa a partir de C (base da figura da BFS):");
        for (int[] linha : completa.distancia) {
            StringBuilder texto = new StringBuilder("  ");
            for (int nivel : linha) {
                texto.append(nivel == -1 ? '.' : (char) ('0' + nivel)).append(' ');
            }
            System.out.println(texto);
        }
        System.out.println();
    }

    private static void imprimirPorCaso(String[] nomes, char[][][] tabuleiros) {
        System.out.println("== Resultado por caso (tempo: melhor de " + REPETICOES + ") ==");
        System.out.println("| Caso | N | Movimentos | Casas retiradas até S | % de N² | Tempo (ms) |");
        System.out.println("| --- | --- | --- | --- | --- | --- |");
        for (int i = 0; i < nomes.length; i++) {
            char[][] t = tabuleiros[i];
            long n2 = (long) t.length * t.length;
            Exploracao ateS = explorar(t, true);
            double segundos = melhorTempo(() -> CavaloHiperpulos.menorNumeroDeMovimentos(t));
            System.out.printf("| %s | %d | %s | %,d | %.1f%% | %.2f |%n",
                    nomes[i].replace(".txt", ""), t.length, textoMovimentos(ateS.movimentos),
                    ateS.retiradas, 100.0 * ateS.retiradas / n2, segundos * 1000);
        }
        System.out.println();
    }

    private static void imprimirExploracaoCompleta(String[] nomes, char[][][] tabuleiros) {
        System.out.println("== Exploração completa, sem parar em S (tempo: melhor de " + REPETICOES + ") ==");
        System.out.println("| Caso | N | N² | Casas alcançadas | Tempo (ms) | ns por casa | Tempo / anterior | N² / anterior |");
        System.out.println("| --- | --- | --- | --- | --- | --- | --- | --- |");
        double tempoAnterior = 0;
        long n2Anterior = 0;
        for (int i = 0; i < nomes.length; i++) {
            char[][] t = tabuleiros[i];
            long n2 = (long) t.length * t.length;
            long alcancadas = contarAlcancadas(explorar(t, false).distancia);
            double segundos = melhorTempo(() -> explorar(t, false).movimentos);
            String razaoTempo = i == 0 ? "-" : String.format("%.1f", segundos / tempoAnterior);
            String razaoN2 = i == 0 ? "-" : String.format("%.1f", (double) n2 / n2Anterior);
            System.out.printf("| %s | %d | %,d | %,d | %.2f | %.0f | %s | %s |%n",
                    nomes[i].replace(".txt", ""), t.length, n2, alcancadas, segundos * 1000,
                    segundos * 1e9 / alcancadas, razaoTempo, razaoN2);
            tempoAnterior = segundos;
            n2Anterior = n2;
        }
        System.out.println();
    }

    private static void imprimirVariantes(String[] nomes, char[][][] tabuleiros) {
        System.out.println("== Variantes do solver (tempo: melhor de " + REPETICOES + ") ==");
        System.out.println("A = solver atual (para ao retirar S da fila, fila de int[])");
        System.out.println("B = A, mas devolve assim que S é descoberto");
        System.out.println("C = B com casas codificadas como linha*N+coluna e pulos sem alocar vetores");
        System.out.println("| Caso | N | A (ms) | B (ms) | C (ms) | A/B | A/C |");
        System.out.println("| --- | --- | --- | --- | --- | --- | --- |");
        for (int i = 0; i < nomes.length; i++) {
            char[][] t = tabuleiros[i];
            double a = melhorTempo(() -> CavaloHiperpulos.menorNumeroDeMovimentos(t));
            double b = melhorTempo(() -> comParadaAoDescobrir(t));
            double c = melhorTempo(() -> comInteiros(t));
            System.out.printf("| %s | %d | %.2f | %.2f | %.2f | %.1fx | %.1fx |%n",
                    nomes[i].replace(".txt", ""), t.length, a * 1000, b * 1000, c * 1000, a / b, a / c);
        }
        System.out.println();
    }

    private static void imprimirSensibilidade(String[] nomes, char[][][] tabuleiros) {
        System.out.println("== Sensibilidade às ambiguidades do enunciado (movimentos) ==");
        System.out.println("Origem  = o dígito da casa onde o cavalo está define o pulo (leitura adotada)");
        System.out.println("Destino = o dígito da casa onde o cavalo vai cair define o pulo");
        System.out.println("Dígito escondido: sob o C na leitura Origem, sob o S na leitura Destino;"
                + " 0 = pulo normal, qualquer = o menor resultado entre os dígitos 0 a 9");
        System.out.println("| Caso | Origem, C=0 (adotada) | Origem, C qualquer | Destino, S=0 | Destino, S qualquer | Todas iguais? |");
        System.out.println("| --- | --- | --- | --- | --- | --- |");
        for (int i = 0; i < nomes.length; i++) {
            char[][] t = tabuleiros[i];
            int[] valores = {
                CavaloHiperpulos.menorNumeroDeMovimentos(t, 0),
                CavaloHiperpulos.menorNumeroDeMovimentos(t, UNIAO),
                comDigitoDoDestino(t, 0),
                comDigitoDoDestino(t, UNIAO),
            };
            boolean iguais = Arrays.stream(valores).distinct().count() == 1;
            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", nomes[i].replace(".txt", ""),
                    textoMovimentos(valores[0]), textoMovimentos(valores[1]),
                    textoMovimentos(valores[2]), textoMovimentos(valores[3]), iguais ? "sim" : "não");
        }
        System.out.println();
    }

    private static String textoMovimentos(int movimentos) {
        return movimentos == -1 ? "impossível" : String.valueOf(movimentos);
    }

    private static long contarAlcancadas(int[][] distancia) {
        long total = 0;
        for (int[] linha : distancia) {
            for (int nivel : linha) {
                if (nivel != -1) {
                    total++;
                }
            }
        }
        return total;
    }

    private static double melhorTempo(IntSupplier execucao) {
        System.gc(); // a coleta de lixo da medição anterior não deve cair dentro desta
        long melhor = Long.MAX_VALUE;
        for (int i = 0; i < REPETICOES; i++) {
            long inicio = System.nanoTime();
            sumidouro += execucao.getAsInt();
            melhor = Math.min(melhor, System.nanoTime() - inicio);
        }
        return melhor / 1_000_000_000.0;
    }

    // ------------------------------------------------------------------ BFS instrumentada

    private static final class Exploracao {
        int movimentos = -1;   // distância até S, ou -1 se S não foi alcançada
        long retiradas;        // casas retiradas da fila (S incluída, se chegou a sair)
        int[][] distancia;     // -1 nas casas não descobertas
    }

    // Mesma BFS do solver, com contadores. Com pararAoChegar=false ela continua depois de
    // retirar S (sem expandi-la: a busca real termina ali), para medir o custo de varrer
    // tudo o que é alcançável.
    private static Exploracao explorar(char[][] tabuleiro, boolean pararAoChegar) {
        int n = tabuleiro.length;
        int[] origem = localizar(tabuleiro, 'C');
        int[] destino = localizar(tabuleiro, 'S');

        Exploracao resultado = new Exploracao();
        resultado.distancia = new int[n][n];
        for (int[] linha : resultado.distancia) {
            Arrays.fill(linha, -1);
        }

        Deque<int[]> fila = new ArrayDeque<>();
        resultado.distancia[origem[0]][origem[1]] = 0;
        fila.add(origem);

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            resultado.retiradas++;
            if (atual[0] == destino[0] && atual[1] == destino[1]) {
                resultado.movimentos = resultado.distancia[atual[0]][atual[1]];
                if (pararAoChegar) {
                    return resultado;
                }
                continue;
            }
            int digito = digitoDaCasa(tabuleiro, atual, origem);
            for (int[] deslocamento : CavaloHiperpulos.deslocamentosParaDigito(digito)) {
                int novaLinha = Math.floorMod(atual[0] + deslocamento[0], n);
                int novaColuna = Math.floorMod(atual[1] + deslocamento[1], n);
                if (resultado.distancia[novaLinha][novaColuna] == -1) {
                    resultado.distancia[novaLinha][novaColuna] = resultado.distancia[atual[0]][atual[1]] + 1;
                    fila.add(new int[]{novaLinha, novaColuna});
                }
            }
        }
        return resultado;
    }

    private static int digitoDaCasa(char[][] tabuleiro, int[] casa, int[] origem) {
        if (casa[0] == origem[0] && casa[1] == origem[1]) {
            return CavaloHiperpulos.DIGITO_SUPOSTO_EM_C;
        }
        return tabuleiro[casa[0]][casa[1]] - '0';
    }

    // ------------------------------------------------------------------ caminho do exemplo

    // Reconstrói um caminho mínimo andando de S para trás: em cada nível procura uma casa
    // cujo pulo chega na casa seguinte. Só serve para tabuleiros pequenos, como o exemplo.
    private static int[][] caminhoMinimo(char[][] tabuleiro, int[][] distancia) {
        int n = tabuleiro.length;
        int[] origem = localizar(tabuleiro, 'C');
        int[] destino = localizar(tabuleiro, 'S');
        int passos = distancia[destino[0]][destino[1]];

        int[][] caminho = new int[passos + 1][];
        caminho[passos] = destino;
        for (int nivel = passos - 1; nivel >= 0; nivel--) {
            caminho[nivel] = anteriorNoNivel(tabuleiro, distancia, caminho[nivel + 1], nivel, origem);
        }
        return caminho;
    }

    private static int[] anteriorNoNivel(char[][] tabuleiro, int[][] distancia, int[] alvo, int nivel,
            int[] origem) {
        int n = tabuleiro.length;
        for (int linha = 0; linha < n; linha++) {
            for (int coluna = 0; coluna < n; coluna++) {
                if (distancia[linha][coluna] != nivel) {
                    continue;
                }
                int[] candidata = {linha, coluna};
                int digito = digitoDaCasa(tabuleiro, candidata, origem);
                if (deslocamentoQueChega(candidata, alvo, digito, n) != null) {
                    return candidata;
                }
            }
        }
        throw new IllegalStateException("Nenhuma casa do nível " + nivel + " chega em "
                + Arrays.toString(alvo));
    }

    // Primeiro deslocamento (na ordem de deslocamentosParaDigito) que leva de 'de' a 'para'
    // depois do módulo, ou null se nenhum leva.
    private static int[] deslocamentoQueChega(int[] de, int[] para, int digito, int n) {
        for (int[] deslocamento : CavaloHiperpulos.deslocamentosParaDigito(digito)) {
            if (Math.floorMod(de[0] + deslocamento[0], n) == para[0]
                    && Math.floorMod(de[1] + deslocamento[1], n) == para[1]) {
                return deslocamento;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ variantes

    // B: igual ao solver, mas como a BFS já atribui a distância final de uma casa ao
    // descobri-la, não precisa esperar S chegar à frente da fila para devolver a resposta.
    private static int comParadaAoDescobrir(char[][] tabuleiro) {
        int n = tabuleiro.length;
        int[] origem = localizar(tabuleiro, 'C');
        int[] destino = localizar(tabuleiro, 'S');

        int[][] distancia = new int[n][n];
        for (int[] linha : distancia) {
            Arrays.fill(linha, -1);
        }

        Deque<int[]> fila = new ArrayDeque<>();
        distancia[origem[0]][origem[1]] = 0;
        fila.add(origem);

        while (!fila.isEmpty()) {
            int[] atual = fila.poll();
            int digito = digitoDaCasa(tabuleiro, atual, origem);
            for (int[] deslocamento : CavaloHiperpulos.deslocamentosParaDigito(digito)) {
                int novaLinha = Math.floorMod(atual[0] + deslocamento[0], n);
                int novaColuna = Math.floorMod(atual[1] + deslocamento[1], n);
                if (distancia[novaLinha][novaColuna] == -1) {
                    distancia[novaLinha][novaColuna] = distancia[atual[0]][atual[1]] + 1;
                    if (novaLinha == destino[0] && novaColuna == destino[1]) {
                        return distancia[novaLinha][novaColuna];
                    }
                    fila.add(new int[]{novaLinha, novaColuna});
                }
            }
        }
        return -1;
    }

    // C: B sem alocação por casa. A casa (linha, coluna) vira o inteiro linha*N+coluna, a
    // fila é um vetor de int com N² posições (cada casa entra no máximo uma vez) e as oito
    // orientações do pulo saem de bits do índice do laço, sem criar vetores de deslocamento.
    private static int comInteiros(char[][] tabuleiro) {
        int n = tabuleiro.length;
        int total = n * n;

        byte[] digitos = new byte[total];
        int origem = -1;
        int destino = -1;
        for (int linha = 0; linha < n; linha++) {
            for (int coluna = 0; coluna < n; coluna++) {
                char simbolo = tabuleiro[linha][coluna];
                int casa = linha * n + coluna;
                if (simbolo == 'C') {
                    origem = casa;
                    digitos[casa] = (byte) CavaloHiperpulos.DIGITO_SUPOSTO_EM_C;
                } else if (simbolo == 'S') {
                    destino = casa;
                } else {
                    digitos[casa] = (byte) (simbolo - '0');
                }
            }
        }

        int[] distancia = new int[total];
        Arrays.fill(distancia, -1);
        int[] fila = new int[total];
        int inicio = 0;
        int fim = 0;
        distancia[origem] = 0;
        fila[fim++] = origem;

        while (inicio < fim) {
            int atual = fila[inicio++];
            int linha = atual / n;
            int coluna = atual % n;
            int pernaCurta = 1 + digitos[atual];
            int pernaLonga = 2 + digitos[atual];
            for (int orientacao = 0; orientacao < 8; orientacao++) {
                boolean longaNaLinha = (orientacao & 4) != 0;
                int pernaLinha = longaNaLinha ? pernaLonga : pernaCurta;
                int pernaColuna = longaNaLinha ? pernaCurta : pernaLonga;
                int sinalLinha = (orientacao & 1) == 0 ? 1 : -1;
                int sinalColuna = (orientacao & 2) == 0 ? 1 : -1;
                int novaLinha = Math.floorMod(linha + sinalLinha * pernaLinha, n);
                int novaColuna = Math.floorMod(coluna + sinalColuna * pernaColuna, n);
                int nova = novaLinha * n + novaColuna;
                if (distancia[nova] == -1) {
                    distancia[nova] = distancia[atual] + 1;
                    if (nova == destino) {
                        return distancia[nova];
                    }
                    fila[fim++] = nova;
                }
            }
        }
        return -1;
    }

    // Leitura alternativa do enunciado: quem define o tamanho do pulo é o dígito da casa onde
    // o cavalo vai cair. O pulo X -> Y existe se Y fica a (±(1+dY), ±(2+dY)) de X (ou trocando as
    // pernas), com dY o dígito de Y. Como o dígito de Y é que decide, a busca testa os dez
    // tamanhos de pulo a partir de cada casa e só aceita os que caem em uma casa com aquele dígito.
    // O dígito escondido sob S (a última casa do caminho) é digitoEmS, ou UNIAO para qualquer um.
    private static int comDigitoDoDestino(char[][] tabuleiro, int digitoEmS) {
        int n = tabuleiro.length;
        int total = n * n;

        byte[] digitos = new byte[total];
        int origem = -1;
        int destino = -1;
        for (int linha = 0; linha < n; linha++) {
            for (int coluna = 0; coluna < n; coluna++) {
                char simbolo = tabuleiro[linha][coluna];
                int casa = linha * n + coluna;
                if (simbolo == 'C') {
                    origem = casa;
                    digitos[casa] = OCULTO;
                } else if (simbolo == 'S') {
                    destino = casa;
                    digitos[casa] = OCULTO;
                } else {
                    digitos[casa] = (byte) (simbolo - '0');
                }
            }
        }

        int[] distancia = new int[total];
        Arrays.fill(distancia, -1);
        int[] fila = new int[total];
        int inicio = 0;
        int fim = 0;
        distancia[origem] = 0;
        fila[fim++] = origem;

        while (inicio < fim) {
            int atual = fila[inicio++];
            int linha = atual / n;
            int coluna = atual % n;
            for (int digito = 0; digito <= 9; digito++) {
                int pernaCurta = 1 + digito;
                int pernaLonga = 2 + digito;
                for (int orientacao = 0; orientacao < 8; orientacao++) {
                    boolean longaNaLinha = (orientacao & 4) != 0;
                    int pernaLinha = longaNaLinha ? pernaLonga : pernaCurta;
                    int pernaColuna = longaNaLinha ? pernaCurta : pernaLonga;
                    int sinalLinha = (orientacao & 1) == 0 ? 1 : -1;
                    int sinalColuna = (orientacao & 2) == 0 ? 1 : -1;
                    int novaLinha = Math.floorMod(linha + sinalLinha * pernaLinha, n);
                    int novaColuna = Math.floorMod(coluna + sinalColuna * pernaColuna, n);
                    int nova = novaLinha * n + novaColuna;
                    if (distancia[nova] != -1) {
                        continue;
                    }
                    boolean pulaAqui = nova == destino
                            ? digitoEmS == UNIAO || digitoEmS == digito
                            : digitos[nova] == digito;
                    if (pulaAqui) {
                        distancia[nova] = distancia[atual] + 1;
                        if (nova == destino) {
                            return distancia[nova];
                        }
                        fila[fim++] = nova;
                    }
                }
            }
        }
        return -1;
    }

    private static int[] localizar(char[][] tabuleiro, char alvo) {
        for (int linha = 0; linha < tabuleiro.length; linha++) {
            for (int coluna = 0; coluna < tabuleiro[linha].length; coluna++) {
                if (tabuleiro[linha][coluna] == alvo) {
                    return new int[]{linha, coluna};
                }
            }
        }
        throw new IllegalStateException("Casa '" + alvo + "' não encontrada no tabuleiro");
    }
}

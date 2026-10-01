import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class CavaloHiperpulos {

    public static final String[] CASOS = {
        "caso40.txt", "caso80.txt", "caso100.txt", "caso150.txt",
        "caso200.txt", "caso400.txt", "caso800.txt", "caso1500.txt",
    };

    public static final Path PASTA_CASOS = Path.of("..", "casos-teste");

    // Tabuleiro de exemplo do enunciado, com a resposta que ele mesmo informa.
    public static final String[] EXEMPLO_ENUNCIADO = {
        "4286305993",
        "67126514C1",
        "3541559238",
        "6344361236",
        "0419867802",
        "4271613350",
        "S668962685",
        "6244051139",
        "3714702030",
        "0084825587",
    };

    public static final int MOVIMENTOS_DO_EXEMPLO = 3;

    // O marcador C cobre o dígito daquela casa. Pelo enunciado, o dígito que define o
    // tamanho do pulo é o da casa onde o cavalo está, e em C o tamanho do pulo é 0: o
    // primeiro pulo é um pulo comum de xadrez.
    public static final int DIGITO_EM_C = 0;

    public static void main(String[] args) throws IOException {
        autoTeste();

        for (String nomeArquivo : CASOS) {
            char[][] tabuleiro = lerTabuleiro(PASTA_CASOS.resolve(nomeArquivo));

            long inicio = System.nanoTime();
            int movimentos = menorNumeroDeMovimentos(tabuleiro);
            double segundos = (System.nanoTime() - inicio) / 1_000_000_000.0;

            String resultado = movimentos == -1 ? "impossível" : movimentos + " movimento(s)";
            System.out.printf("%-14s N=%,5d -> %-20s (%.3fs)%n",
                    nomeArquivo, tabuleiro.length, resultado, segundos);
        }
    }

    // Confere o algoritmo contra o exemplo do próprio enunciado (C -> S em 3 pulos)
    // antes de confiar no resultado dos casos de teste reais.
    private static void autoTeste() {
        int obtido = menorNumeroDeMovimentos(paraTabuleiro(EXEMPLO_ENUNCIADO));
        if (obtido != MOVIMENTOS_DO_EXEMPLO) {
            throw new IllegalStateException("Auto-teste falhou: esperado " + MOVIMENTOS_DO_EXEMPLO
                    + " movimento(s), obtido " + obtido);
        }
    }

    public static char[][] paraTabuleiro(String[] linhas) {
        char[][] tabuleiro = new char[linhas.length][];
        for (int i = 0; i < linhas.length; i++) {
            tabuleiro[i] = linhas[i].toCharArray();
        }
        return tabuleiro;
    }

    public static char[][] lerTabuleiro(Path caminho) throws IOException {
        List<String> linhas = Files.readAllLines(caminho);
        char[][] tabuleiro = new char[linhas.size()][];
        for (int i = 0; i < linhas.size(); i++) {
            tabuleiro[i] = linhas.get(i).toCharArray();
        }
        return tabuleiro;
    }

    public static int menorNumeroDeMovimentos(char[][] tabuleiro) {
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
            if (atual[0] == destino[0] && atual[1] == destino[1]) {
                return distancia[atual[0]][atual[1]];
            }

            boolean naOrigem = atual[0] == origem[0] && atual[1] == origem[1];
            for (int[] deslocamento : deslocamentosDisponiveis(tabuleiro, atual, naOrigem)) {
                int novaLinha = Math.floorMod(atual[0] + deslocamento[0], n);
                int novaColuna = Math.floorMod(atual[1] + deslocamento[1], n);
                if (distancia[novaLinha][novaColuna] == -1) {
                    distancia[novaLinha][novaColuna] = distancia[atual[0]][atual[1]] + 1;
                    fila.add(new int[]{novaLinha, novaColuna});
                }
            }
        }
        return -1;
    }

    private static int[][] deslocamentosDisponiveis(char[][] tabuleiro, int[] posicao, boolean naOrigem) {
        if (naOrigem) {
            return deslocamentosParaDigito(DIGITO_EM_C);
        }
        int digito = tabuleiro[posicao[0]][posicao[1]] - '0';
        return deslocamentosParaDigito(digito);
    }

    // Pulo em L generalizado: dígito d estica as duas pernas do L padrão de xadrez
    // (1,2) para (1+d, 2+d), nas 8 orientações usuais.
    public static int[][] deslocamentosParaDigito(int digito) {
        int pernaCurta = 1 + digito;
        int pernaLonga = 2 + digito;
        return new int[][]{
            {pernaCurta, pernaLonga}, {pernaCurta, -pernaLonga}, {-pernaCurta, pernaLonga}, {-pernaCurta, -pernaLonga},
            {pernaLonga, pernaCurta}, {pernaLonga, -pernaCurta}, {-pernaLonga, pernaCurta}, {-pernaLonga, -pernaCurta},
        };
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

import java.util.ArrayList;
import java.util.List;

public class NumerosDecompostos {

    // Faixas exigidas pelo enunciado (limites inclusivos dos dois lados).
    private static final int[][] FAIXAS = {
        {2, 100_000},
        {100_000, 200_000},
        {200_000, 400_000},
        {400_000, 800_000},
        {800_000, 1_000_000},
        {1_000_000, 1_500_000},
        {1_500_000, 2_000_000},
    };

    public static int contarDecomposicoes(int n) {
        return contar(n, 1);
    }

    // Percorre os tamanhos de sequência k = 1, 2, 3, ... enquanto a menor soma possível
    // para aquele k (1+2+...+k) ainda cabe em n. Para cada k existe no máximo uma
    // sequência candidata, então basta checar se o ponto de partida dela é inteiro.
    private static int contar(int n, int k) {
        int menorSoma = k * (k + 1) / 2;
        if (menorSoma > n) {
            return 0;
        }
        int resto = n - k * (k - 1) / 2;
        int decomposicaoValida = (resto % k == 0) ? 1 : 0;
        return decomposicaoValida + contar(n, k + 1);
    }

    public static void main(String[] args) {
        validarExemplos();
        for (int[] faixa : FAIXAS) {
            resolverFaixa(faixa[0], faixa[1]);
        }
    }

    private static void validarExemplos() {
        int[] exemplos = {15, 9, 16};
        for (int n : exemplos) {
            System.out.println(n + " tem " + contarDecomposicoes(n) + " decomposições");
        }
        System.out.println();
    }

    private static void resolverFaixa(int inicio, int fim) {
        long inicioNs = System.nanoTime();

        int maiorContagem = 0;
        List<Integer> vencedores = new ArrayList<>();
        for (int n = inicio; n <= fim; n++) {
            int contagem = contarDecomposicoes(n);
            if (contagem > maiorContagem) {
                maiorContagem = contagem;
                vencedores.clear();
                vencedores.add(n);
            } else if (contagem == maiorContagem) {
                vencedores.add(n);
            }
        }

        double segundos = (System.nanoTime() - inicioNs) / 1_000_000_000.0;
        System.out.printf("[%,d, %,d] -> %d decomposições, número(s) = %s (%.3fs)%n",
                inicio, fim, maiorContagem, vencedores, segundos);
    }
}

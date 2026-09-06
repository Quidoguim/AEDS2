public class ComparacaoIngenua {

    // Abordagem óbvia: tenta cada início de sequência possível, de 1 até n,
    // e para cada início vai somando termo a termo até alcançar ou passar n.
    public static int contarIngenuo(int n) {
        return contarComecosPossiveis(n, 1);
    }

    private static int contarComecosPossiveis(int n, int inicio) {
        if (inicio > n) {
            return 0;
        }
        int decomposicaoValida = alcancaSoma(n, inicio, inicio, inicio) ? 1 : 0;
        return decomposicaoValida + contarComecosPossiveis(n, inicio + 1);
    }

    private static boolean alcancaSoma(int n, int inicio, int atual, int soma) {
        if (soma == n) {
            return true;
        }
        if (soma > n) {
            return false;
        }
        return alcancaSoma(n, inicio, atual + 1, soma + atual + 1);
    }

    private static final int[] TAMANHOS = {500, 1_000, 2_000, 4_000, 8_000, 16_000, 32_000};

    public static void main(String[] args) {
        for (int tamanho : TAMANHOS) {
            comparar(tamanho);
        }
    }

    private static void comparar(int tamanho) {
        long inicioIngenuo = System.nanoTime();
        int maiorIngenuo = 0;
        for (int n = 2; n <= tamanho; n++) {
            maiorIngenuo = Math.max(maiorIngenuo, contarIngenuo(n));
        }
        double tempoIngenuo = (System.nanoTime() - inicioIngenuo) / 1_000_000_000.0;

        long inicioEficiente = System.nanoTime();
        int maiorEficiente = 0;
        for (int n = 2; n <= tamanho; n++) {
            maiorEficiente = Math.max(maiorEficiente, NumerosDecompostos.contarDecomposicoes(n));
        }
        double tempoEficiente = (System.nanoTime() - inicioEficiente) / 1_000_000_000.0;

        System.out.printf("faixa [2, %d]: ingênuo = %.3fs, eficiente = %.3fs (mesmo resultado: %s)%n",
                tamanho, tempoIngenuo, tempoEficiente, maiorIngenuo == maiorEficiente);
    }
}

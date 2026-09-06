public class NumerosDecompostos {

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
        int[] exemplos = {15, 9, 16};
        for (int n : exemplos) {
            System.out.println(n + " tem " + contarDecomposicoes(n) + " decomposições");
        }
    }
}

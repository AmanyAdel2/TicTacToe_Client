
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hardCompMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HardCompMode {
    private char[][] board;
    private Random random = new Random();

    public HardCompMode() {
        resetGame();
    }

    public void resetGame() {
        board = new char[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = '-';
            }
        }
    }

    public boolean makeMove(int row, int col, char player) {
        if (board[row][col] == '-') {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public char[][] getBoard() {
        return board;
    }

    public List<int[]> checkWinner(char player) {
        List<int[]> winningCells = new ArrayList<>();

        
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                winningCells.add(new int[]{i, 0});
                winningCells.add(new int[]{i, 1});
                winningCells.add(new int[]{i, 2});
                return winningCells;
            }
        }

        
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                winningCells.add(new int[]{0, i});
                winningCells.add(new int[]{1, i});
                winningCells.add(new int[]{2, i});
                return winningCells;
            }
        }

        
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            winningCells.add(new int[]{0, 0});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 2});
            return winningCells;
        }

        
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            winningCells.add(new int[]{0, 2});
            winningCells.add(new int[]{1, 1});
            winningCells.add(new int[]{2, 0});
            return winningCells;
        }

        return winningCells; 
    }

    public boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] findBestMove(char player) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = player;
                    int score = minimax(board, false);
                    board[i][j] = '-';

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(char[][] board, boolean isMaximizing) {
        char currentPlayer = isMaximizing ? 'O' : 'X';

        if (checkWinner('O').size() > 0) return 1;
        if (checkWinner('X').size() > 0) return -1;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = 'O';
                        int score = minimax(board, false);
                        board[i][j] = '-';
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = 'X';
                        int score = minimax(board, true);
                        board[i][j] = '-';
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }
}